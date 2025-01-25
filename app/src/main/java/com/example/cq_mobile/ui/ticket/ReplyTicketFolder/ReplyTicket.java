package com.example.cq_mobile.ui.ticket.ReplyTicketFolder;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.ticket.ReplyTicketFolder.TicketRepliesFolder.TicketRepliesManager;
import com.example.cq_mobile.ui.ticket.TicketsFolder.TicketAPIItem;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReplyTicket extends AppCompatActivity {
    ImageView del_btn_on, del_btn_off, avatar, indicator_green;
    TextView create_ticket_category, name, item_title, resolve_ticket, body_text, reply, reply_off, reply_show;
    CardView cardView4;
    EditText reply_body;
    private int ticketIDMain; // Class-level variable
    private String accessToken;
    int userId;
    private TicketRepliesManager ticketRepliesManager;
    private List<TicketAPIItem.Message> replyList = new ArrayList<>();
    private RecyclerView repliesRecyclerView;
    private ReplyAdapter replyAdapter;

    private ArrayList<Integer> ticketReplyIDs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_ticket);

        // Initialize UI components
        avatar = findViewById(R.id.avatar);
        indicator_green = findViewById(R.id.indicator_green);
        item_title = findViewById(R.id.item_title);
        resolve_ticket = findViewById(R.id.resolve_ticket);
        name = findViewById(R.id.name);
        create_ticket_category = findViewById(R.id.create_ticket_category);
        body_text = findViewById(R.id.body_text);
        reply = findViewById(R.id.reply);
        reply_off = findViewById(R.id.reply_off);
        cardView4 = findViewById(R.id.cardView4);
        reply_body = findViewById(R.id.reply_body);
        reply_show = findViewById(R.id.reply_show);
        del_btn_on = findViewById(R.id.del_btn_on);
        del_btn_off = findViewById(R.id.del_btn_off);
        // Retrieve data from the intent
        String subject = getIntent().getStringExtra("subject");
        String categoryName = getIntent().getStringExtra("categoryName");
        String status = getIntent().getStringExtra("status");
        String categoryColor = getIntent().getStringExtra("categoryColor");

        String messageJson = getIntent().getStringExtra("messageJson");
        Log.d("ReplyTicket", "messageJson: --->> " + messageJson);
        int ticketID = getIntent().getIntExtra("ticketID", -1);

        // Deserialize messages
        String messagesJsonList = getIntent().getStringExtra("messagesJsonList");

        repliesRecyclerView = findViewById(R.id.recycler_view);
        repliesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize adapter with an empty list for now
        replyAdapter = new ReplyAdapter(this, accessToken, replyList);
        repliesRecyclerView.setAdapter(replyAdapter);


        // Set initial status
        resolve_ticket.setText(status != null && status.equals("open") ? "Resolve Ticket" : "Resolved");

        // Deserialize message JSON
        ticketIDMain = ticketID;
        if (messageJson != null) {
            try {
                Gson gson = new Gson();
                TicketAPIItem.Message message = gson.fromJson(messageJson, TicketAPIItem.Message.class);


                if (message != null) {
                    if (message.getUser() != null && message.getUser().getAvatar() != null) {
                        Glide.with(this)
                                .load(message.getUser().getAvatar())
                                .placeholder(R.drawable.circular_background)
                                .error(R.drawable.emptyglide)
                                .into(avatar);

                        name.setText(message.getUser().getName());
                        String cleanBodyText = message.getBody().replaceAll("<[^>]*>", "");
                        body_text.setText(cleanBodyText);


                    }

                }
            } catch (JsonSyntaxException e) {
                Log.e("ReplyTicket", "Failed to parse message JSON", e);
            }
        }

        if (messagesJsonList != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<TicketAPIItem.Message>>() {}.getType();

            List<TicketAPIItem.Message> messageList = gson.fromJson(messagesJsonList, listType);

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

        // Toggle delete button
        del_btn_on.setOnClickListener(v -> {
            del_btn_on.setVisibility(View.GONE);
            del_btn_off.setVisibility(View.VISIBLE);
        });

        del_btn_off.setOnClickListener(v -> {
            del_btn_off.setVisibility(View.GONE);
            del_btn_on.setVisibility(View.VISIBLE);
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
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(ReplyTicket.this, "Failed to send reply: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}



