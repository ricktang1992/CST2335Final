package algonquin.cst2335.cst2355final.yuxing;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import algonquin.cst2335.cst2355final.Data.SearchViewModel;
import algonquin.cst2335.cst2355final.R;
import algonquin.cst2335.cst2355final.databinding.SearchRoomBinding;
import algonquin.cst2335.cst2355final.databinding.SearchMessageBinding;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
    private RecyclerView.Adapter myAdapter;
    protected String term;
    protected String termurl;
    protected RequestQueue queue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatModel = new ViewModelProvider(this).get(SearchViewModel.class);
        chatModel.selectedMessage.observe(this, (selectedMessage) -> {
            SearchDetailsFragment newFragment = new SearchDetailsFragment(selectedMessage);

            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();

            tx.replace(R.id.yuxingframeLayout, newFragment);
            tx.commit();

        });

        SearchDatabase db = Room.databaseBuilder(getApplicationContext(), SearchDatabase.class, "databaseFileOnPhone").build();
        mDAO = db.searchTermDao();



        //database


        binding = SearchRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.yuxingtoolbar);
//        queue = Volley.newRequestQueue(this);
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
                term = URLEncoder.encode(binding.yuxingeditTextSearch.getText().toString(),"UTF-8");
                termurl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + term;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, termurl, null, (response) -> {
                    try {
                        JSONObject mainObject = response.getJSONObject("0");

                        JSONArray meanings = mainObject.getJSONArray ( "meanings" );

                        for(int i = 0; i < meanings.length(); i++){
                            JSONObject aMeaning = meanings.getJSONObject(i);
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, (error) -> { });
                queue.add(request);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            String termName = binding.yuxingeditTextSearch.getText().toString();
            String termDefinnition = "";
            SearchTerm thisMessage = new SearchTerm(termName, currentDateandTime,termDefinnition);
            messages.add(thisMessage);

            // clear the previous text
            binding.yuxingeditTextSearch.setText("");
            myAdapter.notifyDataSetChanged();

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(new Runnable() {
                @Override
                public void run() {
                    long id = mDAO.insertSearchTerm(thisMessage);
                    thisMessage.id = id;
                }
            });


            runOnUiThread(() ->{myAdapter.notifyItemInserted(messages.size() - 1);});

            binding.yuxingeditTextSearch.setText("");
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


                SearchMessageBinding binding = SearchMessageBinding.inflate(getLayoutInflater(), parent, false);

                    return new MyRowHolder(binding.getRoot()); // getRoot returns a ConstraintLayout with TextViews inside


            }


            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                SearchTerm obj = messages.get(position);
                holder.messageText.setText(obj.getTerm());
//                holder.messageText.setText("");
                holder.timeText.setText(obj.getTimeSent());
                holder.definitionText.setText(obj.getDefinition());
            }

            @Override
            public int getItemCount() {
                return messages.size();
            }
        });

        binding.yuxingrecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.yuxing_menu_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId() )
        {
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to delete the selected message?");
                builder.setTitle("Delete Message")
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
                break;
            case R.id.about:

                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                Toast.makeText(this,"Version 1.0, Created by Yuxing Xu",Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }


    class MyRowHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView timeText;
        TextView definitionText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                SearchTerm selected = messages.get(position);

                chatModel.selectedMessage.postValue(selected);
            });
            messageText = itemView.findViewById(R.id.yuxingTermMessage);
            timeText =itemView.findViewById(R.id.yuxingSearchtime);
            definitionText = itemView.findViewById(R.id.yuxingtermDefinnition);
        }
    }
}
