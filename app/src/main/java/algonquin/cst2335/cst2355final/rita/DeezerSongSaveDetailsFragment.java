package algonquin.cst2335.cst2355final.rita;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.cst2355final.Data.DeezerSongViewModel;
import algonquin.cst2335.cst2355final.R;

import algonquin.cst2335.cst2355final.databinding.SongSaveDetailBinding;

/**
 * DeezerSongDetailsFragment is a fragment that displays details of a selected Deezer song,
 * including the song title, duration, album name, and album cover image.
 */
public class DeezerSongSaveDetailsFragment extends Fragment {
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
     * Default constructor for DeezerSongDetailsFragment.
     */
    public DeezerSongSaveDetailsFragment() {
    }
    /**
     * Constructor for DeezerSongDetailsFragment that takes a DeezerSong object as a parameter.
     *
     * @param s The DeezerSong object to display details for.
     */
    public DeezerSongSaveDetailsFragment(DeezerSong s) {
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

            SongSaveDetailBinding binding = SongSaveDetailBinding.inflate(inflater);

            songName = selected.getName();
            binding.songTitle.setText(selected.title);
            binding.duration.setText(String.valueOf(selected.duration));
            binding.albumName.setText(selected.name);

            queue = Volley.newRequestQueue(requireContext());
            String coverURL = selected.cover;
            queue = Volley.newRequestQueue(requireContext());
            Picasso.get().load(coverURL).into(binding.albumCover);

        return binding.getRoot();
        } else {
            // Handle the case where selected is null, return an appropriate view or null
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }
}




