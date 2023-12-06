package algonquin.cst2335.cst2355final.Data;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2335.cst2355final.ziyao.Recipe;


/**
 * ViewModel class for managing Recipe data in the application.
 *
 * This class extends AndroidX ViewModel and is responsible for holding and managing the
 * UI-related data in a lifecycle-conscious way. It is associated with the RecipeFragment
 * and communicates with the underlying data source to provide data to the UI.
 *
 * This ViewModel contains MutableLiveData instances for observing changes in the list of recipes
 * and the currently selected recipe. These LiveData objects can be observed by UI components
 * to update the UI when the underlying data changes.
 *
 * @see androidx.lifecycle.ViewModel
 * @see androidx.lifecycle.MutableLiveData
 * @see algonquin.cst2335.cst2355final.ziyao.Recipe
 */
public class RecipeViewModel extends ViewModel {

    /**
     * MutableLiveData for the list of recipes. Observing this LiveData allows UI components
     * to update when the list of recipes changes.
     */
    public MutableLiveData<ArrayList<Recipe>> recipes = new MutableLiveData<>();

    /**
     * MutableLiveData for the currently selected recipe. Observing this LiveData allows UI components
     * to update when the selected recipe changes.
     */
    public MutableLiveData<Recipe> selectedRecipe = new MutableLiveData<>();
}
