package algonquin.cst2335.cst2355final.ziyao;

import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.cst2355final.Data.RecipeViewModel;
import algonquin.cst2335.cst2355final.MainActivity;
import algonquin.cst2335.cst2355final.R;
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

                Recipe removedRecipe = recipeModel.selectedRecipe.getValue();
                int position = recipes.indexOf(removedRecipe);
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeMain.this);

                builder.setMessage("Do you want to delete the message:"
                                + removedRecipe.getTitle()).setTitle("Question: ")
                        .setNegativeButton("No", (dialog, cl) -> {
                        })
                        .setPositiveButton("Yes", (dialog, cl) -> {

                            recipes.remove(position);
                            myAdapter.notifyDataSetChanged();
                            Executor thread1 = Executors.newSingleThreadExecutor();
                            thread1.execute(( ) -> {
                                //this is on a background thread
                                mDAO.deleteRecipe(removedRecipe); //get the ID from the database
                                Log.d("TAG", "The id removed is:" + removedRecipe.id);
                            }); //the body of run()
                            Snackbar.make(this.findViewById(R.id.searchText),"You deleted message #"
                                            + position,Snackbar.LENGTH_LONG)
                                    .setAction("Undo", click -> {
                                        recipes.add(position,removedRecipe);
                                        myAdapter.notifyDataSetChanged();
                                        Executor thread2 = Executors.newSingleThreadExecutor();
                                        thread2.execute(( ) -> {
                                            //this is on a background thread
                                            removedRecipe.id = (int)mDAO.insertRecipe(removedRecipe); //get the ID from the database
                                            Log.d("TAG", "The id created is:" + removedRecipe.id);
                                        }); //the body of run()
                                    }).show();
                        }).create().show();
                getSupportFragmentManager() .popBackStack();
                break;
        }
//test
        return true;
    }
}