package algonquin.cst2335.cst2355final.rita;

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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.cst2355final.Data.DeezerSongViewModel;
import algonquin.cst2335.cst2355final.MainActivity;
import algonquin.cst2335.cst2355final.R;
import algonquin.cst2335.cst2355final.databinding.SongListBinding;
import algonquin.cst2335.cst2355final.databinding.SongSavedBinding;
import algonquin.cst2335.cst2355final.tianjiaosun.SunActivity;
import algonquin.cst2335.cst2355final.yuxing.SearchRoom;
import algonquin.cst2335.cst2355final.ziyao.RecipeSearch;

/**
 * DeezerSongList is an activity that displays a list of saved Deezer songs and allows users to interact with them.
 */
public class DeezerSongList extends AppCompatActivity {
    SongSavedBinding binding;
    ArrayList<DeezerSong> saveSongs = new ArrayList<>();
    DeezerSongViewModel saveModel;
    DeezerSongDAO dsDAO;
    RecyclerView.Adapter savedAdapter; // Initialize an adapter for RecyclerView
    Executor thread = Executors.newSingleThreadExecutor();

    /**
     * Initializes the activity when it is created.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveModel = new ViewModelProvider(this).get(DeezerSongViewModel.class);
        DeezerSongDatabase db = Room.databaseBuilder(getApplicationContext(), DeezerSongDatabase.class, "Song-favorite").build();
        dsDAO = db.dsDAO();
        saveSongs = saveModel.favoriteSongsArray.getValue();

        if (saveSongs == null) {
            saveModel.favoriteSongsArray.postValue(saveSongs = new ArrayList<>());
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                saveSongs.addAll(dsDAO.getAllSongs());

                runOnUiThread(() -> {
                    savedAdapter.notifyDataSetChanged();
                    binding.saveSongRecyclerView.setAdapter(savedAdapter);
                });
            });
        }

        binding = SongSavedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.mySaveSongToolbar);

        saveModel.selectedSong.observe(this, (newSongValue) -> {
            if (newSongValue != null) {
                DeezerSongDetailsFragment dsf = new DeezerSongDetailsFragment(newSongValue);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack("");
                ft.replace(R.id.saveSongFragmentLocation, dsf);
                ft.commit();
            }
        });


        binding.saveSongRecyclerView.setAdapter(savedAdapter = new RecyclerView.Adapter<MySaveRowHolder>() {
            @NonNull
            @Override
            public MySaveRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                SongListBinding binding =
                        SongListBinding.inflate(getLayoutInflater(), parent, false);
                // Set layout parameters to wrap content
                binding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                // 2. call our constructor below
                return new MySaveRowHolder(binding.getRoot()); // getRoot returns a ConstraintLayout with TextViews inside

            }

            @Override
            public void onBindViewHolder(@NonNull MySaveRowHolder holder, int position) {
                DeezerSong obj = saveSongs.get(position);
                holder.songTitleText.setText(obj.getName());
            }

            //return the number of rows to draw
            @Override
            public int getItemCount() {
                return saveSongs.size();
            }
        });
        binding.saveSongRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Custom ViewHolder class for the RecyclerView in the saved song list.
     */
    class MySaveRowHolder extends RecyclerView.ViewHolder {
        /**
         * TextView to display the title of a saved song.
         */
        public TextView songTitleText;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view representing each item in the RecyclerView.
         */
        public MySaveRowHolder(@NonNull View itemView) {
            super(itemView);
            songTitleText = itemView.findViewById(R.id.songTitleText);

            binding.favoriteDeleteBtn.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(DeezerSongList.this);
                String deleteQuestion = getString(R.string.deleteQuestion);
                String deleteTitle = getString(R.string.deleteTitle);
                String confirm = getString(R.string.confirm);
                builder.setMessage(deleteQuestion)
                        .setTitle(deleteTitle).
                        setNegativeButton(getString(R.string.reject), (dialog, cl) -> {

                        })
                        .setPositiveButton(confirm, (dialog, cl) -> {
                            DeezerSong song = saveSongs.get(position);

                            thread.execute(() -> {
                                dsDAO.deleteSong(song);
                                runOnUiThread(() -> binding.saveSongRecyclerView.setAdapter(savedAdapter));
                            });
                            saveSongs.remove(position);
                            savedAdapter.notifyItemRemoved(position);

                            String deletedSong = getString(R.string.deletedSong);
                            String undo = getString(R.string.undo);

                            Snackbar.make(songTitleText, deletedSong + " " + songTitleText.getText().toString(),
                                            Snackbar.LENGTH_LONG)
                                    .setAction(undo, click -> {
                                        saveSongs.add(position, song);
                                        savedAdapter.notifyItemInserted(position);
                                        thread.execute(() ->
                                        {
                                            dsDAO.insertSong(song);
                                            runOnUiThread(() -> binding.saveSongRecyclerView.setAdapter(savedAdapter));
                                        });
                                    })
                                    .show();
                        })
                        .create().show();
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
        getMenuInflater().inflate(R.menu.song_memu, menu);
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
        switch (item.getItemId()) {
            case R.id.returnHomeMenu:
                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                AlertDialog.Builder builder = new AlertDialog.Builder(DeezerSongList.this);
                builder.setMessage(getString(R.string.goToHomeSnack))
                        .setTitle(R.string.question)
                        .setNegativeButton(getString(R.string.reject), (a, b) -> {
                        })
                        .setPositiveButton(getString(R.string.confirm), (a, b) -> {
                            Intent SongSavedList = new Intent(DeezerSongList.this, DeezerAlbum.class);
                            CharSequence text3 = getString(R.string.goToHomeSnack);
                            Toast.makeText(this, text3, Toast.LENGTH_SHORT).show();
                            startActivity(SongSavedList);
                            Snackbar.make(binding.mySaveSongToolbar, getString(R.string.goToHomeSnack), Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.undo), clk -> {
                                        Intent mainPage = new Intent(DeezerSongList.this, MainActivity.class);
                                        CharSequence text1 = getResources().getString(R.string.ziyaoyxSunpage);
                                        Toast.makeText(this, text1, Toast.LENGTH_SHORT).show();
                                        startActivity(mainPage);
                                    })
                                    .show();
                        }).create().show();
                break;

            case R.id.showSaveList:
                Intent SongSavedList = new Intent( DeezerSongList.this, DeezerSongList.class );
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
                instructionsDialog.setMessage(R.string.yxAboutUse)
                        .setTitle(R.string.yxAboutTitle)
                        .setNegativeButton(getString(R.string.confirm), (dialog, cl) -> {})
                        .create().show();
                break;

            case R.id.ritaSunpage:
                // Display instructions on how to use the interface
                Intent sunPage = new Intent( DeezerSongList.this, SunActivity.class);
                CharSequence text1 = getResources().getString(R.string.ziyaoyxSunpage);
                Toast.makeText(this,text1, Toast.LENGTH_SHORT).show();
                startActivity( sunPage);
                break;

            case R.id.ritaRecipePage:
                // Display instructions on how to use the interface
                Intent recipePage = new Intent( DeezerSongList.this, RecipeSearch.class);
                CharSequence text2 = getResources().getString(R.string.yxRecipename);
                Toast.makeText(this,text2, Toast.LENGTH_SHORT).show();
                startActivity( recipePage);
                break;

            case R.id.ritaDictionary:
                // Display instructions on how to use the interface
                Intent dicPage = new Intent( DeezerSongList.this, SearchRoom.class);
                CharSequence text3 = getResources().getString(R.string.ziyaodictionarying);
                Toast.makeText(this,text3, Toast.LENGTH_SHORT).show();
                startActivity( dicPage);
                break;

            case R.id.ritaSongpage:
                // Display instructions on how to use the interface

                Intent songPage = new Intent( DeezerSongList.this, DeezerAlbum.class);
                CharSequence text4 = getResources().getString(R.string.ziyaosongPage);
                Toast.makeText(this,text4, Toast.LENGTH_SHORT).show();
                startActivity( songPage);
                break;
        }
            return true;
    }
}
