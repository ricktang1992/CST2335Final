package algonquin.cst2335.cst2355final.Data;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import algonquin.cst2335.cst2355final.rita.DeezerSong;

/**
 * ViewModel class for managing DeezerSong data in the application.
 *
 * This class extends AndroidX ViewModel and is responsible for holding and managing the
 * UI-related data in a lifecycle-conscious way. It is associated with the DeezerSongFragment
 * and communicates with the underlying data source to provide data to the UI.
 *
 * This ViewModel contains MutableLiveData instances for observing changes in the list of songs,
 * the currently selected song, and the list of favorite songs. These LiveData objects can be
 * observed by UI components to update the UI when the underlying data changes.
 *
 * @see androidx.lifecycle.ViewModel
 * @see androidx.lifecycle.MutableLiveData
 * @see algonquin.cst2335.cst2355final.rita.DeezerSong
 */
public class DeezerSongViewModel extends ViewModel {

    /**
     * MutableLiveData for the list of Deezer songs. Observing this LiveData allows UI components
     * to update when the list of songs changes.
     */
    public MutableLiveData<ArrayList<DeezerSong>> songs = new MutableLiveData<>();

    /**
     * MutableLiveData for the currently selected Deezer song. Observing this LiveData allows UI
     * components to update when the selected song changes.
     */
    public MutableLiveData<DeezerSong> selectedSong = new MutableLiveData<>();

    /**
     * MutableLiveData for the list of favorite Deezer songs. Observing this LiveData allows UI
     * components to update when the list of favorite songs changes.
     */
    public MutableLiveData<ArrayList<DeezerSong>> favoriteSongsArray = new MutableLiveData<>();
}
