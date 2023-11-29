package algonquin.cst2335.cst2355final.yuxing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import algonquin.cst2335.cst2355final.Data.SearchViewModel;
import algonquin.cst2335.cst2355final.MainActivity;
import algonquin.cst2335.cst2355final.R;
import algonquin.cst2335.cst2355final.Tianjiao.SunRoom;
import algonquin.cst2335.cst2355final.databinding.SearchRoomBinding;
import algonquin.cst2335.cst2355final.databinding.SearchMessageBinding;
import algonquin.cst2335.cst2355final.databinding.SearchSavedBinding;
import algonquin.cst2335.cst2355final.rita.DeezerAlbum;
import algonquin.cst2335.cst2355final.ziyao.RecipeSearch;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The main activity for the search functionality in the application.
 * Allows users to search for terms, view detailed information, save terms, and access saved terms.
 * Uses Volley for API requests, Room for local database storage, and implements RecyclerView
 * for displaying search results and saved terms.
 *
 * Features:
 * - Search for terms using the dictionary API and display definitions.
 * - View detailed information about a selected term in a separate fragment.
 * - Save terms to a local database and access the saved terms.
 * - Delete saved terms from the database.
 * - Provide instructions on how to use the interface.
 * - Navigate to other projects (Recipe, Song, Sun) from the menu.
 *
 * The activity includes functionality to create and manage fragments, perform API requests,
 * persist data using Room database, and handle user interactions through RecyclerView.
 *
 * Layout components:
 * - EditText for entering search terms.
 * - Button for initiating the search.
 * - RecyclerView for displaying search results and saved terms.
 * - Toolbar for displaying options menu.
 *
 * Dependencies:
 * - SearchViewModel: View Model for managing data related to search.
 * - SearchDetailsFragment: Fragment for displaying detailed information about a search term.
 * - SearchTerm, SearchTermDAO: Entities and Data Access Object for Room database.
 * - DeezerAlbum, RecipeSearch, SunRoom: Intents for navigating to other projects.
 *
 * Usage:
 * - Enter a search term in the provided EditText and click the 'Search' button.
 * - View detailed information about a selected term in a separate fragment.
 * - Save terms to the local database and access the saved terms.
 * - Delete saved terms from the database.
 * - Provide instructions on how to use the interface through the options menu.
 * - Navigate to other projects (Recipe, Song, Sun) from the menu.
 *
 * @author Yuxing Xu
 * @version 1.0
 * @since 2023-11-29
 */

public class SearchRoom extends AppCompatActivity {
    /**
     * SearchRoomBinding object for data binding.
     */
    SearchRoomBinding binding;

    /**
     * ArrayList to store SearchTerm objects representing search results.
     */
    ArrayList<SearchTerm> messages = new ArrayList<>();

    /**
     * ViewModel for managing data related to search.
     */
    SearchViewModel chatModel;

    /**
     * Data Access Object for Room database.
     */
    SearchTermDAO mDAO;

    /**
     * Represents a search term retrieved from the dictionary API.
     */
    SearchTerm dictionaryFromApi;

    /**
     * Adapter for the RecyclerView displaying search results and saved terms.
     */
    private RecyclerView.Adapter<MyRowHolder> myAdapter;

    /**
     * The search term entered by the user.
     */
    protected String searchMessage;

    /**
     * The URL for making API requests based on the search term.
     */
    protected String termurl;

    /**
     * RequestQueue for managing Volley network requests.
     */
    protected RequestQueue queue = null;

    /**
     * Intent for navigating to the saved terms page.
     */
    Intent dictionarySavedPage;

    /**
     * Intent for navigating to the song project.
     */
    Intent songPage;

    /**
     * Intent for navigating to the recipe project.
     */
    Intent RecipePage;

    /**
     * Intent for navigating to the sun project.
     */
    Intent sunPage;

    /**
     * Initializes the options menu.
     *
     * @param menu The menu to be initialized.
     * @return true if the menu is successfully created; false otherwise.
     */
    @Override
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


        chatModel = new ViewModelProvider(this).get(SearchViewModel.class);
        chatModel.selectedMessage.observe(this, (selectedMessage) -> {
            SearchDetailsFragment newFragment = new SearchDetailsFragment(selectedMessage);

            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            tx.addToBackStack("");
            tx.replace(R.id.yuxingframeLayout, newFragment);
            tx.commit();

        });

        SearchDatabase db = Room.databaseBuilder(getApplicationContext(), SearchDatabase.class, "Dictionary").build();
        mDAO = db.searchTermDao();
        //database


        binding = SearchRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //volley
        queue = Volley.newRequestQueue(this);
        setSupportActionBar(binding.yuxingtoolbar);


        //SharedPreferences to save something about what was typed in the EditText for use the next time
        SharedPreferences prefs = getSharedPreferences("searchHistory", Context.MODE_PRIVATE);
        AtomicReference<EditText> searchText = new AtomicReference<>(binding.yuxingeditTextSearch);

        binding.yuxingbtnSearch.setOnClickListener(clk -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("searchText", searchText.get().getText().toString() );
            editor.apply();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            // search from url
            try {
                searchMessage = URLEncoder.encode(binding.yuxingeditTextSearch.getText().toString(),"UTF-8");
                termurl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + searchMessage;
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, termurl, null, (response) -> {
                    try {
                        JSONObject mainObject = response.getJSONObject(0);

                        JSONArray meanings = mainObject.getJSONArray ( "meanings" );
                        messages.clear();
                        for(int i = 0; i < meanings.length(); i++){
                            JSONObject aMeaning = meanings.getJSONObject(i);
                            JSONArray aDefinition = aMeaning.getJSONArray("definitions");
                            for(int j = 0; j < aDefinition.length(); j++){
                                String def = aDefinition.getJSONObject(j).getString("definition");
                                Log.d( "received meaning",def);

                                dictionaryFromApi = new SearchTerm(searchMessage,currentDateandTime,def);
                                messages.add(dictionaryFromApi);
                            }
                            myAdapter.notifyDataSetChanged();
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, (error) -> { });
                queue.add(request);
                binding.yuxingeditTextSearch.setText("");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

//            String termName = binding.yuxingeditTextSearch.getText().toString();
//            String termDefinnition = "";
//            SearchTerm thisMessage = new SearchTerm(termName, currentDateandTime,termDefinnition);
//            RecyclerView.Adapter<MyRowHolder>

//             clear the previous text
//            binding.yuxingeditTextSearch.setText("");
//            myAdapter.notifyDataSetChanged();

//            Executor thread = Executors.newSingleThreadExecutor();
//            thread.execute(new Runnable() {
//                @Override
//                public void run() {
//                    long id = mDAO.insertSearchTerm(thisMessage);
//                    thisMessage.id = id;
//                }
//            });
//
//
//            runOnUiThread(() ->{myAdapter.notifyItemInserted(messages.size() - 1);});
//
//            binding.yuxingeditTextSearch.setText("");
        });

        searchText.get().setText(prefs.getString("searchText",""));
        chatModel.selectedMessage.observe(this, (newMessageValue) -> {
            SearchDetailsFragment dictionaryFragment = new SearchDetailsFragment( newMessageValue );
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            tx.addToBackStack("");
            tx.replace(R.id.yuxingframeLayout,dictionaryFragment);
            tx.commit();
        });

        /**
         * Adapter for the RecyclerView displaying search results and saved terms in the SearchRoom activity.
         * The adapter binds the data to the RecyclerView and handles the creation of ViewHolder objects.
         */
        binding.yuxingrecyclerView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {

            /**
             * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
             *
             * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
             * @param viewType The view type of the new View.
             * @return A new ViewHolder that holds a View of the given view type.
             */
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    // 1. load a XML layout
                    SearchMessageBinding binding =                            // parent is incase matchparent
                            SearchMessageBinding.inflate(getLayoutInflater(), parent, false);

                    // 2. call our constructor below
                    return new MyRowHolder(binding.getRoot()); // getRoot returns a ConstraintLayout with TextViews inside

            }


            /**
             * Called by RecyclerView to display the data at the specified position.
             *
             * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position
             *                 in the data set.
             * @param position The position of the item within the adapter's data set.
             */
            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                SearchTerm obj = messages.get(position);
                holder.messageText.setText(obj.getTerm());
//              recylerview message format
                holder.timeText.setText(obj.getTimeSent());
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
        /**
         * Sets the layout manager for the RecyclerView in the SearchRoom activity.
         */
        binding.yuxingrecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    /**
     * ViewHolder class for the items in the RecyclerView used in the SearchRoom activity.
     * Represents a single item view and holds references to its child views.
     */
    class MyRowHolder extends RecyclerView.ViewHolder {

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
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                SearchTerm selected = messages.get(position);

                chatModel.selectedMessage.postValue(selected);
            });
            messageText = itemView.findViewById(R.id.yuxingTermWord);
            timeText = itemView.findViewById(R.id.yuxingSearchtime);
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
                            SearchTerm selectedMessage = chatModel.selectedMessage.getValue();
                            if (selectedMessage != null) {
                                // Delete the message from the database
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    mDAO.deleteMessage(selectedMessage);
                                });

                                // Remove the message from the list and update the RecyclerView
                                messages.remove(selectedMessage);
                                myAdapter.notifyDataSetChanged();

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
                                                    myAdapter.notifyDataSetChanged();
                                                });
                                            });
                                        })
                                        .show();
                            }
                        })
                        .setNegativeButton("No", (dialog, cl) -> {})
                        .create().show();
                break;

            case R.id.saveTheTERM:

                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                SearchTerm addTerm = chatModel.selectedMessage.getValue();
                messages.add(addTerm);
                myAdapter.notifyDataSetChanged();
                Executor addthread = Executors.newSingleThreadExecutor();
                addthread.execute(( ) -> {
                    //this is on a background thread
                    addTerm.id = (int)mDAO.insertSearchTerm(addTerm); //get the ID from the database
                    Log.d("TAG", "The id created is:" + addTerm.id);
                }); //the body of run()
                Snackbar.make(this.findViewById(R.id.yuxingeditTextSearch),"You added the term "
                        +addTerm.getTerm(),Snackbar.LENGTH_LONG).show();
                getSupportFragmentManager().popBackStack();
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
                RecipePage = new Intent( SearchRoom.this, RecipeSearch.class);
                CharSequence text = "Going to Recipe Project...";
                Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
                startActivity( RecipePage);
                break;
            case R.id.yxSongpage:
                // Display instructions on how to use the interface
                songPage = new Intent( SearchRoom.this, DeezerAlbum.class);
                CharSequence text1 = "Going to Song Project...";
                Toast.makeText(this,text1, Toast.LENGTH_SHORT).show();
                startActivity( songPage);

            case R.id.yxSunpage:
                // Display instructions on how to use the interface
                sunPage = new Intent( SearchRoom.this, SunRoom.class);
                CharSequence text2 = "Going to Sun Project...";
                Toast.makeText(this,text2, Toast.LENGTH_SHORT).show();
                startActivity( sunPage);

                break;
            case R.id.yxsavedTerm:
                // Display instructions on how to use the interface
                dictionarySavedPage = new Intent( SearchRoom.this, SearchSaved.class);
                CharSequence text3 = "Going to Saved term page...";
                Toast.makeText(this,text3, Toast.LENGTH_SHORT).show();
                startActivity( dictionarySavedPage);

                break;
        }

        return true;
    }



}
