package algonquin.cst2335.cst2355final;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class DeezerSongViewModel extends ViewModel {
    public MutableLiveData<ArrayList<DeezerSong>> songs = new MutableLiveData<>();

    public MutableLiveData<DeezerSong> selectedSong = new MutableLiveData<>();
}
