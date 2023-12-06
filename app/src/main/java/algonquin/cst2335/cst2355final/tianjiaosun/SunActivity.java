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

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import algonquin.cst2335.cst2355final.MainActivity;
import algonquin.cst2335.cst2355final.R;
import algonquin.cst2335.cst2355final.databinding.ActivitySunBinding;
import algonquin.cst2335.cst2355final.databinding.SunRecordBinding;
import algonquin.cst2335.cst2355final.rita.DeezerAlbum;
import algonquin.cst2335.cst2355final.yuxing.SearchRoom;
import algonquin.cst2335.cst2355final.ziyao.RecipeMain;
import algonquin.cst2335.cst2355final.ziyao.RecipeSearch;

/**
 * Activity class for managing the Sun functionality in the application.
 * This class handles user interactions, network requests to fetch sun data, database operations, and updating the user interface.
 * It also manages transitions between different fragments and activities.
 *
 * Author: Tianjiao Feng
 */
public class SunActivity extends AppCompatActivity {

    // Binding class for accessing views in the layout.
    ActivitySunBinding binding;

    // List to store Sun objects, initially set in SunViewModel.
    ArrayList<Sun> suns = null;

    // ViewModel for maintaining data persistence during configuration changes.
    SunViewModel sunModel;
    private RecyclerView.Adapter sunAdapter;     // Adapter for displaying Sun objects in the RecyclerView.

    SunDAO sDAO;     // DAO for interacting with the Sun database.

    int selectedRow;     // Variable to store the position of the selected row.


    Sun sToPass;  // Sun object used for passing data between classes or methods.
    protected String cityName;  // Variable to hold the user-input city name.


    protected RequestQueue queue = null; // Volley request queue for network requests.


    /**
     * Called when the activity is first created.
     * Initializes the activity, sets up the user interface, database connections, and event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the most recent data, otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySunBinding.inflate(getLayoutInflater());
        queue = Volley.newRequestQueue(this);
        setContentView(binding.getRoot());


        SharedPreferences prefs = getSharedPreferences("sunSharedData", Context.MODE_PRIVATE);
        binding.latInput.setText(prefs.getString("latitude",""));
        binding.lngInput.setText(prefs.getString("longitude",""));

        setSupportActionBar(binding.sunToolbar);// initialize the toolbar
        getSupportActionBar().setTitle(getString(R.string.sun_toolbar_title));


        sunModel = new ViewModelProvider(this).get(SunViewModel.class);
        suns = sunModel.suns.getValue();


        sunModel.selectedSun.observe(this,(selectedSun) ->{
            if(selectedSun != null) {

                SunDetailsFragment sunFragment = new SunDetailsFragment(selectedSun);

                FragmentManager fMgr = getSupportFragmentManager();
                FragmentTransaction transaction = fMgr.beginTransaction();
                transaction.addToBackStack("Add to back stack");
                transaction.replace(R.id.sunFragmentLocation, sunFragment);
                transaction.commit();
            }
        });


        SunDatabase db = Room.databaseBuilder(getApplicationContext(),SunDatabase.class, "sunDatabase").build();

        sDAO = db.sunDAO();

        if (suns == null) {
            sunModel.suns.postValue(suns = new ArrayList<Sun>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                suns.addAll(sDAO.getAllSuns());
                runOnUiThread(() -> binding.sunRecycleView.setAdapter(sunAdapter));
            });
        }


        binding.sunSearchButton.setOnClickListener( cli ->{

            String sunLatitude = binding.latInput.getText().toString();
            String sunLongitude = binding.lngInput.getText().toString();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("latitude", sunLatitude);
            editor.putString("longitude", sunLongitude);
            editor.apply();


            String url = "https://api.sunrisesunset.io/json?lat=" + sunLatitude + "&lng=" + sunLongitude;
            Log.d("Sunrise Sunset", "Request URL: " + url);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    (response) -> {
                        try {
                            if (response.has("results")) {
                                Log.d("Sun API Results", "Sun API Request has results");
                                JSONObject results = response.getJSONObject("results");
                                String status = response.getString("status");
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
                            String status = response.getString("status");

                            if (results.length() == 0) {

                                Toast.makeText(this, getString(R.string.sun_found_nothing), Toast.LENGTH_SHORT).show();
                            } else if (!"OK".equals(status)) {
                                // Status is not OK
                                Toast.makeText(this, getString(R.string.sun_sun_api_status_not_ok), Toast.LENGTH_SHORT).show();
                            } else {


                                // Read the values in the "results" in JSON
                                String sunriseResult = results.getString("sunrise");
                                String sunsetResult = results.getString("sunset");


                                Sun s = new Sun(sunLatitude, sunLongitude, sunriseResult, sunsetResult);
                                sToPass = s; // pass the sun obj to the class level

                                sunAdapter.notifyDataSetChanged();

                                SunDetailsFragment sunFragment = new SunDetailsFragment(s);

                                FragmentManager fMgr = getSupportFragmentManager();
                                FragmentTransaction transaction = fMgr.beginTransaction();
                                transaction.addToBackStack("Add to back stack");
                                transaction.replace(R.id.sunFragmentLocation, sunFragment);
                                transaction.commit();

                                //clear the previous text
                                binding.latInput.setText("");
                                binding.lngInput.setText("");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    (error) -> {
                    });
            queue.add(request);


            binding.latInput.setText("");
            binding.lngInput.setText("");

        });

        // Will draw the recycle view
        binding.sunRecycleView.setAdapter(sunAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                SunRecordBinding binding2 = SunRecordBinding.inflate(getLayoutInflater(),parent,false);
                return new MyRowHolder(binding2.getRoot());
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                // where to overwrite default text:
                Sun obj = suns.get(position);

                holder.sunLatitudeView.setText(obj.getSunLatitude());
                holder.sunLongitudeView.setText(obj.getSunLongitude());

            }


            @Override
            public int getItemCount() {
                return suns.size();
            }
        });


        binding.sunRecycleView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * ViewHolder class for managing views in the RecyclerView.
     * This class holds references to the individual views within a list item and handles click events for each row.
     */
    public class MyRowHolder extends RecyclerView.ViewHolder {

        public TextView sunLatitudeView;
        public TextView sunLongitudeView;
        public TextView sunriseView;
        public TextView sunsetView;
        /**
         * Constructor for the ViewHolder.
         * Initializes the views and sets up click listeners.
         *
         * @param theRootConstraintLayout The root view of the list item layout.
         */
        public MyRowHolder(@NonNull View theRootConstraintLayout){
            super(theRootConstraintLayout);


            theRootConstraintLayout.setOnClickListener(clk ->{
                int position = getAbsoluteAdapterPosition();
                Sun selected = suns.get(position);

                String url = "https://api.sunrisesunset.io/json?lat=" + selected.getSunLatitude() + "&lng=" + selected.getSunLongitude();
                Log.d("Sunrise Sunset", "Request URL: " + url);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        (response) -> {
                    try {
                        JSONObject results = response.getJSONObject("results");
                        String status = response.getString("status");
                        if (results.length() == 0) {
                              Toast.makeText(SunActivity.this, getString(R.string.sun_found_nothing), Toast.LENGTH_SHORT).show();

                        } else if (!"OK".equals(status)) {
                            Log.e("Sun API Status not OK", "The Sun API status is not OK");
                            Toast.makeText(SunActivity.this, getString(R.string.sun_sun_api_status_not_ok), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Sun API ResultsStatusOK", "Sun API Results and Status OK");


                            String sunriseResult = results.getString("sunrise");
                            String sunsetResult = results.getString("sunset");

                            selected.setSunrise(sunriseResult);
                            selected.setSunset(sunsetResult);

                            // TODO: this added thread cause an extra Fragment!
                            Executor threadUpdate = Executors.newSingleThreadExecutor();
                            threadUpdate.execute(()->{
                                sDAO.updateSun(selected);
                            });

                            sunAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                        }, error -> {                });
                queue.add(request);

                sunModel.selectedSun.postValue(selected);

                selectedRow = position;
            });

            sunLatitudeView = theRootConstraintLayout.findViewById(R.id.lat_detail);
            sunLongitudeView = theRootConstraintLayout.findViewById(R.id.lng_detail);
            sunriseView= theRootConstraintLayout.findViewById(R.id.sun_sunrise_detail);
            sunsetView= theRootConstraintLayout.findViewById(R.id.sun_sunset_detail);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

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

                                    sunAdapter.notifyDataSetChanged();
                                    Toast.makeText(this, getResources().getString(R.string.sun_save_success), Toast.LENGTH_SHORT).show();
                                });
                            } catch (Exception e) {
                                Log.d("Sun Save", "Exception, sun already in Fav");
                                runOnUiThread(() -> Toast.makeText(SunActivity.this, getString(R.string.sun_save_dupe_record_warning), Toast.LENGTH_SHORT).show());
                            }
                        });

                break;

            case R.id.deleteSun:

                int position = suns.indexOf(sunModel.selectedSun.getValue());
                if(position != RecyclerView.NO_POSITION) {
                    Sun toDelete = suns.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SunActivity.this);



                    builder.setMessage(getString(R.string.sun_del_warning_text) + toDelete.getSunLatitude() + ", " + toDelete.getSunLongitude());
                    builder.setTitle(getString(R.string.sun_del_warning_title));

                    builder.setNegativeButton(getString(R.string.sun_no), (btn, obj) -> { /* if no is clicked */ });
                    builder.setPositiveButton(getString(R.string.sun_yes), (btn, obj) -> { /* if yes is clicked */
                        Executor thread = Executors.newSingleThreadExecutor();
                        thread.execute(() -> {
                            sDAO.deleteSun(toDelete);
                        });
                        suns.remove(position);

                        sunAdapter.notifyDataSetChanged();
                        getSupportFragmentManager().popBackStack();

                        Snackbar.make(binding.sunRecycleView, getString(R.string.sun_del_after) + position, Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.sun_undo), click -> {
                                    Executor thread2 = Executors.newSingleThreadExecutor();
                                    thread2.execute(() -> {
                                        sDAO.insertSun(toDelete);
                                    });

                                    suns.add(position, toDelete);
                                    sunAdapter.notifyDataSetChanged();

                                }).show();
                    });
                    builder.create().show();
                }
                break;

            case R.id.sunBackToMainItem:
                Intent nextPage1 = new Intent(SunActivity.this, MainActivity.class);
                startActivity(nextPage1);
                break;

            case R.id.sunGotoRecipeItem:
                Intent nextPage2 = new Intent(SunActivity.this, RecipeSearch.class);
                startActivity(nextPage2);
                break;

            case R.id.sunGotoMusicItem:
                Intent nextPage3 = new Intent(SunActivity.this, DeezerAlbum.class);
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

    /**
     * Displays a warning dialog if the user input is invalid.
     *
     * @param message The message to be displayed in the warning dialog.
     */
    protected void showInvalidInputWarning(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.invalid_input_title));
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

}