package algonquin.cst2335.cst2355final.Data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2335.cst2355final.Tianjiao.SunTerm;

public class SunViewModel extends ViewModel {
    public MutableLiveData<ArrayList<SunTerm>> messages = new MutableLiveData< >(null);
    public MutableLiveData<SunTerm> selectedMessage = new MutableLiveData< >();

}
