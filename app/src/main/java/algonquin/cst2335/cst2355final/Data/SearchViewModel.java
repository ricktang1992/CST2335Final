package algonquin.cst2335.cst2355final.Data;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2335.cst2355final.SearchTerm;

public class SearchViewModel extends ViewModel {
    public MutableLiveData<ArrayList<SearchTerm>> messages = new MutableLiveData< >(null);
    public MutableLiveData<SearchTerm> selectedMessage = new MutableLiveData< >();

}
