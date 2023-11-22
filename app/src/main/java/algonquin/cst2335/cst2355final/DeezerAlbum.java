package algonquin.cst2335.cst2355final;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.cst2355final.databinding.SongDetailBinding;
import algonquin.cst2335.cst2355final.databinding.SongMainBinding;


public class DeezerAlbum extends AppCompatActivity {

    SongMainBinding binding; // Initialize a binding object
    ArrayList<DeezerSong> songs = new ArrayList<>();// Create an ArrayList to store messages
    RecyclerView.Adapter myAdapter = null; // Initialize an adapter for RecyclerView
    DeezerSongDAO dsDAO;
    int selectedRow;
    DeezerSongViewModel songModel; // Initialize a ViewModel

    /**
     * Called when the activity is created.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SongMainBinding.inflate(getLayoutInflater());// Inflate the layout using binding
        setContentView(binding.getRoot());// Set the content view to the inflated layout


        DeezerSongDatabase db = Room.databaseBuilder(getApplicationContext(), DeezerSongDatabase.class, "database-song").build();
        dsDAO = db.dsDAO();
        setSupportActionBar( binding.mysongToolbar);
//        songModel = new ViewModelProvider(this).get(DeezerSongViewModel.class); // Initialize ViewModel
        //load messages from the database
        //initialize the variable
//        songs = songModel.songs.getValue();// Get messages from the ViewModel

//        songModel.selectedSong.observe(this, (newMessageValue) -> {
//            // Create a new instance of MessageDetailsFragment and set the selected message
//            DeezerSongDetailsFragment songFragment = new DeezerSongDetailsFragment(newMessageValue);
//
//            // Get the FragmentManager
//            FragmentManager fragmentManager = getSupportFragmentManager();
//
//            // Begin the FragmentTransaction
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//            fragmentTransaction.addToBackStack("hi?");
//            // Replace the existing fragment in fragmentLocation with the new MessageDetailsFragment
//            fragmentTransaction.replace(R.id.fragmentLocation, songFragment);
//
//            // Commit the transaction
//            fragmentTransaction.commit();
//        });

        if (songs == null) {
            songModel.songs.setValue(songs = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                songs.addAll( dsDAO.getAllSongs()); //Once you get the data from database

                runOnUiThread( () -> binding.songrecyclerView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });
        }

        // Handle a click on the Send button
        binding.searchsongButton.setOnClickListener(click -> {
            String text = binding.searchSongText.getText().toString();
            DeezerSong theSong = new DeezerSong("","",0,"");
            songs.add(theSong); // Add the message to the ArrayList
            myAdapter.notifyItemInserted(songs.size() - 1);
            binding.searchSongText.setText("");//remove the text in EditText

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(new Runnable() {
                @Override
                public void run() {
                    dsDAO.insertSong(theSong);
                }
            });
        });


        binding.songrecyclerView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    SongDetailBinding binding =
                            SongDetailBinding.inflate(getLayoutInflater(), parent, false);

                    return new MyRowHolder(binding.getRoot());

            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                // Bind data to the views in each row
                DeezerSong obj = songs.get(position); // Get the message at the current position
//                holder.songText.setText(obj.getTitle()); // Set the message in the TextV

            }

            //return the number of rows to draw
            @Override
            public int getItemCount() {
                return songs.size();
            }

            public int getItemViewType(int position) {
                // Get the message at the current position
                DeezerSong message = songs.get(position); // the first letter
                    return 0;
            }
        });

        binding.songrecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    class MyRowHolder extends RecyclerView.ViewHolder {
        public EditText songText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            songText = itemView.findViewById(R.id.searchSongText);

            itemView.setOnClickListener(click ->{
                int position = getAbsoluteAdapterPosition();
                DeezerSong selected = songs.get(position);
                songModel.selectedSong.postValue(selected);//launch a fragment

                selectedRow = position;
            });

        }
    } //end of onCreat
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.song_memu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.returnHomeMenu:
                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.

                AlertDialog.Builder builder = new AlertDialog.Builder(DeezerAlbum.this);

                builder.setMessage("Do you want to return the home page: ")
                        .setTitle("Question:")
                        .setNegativeButton("No", (a, b) -> {
                        })
                        .setPositiveButton("Yes", (a, b) -> {

                            Executors.newSingleThreadExecutor().execute(() -> {

                            });


                            Snackbar.make(binding.mysongToolbar, "You return to home page", Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk -> {
                                        Executors.newSingleThreadExecutor().execute(()->{
//                                            dsDAO.insertMessage(removeMessage);
                                        });

                                    })
                                    .show();
                        }).create().show();
                break;

            case R.id.addToList:
                Toast.makeText(this,"Version 1.0, created by Li Jiang", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

}
