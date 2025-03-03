package com.example.cq_mobile.ui.home.HomeFolder.NotesNFilesAPI_folder.NotesFolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {
    private List<Note> notesList;
    NotesActivity notesActivity;
    public NotesAdapter(List<Note> notesList, NotesActivity notesActivity) {
        this.notesList = notesList;
        this.notesActivity = notesActivity;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Note note = notesList.get(position);
        holder.noteTitle.setText(note.getUser().getName());
        holder.noteContent.setText(note.getNote());
        holder.note_timeStamp.setText(note.getDate());

        Glide.with(holder.itemView.getContext())
                .load(note.getUser().getAvatar())
                .circleCrop()
                .placeholder(R.drawable.baseline_circle)
                .error(R.drawable.emptyglide)
                .into(holder.noteImage);
    }


    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public void addData(List<Note> newNotes) {
        int startPos = notesList.size();
        notesList.addAll(newNotes);
        notifyItemRangeInserted(startPos, newNotes.size());
    }


    static class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent,note_timeStamp;
        CircleImageView noteImage;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.note_name);
            noteContent = itemView.findViewById(R.id.note_content);
            noteImage = itemView.findViewById(R.id.image1);
            note_timeStamp = itemView.findViewById(R.id.note_timeStamp);
        }
    }
}
