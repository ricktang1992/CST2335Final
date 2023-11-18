package algonquin.cst2335.cst2355final;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import algonquin.cst2335.cst2355final.Data.RecipeViewModel;
import algonquin.cst2335.cst2355final.databinding.RecipeSearchBinding;
import algonquin.cst2335.cst2355final.databinding.RecipeSearchBinding;
import algonquin.cst2335.cst2355final.databinding.RecipeTitleBinding;

public class RecipeSearch extends AppCompatActivity {
    private RecipeSearchBinding binding;
    RequestQueue queue = null;
    private RecyclerView.Adapter myAdapter;
    String searchmess;
    String stringURL;
    //ArrayList<String> messages = new ArrayList<>();
    ArrayList<Recipe> recipes = null;
    RecipeDAO mDAO;
    RecipeViewModel recipeModel;
    Intent homePage;
    Intent savedPage;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ziyaos_menu, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecipeSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar( binding.myZiyaoToolBar);
        recipeModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        RecipeDatabase db = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "database-name").build();
        mDAO = db.cmDAO();
        homePage = new Intent( RecipeSearch.this, MainActivity.class);
        savedPage = new Intent( RecipeSearch.this, RecipeMain.class);
        queue = Volley.newRequestQueue(this);
        recipes = recipeModel.recipes.getValue();
        //get data from Database
        if(recipes == null)
        {
            recipeModel.recipes.setValue(recipes = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                recipes.addAll( mDAO.getAllRecipe() ); //Once you get the data from database

                runOnUiThread( () ->  binding.recycleView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });
        }

        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        AtomicReference<EditText> searchText = new AtomicReference<>(binding.searchText);
        Button searchButton = binding.searchButton;
        searchButton.setOnClickListener(clk->
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("searchText", searchText.get().getText().toString() );
            editor.apply();
            CharSequence text = "Searching...";
            Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
            searchmess = binding.searchText.getText().toString();
            try {
                stringURL = "https://api.openweathermap.org/data/2.5/weather?q="
                        + URLEncoder.encode(searchmess, "UTF-8")
                        + "&appid=7e943c97096a9784391a981c4d878b22&units=metric";
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            //this goes in the button click handler:
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringURL, null,
                    (response) -> {
                        try {
                            JSONArray weatherArray= response.getJSONArray("weather");
                            int vis = response.getInt("visibility");
                            String name = response.getString("name");
                            JSONObject position0 = weatherArray.getJSONObject(0);
                            String description = position0.getString("description");
                            String iconName = position0.getString("icon");
                            JSONObject mainObject = response.getJSONObject("main");
                            double current = mainObject.getDouble("temp");
                            double min = mainObject.getDouble("temp_min");
                            double max = mainObject.getDouble("temp_max");
                            int humidity = mainObject.getInt("humidity");
                            String iconUrl = "https://openweathermap.org/img/w/" + iconName + ".png";
                            String pathname = getFilesDir() + "/" + iconName + ".png";

                            Log.d("WeatherResponse", "Visibility: " + vis);
                            Log.d("WeatherResponse", "Name: " + name);
                            Log.d("WeatherResponse", "Description: " + description);
                            Log.d("WeatherResponse", "IconName: " + iconName);
                            Log.d("WeatherResponse", "Current Temperature: " + current);
                            Log.d("WeatherResponse", "Min Temperature: " + min);
                            Log.d("WeatherResponse", "Max Temperature: " + max);
                            Log.d("WeatherResponse", "Humidity: " + humidity);
                            Log.d("WeatherResponse", "iconURL: " + "http://openweathermap.org/img/w/" + iconName + ".png");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    (error) -> {
                    });
            queue.add(request);

        });
        searchText.get().setText(prefs.getString("searchText",""));

        Button savedButton = binding.savedButton;
        savedButton.setOnClickListener(clk->
        {
            Recipe recipe=new Recipe("aaaa","a",12);
            recipes.add(recipe);
            myAdapter.notifyDataSetChanged();
            binding.searchText.setText("dashabi");
            Executor thread1 = Executors.newSingleThreadExecutor();
            thread1.execute(( ) -> {
                //this is on a background thread
                recipe.id = (int)mDAO.insertRecipe(recipe); //get the ID from the database
                Log.d("TAG", "The id created is:" + recipe.id);
            }); //the body of run()
            Snackbar.make(searchText.get(),"Checking Your Saved Recipes!",Snackbar.LENGTH_LONG).show();
        } );


        binding.recycleView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                RecipeTitleBinding binding = RecipeTitleBinding.inflate(getLayoutInflater());
                return new MyRowHolder( binding.getRoot() );
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                holder.recipeTitle.setText("aaaaaaaaa");
                String obj = recipes.get(position).getTitle();
                holder.recipeTitle.setText(obj);
            }

            @Override
            public int getItemCount() {
                return recipes.size();
            }
        });

        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));

    }
    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle;
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitle=itemView.findViewById(R.id.recipeTitle);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId() )
        {
            case R.id.homeMenuZ:
                CharSequence text = "Back to home page...";
                Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
                startActivity( homePage);
                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                break;
            case R.id.savedRecipeMenu   :
                CharSequence text2 = "Going to searching page...";
                Toast.makeText(this,text2, Toast.LENGTH_SHORT).show();
                startActivity( savedPage );
                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                break;
            case R.id.aboutRecipeMenu:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage("This application is the recipe search application created by Ziyao Tang.").setTitle("About: ")
                        .setNegativeButton("OK", (dialog, cl) -> {
                        }).create().show();
                break;
            case R.id.deleteRecipe:

                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                break;
        }

        return true;
    }
}