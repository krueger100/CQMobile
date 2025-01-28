package com.example.cq_mobile.ui.ticket.CreateFolder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.ticket.CreateFolder.TicketCreateFolder.TicketCreateApiManager;

public class CreateTicket extends AppCompatActivity {
TextView send_ticket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);
        send_ticket = findViewById(R.id.send_ticket);

        send_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketCreateApiManager.createTicket("ticket from MOBILE APP", "<p>Ticket Create using CQ APP second Test</p>", 3,
                        new TicketCreateApiManager.ApiCallback() {
                            @Override
                            public void onSuccess() {
                                System.out.println("Ticket created successfully!");
                                Log.d("CreateTicket", "Ticket created successfully!");

                            }

                            @Override
                            public void onFailure(String error) {
                                System.err.println("Failed to create ticket: " + error);
                                Log.d("CreateTicket", "Ticket created error!   " +error);
                            }
                        }
                );

            }
        });




    }
}