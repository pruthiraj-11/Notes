package com.bunty.notes.Database;

import static androidx.room.OnConflictStrategy.REPLACE;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.bunty.notes.Models.Notes;

import java.util.List;

@Dao
public interface MainDAO {

    @Insert(onConflict = REPLACE)
    void insert(Notes notes);

    @Query("select *from notes order by ID desc")
    List<Notes> getAll();

    @Query("update notes set title=:title, notes=:notes where ID=:id")
    void update(int id, String title, String notes);

    @Delete
    void delete(Notes notes);

    @Query("update notes set pinned=:pin where id=:id")
    void pin(int id, boolean pin);

    @Query("update notes set isOld=:isOld where id=:id")
    void isOld(int id,boolean isOld);
}
