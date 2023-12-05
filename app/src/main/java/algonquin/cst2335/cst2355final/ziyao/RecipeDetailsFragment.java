package algonquin.cst2335.cst2355final.ziyao;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
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

import org.json.JSONException;

import algonquin.cst2335.cst2355final.databinding.RecipeInfoBinding;
import algonquin.cst2335.cst2355final.ziyao.Recipe;


/**
 * Fragment class responsible for displaying detailed information about a selected Recipe.
 */
public class RecipeDetailsFragment extends Fragment {

    /**
     * The selected Recipe to display details for.
     */
    Recipe selected;

    /**
     * Constructs a new RecipeDetailsFragment with the specified Recipe.
     *
     * @param recipe The Recipe object to display details for.
     */
    public  RecipeDetailsFragment(Recipe recipe){
        selected =recipe;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        // Inflating the layout using data binding
        RecipeInfoBinding binding = RecipeInfoBinding.inflate(inflater);

        // Setting the title of the recipe
        binding.recipeInfoTitle.setText(selected.getTitle());

        // Setting up an ImageRequest to load and display the recipe image
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
                    // Handle exceptions related to image loading
                }
                Log.e("ImageRequest", " loading image: " + iconUrl);
//test
            }
        }, 1024, 1024, ImageView.ScaleType.CENTER, null, (error) -> {
            Log.e("ImageRequest", "Error loading image: " + error.getMessage());
        });

        // Setting up a JsonObjectRequest to fetch and display additional information about the recipe
        String stringURL="https://api.spoonacular.com/recipes/"
                +selected.getWebsiteID()
                +"/information?apiKey=670608d3fd1e4b15b120493cad68231a";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringURL, null,
                (response) -> {
                    try {
                        String summary=response.getString("summary");

                        Spanned spannedsummary= Html.fromHtml(summary,Html.FROM_HTML_MODE_LEGACY);
                        Log.d("WeatherResponse", "summary: " + summary);
                        binding.recipeInfoSum.setText(spannedsummary);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                (error) -> {
                    // Handle errors in fetching recipe information
                });
        queue.add(request);
        queue.add(imgReq2);

        // Returning the root view of the fragment
        return binding.getRoot();
    }
}
