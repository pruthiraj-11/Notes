package com.bunty.notes.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Notes implements Serializable {

    public Notes() {}

    @PrimaryKey(autoGenerate = true)
    int ID=0;

    @ColumnInfo(name = "title")
    String title="";

    @ColumnInfo(name = "notes")
    String notes="";

    @ColumnInfo(name = "date")
    String date="";

    @ColumnInfo(name = "pinned")
    boolean isPinned=false;

    @ColumnInfo(name = "isOld")
    boolean isOld=false;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isOld() {
        return isOld;
    }

    public void setOld(boolean old) {
        isOld = old;
    }
}
