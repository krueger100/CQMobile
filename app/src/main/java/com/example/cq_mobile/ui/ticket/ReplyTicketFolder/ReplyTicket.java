package com.example.cq_mobile.ui.ticket.ReplyTicketFolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cq_mobile.MainActivity;
import com.example.cq_mobile.R;
import com.example.cq_mobile.databinding.FragmentTicketBinding;
import com.example.cq_mobile.ui.ticket.CreateFolder.DeleteTicketFolder.DeleteTicketApiManager;
import com.example.cq_mobile.ui.ticket.ItemAdapter;
import com.example.cq_mobile.ui.ticket.TicketAPICategoryFolder.TicketCategoryManager;
import com.example.cq_mobile.ui.ticket.TicketFragment;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketManager;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReplyTicket extends AppCompatActivity {

    ImageView  indicator_green;
    ImageButton del_btn_on;
    ImageView back;
    TextView create_ticket_category, item_title, resolve_ticket, reply, reply_off, reply_show;
    CardView cardView4;
    EditText reply_body;
    private int ticketIDMain; // Class-level variable
    private String accessToken;
    private List<TicketAPIItem.Message> replyList = new ArrayList<>();
    private RecyclerView repliesRecyclerView;  // No need for another recyclerView

    private ReplyAdapter replyAdapter;
    private ItemAdapter itemAdapter;

    private ArrayList<Integer> ticketReplyIDs = new ArrayList<>();
    List<TicketAPIItem.Message> messageList;
    String messagesJsonList;
    private FragmentTicketBinding binding;
    private boolean isLoading = false;

    private TicketCategoryManager ticketCategoryManager;
    private TicketManager ticketManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_ticket); // Your main activity layout

        // Initialize UI components
        indicator_green = findViewById(R.id.indicator_green);
        item_title = findViewById(R.id.item_title);
        resolve_ticket = findViewById(R.id.resolve_ticket);
        create_ticket_category = findViewById(R.id.create_ticket_category);
        reply = findViewById(R.id.reply);
        reply_off = findViewById(R.id.reply_off);
        cardView4 = findViewById(R.id.cardView4);
        reply_body = findViewById(R.id.reply_body);
        reply_show = findViewById(R.id.reply_show);
        del_btn_on = findViewById(R.id.del_btn_on);


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
        replyAdapter = new ReplyAdapter(this, accessToken, replyList,ticketID);
        repliesRecyclerView.setAdapter(replyAdapter);
        resolve_ticket.setText(status != null && status.equals("open") ? "Resolve Ticket" : "Resolved");
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        resolve_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        // Deserialize message JSON
        ticketIDMain = ticketID;
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
            // Show an AlertDialog for confirmation
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
                                    Intent intent = new Intent(ReplyTicket.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(ReplyTicket.this, "Failed to delete ticket: " + response, Toast.LENGTH_LONG).show();
                                }
                            });
                        }).start(); // Run the API call in a background thread
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // Dismiss the dialog
                        dialog.dismiss();
                    })
                    .show();
        });



        // Show reply form
        reply_show.setOnClickListener(v -> {
            cardView4.setVisibility(View.VISIBLE);
            reply.setVisibility(View.VISIBLE);
        });

        // Hide reply form
        reply_off.setOnClickListener(v -> {
            cardView4.setVisibility(View.GONE);
            reply_show.setVisibility(View.VISIBLE);
            reply.setVisibility(View.GONE);
        });

        // Send reply
        reply.setOnClickListener(v -> {
            String replyText = reply_body.getText().toString().trim();
            if (replyText.isEmpty()) {
                Toast.makeText(ReplyTicket.this, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }



            if (ticketIDMain > 0) {
                // Wrap text in <p> tags for API
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
        // Call the API manager to send the reply
        ReplyTicketApiManager.replyToTicket(String.valueOf(ticketId), replyText, new ReplyTicketApiManager.ApiCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> Toast.makeText(ReplyTicket.this, "Reply sent successfully", Toast.LENGTH_SHORT).show());
                closeKeyboard();

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
  swipeRefreshLayout.setOnRefreshListener(() -> {
            loadTicketsManager.loadTickets(1, 10, "richard.anthony.wetherell@gmail.com", "123456", new LoadTicketsManager.TicketsLoadedCallback() {
                @Override
                public void onTicketsLoaded(List<TicketAPIItem> tickets, String token) {
                    if (tickets != null && !tickets.isEmpty()) {
                        itemAdapter = new ItemAdapter(ReplyTicket.this, token, tickets);
                        repliesRecyclerView.setAdapter(itemAdapter);
                        refreshData(tickets);


                    } else {
                        Log.d("TicketFragment", "No tickets received.");
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("LoadTicketsError", errorMessage);
                }
            });
        });

    private void refreshData(List<TicketAPIItem> newTickets) {
        if (itemAdapter != null) {
            // Update the dataset in the adapter
            itemAdapter.updateData(newTickets);
            itemAdapter.notifyDataSetChanged();
            Log.d("ReplyTicket", "Data successfully updated in ItemAdapter with " + newTickets.size() + " tickets.");
           recreate();
        } else {
            Log.e("ReplyTicket", "ItemAdapter is null, data not updated.");
        }
    }


 */