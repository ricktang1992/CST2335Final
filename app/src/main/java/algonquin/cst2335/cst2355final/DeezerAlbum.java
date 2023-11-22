package algonquin.cst2335.cst2355final;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.cst2355final.databinding.SongMainBinding;


public class DeezerAlbum extends AppCompatActivity {

    private SongMainBinding binding;

    ArrayList<DeezerSong> songs = new ArrayList<>();
    RecyclerView.Adapter myAdapter = null;
    DeezerSongDAO dsDAO;
    DeezerSongViewModel songModel;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.song_memu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch( item.getItemId() )
//        {
//            case R.id.savedSongMenu:
//
//                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
//                //asking if the user wants to delete this message.
//                break;
//
//            case R.id.aboutSongMenu:
//
//                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
//                builder2.setMessage("This application is the song.").setTitle("About: ")
//                        .setNegativeButton("OK", (dialog, cl) -> {
//                        }).create().show();
//                break;
//            case R.id.saveTheSong:
//
//                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
//                //asking if the user wants to delete this message.
//                break;
//        }
//
//        return true;
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = SongMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        songModel = new ViewModelProvider(this).get(DeezerSongViewModel.class);
        DeezerSongDatabase db = Room.databaseBuilder(getApplicationContext(), DeezerSongDatabase.class, "Deezer-song").build();
        dsDAO = db.dsDAO();

        songs = songModel.songs.getValue();

        if (songs == null) {
            songModel.songs.setValue(songs = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                songs.addAll( dsDAO.getAllSongs()); //Once you get the data from database

                runOnUiThread( () -> binding.recyclerView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });
        }

        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        EditText searchText =binding.titleTextInput;
        Button searchButton = binding.searchButton;
        searchButton.setOnClickListener(clk->
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("searchText", searchText.getText().toString() );
            editor.apply();
            CharSequence text = "Searching...";
            Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
        } );

        setSupportActionBar(binding.songToolBar);
        searchText.setText(prefs.getString("searchText",""));


        songModel.selectedSong.observe(this, (newSongValue) ->{
            DeezerSongDetailsFragment songFragment = new DeezerSongDetailsFragment(newSongValue);
            // Get the FragmentManager
            FragmentManager fragmentManager = getSupportFragmentManager();

            // Begin the FragmentTransaction
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Replace the existing fragment in fragmentLocation with the new MessageDetailsFragment
            fragmentTransaction.replace(R.id.fragmentLocation, songFragment);

            fragmentTransaction.addToBackStack("");
            // Commit the transaction
            fragmentTransaction.commit();
        });

        // Handle a click on the Send button
        binding.searchButton.setOnClickListener(click -> {
            DeezerSong theSong = new DeezerSong("title","", 0,"albumCoverText");
            songs.add(theSong); // Add the message to the ArrayList
            myAdapter.notifyItemInserted(songs.size() - 1);
            binding.titleTextInput.setText("");//remove the text in EditText

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(new Runnable() {
                @Override
                public void run() {
                    dsDAO.insertSong(theSong);
                }
            });
        });


        binding.recyclerView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //1)load a XML layout

                return new MyRowHolder(binding.getRoot());
            }


            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                // Bind data to the views in each row
                DeezerSong obj = songs.get(position); // Get the message at the current position
                holder.messageText.setText(obj.getTitle()); // Set the message in the TextV
                holder.timeText.setText(obj.getName());

            }

            //return the number of rows to draw
            @Override
            public int getItemCount() {
                return songs.size();
            }

        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    class MyRowHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView timeText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.title_text_input);

            itemView.setOnClickListener(click -> {

                int position = getAbsoluteAdapterPosition();
                DeezerSong selected = songs.get(position);
                songModel.selectedSong.postValue(selected);

            });
        }
    }
}