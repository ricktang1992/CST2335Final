package algonquin.cst2335.cst2355final;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import algonquin.cst2335.cst2355final.databinding.DetailsLayoutBinding;

public class SongsDetailsFragment extends Fragment {

    DeezerSong selected;

    public SongsDetailsFragment(){}

    public SongsDetailsFragment(DeezerSong s){
        selected = s;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // No need to call super.onCreateView(inflater, container, savedInstanceState);

        if (selected != null) {
            DetailsLayoutBinding binding = DetailsLayoutBinding.inflate(inflater);

            binding.songTitle.setText(selected.title);
            binding.duration.setText(selected.duration);
            binding.albumName.setText(selected.name);
            binding.albumCover.setText(selected.cover);

            return binding.getRoot();

        } else {
            // Handle the case where selected is null, return an appropriate view or null
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

}
