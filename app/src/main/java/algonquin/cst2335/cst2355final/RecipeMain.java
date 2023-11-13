package algonquin.cst2335.cst2355final;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.cst2355final.Data.RecipeViewModel;
import algonquin.cst2335.cst2355final.databinding.RecipeMainBinding;
import algonquin.cst2335.cst2355final.databinding.RecipeTitleBinding;

public class RecipeMain extends AppCompatActivity {
    private RecipeMainBinding binding;
    private RecyclerView.Adapter myAdapter;
    //ArrayList<String> messages = new ArrayList<>();
    ArrayList<Recipe> recipes = null;
    RecipeDAO mDAO;
    RecipeViewModel recipeModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecipeMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recipeModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        RecipeDatabase db = Room.databaseBuilder(getApplicationContext(), RecipeDatabase.class, "database-name").build();
        mDAO = db.cmDAO();


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
        EditText searchText =binding.searchText;
        Button searchButton = binding.searchButton;
        searchButton.setOnClickListener(clk->
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("searchText", searchText.getText().toString() );
            editor.apply();
            CharSequence text = "Searching...";
            Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
        } );
        searchText.setText(prefs.getString("searchText",""));

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
            Snackbar.make(searchText,"Checking Your Saved Recipes!",Snackbar.LENGTH_LONG).show();
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
}