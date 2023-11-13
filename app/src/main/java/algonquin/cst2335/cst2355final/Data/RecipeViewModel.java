package algonquin.cst2335.cst2355final.Data;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2335.cst2355final.Recipe;


public class RecipeViewModel extends ViewModel {
    public MutableLiveData<ArrayList<Recipe>> recipes = new MutableLiveData< >();
    public MutableLiveData<Recipe> selectedRecipe = new MutableLiveData<>();
}