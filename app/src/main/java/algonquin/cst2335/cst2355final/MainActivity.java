package algonquin.cst2335.cst2355final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;


import algonquin.cst2335.cst2355final.databinding.ActivityMainBinding;
import algonquin.cst2335.cst2355final.rita.DeezerAlbum;
import algonquin.cst2335.cst2355final.tianjiaosun.SunActivity;
import algonquin.cst2335.cst2355final.yuxing.SearchRoom;
import algonquin.cst2335.cst2355final.ziyao.RecipeSearch;

//test
/**
 * This is the main activity of the CST2355 Final Project.
 * It serves as the entry point to various features of the application.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Binding object for the main activity layout.
     */
    private ActivityMainBinding variableBinding;

    /**
     * Intent for navigating to the recipe page.
     */
    Intent nextPage;
    /**
     * Intent for navigating to the song page.
     */
    Intent songPage;
    /**
     * Intent for navigating to the dictionary page.
     */
    Intent dictionaryPage;
    /**
     * Intent for navigating to the sun page.
     */
    Intent sunPage;

    /**
     * Initializes the options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return true for the menu to be displayed; false for it to be hidden.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }
    /**
     * Initializes the activity and sets up the layout.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        variableBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(variableBinding.getRoot());
        setSupportActionBar( variableBinding.myToolbar);
        // Initialize Intent objects for different activities
        nextPage = new Intent( MainActivity.this, RecipeSearch.class);
        songPage = new Intent( MainActivity.this, DeezerAlbum.class);
        sunPage = new Intent( MainActivity.this, SunActivity.class);


        Button recipeButton=variableBinding.recipe;
        Button songButton = variableBinding.deezer;
// Set up click listeners for buttons
        recipeButton.setOnClickListener(clk->
        {
            String goingToRecipe = getResources().getString(R.string.goingTorecipeApp);
            CharSequence goingToRecipe1 = goingToRecipe;
            Toast.makeText(this,goingToRecipe1, Toast.LENGTH_SHORT).show();
            startActivity( nextPage);

        } );

// Set up click listeners for buttons
        songButton.setOnClickListener(clk->
        {
            String goingToSongApp = getResources().getString(R.string.goingTodeezerApp);
            CharSequence goingToSongApp1 = goingToSongApp;
            Toast.makeText(this,goingToSongApp1, Toast.LENGTH_SHORT).show();
            startActivity( songPage);

        } );


        dictionaryPage = new Intent( MainActivity.this, SearchRoom.class);
        Button dictionaryButton=variableBinding.dictionary;
        // Set up click listeners for buttons
        dictionaryButton.setOnClickListener(clk->
        {
            String goingToDictionaryApp = getResources().getString(R.string.goingTodictionaryApp);
            CharSequence goingToDictionaryApp1 = goingToDictionaryApp;
            Toast.makeText(this,goingToDictionaryApp1, Toast.LENGTH_SHORT).show();
            startActivity( dictionaryPage);

        } );

        sunPage = new Intent( MainActivity.this, SunActivity.class);
        Button sunrise=variableBinding.sunrise;
        // Set up click listeners for buttons
        sunrise.setOnClickListener(clk->
        {
            String goingToSunApp = getResources().getString(R.string.goingTosunApp);
            CharSequence goingToSunApp1 = goingToSunApp;
            Toast.makeText(this,goingToSunApp1, Toast.LENGTH_SHORT).show();
            startActivity( sunPage);

        } );

    }
    /**
     * Handles options menu item selection.
     *
     * @param item The menu item that was selected.
     * @return true to indicate that the event has been consumed.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId() )
        {
            case R.id.recipeMenu:

                String goingToRecipe = getResources().getString(R.string.goingTorecipeApp);
                CharSequence goingToRecipe1 = goingToRecipe;
                Toast.makeText(this,goingToRecipe1, Toast.LENGTH_SHORT).show();
                startActivity( nextPage);
                break;
            case R.id.sunMenu:

                String goingToSunApp = getResources().getString(R.string.goingTosunApp);
                CharSequence goingToSunApp1 = goingToSunApp;
                Toast.makeText(this,goingToSunApp1, Toast.LENGTH_SHORT).show();
                startActivity( sunPage);
                break;
            case R.id.deezer:

                String goingToSongApp = getResources().getString(R.string.goingTodeezerApp);
                CharSequence goingToSongApp1 = goingToSongApp;
                Toast.makeText(this,goingToSongApp1, Toast.LENGTH_SHORT).show();
                startActivity( songPage);
                break;
            case R.id.dictionary:

                String goingToDictionaryApp = getResources().getString(R.string.goingTodictionaryApp);
                CharSequence goingToDictionaryApp1 = goingToDictionaryApp;
                Toast.makeText(this,goingToDictionaryApp1, Toast.LENGTH_SHORT).show();
                startActivity( dictionaryPage);
                break;

            case R.id.about:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage(getResources().getString(R.string.aboutDetail)).setTitle(getResources().getString(R.string.ziyaoAbout)+":")
                        .setNegativeButton(getResources().getString(R.string.ziyaoOK), (dialog, cl) -> {
                        }).create().show();
                break;
        }

        return true;
    }
}