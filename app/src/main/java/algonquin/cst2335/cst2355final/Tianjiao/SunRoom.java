package algonquin.cst2335.cst2355final.Tianjiao;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import algonquin.cst2335.cst2355final.Data.SunViewModel;
import algonquin.cst2335.cst2355final.R;
import algonquin.cst2335.cst2355final.databinding.SunMessageBinding;
import algonquin.cst2335.cst2355final.databinding.SunRoomBinding;

public class SunRoom extends AppCompatActivity {
    SunRoomBinding binding;
    ArrayList<SunTerm> texts1 = new ArrayList<>();

    SunViewModel sunModel ;
    SunTermDAO mDAO;
    private RecyclerView.Adapter sunAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sunModel = new ViewModelProvider(this).get(SunViewModel.class);
        sunModel.selectedMessage.observe(this, (sundMessage) -> {
            SunDetailsFragment newFragment = new SunDetailsFragment(sundMessage);

            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();

            tx.addToBackStack("");
            tx.replace(R.id.sunframeLayout, newFragment);
            tx.commit();

        });

        SunDatabase db = Room.databaseBuilder(getApplicationContext(), SunDatabase.class, "databaseFileOnPhone").build();
        mDAO = db.sunTermDao();

        texts1 = sunModel.messages.getValue();
        if(texts1 == null)
        {
            sunModel.messages.setValue(texts1 = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                texts1.addAll( mDAO.getAllSunTerms() ); //Once you get the data from database

                runOnUiThread( () ->  binding.recyclerView.setAdapter( sunAdapter )); //You can then load the RecyclerView
            });
        }


        //database


        binding = SunRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        //SharedPreferences to save something about what was typed in the EditText for use the next time
        SharedPreferences prefs = getSharedPreferences("searchHistory", Context.MODE_PRIVATE);
        AtomicReference<EditText> sunText = new AtomicReference<>(binding.editTextSearch );
       AtomicReference<EditText> sunsText = new AtomicReference<>(binding.latitudeSearch );


        binding.btnSearch.setOnClickListener(clk -> {

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("sunText", sunText.get().getText().toString() );
//            editor.putString("sunsText", sunsText.get().getText().toString() );

            editor.apply();
      //      SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
      //      String currentDateandTime = sdf.format(new Date());
            String inputMessage = binding.editTextSearch.getText().toString();
//           String inputMessages = binding.latitudeSearch.getText().toString();

            SunTerm thisMessage = new SunTerm(inputMessage,"00000");
            texts1.add(thisMessage);

            // clear teh previous text
            binding.editTextSearch.setText("");
            sunAdapter.notifyDataSetChanged();

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(new Runnable() {
                @Override
                public void run() {
                    long id = mDAO.insertSunTerm(thisMessage);
                    thisMessage.id = id;
                }
            });


            runOnUiThread(() ->{sunAdapter.notifyItemInserted(texts1.size() - 1);});

            binding.editTextSearch.setText("");
        });

        sunText.get().setText(prefs.getString("searchText",""));
        sunModel.selectedMessage.observe(this, (newMessageValue) -> {
            SunDetailsFragment sunframeLayout = new SunDetailsFragment( newMessageValue );
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            tx.addToBackStack("back");
            tx.replace(R.id.sunframeLayout,sunframeLayout);
            tx.commit();
        });

        binding.recyclerView.setAdapter(sunAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                SunMessageBinding binding = SunMessageBinding.inflate(getLayoutInflater(), parent, false);

                    return new MyRowHolder(binding.getRoot()); // getRoot returns a ConstraintLayout with TextViews inside


            }


            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                SunTerm obj = texts1.get(position);
                holder.messageText.setText(obj.getTerm());
//                holder.messageText.setText("");
                holder.timeText.setText(obj.getTimeSent());

            }

            @Override
            public int getItemCount() {
                return texts1.size();
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.tianjiao_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch( item.getItemId() )
        {
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to delete the selected message?");
                builder.setTitle("Delete Message")
                        .setPositiveButton("Yes", (dialog, cl) -> {
                            // Get the selected message
                            SunTerm selectedMessage = sunModel.selectedMessage.getValue();
                            if (selectedMessage != null) {
                                // Delete the message from the database
                                Executors.newSingleThreadExecutor().execute(() -> {
                                    mDAO.deleteMessage(selectedMessage);
                                });

                                // Remove the message from the list and update the RecyclerView
                                texts1.remove(selectedMessage);
                                sunAdapter.notifyDataSetChanged();

                                // Show a Snackbar with an undo option
                                Snackbar.make(binding.getRoot(), "Message deleted", Snackbar.LENGTH_LONG)
                                        .setAction("Undo", clk2 -> {
                                            // Insert the deleted message back to the database
                                            Executors.newSingleThreadExecutor().execute(() -> {
                                                long id = mDAO.insertSunTerm(selectedMessage);
                                                selectedMessage.id = id;
                                                runOnUiThread(() -> {
                                                    // Add the message back to the list and update the RecyclerView
                                                    texts1.add(selectedMessage);
                                                    sunAdapter.notifyDataSetChanged();
                                                });
                                            });
                                        })
                                        .show();
                            }
                        })
                        .setNegativeButton("No", (dialog, cl) -> {})
                        .create().show();
                break;

            case R.id.saveTheTERM:

                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                break;
            case R.id.about:

                //put your ChatMessage deletion code here. If you select this item, you should show the alert dialog
                //asking if the user wants to delete this message.
                Toast.makeText(this,"Version 1.0, Created by Tianjiao Feng", Toast.LENGTH_LONG).show();
                break;
        }

        return true;
    }


    class MyRowHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView timeText;

        public MyRowHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(clk -> {
                int position = getAbsoluteAdapterPosition();
                SunTerm selected = texts1.get(position);

                sunModel.selectedMessage.postValue(selected);
            });
            messageText = itemView.findViewById(R.id.message);
            timeText =itemView.findViewById(R.id.time);
        }
    }
}
