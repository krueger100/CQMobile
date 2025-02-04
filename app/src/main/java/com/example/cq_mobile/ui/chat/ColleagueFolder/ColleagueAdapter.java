package com.example.cq_mobile.ui.chat.ColleagueFolder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ColleagueAdapter extends RecyclerView.Adapter<ColleagueAdapter.ColleagueViewHolder> {
    private static final String TAG = "ColleagueAdapter";
    private List<ColleagueAPIItem> colleagueList;
    private Context context;

    public ColleagueAdapter(Context context, List<ColleagueAPIItem> colleagueList) {
        this.context = context;
        this.colleagueList = colleagueList != null ? colleagueList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ColleagueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called for viewType: " + viewType);
        View view = LayoutInflater.from(context).inflate(R.layout.item_colleague, parent, false);
        return new ColleagueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColleagueViewHolder holder, int position) {
        ColleagueAPIItem colleagueItem = colleagueList.get(position);
        Gson gson = new Gson();

        // Access the contacts list from ColleagueAPIItem
        List<Contact> contacts = colleagueItem.getContacts();

        if (contacts != null && !contacts.isEmpty()) {
            // You can access the contacts data here
            for (Contact contact : contacts) {
                String contactName = contact.getName();
                String contactEmail = contact.getEmail();
                String contactAvatar = contact.getAvatar_path();

                // Use this data to display information about the contact
                Log.d(TAG, "Contact Name: " + contactName);
                Log.d(TAG, "Contact Email: " + contactEmail);
                Log.d(TAG, "Contact Avatar: " + contactAvatar);
            }
        }

        // If there is no member data, set a default text
        holder.colleagueName.setText(colleagueItem.getName() != null ? colleagueItem.getName() : "Unknown Colleague");

        // For logging purposes
        String chatMembers = gson.toJson(colleagueItem.getMembers() != null ? colleagueItem.getMembers() : "No Member Found");
        Log.d(TAG, "Members Item at position " + position + ": " + chatMembers);

        String colleagueAPIData = gson.toJson(colleagueItem);
        Log.d(TAG, "Colleague Item at position " + position + ": " + colleagueAPIData);

        String chatAPIData = gson.toJson(colleagueItem);
        Log.d(TAG, "Colleague Item at position " + position + ": " + chatAPIData);
        Log.d(TAG, "Colleague Message ----- Count     " + position + ": " + colleagueList.size());
        Log.d(TAG, "Colleague Message ----- IsRead   " + position + ": " + colleagueItem.getMessage_read());
        Log.d(TAG, "Colleague Message ----- Name   " + position + ": " + colleagueItem.getName());
        Log.d(TAG, "Colleague Message ----- SENDER Name    " + position + ": " + colleagueItem.getName());
        Log.d(TAG, "Colleague Message ----- RECIEVER Name    " + position + ": " + colleagueItem.getChat_name());

        // Load the avatar for the colleague
        if (colleagueItem.getAvatar_path() != null && !colleagueItem.getAvatar_path().isEmpty()) {
            Glide.with(context)

                    .load(colleagueItem.getAvatar_path())
                    .placeholder(R.drawable.circular_background)
                    .error(R.drawable.emptyglide)
                    .into(holder.colleagueAvatar);
        } else {
            holder.colleagueAvatar.setImageResource(R.drawable.emptyglide);
        }
    }


    @Override
    public int getItemCount() {
        return colleagueList.size();
    }

    public void addColleagues(List<ColleagueAPIItem> newColleagues) {
        if (newColleagues != null) {
            colleagueList.addAll(newColleagues);
            notifyDataSetChanged();
        }
    }

    static class ColleagueViewHolder extends RecyclerView.ViewHolder {
        TextView colleagueName;
        ImageView colleagueAvatar;

        public ColleagueViewHolder(@NonNull View itemView) {
            super(itemView);
            colleagueName = itemView.findViewById(R.id.colleagueName);
            colleagueAvatar = itemView.findViewById(R.id.colleagueAvatar);
        }
    }
}
