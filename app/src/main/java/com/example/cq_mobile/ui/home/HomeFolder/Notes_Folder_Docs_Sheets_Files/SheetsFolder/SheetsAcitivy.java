package com.example.cq_mobile.ui.home.HomeFolder.Notes_Folder_Docs_Sheets_Files.SheetsFolder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.NavigationManagerForTask;
import com.example.cq_mobile.HelperManagers.CustomBottomNavFolder.Notes_Files_Docs_Sheets_nav;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.home.HomeFolder.NewBuildFolder.NewBuild;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
public class SheetsAcitivy extends AppCompatActivity {
    TextView sheets_back, sheets_back2;
    private NavigationManagerForTask navigationManager;
    private RecyclerView recyclerView;
    private SheetAdapter adapter;
    private List<SheetItem> sheetList;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheets);

        context = getApplicationContext();
        sheets_back = findViewById(R.id.sheets_back);
        sheets_back2 = findViewById(R.id.sheets_back2);
        recyclerView = findViewById(R.id.recycler_view);
        if (recyclerView == null) {
            Log.e("SheetsActivity", "RecyclerView is not found");
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            sheetList = new ArrayList<>();
            adapter = new SheetAdapter(context, sheetList);
            recyclerView.setAdapter(adapter);
        } else {
            Log.e("SheetsAcitivy", "RecyclerView is null");
        }

        Intent intent1 = getIntent();

        // Retrieve the ArrayLists instead of arrays
        ArrayList<String> sheetTitles = intent1.getStringArrayListExtra("sheetTitle");
        ArrayList<String> sheetOtherTitles = intent1.getStringArrayListExtra("sheetOtherTitle");

        if (sheetTitles != null && sheetOtherTitles != null) {
            for (int i = 0; i < sheetTitles.size(); i++) {
                sheetList.add(new SheetItem(sheetTitles.get(i), sheetOtherTitles.get(i)));
            }
            adapter.notifyDataSetChanged();
        }

        Notes_Files_Docs_Sheets_nav bottomNavView = findViewById(R.id.nfds_bottom);
        navigationManager = new NavigationManagerForTask(this);
        navigationManager.setUpNavigation(bottomNavView, sheets_back2);

        sheets_back.setOnClickListener(v -> {
            Intent intent = new Intent(SheetsAcitivy.this, NewBuild.class);
            intent.putExtra("job_id", intent1.getStringExtra("job_id"));
            startActivity(intent);
        });

        sheets_back2.setOnClickListener(v -> {
            Intent intent = new Intent(SheetsAcitivy.this, NewBuild.class);
            intent.putExtra("job_id", intent1.getStringExtra("job_id"));
            startActivity(intent);
        });
    }
}
