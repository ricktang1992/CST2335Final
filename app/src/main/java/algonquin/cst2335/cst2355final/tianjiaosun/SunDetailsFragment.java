package algonquin.cst2335.cst2355final.tianjiaosun;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import algonquin.cst2335.cst2355final.databinding.SunDetailsLayoutBinding;

/**
 * A Fragment for displaying the details of a Sun object.
 * This class is responsible for handling the presentation of Sun data, including latitude, longitude, sunrise, and sunset times.
 */
public class SunDetailsFragment extends Fragment {
    Sun selected;

    /**
     * Default constructor for SunDetailsFragment.
     */
    public SunDetailsFragment(){}

    /**
     * Constructor for SunDetailsFragment with a specific Sun object to display.
     *
     * @param toDisplay The Sun object whose details are to be displayed.
     */
    public SunDetailsFragment(Sun toDisplay){
        selected = toDisplay;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This method inflates the layout for the fragment's view and initializes the view with the Sun object's details.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);

        SunDetailsLayoutBinding binding = SunDetailsLayoutBinding.inflate(getLayoutInflater());

        binding.sunLatDetail.setText(selected.sunLatitude);
        binding.sunLngDetail.setText(selected.sunLongitude);

        binding.sunSunriseDetail.setText(selected.sunrise);
        binding.sunSunsetDetail.setText(selected.sunset);

        return binding.getRoot();
    }
}
