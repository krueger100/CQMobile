package com.example.cq_mobile.ui.chat;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.R;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatAPIItem;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatPageFragment extends Fragment {

    private ChatManager chatManager;
    private String accessToken;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private int currentPage = 1;  // Track pagination
    private final int pageSize = 20;
    private boolean isLoading = false;
    String email;
    String password;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_page, container, false);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(requireContext());
         email = sharedPrefManager.getEmail();
         password = sharedPrefManager.getPassword();

        Log.d("ChatPageFragment", "Email: " + email);
        Log.d("ChatPageFragment", "Password: " + password);

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        chatManager = new ChatManager(getContext());


        AccessTokenRequest tokenRequest = new AccessTokenRequest(email, password);
        getAccessTokenAndLoadChats(tokenRequest, progressBar,currentPage,pageSize);

        return view;
    }

    private void getAccessTokenAndLoadChats(AccessTokenRequest tokenRequest, ProgressBar progressBar, int page, int pageSize) {
        chatManager.getAccessToken(tokenRequest, new ChatManager.AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String token) {
                accessToken = token;
                Log.d("ChatPageFragment", "Access Token received: " + token);
                loadChatsWithToken(token,progressBar);
            }


            @Override
            public void onError(String errorMessage) {
                Log.e("ChatPageFragment", "Error fetching access token: " + errorMessage);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadChatsWithToken(String token, ProgressBar progressBar) {
        if (isLoading) return;  // Prevent duplicate loads
        isLoading = true;

        chatManager.loadChats(currentPage, pageSize, new ChatManager.AllChatsCallback() {
            @Override
            public void onAllChatsLoaded(List<ChatAPIItem> chats, String rawJson) {
                setProgressBarVisibility(false, progressBar);
                isLoading = false;

                if (chats != null && !chats.isEmpty()) {
                    List<ChatDetails> chatDetailsList = extractChatDetails(rawJson);
                    Log.d("ChatPageFragment", "Extracted chat details: " + chatDetailsList);

                    displayChats(chatDetailsList, token, progressBar);
                    currentPage++;  // Increment page after successful load
                } else {
                    Log.d("ChatPageFragment", "No chats received.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                setProgressBarVisibility(false, progressBar);
                isLoading = false;
                Log.e("ChatPageFragment", "Chat Loading Error: " + errorMessage);
            }
        });
    }

    private List<ChatDetails> extractChatDetails(String rawJson) {
        List<ChatDetails> chatDetailsList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(rawJson); // Convert rawJson to JSON Object
            if (jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                if (dataObject.has("chats")) {
                    JSONArray chatsArray = dataObject.getJSONArray("chats");

                    for (int i = 0; i < chatsArray.length(); i++) {
                        JSONObject chatObject = chatsArray.getJSONObject(i);

                        if (chatObject.has("chat_name")) {
                            // Extracting all necessary fields for each chat
                            String chatName = chatObject.getString("chat_name");
                            String id = chatObject.getString("id");
                            String name = chatObject.getString("name");
                            String avatarPath = chatObject.getString("avatar_path");
                            String message = chatObject.getString("message");
//                            String online = chatObject.getString("online");
//                            int channel = chatObject.getInt("channel");
//                            int status = chatObject.getInt("status");
//                            int channelStatus = chatObject.getInt("channel_status");
//                            String members = chatObject.getString("members");

                            // Create a new ChatDetails object and add it to the list
                            ChatDetails chatDetails = new ChatDetails(chatName, id, name, avatarPath, message);///, online , channel, status, channelStatus, members);
                            chatDetailsList.add(chatDetails);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("InnerChats", "JSON Parsing Error: " + e.getMessage());
        }
        return chatDetailsList; // Return list of full chat details
    }

    private void displayChats(List<ChatDetails> chatDetailsList, String token, ProgressBar progressBar) {
        if (chatAdapter == null) {
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            chatAdapter = new ChatAdapter(requireContext(), chatDetailsList, accessToken, email, password);
            chatRecyclerView.setAdapter(chatAdapter);
        } else {
            chatAdapter.addChats(chatDetailsList); // Ensure this method exists in ChatAdapter
        }
    }


    private void setProgressBarVisibility(boolean visible, ProgressBar progressBar) {
        if (progressBar != null) {
            progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (chatManager != null) {
            chatManager.cancelAllCalls();
        }
    }
}
/*
curl -X GET "https://aws.customquoter.co.uk/api/m/chats?page=1&per_page=10" \
-H "Authorization: Bearer 7898|QVu8LPIEoPkdOLqJToYdAYEoE3ydz1Qu95vx4npS" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0" \
-H "Accept-Encoding: gzip, deflate, br" \
-H "Connection: keep-alive"

 */