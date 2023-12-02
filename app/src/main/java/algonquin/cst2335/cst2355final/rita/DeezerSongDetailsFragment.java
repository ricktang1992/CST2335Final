package algonquin.cst2335.cst2355final.rita;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.cst2355final.Data.DeezerSongViewModel;
import algonquin.cst2335.cst2355final.R;
import algonquin.cst2335.cst2355final.databinding.SongDetailBinding;

/**
 * DeezerSongDetailsFragment is a fragment that displays details of a selected Deezer song,
 * including the song title, duration, album name, and album cover image.
 */
public class DeezerSongDetailsFragment extends Fragment {
    /**
     * The selected DeezerSong object to display details for.
     */
    DeezerSong selected;
    /**
     * RequestQueue for handling network requests using Volley.
     */
    RequestQueue queue; //create a Volley object that will connect to a server
    /**
     * Executor for handling background thread tasks.
     */
    Executor thread = Executors.newSingleThreadExecutor();
    /**
     * Data Access Object for interacting with the DeezerSong database.
     */
    DeezerSongDAO dsDAO;

    /**
     * The name of the song
     */
    String songName;
    /**
     * The name of the Album
     */
    String songAlbum;
    /**
     * Default constructor for DeezerSongDetailsFragment.
     */
    public DeezerSongDetailsFragment() {
    }
    /**
     * Constructor for DeezerSongDetailsFragment that takes a DeezerSong object as a parameter.
     *
     * @param s The DeezerSong object to display details for.
     */
    public DeezerSongDetailsFragment(DeezerSong s) {
        selected = s;
    }
    /**
     * Called to create and return the view hierarchy associated with the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        DeezerSongDatabase db = Room.databaseBuilder(requireActivity(), DeezerSongDatabase.class, "Song-favorite").build();
        dsDAO = db.dsDAO();

        if (selected != null) {

            SongDetailBinding binding = SongDetailBinding.inflate(inflater);

            songName = selected.getName();
            binding.songTitle.setText(selected.title);
            binding.duration.setText(String.valueOf(selected.duration));
            binding.albumName.setText(selected.name);

            queue = Volley.newRequestQueue(requireContext());
            String coverURL = selected.cover;
            queue = Volley.newRequestQueue(requireContext());

            // Use Picasso to load and display the image
            Picasso.get().load(coverURL).into(binding.albumCover);

            binding.saveSongButton.setOnClickListener(c -> {
                thread.execute(() -> {
                    DeezerSong song = selected;
                    DeezerSong songExists = dsDAO.searchSongByName(songName, songAlbum);
                    // If the song already is in the db, don't insert it.
                    if (songExists == null) {
                        requireActivity().runOnUiThread(() -> {
                            String addQuestion = getString(R.string.addQuestion);
                            String addFavorites = getString(R.string.addTitle);
                            String confirm = getString(R.string.confirm);
                            String favoritesAdded = getString(R.string.favoritesAdded);
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                            builder.setMessage(addQuestion)
                                    .setTitle(addFavorites).
                                    setNegativeButton(getString(R.string.reject), (dialog, cl) -> {

                                    })
                                    .setPositiveButton(confirm, (dialog, cl) -> {
                                        thread.execute(() -> {
                                            long id = dsDAO.insertSong(song);
                                            song.id = id;
                                            requireActivity().runOnUiThread(() -> {
                                                Toast.makeText(requireActivity(), songName + " " + favoritesAdded, Toast.LENGTH_SHORT).show();
                                            });
                                        });
                                    })
                                    .create().show();
                        });
                    } else {
                        String alreadyFavs = getString(R.string.songInFavorties);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireActivity(), alreadyFavs, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            });

            return binding.getRoot();
        } else {
            // Handle the case where selected is null, return an appropriate view or null
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}




