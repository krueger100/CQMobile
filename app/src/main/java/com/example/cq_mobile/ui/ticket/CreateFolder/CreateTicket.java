package com.example.cq_mobile.ui.ticket.CreateFolder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.ticket.CreateFolder.TicketCreateFolder.TicketCreateApiManager;
import com.example.cq_mobile.ui.ticket.ReplyTicketFolder.ReplyTicket;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketAPICategoryItems;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketCategoryManager;

import java.util.ArrayList;
import java.util.List;

public class CreateTicket extends AppCompatActivity {
    TextView send_ticket;
    ImageView back;
    Spinner category_spinner;
    EditText subject, body_text;
    TicketCategoryManager ticketCategoryManager;
    List<TicketAPICategoryItems> categoryItems; // Store category data
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        subject = findViewById(R.id.subject);
        body_text = findViewById(R.id.body_text);
        category_spinner = findViewById(R.id.category_spinner);
        send_ticket = findViewById(R.id.send_ticket);
        back = findViewById(R.id.back);
        progressBar = findViewById(R.id.progressBar); // Assuming you have a ProgressBar in your layout

        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        String accessToken = AuthManager.getInstance(this).getToken();
        Log.d("CreateTicket", "Access Token: " + accessToken);

        ticketCategoryManager = new TicketCategoryManager(this, accessToken);

        loadCategoriesIntoSpinner();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        send_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                int selectedPosition = category_spinner.getSelectedItemPosition();
                if (selectedPosition < 0 || categoryItems == null || categoryItems.isEmpty()) {
                    Log.d("CreateTicket", "No category selected or category list is empty");
                    return;
                }
                int selectedCategoryId = categoryItems.get(selectedPosition).getId();

                String subjectText = subject.getText().toString().trim();
                String bodyText = body_text.getText().toString().trim();

                if (subjectText.isEmpty() || bodyText.isEmpty()) {
                    Log.d("CreateTicket", "Subject or body is empty.");
                    return;
                }

                TicketCreateApiManager.createTicket(subjectText, bodyText, selectedCategoryId, progressBar,accessToken,
                        new TicketCreateApiManager.ApiCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("CreateTicket", "Ticket created successfully!>>>>");
                                // Save true in SharedPreferences when the reply is successfully sent
                                SharedPreferences sharedPreferences = getSharedPreferences("ReplyData", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isReplySent", true);
                                editor.apply();

                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);

                            }

                            @Override
                            public void onFailure(String error) {
                                Log.d("CreateTicket", "Ticket creation failed: " + error);
                            }
                        }
                );
            }
        });
    }

    private void loadCategoriesIntoSpinner() {
        ticketCategoryManager.loadCategoryTickets(1, 20, new TicketCategoryManager.TicketsCallback() {
            @Override
            public void onTicketsLoaded(List<TicketAPICategoryItems> tickets) {
                if (tickets != null && !tickets.isEmpty()) {
                    // Log ticket details
                    for (TicketAPICategoryItems ticket : tickets) {
                        Log.d("CreateTicket", "Ticket ID: " + ticket.getId());
                        Log.d("CreateTicket", "Ticket Name: " + ticket.getName());
                        Log.d("CreateTicket", "Ticket Color: " + ticket.getColor());
                    }

                    // Save the category items for later use
                    categoryItems = tickets;

                    // Create a list of category names for display
                    List<String> categoryNames = new ArrayList<>();
                    for (TicketAPICategoryItems item : tickets) {
                        categoryNames.add(item.getName());
                    }

                    // Create a custom adapter to set images and styles dynamically
                    CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(CreateTicket.this, tickets);
                    category_spinner.setAdapter(adapter);
                } else {
                    Log.d("CreateTicket", "No tickets available to load into the spinner.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("CreateTicket", "Failed to load categories: " + errorMessage);
            }
        });
    }
}
