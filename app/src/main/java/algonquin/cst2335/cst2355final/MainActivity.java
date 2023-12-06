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
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding variableBinding;
    private static String TAG = "MainActivity";
    Intent nextPage;

    Intent songPage;

    Intent dictionaryPage;
    Intent sunPage;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        variableBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(variableBinding.getRoot());
        setSupportActionBar( variableBinding.myToolbar);
        nextPage = new Intent( MainActivity.this, RecipeSearch.class);
        songPage = new Intent( MainActivity.this, DeezerAlbum.class);
        sunPage = new Intent( MainActivity.this, SunActivity.class);


        Button recipeButton=variableBinding.recipe;
        Button songButton = variableBinding.deezer;

        recipeButton.setOnClickListener(clk->
        {
            String goingToRecipe = getResources().getString(R.string.goingTorecipeApp);
            CharSequence goingToRecipe1 = goingToRecipe;
            Toast.makeText(this,goingToRecipe1, Toast.LENGTH_SHORT).show();
            startActivity( nextPage);

        } );


        songButton.setOnClickListener(clk->
        {
            String goingToSongApp = getResources().getString(R.string.goingTodeezerApp);
            CharSequence goingToSongApp1 = goingToSongApp;
            Toast.makeText(this,goingToSongApp1, Toast.LENGTH_SHORT).show();
            startActivity( songPage);

        } );


        dictionaryPage = new Intent( MainActivity.this, SearchRoom.class);
        Button dictionaryButton=variableBinding.dictionary;
        dictionaryButton.setOnClickListener(clk->
        {
            String goingToDictionaryApp = getResources().getString(R.string.goingTodictionaryApp);
            CharSequence goingToDictionaryApp1 = goingToDictionaryApp;
            Toast.makeText(this,goingToDictionaryApp1, Toast.LENGTH_SHORT).show();
            startActivity( dictionaryPage);

        } );

        sunPage = new Intent( MainActivity.this, SunActivity.class);
        Button sunrise=variableBinding.sunrise;
        sunrise.setOnClickListener(clk->
        {
            String goingToSunApp = getResources().getString(R.string.goingTosunApp);
            CharSequence goingToSunApp1 = goingToSunApp;
            Toast.makeText(this,goingToSunApp1, Toast.LENGTH_SHORT).show();
            startActivity( sunPage);

        } );

    }
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