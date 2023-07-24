package com.bunty.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bunty.notes.Database.RoomDB;
import com.bunty.notes.Models.Notes;
import com.bunty.notes.databinding.ActivityNotesAdderBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesAdder extends AppCompatActivity {
    ActivityNotesAdderBinding binding;
    Notes notes;
    RoomDB roomDB;
    boolean isOldNote=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding=ActivityNotesAdderBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        notes=new Notes();
        roomDB=RoomDB.getInstance(this);
        try {
            notes= (Notes) getIntent().getSerializableExtra("old_note");
            binding.editTextTitle.setText(notes.getTitle());
            binding.editTextNotes.setText(notes.getNotes());
            isOldNote=true;
        } catch (Exception e){
            e.printStackTrace();
        }
        binding.imagesave.setOnClickListener(v -> {
            String title=binding.editTextTitle.getText().toString();
            String desc=binding.editTextNotes.getText().toString();
//            if(desc.isEmpty()){
//                Toast.makeText(getApplicationContext(),"Please add some text",Toast.LENGTH_SHORT).show();
//                return;
//            }
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE,d MMM yyyy HH:mm a");
            Date date=new Date();
            if(!isOldNote){
                notes=new Notes();
            }
            notes.setTitle(title);
            notes.setNotes(desc);
            notes.setDate(simpleDateFormat.format(date));
            roomDB.mainDAO().isOld(notes.getID(),true);
            Intent intent=new Intent();
            intent.putExtra("note",notes);
            setResult(Activity.RESULT_OK,intent);
            finish();
        });
    }
    @Override
    public void onBackPressed() {
        String title=binding.editTextTitle.getText().toString();
        String desc=binding.editTextNotes.getText().toString();
//        if(desc.isEmpty()){
//            Toast.makeText(getApplicationContext(),"Please add some text",Toast.LENGTH_SHORT).show();
//            return;
//        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE,d MMM yyyy HH:mm a");
        Date date=new Date();
        if(!isOldNote){
            notes=new Notes();
        }
        notes.setTitle(title);
        notes.setNotes(desc);
        notes.setDate(simpleDateFormat.format(date));
        roomDB.mainDAO().isOld(notes.getID(),true);
        Intent intent=new Intent();
        intent.putExtra("note",notes);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}