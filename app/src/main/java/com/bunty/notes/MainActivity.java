package com.bunty.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bunty.notes.Adapters.NoteAdapter;
import com.bunty.notes.Database.RoomDB;
import com.bunty.notes.Models.Notes;
import com.bunty.notes.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, ConnectionReceiver.ReceiverListener {
    ActivityMainBinding binding;
    NoteAdapter noteAdapter;
    List<Notes> list=new ArrayList<>();
    RoomDB roomDB;
    Notes clickedNotes;
    FirebaseDatabase database;
    String android_device_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        roomDB=RoomDB.getInstance(this);
        database=FirebaseDatabase.getInstance();
        list=roomDB.mainDAO().getAll();
        update(list);
        binding.fabAdd.setOnClickListener(v -> {
            Intent intent=new Intent(getApplicationContext(), NotesAdder.class);
            startActivityForResult(intent,101);
        });
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                List<Notes> filter_list=new ArrayList<>();
                for(Notes notes:list){
                    if(notes.getTitle().toLowerCase().contains(newText.toLowerCase())||notes.getNotes().toLowerCase().contains(newText.toLowerCase())){
                        filter_list.add(notes);
                    }
                }
                noteAdapter.filterList(filter_list);
                return true;
            }
        });
        android_device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            if(resultCode== Activity.RESULT_OK){
                Notes notes= (Notes) Objects.requireNonNull(data).getSerializableExtra("note");
                if(!Objects.requireNonNull(notes).getTitle().isEmpty()&&!notes.getNotes().isEmpty()){
                    roomDB.mainDAO().insert(notes);
                    list.clear();
                    list.addAll(roomDB.mainDAO().getAll());
                    noteAdapter.notifyDataSetChanged();
                }
//                else {
//                    Toast.makeText(getApplicationContext(),"Blank can't be created",Toast.LENGTH_SHORT).show();
//                }
            }
        } else if (requestCode==102) {
            if(resultCode==Activity.RESULT_OK){
                assert data != null;
                Notes notes= (Notes) data.getSerializableExtra("note");
                roomDB.mainDAO().update(Objects.requireNonNull(notes).getID(),notes.getTitle(),notes.getNotes());
                list.clear();
                list.addAll(roomDB.mainDAO().getAll());
                noteAdapter.notifyDataSetChanged();
            }
        }
    }
    private void update(List<Notes> list) {
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        noteAdapter=new NoteAdapter(MainActivity.this,list,notesOnClickListener);
        binding.recyclerView.setAdapter(noteAdapter);
    }
    private final NotesOnClickListener notesOnClickListener=new NotesOnClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent=new Intent(getApplicationContext(), NotesAdder.class);
            intent.putExtra("old_note",notes);
            startActivityForResult(intent,102);
        }
        @Override
        public void onLongClick(Notes notes, CardView cardView) {
                  clickedNotes=new Notes();
                  clickedNotes=notes;
                  showPopMenu(cardView);
        }
    };
    private void showPopMenu(CardView cardView){
        PopupMenu popupMenu= new PopupMenu(this,cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu);
        popupMenu.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.pin){
            if(clickedNotes.isPinned()){
                roomDB.mainDAO().pin(clickedNotes.getID(),false);
                showMessage("Unpinned");
            }
            else {
                roomDB.mainDAO().pin(clickedNotes.getID(),true);
                showMessage("Pinned");
            }
            list.clear();
            list.addAll(roomDB.mainDAO().getAll());
            noteAdapter.notifyDataSetChanged();
            return true;
        }
        else if(id==R.id.delete){
            database.getReference().child("UserTrashNotes").child(android_device_id).child(String.valueOf(clickedNotes.getID())).setValue(clickedNotes);
            roomDB.mainDAO().delete(clickedNotes);
            list.remove(clickedNotes);
            noteAdapter.notifyDataSetChanged();
//            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
//            final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "Deleted", Snackbar.LENGTH_LONG);
//            snackBar.setAction("Dismiss", v -> snackBar.dismiss());
//            snackBar.show();
            showMessage("Deleted");
            startActivity(new Intent(getApplicationContext(), TrashActivity.class));
            return true;
        } else if (id==R.id.swc) {
            if(checkConnection()){
                database.getReference().getRoot().child("UserNotes").child(android_device_id).child(String.valueOf(clickedNotes.getID())).setValue(clickedNotes);
                showMessage("Saved");
            }
            else {
                showMessage("You are offline!");
            }
            return true;
        } else {
            return false;
        }
    }
    private boolean checkConnection() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new ConnectionReceiver(), intentFilter);
        ConnectionReceiver.Listener = (ConnectionReceiver.ReceiverListener) this;
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        return isConnected;
    }
    public void onNetworkChange(boolean isConnected) {
        showMessage("You are offline!");
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> MainActivity.super.onBackPressed()).create().show();
    }
    public void showMessage(String message){
        final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackBar.setAction("Dismiss", v -> snackBar.dismiss());
        snackBar.show();
    }
}