package algonquin.cst2335.cst2355final.rita;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import algonquin.cst2335.cst2355final.Data.DeezerSongViewModel;
import algonquin.cst2335.cst2355final.databinding.SongDetailBinding;


public class DeezerSongDetailsFragment extends Fragment {

    DeezerSong selected;
    RequestQueue queue; //create a Volley object that will connect to a server
    protected DeezerSongViewModel songModel; // Initialize a ViewModel
    ArrayList<DeezerSong> savedSong = new ArrayList<>();// Create an ArrayList to store messages


    public DeezerSongDetailsFragment(){}

    public DeezerSongDetailsFragment(DeezerSong s){
        selected = s;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (selected != null) {

            SongDetailBinding binding = SongDetailBinding.inflate(inflater);

            binding.songTitle.setText(selected.title);
            binding.duration.setText(String.valueOf(selected.duration));
            binding.albumName.setText(selected.name);

            queue = Volley.newRequestQueue(requireContext());
            String coverURL = selected.cover;
            queue = Volley.newRequestQueue(requireContext());

            // Use Picasso to load and display the image
            Picasso.get().load(coverURL).into(binding.albumCover);

            Button saveButton = binding.saveSongButton;

            songModel = new ViewModelProvider(this).get(DeezerSongViewModel.class); // Initialize ViewModel

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Save the selected song to the database
                    savedSong.add(selected);

                    // Convert the list of saved songs to a JSON array
                    JSONArray jsonArray = new JSONArray();
                    for (DeezerSong song: savedSong) {
                        JSONObject jsonSong = new JSONObject();
                        try {
                            jsonSong.put("title", song.getTitle());
                            jsonSong.put("name", song.getName());
                            jsonSong.put("duration", song.getDuration());
                            jsonSong.put("cover", song.getCover());
                            jsonArray.put(jsonSong);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // Save the JSON array to SharedPreferences
                    SharedPreferences preferences = requireContext().getSharedPreferences("SavedSongs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("savedSongs", jsonArray.toString());
                    editor.apply();
                }
            });

            return binding.getRoot();
            } else {
            // Handle the case where selected is null, return an appropriate view or null
            return super.onCreateView(inflater, container, savedInstanceState);

        }
    }

}
