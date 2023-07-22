package com.bunty.notes.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bunty.notes.Models.Notes;
import com.bunty.notes.NotesOnClickListener;
import com.bunty.notes.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    Context context;
    List<Notes> list;
    NotesOnClickListener notesOnClickListener;

    public NoteAdapter(Context context, List<Notes> list,NotesOnClickListener notesOnClickListener) {
        this.context = context;
        this.list = list;
        this.notesOnClickListener=notesOnClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sanple_notes,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.title.setSelected(true);
        holder.notes_text.setText(list.get(position).getNotes());
        holder.note_date.setText(list.get(position).getDate());
        holder.note_date.setSelected(true);
        if(list.get(position).isPinned()){
            holder.imageView.setImageResource(R.drawable.baseline_push_pin_24);
        }
        else {
            holder.imageView.setImageResource(0);
        }
        int color_code=getRandomColour();
        holder.cardView.setCardBackgroundColor(holder.itemView.getResources().getColor(color_code,null));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesOnClickListener.onClick(list.get(holder.getAdapterPosition()));
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                notesOnClickListener.onLongClick(list.get(holder.getAdapterPosition()),holder.cardView);
                return true;
            }
        });
    }
    private int getRandomColour(){
        int code[]={R.color.color1,R.color.color2,R.color.color3,R.color.color4,R.color.color5};
        Random random=new Random();
        int random_color=random.nextInt(code.length);
        return code[random_color];
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public void filterList(List<Notes> list){
        this.list=list;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView title, note_date, notes_text;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.notes_cont);
            title=itemView.findViewById(R.id.note_title);
            note_date=itemView.findViewById(R.id.note_date);
            notes_text=itemView.findViewById(R.id.notes);
            imageView=itemView.findViewById(R.id.note_pin);
        }
    }
}
