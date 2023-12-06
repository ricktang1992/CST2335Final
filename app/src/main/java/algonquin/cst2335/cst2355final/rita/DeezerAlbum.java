package algonquin.cst2335.cst2355final.rita;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import algonquin.cst2335.cst2355final.Data.DeezerSongViewModel;
import algonquin.cst2335.cst2355final.MainActivity;
import algonquin.cst2335.cst2355final.R;
import algonquin.cst2335.cst2355final.databinding.SongDetailBinding;
import algonquin.cst2335.cst2355final.databinding.SongListBinding;
import algonquin.cst2335.cst2355final.databinding.SongMainBinding;
import algonquin.cst2335.cst2355final.tianjiaosun.SunActivity;
import algonquin.cst2335.cst2355final.yuxing.SearchDetailsFragment;
import algonquin.cst2335.cst2355final.yuxing.SearchRoom;
import algonquin.cst2335.cst2355final.ziyao.RecipeMain;
import algonquin.cst2335.cst2355final.ziyao.RecipeSearch;

/**
 * DeezerAlbum is an activity that allows users to search for songs on Deezer, view details, and navigate to a saved song list.
 */
public class DeezerAlbum extends AppCompatActivity {

    protected SongMainBinding binding;
    protected ArrayList<DeezerSong> songs = new ArrayList<>();// Create an ArrayList to store messages
    protected RecyclerView.Adapter myAdapter = null; // Initialize an adapter for RecyclerView
    protected DeezerSongDAO dsDAO;
    protected int selectedRow;
    protected DeezerSongViewModel songModel; // Initialize a ViewModel
    protected RequestQueue queue = null; //create a Volley object that will connect to a server


    /**
     * Called when the activity is created.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songModel = new ViewModelProvider(this).get(DeezerSongViewModel.class); // Initialize ViewModel

        DeezerSongDatabase db = Room.databaseBuilder(getApplicationContext(), DeezerSongDatabase.class, "database-song").build();
        dsDAO = db.dsDAO();

        binding = SongMainBinding.inflate(getLayoutInflater());// Inflate the layout using binding
        setContentView(binding.getRoot());// Set the content view to the inflated layout

        queue = Volley.newRequestQueue(this);
        setSupportActionBar( binding.mysongToolbar);

        SharedPreferences prefer = getSharedPreferences("Search History", Context.MODE_PRIVATE);
        AtomicReference<EditText> searchText = new AtomicReference<>(binding.searchSongText);

        // Handle a click on the Send button
        binding.searchsongButton.setOnClickListener(click -> {
            SharedPreferences.Editor editor = prefer.edit();
            editor.putString("searchText", binding.searchSongText.getText().toString() );
            editor.apply();
            CharSequence text = getString(R.string.songsearch);
            Toast.makeText(this,text, Toast.LENGTH_SHORT).show();

            songs.clear();
            myAdapter.notifyDataSetChanged();

            String stringURL = null;
            try {
                String artistName = URLEncoder.encode(binding.searchSongText.getText().toString(), "UTF-8");
                stringURL = "https://api.deezer.com/search/artist/?q=" + artistName;

                JsonObjectRequest apiRequest = new  JsonObjectRequest(Request.Method.GET, stringURL,null,
                        (response) -> {
                            try {
                                JSONArray data = response.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject anAblum = data.getJSONObject(i);
                                    String tracklistUrl = anAblum.getString("tracklist");

                                    JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET,
                                            tracklistUrl, null,
                                            response1 -> {
                                                JSONArray tracks = null;
                                                try {
                                                    tracks = response1.getJSONArray("data");
                                                    for (int k = 0; k < tracks.length(); k++){
                                                        JSONObject song = tracks.getJSONObject(k);
                                                        String title = song.getString("title");
                                                        int duration = song.getInt("duration");

                                                        JSONObject album = song.getJSONObject("album");
                                                        String name = album.getString("title");
                                                        String cover = album.getString("cover");

                                                        // Create DeezerSong object and add to the list
                                                        DeezerSong deezer = new DeezerSong(title, name, duration, cover);
                                                        songs.add(deezer);
//                                                        myAdapter.notifyDataSetChanged();
                                                        myAdapter.notifyItemInserted(songs.size()-1);
                                                        binding.searchSongText.setText("");
                                                    }
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            },
                                            error -> {});
                                    queue.add(request2);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            // Handle error
                        });
                queue.add(apiRequest);


            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });

        searchText.get().setText(prefer.getString("searchText", ""));

        songModel.selectedSong.observe(this, (newSongValue) ->{
            // Create a new instance of MessageDetailsFragment and set the selected message
            DeezerSongDetailsFragment songFragment = new DeezerSongDetailsFragment(newSongValue);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack("hi?");
            ft.replace(R.id.songfragmentLocation, songFragment);
            ft.commit();
        });

        binding.songrecyclerView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                SongListBinding binding =
                        SongListBinding.inflate(getLayoutInflater(), parent, false);
                return new MyRowHolder(binding.getRoot());
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                DeezerSong obj = songs.get(position);
                holder.songText.setText(obj.getTitle());
            }

            //return the number of rows to draw
            @Override
            public int getItemCount() {
                return songs.size();
            } });

        binding.songrecyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.goToFavList.setOnClickListener(click ->{
            Intent SongSavedList = new Intent( DeezerAlbum.this, DeezerSongList.class );
            CharSequence showText = getString(R.string.goToSaveList);
            Toast.makeText(this,showText, Toast.LENGTH_SHORT).show();
            startActivity( SongSavedList);
        });
    }


    class MyRowHolder extends RecyclerView.ViewHolder {
        public TextView songText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            songText = itemView.findViewById(R.id.songTitleText);
            itemView.setOnClickListener(click ->{
                int position = getAbsoluteAdapterPosition();
                DeezerSong selected = songs.get(position);
                songModel.selectedSong.postValue(selected);//launch a fragment
            });
        }
    } //end of onCreat
    /**
     * Handles options menu creation.
     *
     * @param menu The options menu in which you place your items.
     * @return True to display the menu, false to prevent it from being displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.song_memu,menu);
        return true;
    }
    /**
     * Handles options menu item selection.
     *
     * @param item The menu item that was selected.
     * @return True if the item was successfully handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.returnHomeMenu:
                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                AlertDialog.Builder builder = new AlertDialog.Builder(DeezerAlbum.this);
                builder.setMessage(getString(R.string.goToHomeSnack))
                        .setTitle(R.string.question)
                        .setNegativeButton(getString(R.string.reject), (a, b) -> {
                        })
                        .setPositiveButton(getString(R.string.confirm), (a, b) -> {
                            Intent  SongSavedList = new Intent( DeezerAlbum.this, MainActivity.class );
                            CharSequence text3 = getString(R.string.goToHomeSnack);
                            Toast.makeText(this,text3, Toast.LENGTH_SHORT).show();
                            startActivity( SongSavedList);
                            Snackbar.make(binding.mysongToolbar, getString(R.string.goToHomeSnack), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.undo), clk -> {
                                        Intent mainPage = new Intent( DeezerAlbum.this, MainActivity.class);
                                        CharSequence text1 = getResources().getString(R.string.ziyaoyxSunpage);
                                        Toast.makeText(this,text1, Toast.LENGTH_SHORT).show();
                                        startActivity( mainPage);
                                    })
                                    .show();
                        }).create().show();
                break;


            case R.id.showSaveList:
                Intent SongSavedList = new Intent( DeezerAlbum.this, DeezerSongList.class );
                CharSequence showText = getString(R.string.goToSaveList);
                Toast.makeText(this,showText, Toast.LENGTH_SHORT).show();
                startActivity( SongSavedList);
                break;

            case R.id.about:
                Toast.makeText(this,getString(R.string.version), Toast.LENGTH_LONG).show();
                break;

            case R.id.help:
                // Display instructions on how to use the interface
                AlertDialog.Builder instructionsDialog = new AlertDialog.Builder(this);
                instructionsDialog.setMessage(R.string.SongAboutuse)
                        .setTitle(R.string.yxAboutTitle)
                        .setNegativeButton(getString(R.string.confirm), (dialog, cl) -> {})
                        .create().show();
                break;

            case R.id.ritaSunpage:
                // Display instructions on how to use the interface
                Intent sunPage = new Intent( DeezerAlbum.this, SunActivity.class);
                CharSequence text1 = getResources().getString(R.string.ziyaoyxSunpage);
                Toast.makeText(this,text1, Toast.LENGTH_SHORT).show();
                startActivity( sunPage);
                break;

            case R.id.ritaRecipePage:
                // Display instructions on how to use the interface
                Intent recipePage = new Intent( DeezerAlbum.this, RecipeSearch.class);
                CharSequence text2 = getResources().getString(R.string.yxRecipename);
                Toast.makeText(this,text2, Toast.LENGTH_SHORT).show();
                startActivity( recipePage);
                break;

            case R.id.ritaDictionary:
                // Display instructions on how to use the interface
                Intent dicPage = new Intent( DeezerAlbum.this, SearchRoom.class);
                CharSequence text3 = getResources().getString(R.string.ziyaodictionarying);
                Toast.makeText(this,text3, Toast.LENGTH_SHORT).show();
                startActivity( dicPage);
                break;

            case R.id.ritaSongpage:
                // Display instructions on how to use the interface

                Intent songPage = new Intent( DeezerAlbum.this, DeezerAlbum.class);
                CharSequence text4 = getResources().getString(R.string.ziyaosongPage);
                Toast.makeText(this,text4, Toast.LENGTH_SHORT).show();
                startActivity( songPage);
                break;
        }
        return true;
    }

}
