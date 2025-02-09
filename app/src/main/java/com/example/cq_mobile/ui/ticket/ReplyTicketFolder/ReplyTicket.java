package com.example.cq_mobile.ui.ticket.ReplyTicketFolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cq_mobile.HelperManagers.Animation.ClickAnimationManager;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.ticket.CreateFolder.DeleteTicketFolder.DeleteTicketApiManager;
import com.example.cq_mobile.ui.ticket.ResolveTicketAPIFolder.UpdateTicketStatusApiManager;
import com.example.cq_mobile.ui.ticket.TicketFragment;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReplyTicket extends AppCompatActivity {

    ImageView  indicator_green;
    ImageButton del_btn_on;
    ImageView back;
    TextView ticket_category, item_title, reply, reply_off, reply_show;
    CardView cardView4;
    EditText reply_body;
    TextView resolve_ticket;
    private int ticketIDMain; // Class-level variable
    private String accessToken;
    private List<TicketAPIItem.Message> replyList = new ArrayList<>();
    private RecyclerView repliesRecyclerView;  // No need for another recyclerView
    private ReplyAdapter replyAdapter;
    private ArrayList<Integer> ticketReplyIDs = new ArrayList<>();
    List<TicketAPIItem.Message> messageList;
    String messagesJsonList;
    ProgressBar progressBar;
    String stats;
    String email;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_ticket); // Your main activity layout

        // Initialize UI components
        indicator_green = findViewById(R.id.indicator_green);
        item_title = findViewById(R.id.item_title);
        resolve_ticket = findViewById(R.id.resolve_ticket);
        ticket_category = findViewById(R.id.ticket_category);
        reply = findViewById(R.id.reply);
        reply_off = findViewById(R.id.reply_off);
        cardView4 = findViewById(R.id.cardView4);
        reply_body = findViewById(R.id.reply_body);
        reply_show = findViewById(R.id.reply_show);
        del_btn_on = findViewById(R.id.del_btn_on);
        progressBar = findViewById(R.id.progressBar);



        SharedPrefManager sharedPrefManager = new SharedPrefManager(ReplyTicket.this);
         email = sharedPrefManager.getEmail();
         password = sharedPrefManager.getPassword();
         accessToken = sharedPrefManager.getAccessToken();
        Log.d("CreateTicket", "Access Token: " + accessToken);
        Log.d("ToDoFragmentSharedPreff", "Retrieved User Data: ");
        Log.d("ToDoFragmentSharedPreff", "Email: "+email);
        Log.d("ToDoFragmentSharedPreff", "Password  : "+password);
        // Retrieve data from the intent
        accessToken = getIntent().getStringExtra("token");
        String subject = getIntent().getStringExtra("subject");
        String categoryName = getIntent().getStringExtra("categoryName");
        String status = getIntent().getStringExtra("status");
        String categoryColor = getIntent().getStringExtra("categoryColor");
        String messageJson = getIntent().getStringExtra("messageJson");
        Log.d("ReplyTicket", "messageJson: --->> " + messageJson);
        int ticketID = getIntent().getIntExtra("ticketID", -1);
        messagesJsonList = getIntent().getStringExtra("messagesJsonList");

        // Initialize the RecyclerView here
        repliesRecyclerView = findViewById(R.id.recyclerView_replies);
        repliesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        replyAdapter = new ReplyAdapter(this, accessToken, replyList,ticketID,progressBar);
        repliesRecyclerView.setAdapter(replyAdapter);
        back = findViewById(R.id.back);
        ticketIDMain = ticketID;


        stats = status;
        Log.d("ResolveTicket", "stats:  " + stats);
        resolve_ticket.setText(status != null && status.equals("open") ? "Resolve Ticket" : "Open");
        resolve_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String status;
                if (stats.equals("open")) {
                    status = "resolved";
                } else {
                    status = "open";
                }
                UpdateTicketStatusApiManager.updateTicketStatus(email,password, ticketIDMain, status, progressBar, accessToken, new UpdateTicketStatusApiManager.ApiCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("ResolveTicket", "Ticket resolved successfully");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Ticket " + status, Toast.LENGTH_SHORT).show();
                                resolve_ticket.setText(status.equals("resolved") ? "Open Ticket" : "Resolve Ticket");
                                stats = status;
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                    @Override
                    public void onFailure(String error) {
                        Log.e("ResolveTicket", "Failed to resolve ticket: " + error);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Failed to resolve ticket", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ticket_category.setText(categoryName);


        if (messagesJsonList != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<TicketAPIItem.Message>>() {}.getType();

        messageList = gson.fromJson(messagesJsonList, listType);

            // Debugging logs
            for (TicketAPIItem.Message message : messageList) {
                Log.d("ReplyTicket", "Message ID: " + message.getId());
                Log.d("ReplyTicket", "User Name: " + message.getUser().getName());
                Log.d("ReplyTicket", "Message Body: " + message.getBody());
            }
            loadReplies(messageList);


        } else {
            Log.d("ReplyTicket", "No messages received.");
        }


        // Apply color filter
        if (categoryColor != null) {
            try {
                indicator_green.setColorFilter(Color.parseColor(categoryColor));
            } catch (IllegalArgumentException e) {
                Log.e("ReplyTicket", "Invalid color: " + categoryColor, e);
            }
        }

        // Update UI elements
        if (subject != null) item_title.setText(subject);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadReplies(messageList);
            swipeRefreshLayout.setRefreshing(false);
        });

        // Toggle delete button
        del_btn_on.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            new AlertDialog.Builder(ReplyTicket.this)
                    .setTitle("Delete Ticket")
                    .setMessage("Are you sure you want to delete this ticket?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Perform delete operation
                        new Thread(() -> {
                            String response = DeleteTicketApiManager.deleteTicket(String.valueOf(ticketIDMain));
                            runOnUiThread(() -> {
                                if (response.startsWith("Error:")) {
                                    Toast.makeText(ReplyTicket.this, "Ticket deleted successfully!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(ReplyTicket.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(ReplyTicket.this, "Failed to delete ticket: " + response, Toast.LENGTH_LONG).show();
                                }
                            });
                        }).start();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Dismiss the dialog
                        dialog.dismiss();
                    })
                    .show();
        });

        // Show reply form
        reply_show.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);
            cardView4.setVisibility(View.VISIBLE);
            reply.setVisibility(View.VISIBLE);
        });

        // Hide reply form
        reply_off.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);
            cardView4.setVisibility(View.GONE);
            reply_show.setVisibility(View.VISIBLE);
            reply.setVisibility(View.GONE);
        });

        // Send reply
        reply.setOnClickListener(v -> {
            ClickAnimationManager.applyClickAnimation(v);

            String replyText = reply_body.getText().toString().trim();
            if (replyText.isEmpty()) {
                Toast.makeText(ReplyTicket.this, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }



            if (ticketIDMain > 0) {

        replyText = "<p>" + replyText + "</p>";
                sendReply(ticketIDMain, replyText);

            } else {
                Toast.makeText(ReplyTicket.this, "Invalid ticket ID", Toast.LENGTH_SHORT).show();
            }
        });




    }



    private void loadReplies(List<TicketAPIItem.Message> messageList) {
        if (messageList == null || messageList.isEmpty()) {
            Log.d("ReplyTicket", "No messages to display.");
            return;
        }

        // Debug: Print all messages
        for (TicketAPIItem.Message message : messageList) {
            Log.d("ReplyTicket", "Message ID: " + message.getId());
            Log.d("ReplyTicket", "User Name: " + message.getUser().getName());
            Log.d("ReplyTicket", "Message Body: " + message.getBody());

            // Collect ticketReplyID
            ticketReplyIDs.add(message.getId());

            // Add the message to the reply list
            replyList.add(message);
        }

        // Update RecyclerView on the main thread
        runOnUiThread(() -> {
            replyAdapter.notifyDataSetChanged();
            Log.d("ReplyTicket", "RecyclerView updated with " + replyList.size() + " replies.");
        });

        // Debugging all ticketReplyIDs
        Log.d("ReplyTicket", "All ticketReplyIDs: " + ticketReplyIDs);
    }

    private void sendReply(int ticketId, String replyText) {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(ReplyTicket.this);
        email = sharedPrefManager.getEmail();
        password = sharedPrefManager.getPassword();
        Log.d("sendReply", "Retrieved User Data: ");
        Log.d("sendReply", "Email: "+email);
        Log.d("sendReply", "Password  : "+password);
        ReplyTicketApiManager.replyToTicket(email,password,String.valueOf(ticketId), replyText, new ReplyTicketApiManager.ApiCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> Toast.makeText(ReplyTicket.this, "Reply sent successfully", Toast.LENGTH_SHORT).show());
                closeKeyboard();

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
                runOnUiThread(() -> Toast.makeText(ReplyTicket.this, "Failed to send reply: " + error, Toast.LENGTH_SHORT).show());
            }
        });

    }

    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }



}

//        Intent intent = new Intent(ReplyTicket.this, TicketFragment.class);
//        startActivity(intent);
/*

 Save
            // Save true in SharedPreferences when the reply is successfully sent
                SharedPreferences sharedPreferences = getSharedPreferences("ReplyData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isReplySent", true);
                editor.apply();




retrieve
  SharedPreferences sharedPreferences = getContext().getSharedPreferences("ReplyData", Context.MODE_PRIVATE);
    boolean isReplySent = sharedPreferences.getBoolean("isReplySent", false);
            if (isReplySent) {
        Log.d("SharedPreferences", "Reply was sent successfully.");
        reloadFragment();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isReplySent");  // Remove the specific key
        editor.apply();
    } else {
        Log.d("SharedPreferences", "Reply was not sent.");

    }

    public void reloadFragment() {
        if (getActivity() != null) {
            int count = itemAdapter.getItemCount();
            if (count > 0) {
                Log.d("reloadFragment", "Data has been added, you can perform any necessary actions here");
                getActivity().recreate();
            } else {
                Log.d("reloadFragment", " No data, handle accordingly");
            }
        }
    }


 */