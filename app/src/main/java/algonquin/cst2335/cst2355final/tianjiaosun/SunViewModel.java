package algonquin.cst2335.cst2355final.tianjiaosun;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

/**
 * ViewModel for managing and storing UI-related data for the Sun objects in a lifecycle-conscious way.
 * This class holds the data related to the list of Sun objects and a selected Sun object, ensuring that the data survives configuration changes such as screen rotations.
 *
 * Author: Tianjiao Feng
 */
public class SunViewModel extends ViewModel {

    /**
     * LiveData for a list of Sun objects.
     * This MutableLiveData holds and observes the list of Sun objects for changes.
     */
    public MutableLiveData<ArrayList<Sun>> suns = new MutableLiveData<>();

    /**
     * LiveData for a single selected Sun object.
     * This MutableLiveData holds and observes the selected Sun object for changes.
     */
    public MutableLiveData<Sun> selectedSun = new MutableLiveData<>();
}
