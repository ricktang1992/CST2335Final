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
import algonquin.cst2335.cst2355final.R;
import algonquin.cst2335.cst2355final.Tianjiao.SunRoom;
import algonquin.cst2335.cst2355final.databinding.SearchMessageBinding;
import algonquin.cst2335.cst2355final.databinding.SearchSavedBinding;
import algonquin.cst2335.cst2355final.rita.DeezerAlbum;
import algonquin.cst2335.cst2355final.ziyao.RecipeSearch;


public class SearchSaved extends AppCompatActivity {
    SearchSavedBinding binding;
    ArrayList<SearchTerm> messages = null;

    SearchViewModel saveModel ;
    SearchTermDAO mDAO;
    Intent dictionarySavedPage;
    Intent songPage;

    Intent RecipePage;
    Intent sunPage;
    private RecyclerView.Adapter savedAdapter;

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.yuxing_menu_file, menu);
        return true;
    }
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
                tx.replace(R.id.yuxingframeLayout, newFragment);
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
            @NonNull
            @Override

            public myRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                // 1. load a XML layout
                SearchMessageBinding binding =                            // parent is incase matchparent
                        SearchMessageBinding.inflate(getLayoutInflater(), parent, false);

                // 2. call our constructor below
                return new myRowHolder(binding.getRoot()); // getRoot returns a ConstraintLayout with TextViews inside

            }



            @Override
            public void onBindViewHolder(@NonNull myRowHolder holder, int position) {
//                SearchTerm obj = messages.get(position);
//                holder.messageText.setText(obj.getTerm());
////              recylerview message format
//                holder.timeText.setText(obj.getTimeSent());
                holder.messageText.setText("aaaaaaaaa");
                String obj = messages.get(position).getTerm();
                holder.messageText.setText(obj);
            }

            @Override
            public int getItemCount() {
                return messages.size();
            }

        });

        binding.yuxingsavedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    class myRowHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView timeText;

        public myRowHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.yuxingTermWord);

            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                SearchTerm selected = messages.get(position);

                saveModel.selectedMessage.postValue(selected);
            });

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
                instructionsDialog.setMessage("Instructions on how to use the interface:\n\n" +
                                "1. Enter a search term in the provided EditText.\n" +
                                "2. Click the 'Search' button to retrieve definitions from the dictionary API.\n" +
                                "3. Click on a search result to see detailed information in a separate fragment.\n" +
                                "4. Click the 'Save' icon to add a term to the saved terms list.\n" +
                                "6. Access the saved terms by clicking the 'Saved' button.\n" +
                                "7. To delete a term, select it and choose the 'Delete' icon from the menu.\n" +
                                "8. For more help, you can contact support.")
                        .setTitle("How to Use")
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
                sunPage = new Intent( SearchSaved.this, SunRoom.class);
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
        }

        return true;
    }
}