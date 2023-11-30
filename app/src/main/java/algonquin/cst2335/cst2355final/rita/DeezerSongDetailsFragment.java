package algonquin.cst2335.cst2355final.rita;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import algonquin.cst2335.cst2355final.databinding.SongDetailBinding;


public class DeezerSongDetailsFragment extends Fragment {

    DeezerSong selected;
    RequestQueue queue; //create a Volley object that will connect to a server

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

            return binding.getRoot();
            } else {
            // Handle the case where selected is null, return an appropriate view or null
            return super.onCreateView(inflater, container, savedInstanceState);

        }
    }

}
