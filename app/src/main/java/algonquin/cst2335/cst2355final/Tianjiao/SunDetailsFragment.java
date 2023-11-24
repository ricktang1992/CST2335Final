package algonquin.cst2335.cst2355final.Tianjiao;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import algonquin.cst2335.cst2355final.databinding.SearchDetailsLayoutBinding;

public class SunDetailsFragment extends Fragment {

    SunTerm selected;

    public SunDetailsFragment(SunTerm m){
        selected = m;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        SearchDetailsLayoutBinding binding = SearchDetailsLayoutBinding.inflate(inflater);

        binding.yuxingTermView.setText(selected.term);
        binding.timeView.setText(selected.timeSent);
        binding.yuxingDefinitionView.setText("Id = " + selected.id);
        return binding.getRoot();
    }
}
