package algonquin.cst2335.cst2355final;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import algonquin.cst2335.cst2355final.databinding.RecipeInfoBinding;

public class RecipeDetailsFragment extends Fragment {
    Recipe selected;
    public  RecipeDetailsFragment(Recipe recipe){
        selected =recipe;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        RecipeInfoBinding binding = RecipeInfoBinding.inflate(inflater);
        binding.recipeInfoTitle.setText(selected.getTitle());


        return binding.getRoot();
    }
}
