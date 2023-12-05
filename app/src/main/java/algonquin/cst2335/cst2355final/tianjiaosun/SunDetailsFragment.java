package algonquin.cst2335.cst2355final.tianjiaosun;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import algonquin.cst2335.cst2355final.databinding.SunDetailsLayoutBinding;

public class SunDetailsFragment extends Fragment {
    Sun selected;

    // no-argument constructor to prevent app crash
    public SunDetailsFragment(){}
    //constructor for your class which takes a Sun object that it will use as a data source for the TextViews:
    public SunDetailsFragment(Sun toDisplay){
        selected = toDisplay;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);
        //inflate an XML layout for this Fragment
        SunDetailsLayoutBinding binding = SunDetailsLayoutBinding.inflate(getLayoutInflater());

        // set the latitude and longitude views:
        binding.sunLatDetail.setText(selected.sunLatitude);
        binding.sunLngDetail.setText(selected.sunLongitude);

        //set the text views:
        binding.sunSunriseDetail.setText(selected.sunrise);
        binding.sunSunsetDetail.setText(selected.sunset);

        return binding.getRoot();
    }

}
