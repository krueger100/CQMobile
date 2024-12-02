package com.example.cq_mobile.ui.home.HomeFolder.API_home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
      //  holder.Site_preparation.setText(user.getFirstName());
      //  holder.state_description.setText(user.getLastName());
     //   Glide.with(context).load(user.getAvatar()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView Site_preparation, state_description;
        ImageView avatar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            Site_preparation = itemView.findViewById(R.id.Site_preparation);
            state_description = itemView.findViewById(R.id.state_description);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }
}
