package com.bunty.notes;

import androidx.cardview.widget.CardView;

import com.bunty.notes.Models.Notes;

public interface NotesOnClickListener {
    void onClick(Notes notes);
    void onLongClick(Notes notes, CardView cardView);
}
