package algonquin.cst2335.cst2355final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import algonquin.cst2335.cst2355final.databinding.ActivityMainBinding;
//test
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding variableBinding;
    private static String TAG = "MainActivity";
    Intent nextPage;
    Intent dictionaryPage;
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
        Button recipeButton=variableBinding.recipe;
        recipeButton.setOnClickListener(clk->
        {
            CharSequence text = "Going to Recipe Project...";
            Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
            startActivity( nextPage);

        } );

        dictionaryPage = new Intent( MainActivity.this, SearchRoom.class);
        Button dictionaryButton=variableBinding.dictionary;
        dictionaryButton.setOnClickListener(clk->
        {
            CharSequence text = "Going to Dictionary Project...";
            Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
            startActivity( dictionaryPage);

        } );
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId() )
        {
            case R.id.recipeMenu:

                CharSequence text = "Going to Recipe Project...";
                Toast.makeText(this,text, Toast.LENGTH_SHORT).show();
                startActivity( nextPage);
                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                break;
            case R.id.dictionary:

                CharSequence textdictionary = "Going to Dictionary Project...";
                Toast.makeText(this,textdictionary, Toast.LENGTH_SHORT).show();
                startActivity( dictionaryPage);
                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                break;
            case R.id.about:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage("This application is the final project of our class CST2355.\n" +
                                "Team Member: \n" +
                                "Ziyao\n" +
                                "Rita\n" +
                                "Tianjiao\n" +
                                "XingXing").setTitle("About: ")
                        .setNegativeButton("OK", (dialog, cl) -> {
                        }).create().show();
                break;
        }

        return true;
    }
}