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
import algonquin.cst2335.cst2355final.databinding.RecipeMainBinding;
import algonquin.cst2335.cst2355final.databinding.RecipeTitleBinding;

public class RecipeMain extends AppCompatActivity {
    private RecipeMainBinding binding;
    RequestQueue queue = null;
    private RecyclerView.Adapter myAdapter;
    String searchmess;
    String stringURL;
    //ArrayList<String> messages = new ArrayList<>();
    ArrayList<Recipe> recipes = null;
    RecipeDAO mDAO;
    RecipeViewModel recipeModel;
    Intent homePage;
    Intent searchPage;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.ziyao_menu, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecipeMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar( binding.myZiyaoToolBar);
        recipeModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        RecipeDatabase db = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "database-name").build();
        mDAO = db.cmDAO();
        homePage = new Intent( RecipeMain.this, MainActivity.class);
        searchPage = new Intent( RecipeMain.this, RecipeSearch.class);
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

        recipeModel.selectedRecipe.observe(this, (newMessageValue) -> {
            RecipeDetailsFragment chatFragment = new RecipeDetailsFragment( newMessageValue );
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            tx.addToBackStack("anything?");
            tx.replace(R.id.fragmentLocation,chatFragment);
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
            case R.id.searchRecipeMenu:
                CharSequence text2 = "Going to searching page...";
                Toast.makeText(this,text2, Toast.LENGTH_SHORT).show();
                startActivity( searchPage);
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
//test
        return true;
    }
}