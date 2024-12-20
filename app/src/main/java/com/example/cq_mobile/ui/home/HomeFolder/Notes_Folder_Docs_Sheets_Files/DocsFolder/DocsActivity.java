package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.DocsFolder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.NavigationManagerForTask;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.Notes_Files_Docs_Sheets_nav;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;

public class DocsActivity  extends AppCompatActivity {
    TextView docs_back,docs_back2;
    private NavigationManagerForTask navigationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docs);
        docs_back = findViewById(R.id.docs_back);
        docs_back2 = findViewById(R.id.docs_back2);
        Intent intent1 = getIntent();
        String jobId = intent1.getStringExtra("job_id");

        Notes_Files_Docs_Sheets_nav bottomNavView = findViewById(R.id.nfds_bottom);
        navigationManager = new NavigationManagerForTask(this);
        navigationManager.setUpNavigation(bottomNavView,docs_back2);

        docs_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocsActivity.this, NewBuild.class);
                intent.putExtra("job_id", jobId);
                startActivity(intent);
            }
        });
        docs_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocsActivity.this, NewBuild.class);
                intent.putExtra("job_id", jobId);
                startActivity(intent);
            }
        });
    }
}