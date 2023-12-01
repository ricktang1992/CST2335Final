package algonquin.cst2335.cst2355final.Data;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2335.cst2355final.yuxing.SearchTerm;

/**
 * The {@code SearchViewModel} class serves as the ViewModel for managing UI-related data in the search functionality.
 * It extends {@link ViewModel} and provides LiveData objects to observe and update data in the associated UI components.
 *
 * LiveData objects:
 * - {@link #messages}: A MutableLiveData representing a list of search terms. Observers can be notified when the list is updated.
 * - {@link #selectedMessage}: A MutableLiveData representing a selected search term. Observers can be notified when the selection changes.
 *
 * Usage:
 * - {@code messages} is used to store and observe a list of search terms.
 * - {@code selectedMessage} is used to store and observe the selected search term.
 *
 * Important Notes:
 * - This class is part of the MVVM architecture and is designed to separate UI-related data management from the UI components.
 * - It provides a convenient way to communicate between different parts of the app by observing changes in the LiveData objects.
 * - The class is associated with the search functionality, and its LiveData objects are used to update the UI accordingly.
 *
 * @author Yuxing Xu
 * @version 1.0
 * @since 2023-11-29
 */
public class SearchViewModel extends ViewModel {

    /**
     * MutableLiveData representing a list of search terms. Observers can be notified when the list is updated.
     */
    public MutableLiveData<ArrayList<SearchTerm>> messages = new MutableLiveData<>();

    /**
     * MutableLiveData representing a selected search term. Observers can be notified when the selection changes.
     */
    public MutableLiveData<SearchTerm> selectedMessage = new MutableLiveData<>();
}

