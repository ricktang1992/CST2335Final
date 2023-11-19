package algonquin.cst2335.cst2355final;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

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
        //binding.recipeInfoSum.setText(selected.getImage());
        String iconUrl=selected.getImage();
        RequestQueue queue = null;
        queue = Volley.newRequestQueue(getActivity());
        ImageRequest imgReq2 = new ImageRequest(iconUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                // Do something with loaded bitmap...

                try {
                    binding.imageRecipe.setImageBitmap(bitmap);
                    Log.e("ImageRequest", " loading image: " + iconUrl);
                } catch (Exception e) {

                }
                Log.e("ImageRequest", " loading image: " + iconUrl);

            }
        }, 1024, 1024, ImageView.ScaleType.CENTER, null, (error) -> {
            Log.e("ImageRequest", "Error loading image: " + error.getMessage());
        });
        String stringURL="https://api.spoonacular.com/recipes/"
                +selected.getWebsiteID()
                +"/information?apiKey=670608d3fd1e4b15b120493cad68231a";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringURL, null,
                (response) -> {
                    try {
                        String summary=response.getString("summary");
                        Log.d("WeatherResponse", "summary: " + summary);
                        binding.recipeInfoSum.setText(summary);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                (error) -> {
                });
        queue.add(request);
        queue.add(imgReq2);

        return binding.getRoot();
    }
}
