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
import algonquin.cst2335.cst2355final.databinding.SearchRoomBinding;
import algonquin.cst2335.cst2355final.databinding.SearchMessageBinding;
import algonquin.cst2335.cst2355final.databinding.SearchSavedBinding;


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


public class SearchRoom extends AppCompatActivity {
    SearchRoomBinding binding;
    ArrayList<SearchTerm> messages = new ArrayList<>();

    SearchViewModel chatModel ;
    SearchTermDAO mDAO;

    SearchTerm dictionaryFromApi;
    private RecyclerView.Adapter myAdapter;
    protected String searchMessage;
    protected String termurl;
    protected RequestQueue queue = null;

    Intent dictionarySavedPage;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.yuxing_menu_file, menu);
        return true;
    }
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

        SearchDatabase db = Room.databaseBuilder(getApplicationContext(), SearchDatabase.class, "yuxingDictionary").build();
        mDAO = db.searchTermDao();
        //database


        binding = SearchRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //volley
        queue = Volley.newRequestQueue(this);
        setSupportActionBar(binding.yuxingtoolbar);


        //go to saved term page
        binding.yuxingsavedBtn.setOnClickListener(clk ->{
            dictionarySavedPage = new Intent( SearchRoom.this, SearchSaved.class);
            startActivity( dictionarySavedPage);
        });
        //SharedPreferences to save something about what was typed in the EditText for use the next time
        SharedPreferences prefs = getSharedPreferences("searchHistory", Context.MODE_PRIVATE);
        AtomicReference<EditText> searchText = new AtomicReference<>(binding.yuxingeditTextSearch);

        binding.yuxingbtnSearch.setOnClickListener(clk -> {
            boolean saveButton = true;
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

                        for(int i = 0; i < meanings.length(); i++){
                            JSONObject aMeaning = meanings.getJSONObject(i);
                            JSONArray aDefinition = aMeaning.getJSONArray("definitions");
                            for(int j = 0; j < aDefinition.length(); j++){
                                String def = aDefinition.getJSONObject(j).getString("definition");
                                Log.d( "received meaning",def);

                                dictionaryFromApi = new SearchTerm(searchMessage,currentDateandTime,def,saveButton);
                                messages.add(dictionaryFromApi);
                                myAdapter.notifyDataSetChanged();
                            }
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, (error) -> { });
                queue.add(request);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

//            String termName = binding.yuxingeditTextSearch.getText().toString();
//            String termDefinnition = "";
//            SearchTerm thisMessage = new SearchTerm(termName, currentDateandTime,termDefinnition);
//            messages.add(thisMessage);

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

        binding.yuxingrecyclerView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override

            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                // viewType will either be 0 or 1

                if (viewType == 0) {
                    // 1. load a XML layout
                    SearchMessageBinding binding =                            // parent is incase matchparent
                            SearchMessageBinding.inflate(getLayoutInflater(), parent, false);

                    // 2. call our constructor below
                    return new MyRowHolder(binding.getRoot()); // getRoot returns a ConstraintLayout with TextViews inside
                }
                else{
                    // 1. load a XML layout
                    SearchMessageBinding binding =                            // parent is incase matchparent
                            SearchMessageBinding.inflate(getLayoutInflater(), parent, false);

                    // 2. call our constructor below
                    return new MyRowHolder(binding.getRoot()); // getRoot returns a ConstraintLayout with TextViews inside

                }
            }

            public int  getItemViewType(int position){
                // determine which layout to load at row position
                if (messages.get(position).isSaveButton() == true) // for the first 5 rows
                {
                    return 0;
                }
                else return 1;
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                SearchTerm obj = messages.get(position);
                holder.messageText.setText(obj.getTerm());
//              recylerview message format
                holder.timeText.setText(obj.getTimeSent());
            }

            @Override
            public int getItemCount() {
                return messages.size();
            }
        });

        binding.yuxingrecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    class MyRowHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView timeText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                SearchTerm selected = messages.get(position);

                chatModel.selectedMessage.postValue(selected);
            });
            messageText = itemView.findViewById(R.id.yuxingTermWord);
            timeText =itemView.findViewById(R.id.yuxingSearchtime);
        }
    }

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

                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //show how to use this app.
                AlertDialog.Builder dictionayUse = new AlertDialog.Builder(this);
                dictionayUse.setMessage("How to use").setTitle("About: ")
                        .setNegativeButton("OK", (dialog, cl) -> {
                        }).create().show();
                break;
        }

        return true;
    }



}
