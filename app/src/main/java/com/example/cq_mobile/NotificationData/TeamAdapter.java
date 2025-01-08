package com.example.cq_mobile.NotificationData;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;

import java.util.List;
public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private List<String> teamNames;  // List of team names
    private Context context;
    private static final String TAG = "TeamAdapter";
    private List<String> teamAvatars; // List of avatar URLs

    // Constructor to pass the data
    public TeamAdapter(List<String> teamNames, Context context, List<String> teamAvatars) {
        this.context = context;
        this.teamNames = teamNames;
        this.teamAvatars = teamAvatars; // Initialize the avatars list
    }

    @Override
    public TeamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_team_item, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TeamViewHolder holder, int position) {
        // Fetch the team name and avatar URL for the current position
        String teamName = teamNames != null && position < teamNames.size() ? teamNames.get(position) : null;
        String avatarUrl = teamAvatars != null && position < teamAvatars.size() ? teamAvatars.get(position) : null;

        Log.d(TAG, "teamAvatars: " + position + ", Avatar URL: " + avatarUrl);

        holder.teamName.setText(teamName);  // Set the team name

        // Check if avatarUrl is valid and load it using Glide
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)  // Load the URL corresponding to the current team
                    .placeholder(R.drawable.circular_background)  // Placeholder image
                    .error(R.drawable.emptyglide)  // Error image
                    .into(holder.teamAvatar);
        } else {
            // Fallback if URL is empty or null
            holder.teamAvatar.setImageResource(R.drawable.emptyglide);
        }
    }

    @Override
    public int getItemCount() {
        return teamNames != null ? teamNames.size() : 0;
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamName, teamColor;
        ImageView teamAvatar;

        public TeamViewHolder(View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.team_name);
            teamAvatar = itemView.findViewById(R.id.team_avatar);
        }
    }
}
