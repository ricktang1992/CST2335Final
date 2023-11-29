package algonquin.cst2335.cst2355final.yuxing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import algonquin.cst2335.cst2355final.databinding.SearchDetailsLayoutBinding;
import algonquin.cst2335.cst2355final.databinding.SongDetailBinding;


/**
 *
 * The detailed information includes the term itself, the time it was sent, and its definition.
 * The information is displayed using a custom layout defined in the {@link SearchDetailsLayoutBinding}.
 *
 * @author Yuxing Xu
 * @version 1.0
 * @since 2023-11-29
 */
public class SearchDetailsFragment extends Fragment {

    /**
     * The selected {@link SearchTerm} to display detailed information.
     */
    SearchTerm selected;

    /**
     * Constructs a new instance of the {@code SearchDetailsFragment}.
     *
     * @param m The selected {@link SearchTerm} to display detailed information.
     */
    public SearchDetailsFragment(SearchTerm m){
        selected = m;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view
     *                           itself, but this can be used to generate the LayoutParams of the
     *                           view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout using the generated binding class
        SearchDetailsLayoutBinding binding = SearchDetailsLayoutBinding.inflate(inflater);

        // Set the displayed information based on the selected search term
        binding.yuxingTermView.setText(selected.term);
        binding.timeView.setText(selected.timeSent);
        binding.yuxingDefinitionView.setText(selected.definition);

        return binding.getRoot();
    }
}
