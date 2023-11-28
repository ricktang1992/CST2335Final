package algonquin.cst2335.cst2355final.yuxing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.cst2355final.Data.SearchViewModel;
import algonquin.cst2335.cst2355final.R;
import algonquin.cst2335.cst2355final.databinding.SearchMessageBinding;
import algonquin.cst2335.cst2355final.databinding.SearchSavedBinding;


public class SearchSaved extends AppCompatActivity {
    SearchSavedBinding binding;
    ArrayList<SearchTerm> messages = new ArrayList<>();

    SearchViewModel saveModel ;
    SearchTermDAO mDAO;

    private RecyclerView.Adapter savedAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SearchSavedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        saveModel = new ViewModelProvider(this).get(SearchViewModel.class);
        saveModel.selectedMessage.observe(this, (selectedMessage) -> {
            SearchDetailsFragment newFragment = new SearchDetailsFragment(selectedMessage);

            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            tx.addToBackStack("");
            tx.replace(R.id.yuxingframeLayout, newFragment);
            tx.commit();
        });
        SearchDatabase db = Room.databaseBuilder(getApplicationContext(), SearchDatabase.class, "yuxingDictionary").build();
        mDAO = db.searchTermDao();
        messages = saveModel.messages.getValue();
        //get data from Database
        if(messages == null)
        {
            saveModel.messages.setValue(messages = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                messages.addAll( mDAO.getAllSearchTerms() ); //Once you get the data from database

                runOnUiThread( () ->  binding.yuxingsavedRecyclerView.setAdapter( savedAdapter )); //You can then load the RecyclerView
            });
        }

        binding.yuxingsavedRecyclerView.setAdapter(savedAdapter = new RecyclerView.Adapter<SearchSaved.MyRowHolder>() {
            @NonNull
            @Override
            public SearchSaved.MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                SearchMessageBinding binding = SearchMessageBinding.inflate(getLayoutInflater(), parent, false);
                return new SearchSaved.MyRowHolder(binding.getRoot());// getRoot returns a ConstraintLayout with TextViews inside
            }


            @Override
            public void onBindViewHolder(@NonNull SearchSaved.MyRowHolder holder, int position) {
                SearchTerm obj = messages.get(position);
                holder.messageText.setText(obj.getTerm());
//              recylerview message format
                holder.timeText.setText(obj.getTimeSent());
            }

            @Override
            public int getItemCount() {
                return messages.size();
            }
        });

        binding.yuxingsavedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    class MyRowHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView timeText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                SearchTerm selected = messages.get(position);

                saveModel.selectedMessage.postValue(selected);
            });
            messageText = itemView.findViewById(R.id.yuxingTermWord);
            timeText =itemView.findViewById(R.id.yuxingSearchtime);
        }
    }

}