package algonquin.cst2335.cst2355final.tianjiaosun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import algonquin.cst2335.cst2355final.MainActivity;
import algonquin.cst2335.cst2355final.R;
import algonquin.cst2335.cst2355final.databinding.ActivitySunBinding;
import algonquin.cst2335.cst2355final.databinding.SunRecordBinding;
import algonquin.cst2335.cst2355final.rita.DeezerSong;
import algonquin.cst2335.cst2355final.yuxing.SearchRoom;
import algonquin.cst2335.cst2355final.ziyao.RecipeMain;


public class SunActivity extends AppCompatActivity {

    ActivitySunBinding binding;
    ArrayList<Sun> suns = null; // At the beginning, there are no messages; initialize in SunViewModel.java
    SunViewModel sunModel; // use a ViewModel to make sure data survive the rotation change
    private RecyclerView.Adapter sunAdapter; // to hold the object below
    SunDAO sDAO;
    int selectedRow; // to hold the "position", find which row this is"

    Sun sToPass; // to hold the "sun" object to pass to other classes or methods
    protected String cityName; // to hold the city name input
//    protected String latClass; // to hold the latitude
//    protected String lngClass; // to hold the longitude

    protected RequestQueue queue = null; // for volley

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySunBinding.inflate(getLayoutInflater());
        queue = Volley.newRequestQueue(this);//HTTP Connections: Volley. A Volley object that will connect to a server
        setContentView(binding.getRoot());
//        String cityName; // to hold the city name input

        SharedPreferences prefs = getSharedPreferences("sunSharedData", Context.MODE_PRIVATE);
        binding.latInput.setText(prefs.getString("latitude",""));
        binding.lngInput.setText(prefs.getString("longitude",""));
//        binding.editCity.setText(prefs.getString("cityName",""));

        // Set up InputFilter for latitude input validation. Range within the range of -90 to +90, up to 6 decimal places
        InputFilter latitudeFilter = new InputFilter() {
            final Pattern pattern = Pattern.compile("^(-?\\d{0,2}(\\.\\d{0,6})?|\\d{0,1}(\\.\\d{0,6})?|90(\\.0{0,6})?)$");

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String input = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());

                if (!pattern.matcher(input).matches()) {
                    showInvalidInputWarning(getString(R.string.valid_input_lat));
                    Log.d("Latitude input invalid", "Latitude input invalid");
                    return "";
                }

                return null;
            }
        };

        // Set up InputFilter for longitude input validation, Range within the range of -180 to +180, up to 6 decimal places
        InputFilter longitudeFilter = new InputFilter() {
            final Pattern pattern = Pattern.compile("^(-?\\d{0,3}(\\.\\d{0,6})?|\\d{0,2}(\\.\\d{0,6})?|180(\\.0{0,6})?)$");

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String input = dest.subSequence(0, dstart) + source.toString() + dest.subSequence(dend, dest.length());

                if (!pattern.matcher(input).matches()) {
                    Log.d("Longitude input invalid", "Longitude input invalid");
                    showInvalidInputWarning(getString(R.string.valid_input_lng));
                    return "";
                }

                return null;
            }
        };


        // Apply the InputFilter to the EditText
        binding.latInput.setFilters(new InputFilter[]{latitudeFilter});
        // Apply the InputFilter to the EditText
        binding.lngInput.setFilters(new InputFilter[]{longitudeFilter});


        // onCreateOptionMenu
        setSupportActionBar(binding.sunToolbar);// initialize the toolbar
        getSupportActionBar().setTitle(getString(R.string.sun_toolbar_title));


        sunModel = new ViewModelProvider(this).get(SunViewModel.class);
        suns = sunModel.suns.getValue(); //get the array list from ViewModelProvider, might be NULL

        //listener to the MutableLiveData object
        sunModel.selectedSun.observe(this,(selectedSun) ->{
            if(selectedSun != null) {
                //create a Sun fragment
                SunDetailsFragment sunFragment = new SunDetailsFragment(selectedSun);

                FragmentManager fMgr = getSupportFragmentManager();
                FragmentTransaction transaction = fMgr.beginTransaction();
                transaction.addToBackStack("Add to back stack"); // adds to the history
                transaction.replace(R.id.sunFragmentLocation, sunFragment);//The add() function needs the id of the FrameLayout where it will load the fragment
                transaction.commit();// This line actually loads the fragment into the specified FrameLayout
            }
        });

        //load sun records from the database:
        SunDatabase db = Room.databaseBuilder(getApplicationContext(),SunDatabase.class, "sunDatabase").build();
        //initialize the variable
        sDAO = db.sunDAO();

        if (suns == null) {
            sunModel.suns.postValue(suns = new ArrayList<Sun>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                suns.addAll(sDAO.getAllSuns()); //Once you get the data from database
                runOnUiThread(() -> binding.sunRecycleView.setAdapter(sunAdapter)); //You can then load the RecyclerView
            });
        }


        binding.sunSearchButton.setOnClickListener( cli ->{

            String sunLatitude = binding.latInput.getText().toString();
            String sunLongitude = binding.lngInput.getText().toString();
            String sunrise = "sunrise";
            String sunset = "sunset";
            String solar_noon = "noon";
            String golden_hour = "golden hour";
            String timezone = "Qingdao";

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("latitude", sunLatitude);
            editor.putString("longitude", sunLongitude);
            editor.apply();


            // Prepare api url
            // can add try and catch (UnsupportedEncodingException e) here if need encode - URLEncoder.encode(varTextInput, "UTF-8")
//            String url = "https://api.sunrisesunset.io/json?lat=" + sunLatitude + "&lng=" + sunLongitude + "&timezone=UTC&date=today"; // if using UTC
            String url = "https://api.sunrisesunset.io/json?lat=" + sunLatitude + "&lng=" + sunLongitude;
            Log.d("Sunrise Sunset", "Request URL: " + url);

            //this goes in the button click handler:
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    (response) -> {
                        try {
                            if (response.has("results")) {
                                Log.d("Sun API Results", "Sun API Request has results");
                                JSONObject results = response.getJSONObject("results");
                                String status = response.getString("status"); // get the JSONArray associated with "status"
                                Log.d("Sun API status", "Status" + status);
                            }
                        } catch (JSONException e) {
                            Log.e("API response: ", "response don't have results");
                            e.printStackTrace();
                            runOnUiThread(() ->
                                    Toast.makeText(SunActivity.this, getString(R.string.sun_sun_api_not_available), Toast.LENGTH_SHORT).show()
                            );
                        }

                        try {
                            JSONObject results = response.getJSONObject("results");
                            String status = response.getString("status"); // get the JSONArray associated with "status"

                            if (results.length() == 0) {
                                Log.e("Sun API Status not OK", "The Sun API results.length() == 0");
                                Toast.makeText(this, getString(R.string.sun_found_nothing), Toast.LENGTH_SHORT).show();
                            } else if (!"OK".equals(status)) {
                                // Status is not OK
                                Log.e("Sun API Status not OK", "The Sun API status is not OK");
                                Toast.makeText(this, getString(R.string.sun_sun_api_status_not_ok), Toast.LENGTH_SHORT).show();
                            } else {
                                // When sunArray and sunStatus both ok:
                                Log.d("Sun API ResultsStatusOK", "Sun API Results and Status OK");

                                // Read the values in the "results" in JSON
                                String sunriseResult = results.getString("sunrise");
                                String sunsetResult = results.getString("sunset");
                                String solar_noonResult = results.getString("solar_noon");
                                String golden_hourResult = results.getString("golden_hour");
                                String timezoneResult = results.getString("timezone");
                                String cityNameFromInput;

                                if(cityName != null) {
                                    cityNameFromInput = cityName;
                                } else {
                                    cityNameFromInput = getResources().getString(R.string.sun_no_name_location);
                                }


                                Sun s = new Sun(sunLatitude, sunLongitude, sunriseResult, sunsetResult, solar_noonResult, golden_hourResult, timezoneResult, cityNameFromInput);
                                sToPass = s; // pass the sun obj to the class level

                                // tell the recycle view that there is new data SetChanged()
                                sunAdapter.notifyDataSetChanged();//redraw the screen

                                SunDetailsFragment sunFragment = new SunDetailsFragment(s);

                                FragmentManager fMgr = getSupportFragmentManager();
                                FragmentTransaction transaction = fMgr.beginTransaction();
                                transaction.addToBackStack("Add to back stack"); // adds to the history
                                transaction.replace(R.id.sunFragmentLocation, sunFragment);//The add() function needs the id of the FrameLayout where it will load the fragment
                                transaction.commit();// This line actually loads the fragment into the specified FrameLayout

                                //clear the previous text
                                binding.latInput.setText("");
                                binding.lngInput.setText("");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    (error) -> {
                        Log.e("JsonObjectRequest Error", "JsonObjectRequest Error");
                    });
            queue.add(request);

            //clear the previous text
            binding.latInput.setText("");
            binding.lngInput.setText("");

        });

        // Will draw the recycle view
        binding.sunRecycleView.setAdapter(sunAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                SunDetailsLayoutBinding binding = SunDetailsLayoutBinding.inflate(getLayoutInflater(), parent, false);

                SunRecordBinding binding2 = SunRecordBinding.inflate(getLayoutInflater(),parent,false);
                return new MyRowHolder(binding2.getRoot());
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                // where to overwrite default text:
                Sun obj = suns.get(position);

                holder.sunLatitudeView.setText(obj.getSunLatitude());
                holder.sunLongitudeView.setText(obj.getSunLongitude());

//                holder.sunriseView.setText(obj.getSunrise());
//                holder.sunsetView.setText(obj.getSunset());
//                holder.solar_noonView.setText(obj.getSolar_noon());
//                holder.golden_hourView.setText(obj.getGolder_hour());
//                holder.timezoneView.setText(obj.getTimezone());
            }


            @Override
            public int getItemCount() {
                return suns.size();
            }
        });

        // To specify a single column scrolling in a Vertical direction
        binding.sunRecycleView.setLayoutManager(new LinearLayoutManager(this));
    }

    // This class represents one row
    public class MyRowHolder extends RecyclerView.ViewHolder {

        public TextView sunLatitudeView; // maybe not needed?
        public TextView sunLongitudeView; // maybe not needed?
        public TextView sunriseView;
        public TextView sunsetView;
        public TextView solar_noonView;
        public TextView golden_hourView;
        public TextView timezoneView;

        public MyRowHolder(@NonNull View theRootConstraintLayout){
            super(theRootConstraintLayout);

            // Feature: deleting a message from the RecyclerView
            theRootConstraintLayout.setOnClickListener(clk ->{
                int position = getAbsoluteAdapterPosition();//find which row this is
                Sun selected = suns.get(position);

                // Prepare api url
                // can add try and catch (UnsupportedEncodingException e) here if need encode - URLEncoder.encode(varTextInput, "UTF-8")
//            String url = "https://api.sunrisesunset.io/json?lat=" + sunLatitude + "&lng=" + sunLongitude + "&timezone=UTC&date=today"; // if using UTC
                String url = "https://api.sunrisesunset.io/json?lat=" + selected.getSunLatitude() + "&lng=" + selected.getSunLongitude();
                Log.d("Sunrise Sunset", "Request URL: " + url);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        (response) -> {
                    try {
                        JSONObject results = response.getJSONObject("results");
                        String status = response.getString("status"); // get the JSONArray associated with "status"
                        if (results.length() == 0) {
                              Toast.makeText(SunActivity.this, getString(R.string.sun_found_nothing), Toast.LENGTH_SHORT).show();

                        } else if (!"OK".equals(status)) {
                            Log.e("Sun API Status not OK", "The Sun API status is not OK");
                            Toast.makeText(SunActivity.this, getString(R.string.sun_sun_api_status_not_ok), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Sun API ResultsStatusOK", "Sun API Results and Status OK");

                            // Read the values in the "results" in JSON
                            String sunriseResult = results.getString("sunrise");
                            String sunsetResult = results.getString("sunset");
                            String solar_noonResult = results.getString("solar_noon");
                            String golden_hourResult = results.getString("golden_hour");
                            String timezoneResult = results.getString("timezone");

                            selected.setSunrise(sunriseResult);
                            selected.setSunset(sunsetResult);
                            selected.setSolar_noon(solar_noonResult);
                            selected.setGolder_hour(golden_hourResult);

                            // TODO: this added thread cause an extra Fragment!
                            Executor threadUpdate = Executors.newSingleThreadExecutor();
                            threadUpdate.execute(()->{
                                sDAO.updateSun(selected); //Update selected sun
                            });

                            // TODO: this added line cause an extra Fragment!
//                            sunModel.selectedSun.postValue(selected);


                            sunAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                        }, error -> {                });
                queue.add(request);

                //starts the loading
                sunModel.selectedSun.postValue(selected);

                selectedRow = position; // pass position to the whole class scope variable to use in another class
            });

            // theRootConstraintLayout.findViewById
            sunLatitudeView = theRootConstraintLayout.findViewById(R.id.lat_detail);// maybe not needed?
            sunLongitudeView = theRootConstraintLayout.findViewById(R.id.lng_detail);// maybe not needed?
            sunriseView= theRootConstraintLayout.findViewById(R.id.sun_sunrise_detail);
            sunsetView= theRootConstraintLayout.findViewById(R.id.sun_sunset_detail);


        }
    }

    //load a Menu layout file,
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //inflate a menu into the toolbar
        getMenuInflater().inflate(R.menu.sun_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch( item.getItemId() ){
            case R.id.favoriteSun:
                Intent nextPage = new Intent(SunActivity.this, SunActivity.class);
                startActivity(nextPage);
                break;

             case R.id.saveSun:

                        Executor threadS = Executors.newSingleThreadExecutor();
                        threadS.execute(()->{
                            try {
                                Log.d("Sun Save", "try insert existing record");
                                sDAO.insertSun(sToPass);
                                runOnUiThread(()->{
                                    Log.d("Sun Save", "Sun saved successfully");
                                    // tell the recycle view that there is new data SetChanged()
                                    sunAdapter.notifyDataSetChanged();//redraw the screen
                                    Toast.makeText(this, getResources().getString(R.string.sun_save_success), Toast.LENGTH_SHORT).show();
                                });
                            } catch (Exception e) {
                                Log.d("Sun Save", "Exception, sun already in Fav");
                                runOnUiThread(() -> Toast.makeText(SunActivity.this, getString(R.string.sun_save_dupe_record_warning), Toast.LENGTH_SHORT).show());
                            }
                        });

                break;

            case R.id.deleteSun:

                //put your Sun deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message

                int position = suns.indexOf(sunModel.selectedSun.getValue());
                if(position != RecyclerView.NO_POSITION) {
                    // temporarily stores the sun location before it is removed from the ArrayList
                    Sun toDelete = suns.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SunActivity.this);

//                    builder.setMessage("Do you want to delete this record: " + toDelete.getSunLatitude() + ", " + toDelete.getSunLongitude());
//                    builder.setTitle("Question: ");

                    builder.setMessage(getString(R.string.sun_del_warning_text) + toDelete.getSunLatitude() + ", " + toDelete.getSunLongitude());
                    builder.setTitle(getString(R.string.sun_del_warning_title));

                    builder.setNegativeButton(getString(R.string.sun_no), (btn, obj) -> { /* if no is clicked */ });
                    builder.setPositiveButton(getString(R.string.sun_yes), (btn, obj) -> { /* if yes is clicked */
                        Executor thread = Executors.newSingleThreadExecutor();
                        thread.execute(() -> {
                            //delete from database
                            sDAO.deleteSun(toDelete); //which sun location to delete?
                        });
                        suns.remove(position); //remove from the array list

                        sunAdapter.notifyDataSetChanged(); //redraw the list
                        getSupportFragmentManager().popBackStack(); // go back to message list

                        Snackbar.make(binding.sunRecycleView, getString(R.string.sun_del_after) + position, Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.sun_undo), click -> {
                                    Executor thread2 = Executors.newSingleThreadExecutor();
                                    thread2.execute(() -> {
                                        sDAO.insertSun(toDelete);
                                    });

                                    suns.add(position, toDelete); // add the toDelete back to ArrayList to undo delete action
                                    sunAdapter.notifyDataSetChanged(); //redraw the list

                                    // after undo, can go back to the fragment
//                                    SunDetailsFragment sunFragment = new SunDetailsFragment(selectedSun);
//
//                                      FragmentManager fMgr = getSupportFragmentManager();
//                                      FragmentTransaction transaction = fMgr.beginTransaction();
//                                      transaction.addToBackStack("Add to back stack"); // adds to the history
//                                      transaction.replace(R.id.sunFragmentLocation, sunFragment);//The add() function needs the id of the FrameLayout where it will load the fragment
//                                      transaction.commit();// This line actually loads the fragment into the specified FrameLayout
                                }).show();
                    });
                    builder.create().show(); //this has to be last
                }
                break;

            case R.id.sunBackToMainItem:
                Intent nextPage1 = new Intent(SunActivity.this, MainActivity.class);
                startActivity(nextPage1);
                break;

            case R.id.sunGotoRecipeItem:
                Intent nextPage2 = new Intent(SunActivity.this, RecipeMain.class);
                startActivity(nextPage2);
                break;

            case R.id.sunGotoMusicItem:
                Intent nextPage3 = new Intent(SunActivity.this, DeezerSong.class);
                startActivity(nextPage3);
                break;

            case R.id.sunGotoDictItem:
                Intent nextPage4 = new Intent(SunActivity.this, SearchRoom.class);
                startActivity(nextPage4);
                break;

            case R.id.sunHelp:
                AlertDialog.Builder builder = new AlertDialog.Builder(SunActivity.this);
                builder.setMessage(getResources().getString(R.string.sun_help2))
                        .setTitle(getResources().getString(R.string.sun_help1))
                        .setPositiveButton("OK", (dialog, cl) -> {})
                        .create().show();
                break;

            case R.id.aboutSun:
                Toast.makeText(this,getString(R.string.sun_about_detail), Toast.LENGTH_LONG).show();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    // Method to show an AlertDialog for invalid input
    protected void showInvalidInputWarning(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.invalid_input_title));
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

}