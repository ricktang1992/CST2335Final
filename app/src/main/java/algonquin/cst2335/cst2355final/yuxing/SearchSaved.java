package algonquin.cst2335.cst2355final.yuxing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.cst2355final.Data.SearchViewModel;
import algonquin.cst2335.cst2355final.MainActivity;
import algonquin.cst2335.cst2355final.R;

import algonquin.cst2335.cst2355final.databinding.SearchMessageBinding;
import algonquin.cst2335.cst2355final.databinding.SearchSavedBinding;
import algonquin.cst2335.cst2355final.rita.DeezerAlbum;
import algonquin.cst2335.cst2355final.tianjiaosun.SunActivity;
import algonquin.cst2335.cst2355final.ziyao.RecipeSearch;

/**
 * The {@code SearchSaved} class represents the activity for displaying and managing saved search terms.
 * It includes functionality for retrieving data from a database, displaying it in a RecyclerView,
 * allowing users to view detailed information about a selected term, and deleting saved terms.
 *
 * This class extends {@link AppCompatActivity} and implements features such as a contextual menu with options
 * to delete selected terms and navigate to other project pages.
 *
 * Features:
 * - Display a list of saved search terms in a RecyclerView.
 * - Allow users to view detailed information about a selected term in a separate fragment.
 * - Provide the option to delete a selected term from the saved list.
 * - Include menu options to navigate to other project pages such as Recipe, Song, Sun, and the Saved term page.
 *
 * Usage:
 * - To delete a term, select it and choose the 'Delete' option from the menu.
 * - Click on a saved term to view detailed information in a separate fragment.
 * - Use the menu to navigate to other project pages (Recipe, Song, Sun, and Saved term page).
 *
 * Important Notes:
 * - This class assumes the existence of a {@link SearchViewModel} for managing UI-related data.
 * - The class relies on a {@link SearchDatabase} for storing and retrieving search terms from a Room database.
 * - The layout includes a RecyclerView (yuxingsavedRecyclerView) for displaying saved search terms.
 * - The class uses a custom RecyclerView Adapter ({@code savedAdapter}) for handling the display of search terms.
 *
 * @author Yuxing Xu
 * @version 1.0
 * @since 2023-11-29
 */
public class SearchSaved extends AppCompatActivity {
    /**
     * The binding object for the layout of the {@code SearchSaved} activity.
     * It provides direct access to views defined in the layout.
     */
    SearchSavedBinding binding;

    /**
     * The list of saved search terms to be displayed in the RecyclerView.
     */
    ArrayList<SearchTerm> messages = null;

    /**
     * The ViewModel for managing UI-related data in the {@code SearchSaved} activity.
     */
    SearchViewModel saveModel;

    /**
     * The Data Access Object (DAO) for interacting with the Room database to handle {@link SearchTerm} entities.
     */
    SearchTermDAO mDAO;

    /**
     * Intent to navigate to the page displaying saved terms in the dictionary.
     */
    Intent dictionarySavedPage;

    /**
     * Intent to navigate to the song page.
     */
    Intent songPage;

    /**
     * Intent to navigate to the recipe page.
     */
    Intent RecipePage;

    /**
     * Intent to navigate to the sun page.
     */
    Intent sunPage;

    /**
     * Intent to navigate to the sun page.
     */
    Intent homePage;

    /**
     * The RecyclerView Adapter for managing the display of saved search terms.
     */
    private RecyclerView.Adapter savedAdapter;
    /**
     * Initializes the options menu.
     *
     * @param menu The menu to be initialized.
     * @return true if the menu is successfully created; false otherwise.
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.yuxing_menu_file, menu);
        return true;
    }
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveModel = new ViewModelProvider(this).get(SearchViewModel.class);
        SearchDatabase db = Room.databaseBuilder(getApplicationContext(), SearchDatabase.class, "Dictionary").build();
        mDAO = db.searchTermDao();
        messages = saveModel.messages.getValue();


        //get data from Database
        if(messages == null)
        {
            saveModel.messages.setValue(messages = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {

                messages.addAll(mDAO.getAllSearchTerms());
                // 从数据库加载所有搜索条目
                runOnUiThread(() -> {
                    savedAdapter.notifyDataSetChanged(); // 通知适配器数据已更改
                    Log.d("SearchSaved", "Data set size: " + messages.size()); // 添加这行日志
                    binding.yuxingsavedRecyclerView.setAdapter(savedAdapter); // 加载 RecyclerView
                });

            });

        }

        binding = SearchSavedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar( binding.yuxingtoolbar2);
        saveModel.selectedMessage.observe(this, (newMessageValue) -> {
            if (newMessageValue != null) {
                SearchDetailsFragment newFragment = new SearchDetailsFragment(newMessageValue);

                FragmentManager fMgr = getSupportFragmentManager();
                FragmentTransaction tx = fMgr.beginTransaction();
                tx.addToBackStack("");
                tx.replace(R.id.yuxingSavedframeLayout, newFragment);
                tx.commit();
            }
        });

//        saveModel.selectedMessage.observe(this, (newMessageValue) -> {
//            SearchDetailsFragment newFragment = new SearchDetailsFragment(newMessageValue);
//
//            FragmentManager fMgr = getSupportFragmentManager();
//            FragmentTransaction tx = fMgr.beginTransaction();
//            tx.addToBackStack("");
//            tx.replace(R.id.yuxingframeLayout, newFragment);
//            tx.commit();
//        });


        binding.yuxingsavedRecyclerView.setAdapter(savedAdapter = new RecyclerView.Adapter<myRowHolder>() {
            /**
             * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
             *
             * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
             * @param viewType The view type of the new View.
             * @return A new ViewHolder that holds a View of the given view type.
             */
            @NonNull
            @Override
            public myRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                // 1. load a XML layout
                SearchMessageBinding binding =                            // parent is incase matchparent
                        SearchMessageBinding.inflate(getLayoutInflater(), parent, false);
                // Set layout parameters to wrap content
                binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                // 2. call our constructor below
                return new myRowHolder(binding.getRoot()); // getRoot returns a ConstraintLayout with TextViews inside

            }


            /**
             * Called by RecyclerView to display the data at the specified position.
             *
             * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position
             *                 in the data set.
             * @param position The position of the item within the adapter's data set.
             */
            @Override
            public void onBindViewHolder(@NonNull myRowHolder holder, int position) {
                SearchTerm obj = messages.get(position);
                holder.messageText.setText(obj.getTerm());
//              recylerview message format
                holder.timeText.setText(obj.getTimeSent());
//                holder.messageText.setText("aaaaaaaaa");
//                String obj = messages.get(position).getTerm();
//                holder.messageText.setText(obj);
            }
            /**
             * Returns the total number of items in the data set held by the adapter.
             *
             * @return The total number of items in this adapter.
             */
            @Override
            public int getItemCount() {
                return messages.size();
            }

        });

        binding.yuxingsavedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * ViewHolder class for the items in the RecyclerView used in the SearchRoom activity.
     * Represents a single item view and holds references to its child views.
     */
    class myRowHolder extends RecyclerView.ViewHolder {
        /**
         * TextView for displaying the search term.
         */
        TextView messageText;
        /**
         * TextView for displaying the time the search was performed.
         */
        TextView timeText;
        /**
         * Constructor for MyRowHolder.
         *
         * @param itemView The root view of the item layout.
         */
        public myRowHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.yuxingTermWord);
            timeText = itemView.findViewById(R.id.yuxingSearchtime);
            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                SearchTerm selected = messages.get(position);

                saveModel.selectedMessage.postValue(selected);
            });

        }
    }
    /**
     * Called when an item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return true if the menu item was successfully handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId() )
        {
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to delete the selected term?");
                builder.setTitle("Delete Term")
                        .setPositiveButton("Yes", (dialog, cl) -> {
                            // Get the selected message
                            SearchTerm selectedMessage = saveModel.selectedMessage.getValue();
                            if (selectedMessage != null) {
                                // Delete the message from the database
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    mDAO.deleteMessage(selectedMessage);
                                });

                                // Remove the message from the list and update the RecyclerView
                                messages.remove(selectedMessage);
                                savedAdapter.notifyDataSetChanged();

                                // Show a Snackbar with an undo option
                                Snackbar.make(binding.getRoot(), "Message deleted", Snackbar.LENGTH_LONG)
                                        .setAction("Undo", clk2 -> {
                                            // Insert the deleted message back to the database
                                            Executors.newSingleThreadExecutor().execute(() -> {
                                                long id = mDAO.insertSearchTerm(selectedMessage);
                                                selectedMessage.id = id;
                                                runOnUiThread(() -> {
                                                    // Add the message back to the list and update the RecyclerView
                                                    messages.add(selectedMessage);
                                                    savedAdapter.notifyDataSetChanged();
                                                });
                                            });
                                        })
                                        .show();
                            }
                        })
                        .setNegativeButton("No", (dialog, cl) -> {})
                        .create().show();
                break;


            case R.id.about:
                // Display instructions on how to use the interface
                AlertDialog.Builder instructionsDialog = new AlertDialog.Builder(this);
                instructionsDialog.setMessage(R.string.yxAboutUse)
                        .setTitle(R.string.yxAboutTitle)
                        .setNegativeButton("OK", (dialog, cl) -> {})
                        .create().show();
                break;
            case R.id.yxrecipepage:
                // Display instructions on how to use the interface
                RecipePage = new Intent( SearchSaved.this, RecipeSearch.class);
                CharSequence text = "Going to Recipe Project...";
                Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
                startActivity( RecipePage);
                break;
            case R.id.yxSongpage:
                // Display instructions on how to use the interface
                songPage = new Intent( SearchSaved.this, DeezerAlbum.class);
                CharSequence text1 = "Going to Song Project...";
                Toast.makeText(this,text1, Toast.LENGTH_SHORT).show();
                startActivity( songPage);
                break;
            case R.id.yxSunpage:
                // Display instructions on how to use the interface
                sunPage = new Intent( SearchSaved.this, SunActivity.class);
                CharSequence text2 = "Going to Sun Project...";
                Toast.makeText(this,text2, Toast.LENGTH_SHORT).show();
                startActivity( sunPage);

                break;
            case R.id.yxsavedTerm:
                // Display instructions on how to use the interface
                dictionarySavedPage = new Intent( SearchSaved.this, SearchSaved.class);
                CharSequence text3 = "Going to Saved term page...";
                Toast.makeText(this,text3, Toast.LENGTH_SHORT).show();


                startActivity( dictionarySavedPage);

                break;
            case R.id.yxHomepage:
                // Display instructions on how to use the interface
                homePage = new Intent( SearchSaved.this, MainActivity.class);
                CharSequence text4 = "Going to Saved home page...";
                Toast.makeText(this,text4, Toast.LENGTH_SHORT).show();
                startActivity( homePage);

                break;
        }

        return true;
    }
}