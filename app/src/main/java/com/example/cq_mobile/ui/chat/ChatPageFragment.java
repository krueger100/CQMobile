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
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMessage;

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
    String chatCount;
    int message_read;
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


                    displayChats(chatDetailsList, token, progressBar,chatCount,message_read);
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
        Log.d("ChatPageFragment", "Raw JSON Input: " + rawJson);

        try {
            JSONObject jsonObject = new JSONObject(rawJson);
            if (jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                if (dataObject.has("chats")) {
                    JSONArray chatsArray = dataObject.getJSONArray("chats");
                    Log.d("ChatPageFragment", "Total Chats Found: " + chatsArray.length());


                    for (int i = 0; i < chatsArray.length(); i++) {
                        JSONObject chatObject = chatsArray.getJSONObject(i);

                        Log.d("ChatPageFragment", "Parsing Chat " + (i + 1) + "/" + chatsArray.length());


                        String chatName = chatObject.optString("chat_name", "");
                        String id = chatObject.optString("id", "");
                        String name = chatObject.optString("name", "");
                        String avatarPath = chatObject.optString("avatar_path", "");
                        String online = chatObject.optString("online", "");
                        int channel = chatObject.optInt("channel", 0);
                        int status = chatObject.optInt("status", 0);
                        int channelStatus = chatObject.optInt("channel_status", 0);

                         message_read = chatObject.optInt("message_read", 0);

                        Log.d("ChatPageFragment", "Extracted Chat: " +
                                "chatName=" + chatName + ", id=" + id + ", name=" + name +
                                ", avatarPath=" + avatarPath + ", online=" + online +
                                ", channel=" + channel + ", status=" + status +
                                ", channelStatus=" + channelStatus);

                        List<ChatMessage> messages = new ArrayList<>();
                        if (chatObject.has("message")) {
                            try {
                                JSONArray messagesArray = new JSONArray(chatObject.getString("message"));
                                Log.d("ChatPageFragment", "Total Messages Found: " + messagesArray.length());

                                for (int j = 0; j < messagesArray.length(); j++) {
                                    JSONObject messageObject = messagesArray.getJSONObject(j);
                                //    int messageId = messageObject.optInt("id", 0);

                                    int messageId = messageObject.optInt("id", 0);
                                    String text = messageObject.optString("text", "");
                                    String time = messageObject.optString("time", "");
                                    String date = messageObject.optString("date", "");
                                    int unread = messageObject.optInt("unread", 0);

                                    Log.d("ChatPageFragment", "Message " + (j + 1) + ": " +
                                            "id=" + messageId + ", text=" + text +
                                            ", time=" + time + ", date=" + date + ", unread=" + unread);

                                    messages.add(new ChatMessage(messageId, text, time, date, unread));
                                }
                            } catch (JSONException e) {
                                Log.e("ChatPageFragment", "Error parsing messages: " + e.getMessage());
                            }
                        }

                        List<ChatMember> members = new ArrayList<>();
                        if (chatObject.has("members")) {
                            try {
                                JSONArray membersArray = new JSONArray(chatObject.getString("members"));
                                Log.d("ChatPageFragment", "Total Members Found: " + membersArray.length());

                                for (int j = 0; j < membersArray.length(); j++) {
                                    JSONObject memberObject = membersArray.getJSONObject(j);
                                    int memberId = memberObject.optInt("id", 0);
                                    String memberName = memberObject.optString("name", "");
                                    String memberAvatarPath = memberObject.optString("avatar_path", "");
                                    String email = memberObject.optString("email", "");
                                    int lastRead = memberObject.optInt("last_read", 0);

                                    Log.d("ChatPageFragment", "Member " + (j + 1) + ": " +
                                            "id=" + memberId + ", name=" + memberName +
                                            ", avatarPath=" + memberAvatarPath + ", email=" + email +
                                            ", lastRead=" + lastRead);

                                    members.add(new ChatMember(memberId, memberName, memberAvatarPath, email, lastRead));
                                }
                            } catch (JSONException e) {
                                Log.e("ChatPageFragment", "Error parsing members: " + e.getMessage());
                            }
                        }

                        ChatDetails chatDetails = new ChatDetails(chatName, id, name, avatarPath, messages, online, channel, status, channelStatus, members,message_read);
                        chatDetailsList.add(chatDetails);
                    }
                } else {
                    Log.w("ChatPageFragment", "No 'chats' array found in JSON.");
                }
            } else {
                Log.w("ChatPageFragment", "No 'data' object found in JSON.");
            }
        } catch (JSONException e) {
            Log.e("ChatPageFragment", "JSON Parsing Error: " + e.getMessage());
        }


        chatCount = String.valueOf( chatDetailsList.size());

        Log.d("ChatPageFragment", "Final Extracted Chats Count: " + chatCount);
        return chatDetailsList;
    }

    private void displayChats(List<ChatDetails> chatDetailsList, String token, ProgressBar progressBar, String chatCount, int message_read) {
        if (chatAdapter == null) {
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            chatAdapter = new ChatAdapter(requireContext(), chatDetailsList, accessToken, email, password,progressBar,chatCount,message_read);
            chatRecyclerView.setAdapter(chatAdapter);
        } else {
            chatAdapter.addChats(chatDetailsList);
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