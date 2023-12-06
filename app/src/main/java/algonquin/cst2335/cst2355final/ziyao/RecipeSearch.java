package algonquin.cst2335.cst2355final.ziyao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import algonquin.cst2335.cst2355final.Data.RecipeViewModel;
import algonquin.cst2335.cst2355final.MainActivity;
import algonquin.cst2335.cst2355final.R;
//import algonquin.cst2335.cst2355final.Tianjiao.SunRoom;
import algonquin.cst2335.cst2355final.databinding.RecipeSearchBinding;
import algonquin.cst2335.cst2355final.databinding.RecipeTitleBinding;
import algonquin.cst2335.cst2355final.rita.DeezerAlbum;
import algonquin.cst2335.cst2355final.tianjiaosun.SunActivity;
import algonquin.cst2335.cst2355final.yuxing.SearchRoom;

/**
 * Activity class responsible for searching and displaying recipe information.
 */
public class RecipeSearch extends AppCompatActivity {

    /**
     * Binding object for the activity layout.
     */
    private RecipeSearchBinding binding;

    /**
     * RequestQueue for handling network requests.
     */
    RequestQueue queue = null;

    /**
     * Adapter for managing the data to be displayed in the RecyclerView.
     */
    private RecyclerView.Adapter myAdapter;

    /**
     * String containing the search message used to fetch recipes.
     */
    String searchmess;

    /**
     * URL string for fetching recipe information.
     */
    String stringURL;

    /**
     * ArrayList to store Recipe objects.
     */
    ArrayList<Recipe> recipes = null;

    /**
     * Data Access Object (DAO) for interacting with the Recipe entity in the Room Database.
     */
    RecipeDAO mDAO;

    /**
     * ViewModel for managing UI-related data for the RecipeSearch activity.
     */
    RecipeViewModel recipeModel;

    /**
     * Intent for navigating to the home page.
     */
    Intent homePage;

    /**
     * Intent for navigating to the saved page.
     */
    Intent savedPage;

    /**
     * Recipe object representing data fetched from the API.
     */
    Recipe recipefromAPI;

    /**
     * Intent for navigating to the song project.
     */
    Intent songPage;

    /**
     * Intent for navigating to the dictionary project.
     */
    Intent ziyaodictionarypage;

    /**
     * Intent for navigating to the sun project.
     */
    Intent sunPage;

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return false, it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ziyaos_menu, menu);
        return true;
    }

    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up: create
     * views, bind data to lists, etc. This method also provides a Bundle containing the activity's previously saved
     * state, if that state was captured.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle
     *                           contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise,
     *                           it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecipeSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar( binding.myZiyaoToolBar);
        getSupportActionBar().setTitle(getResources().getString(R.string.recipeApp));
        recipeModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        RecipeDatabase db = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "database-name").build();
        mDAO = db.cmDAO();
        homePage = new Intent( RecipeSearch.this, MainActivity.class);
        savedPage = new Intent( RecipeSearch.this, RecipeMain.class);
        queue = Volley.newRequestQueue(this);
        recipes = recipeModel.recipes.getValue();
        //recipeModel.recipes.setValue(recipes = new ArrayList<>());
        //get data from Database
        recipeModel.recipes.setValue(recipes = new ArrayList<>());

        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        AtomicReference<EditText> searchText = new AtomicReference<>(binding.ziyaosearchText);
        Button searchButton = binding.ziyaosearchButton;
        searchButton.setOnClickListener(clk->
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("searchText", searchText.get().getText().toString() );
            editor.apply();
            String ziyaoSearching = getResources().getString(R.string.ziyaoSearching);
            CharSequence ziyaoSearching1 = ziyaoSearching;
            Toast.makeText(this,ziyaoSearching1, Toast.LENGTH_SHORT).show();
            searchmess = binding.ziyaosearchText.getText().toString();
            try {
                stringURL = "https://api.spoonacular.com/recipes/complexSearch?query="
                        + URLEncoder.encode(searchmess, "UTF-8")
                        +"&apiKey=670608d3fd1e4b15b120493cad68231a";
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            //this goes in the button click handler:
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringURL, null,
                    (response) -> {
                        try {
                            JSONArray resultsArray= response.getJSONArray("results");
                            recipeModel.recipes.setValue(recipes = new ArrayList<>());
                            int number=response.getInt("number");
                            for(int i=0;i<number;i++){
                                JSONObject position0 = resultsArray.getJSONObject(i);
                                int id=position0.getInt("id");
                                String title = position0.getString("title");
                                String image = position0.getString("image");

                                Log.d("WeatherResponse", "id: " + id);
                                Log.d("WeatherResponse", "title: " + title);
                                Log.d("WeatherResponse", "image: " + image);
                                recipefromAPI= new Recipe(title,image,id);
                                recipes.add(recipefromAPI);
                                myAdapter.notifyDataSetChanged();
                                String pathname = getFilesDir() + "/" + id + ".jpg";
                                File file = new File(pathname);
                                if (file.exists()) {

                                } else {
                                    ImageRequest imgReq2 = new ImageRequest(image, new Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap bitmap) {
                                            // Do something with loaded bitmap...

                                            try {

                                                Log.e("ImageRequest", " loading image: " + bitmap);
                                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, RecipeSearch.this.openFileOutput(id + ".png", Activity.MODE_PRIVATE));
                                            } catch (Exception e) {

                                            }
                                            Log.e("ImageRequest", " loading image: " + image);

                                        }
                                    }, 1024, 1024, ImageView.ScaleType.CENTER, null, (error) -> {
                                        Log.e("ImageRequest", "Error loading image: aaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                                    });
                                    queue.add(imgReq2);
                                }
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    (error) -> {
                    });
            queue.add(request);

        });
        searchText.get().setText(prefs.getString("searchText",""));
        recipeModel.selectedRecipe.observe(this, (newMessageValue) -> {
            RecipeDetailsFragment recipeFragment = new RecipeDetailsFragment( newMessageValue );
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            tx.addToBackStack("anything?");
            tx.replace(R.id.ziyaofragmentLocation,recipeFragment);
            tx.commit();
        });
        binding.ziyaorecycleView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                RecipeTitleBinding binding = RecipeTitleBinding.inflate(getLayoutInflater());
                return new MyRowHolder( binding.getRoot() );
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                holder.recipeTitle.setText(recipes.get(position).getTitle());
                String obj = recipes.get(position).getTitle();
                holder.recipeTitle.setText(obj);
            }

            @Override
            public int getItemCount() {
                return recipes.size();
            }
        });

        binding.ziyaorecycleView.setLayoutManager(new LinearLayoutManager(this));

    }

    /**
     * ViewHolder class for holding views of individual rows in the RecyclerView.
     */
    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitle=itemView.findViewById(R.id.recipeTitle);
            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                Recipe selected = recipes.get(position);

                recipeModel.selectedRecipe.postValue(selected);

            });
        }
    }

    /**
     * Called when a menu item is selected. Handle actions based on the selected item.
     *
     * @param item The selected MenuItem.
     * @return true to consume the event, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId() )
        {
            case R.id.homeMenuZ:
                String ziyaogohome = getResources().getString(R.string.ziyaogohome);
                CharSequence ziyaogohome2 = ziyaogohome;
                Toast.makeText(this,ziyaogohome2, Toast.LENGTH_SHORT).show();
                startActivity( homePage);
                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                break;
            case R.id.savedRecipeMenu   :
                String ziyaogosavedRecipeMenu = getResources().getString(R.string.ziyaogosavedRecipeMenu);
                CharSequence ziyaogosavedRecipeMenu1 = ziyaogosavedRecipeMenu;
                Toast.makeText(this,ziyaogosavedRecipeMenu1, Toast.LENGTH_SHORT).show();
                startActivity( savedPage );
                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                break;
            case R.id.aboutRecipeMenu:
                String ziyaoaboutRecipeMenu = getResources().getString(R.string.ziyaoaboutRecipeMenu);
                String ziyaoaboutRecipeMenuAbout = getResources().getString(R.string.ziyaoaboutRecipeMenuAbout);
                String ziyaoOK = getResources().getString(R.string.ziyaoOK);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage(ziyaoaboutRecipeMenu).setTitle(ziyaoaboutRecipeMenuAbout)
                        .setNegativeButton(ziyaoOK, (dialog, cl) -> {
                        }).create().show();
                break;

            case R.id.helpRecipeMenu:
                String helpRecipeMenu = getResources().getString(R.string.helpRecipeMenu);
                String ziyaoaboutRecipeMenuAbout11 = getResources().getString(R.string.ziyaoaboutRecipeMenuAbout);
                String ziyaoOK11 = getResources().getString(R.string.ziyaoOK);
                AlertDialog.Builder builder22 = new AlertDialog.Builder(this);
                builder22.setMessage(helpRecipeMenu).setTitle(ziyaoaboutRecipeMenuAbout11)
                        .setNegativeButton(ziyaoOK11, (dialog, cl) -> {
                        }).create().show();
                break;
            case R.id.saveTheRecipe:
                String ziyaosaveTheRecipe = getResources().getString(R.string.ziyaosaveTheRecipe);
                CharSequence ziyaosaveTheRecipe1 = ziyaosaveTheRecipe;
                Recipe addRecipe = recipeModel.selectedRecipe.getValue();
                recipes.add(addRecipe);
                myAdapter.notifyDataSetChanged();
                Executor thread2 = Executors.newSingleThreadExecutor();
                thread2.execute(( ) -> {
                    //this is on a background thread
                    addRecipe.id = (int)mDAO.insertRecipe(addRecipe); //get the ID from the database
                    Log.d("TAG", "The id created is:" + addRecipe.id);
                }); //the body of run()
                Snackbar.make(this.findViewById(R.id.ziyaosearchText),ziyaosaveTheRecipe1
                        +addRecipe.getTitle(),Snackbar.LENGTH_LONG).show();
                getSupportFragmentManager() .popBackStack();
                break;
            case R.id.ziyaodictionary:
                // Display instructions on how to use the interface
                String ziyaodictionary = getResources().getString(R.string.ziyaodictionarying);
                CharSequence ziyaodictionary1 = ziyaodictionary;
                ziyaodictionarypage = new Intent( RecipeSearch.this, SearchRoom.class);
                Toast.makeText(this,ziyaodictionary1, Toast.LENGTH_SHORT).show();
                startActivity( ziyaodictionarypage);
                break;
            case R.id.yxSongpage:
                // Display instructions on how to use the interface
                String ziyaosongPage = getResources().getString(R.string.ziyaosongPage);
                CharSequence ziyaosongPage1 = ziyaosongPage;
                songPage = new Intent( RecipeSearch.this, DeezerAlbum.class);
                Toast.makeText(this,ziyaosongPage1, Toast.LENGTH_SHORT).show();
                startActivity( songPage);
                break;
            case R.id.yxSunpage:
                String ziyaoyxSunpage = getResources().getString(R.string.ziyaoyxSunpage);
                CharSequence ziyaoyxSunpage1 = ziyaoyxSunpage;
                // Display instructions on how to use the interface
                sunPage = new Intent( RecipeSearch.this, SunActivity.class);
                Toast.makeText(this,ziyaoyxSunpage1, Toast.LENGTH_SHORT).show();
                startActivity( sunPage);
                break;
        }

        return true;
    }
}