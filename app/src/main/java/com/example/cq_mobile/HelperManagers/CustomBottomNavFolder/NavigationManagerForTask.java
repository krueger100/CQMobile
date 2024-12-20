package com.example.cq_mobile.HelperManagers.CustomBottomNavFolder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.cq_mobile.MoreActivityFolder.MoreActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFragment;
import com.example.cq_mobile.ui.home.HomeFragment;
import com.example.cq_mobile.ui.map.MapFragment;
import com.example.cq_mobile.ui.myJob.myJobFragment;
import com.example.cq_mobile.ui.ticket.TicketFragment;

public class NavigationManagerForTask {
    Context context;

    public NavigationManagerForTask(Context context) {
        this.context = context;
    }

    public void setUpNavigation(Notes_Files_Docs_Sheets_nav bottomNavView, TextView back2) {
        bottomNavView.setOnNavigationItemSelectedListener(new Notes_Files_Docs_Sheets_nav.OnNavigationItemSelectedListener() {
            @Override
            public void onHomeSelected() {
                // Switch to HomeFragment inside NotesActivity
                switchToFragment(new HomeFragment());
                back2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMapSelected() {
                // Handle Map Fragment selection, assuming MapFragment exists in the app
                switchToFragment(new MapFragment());
                back2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMyJobsSelected() {
                switchToFragment(new myJobFragment());
                back2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTicketSelected() {
                switchToFragment(new TicketFragment());
                back2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChatSelected() {
                switchToFragment(new ChatFragment());
                back2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMoreSelected() {
                Intent intent = new Intent(context, MoreActivity.class);
                context.startActivity(intent);
            }


        });
    }


    private void switchToFragment(Fragment fragment) {
        // Ensure the context is an instance of FragmentActivity or AppCompatActivity
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment) // Replace with the fragment container ID
                    .commit();
        }
    }
}
