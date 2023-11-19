package algonquin.cst2335.cst2355final;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.room.util.StringUtil;

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
    Recipe recipefromAPI;
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
        //recipeModel.recipes.setValue(recipes = new ArrayList<>());
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
//            stringURL="https://api.spoonacular.com/recipes/complexSearch?query="
//                     +searchmess
//                     +"&apiKey=670608d3fd1e4b15b120493cad68231a";
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
            tx.replace(R.id.fragmentLocation,recipeFragment);
            tx.commit();
        });
        binding.recycleView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
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

        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));

    }
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