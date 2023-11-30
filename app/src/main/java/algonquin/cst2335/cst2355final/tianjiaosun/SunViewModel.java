package algonquin.cst2335.cst2355final.tianjiaosun;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;


public class SunViewModel extends ViewModel{
    public MutableLiveData<ArrayList<Sun>> suns = new MutableLiveData<>();
    public MutableLiveData<Sun> selectedSun = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Sun>> favSuns = new MutableLiveData<>();

}
