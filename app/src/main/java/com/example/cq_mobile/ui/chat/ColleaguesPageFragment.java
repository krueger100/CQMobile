package com.example.cq_mobile.ui.chat;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cq_mobile.HelperManagers.CacheFolder.CacheManager;
import com.example.cq_mobile.LoginFolder.AuthManager;
import com.example.cq_mobile.LoginFolder.ThreadManager;
import com.example.cq_mobile.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatAPIItem;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMember;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatMessage;
import com.example.cq_mobile.ui.chat.ColleagueFolder.ColleagueAPIItem;
import com.example.cq_mobile.ui.chat.ColleagueFolder.ColleagueAdapter;
import com.example.cq_mobile.ui.chat.ColleagueFolder.ColleagueManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class ColleaguesPageFragment extends Fragment {

    private ColleagueManager colleagueManager;
    AuthManager authManager;
    private RecyclerView colleaguesRecyclerView;
    private ColleagueAdapter colleagueAdapter;
    private int currentPage = 1;
    private final int pageSize = 10;
    private boolean isLoading = false;
    private LinearLayoutManager layoutManager;
    private boolean hasMorePages = true;
    List<ChatDetails> chatDetailsList;
    SwipeRefreshLayout swipeRefreshLayout;
    String accessToken,currentUserName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_colleagues_page, container, false);
        ThreadManager.runOnMainThread(() -> {

            authManager = AuthManager.getInstance(getContext());
            accessToken = authManager.getAccessToken();
            String fname  = AuthManager.getInstance(getContext()).getFirstName();
            String lname  = AuthManager.getInstance(getContext()).getLastName();
            currentUserName  = fname +"\t"+ lname;

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        colleaguesRecyclerView = view.findViewById(R.id.colleaguesRecyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        colleaguesRecyclerView.setLayoutManager(layoutManager);
            Context context = requireContext();
        colleagueManager = new ColleagueManager(getContext());
        progressBar.setVisibility(View.VISIBLE);


            loadColleaguesWithToken(progressBar,accessToken, getContext(), currentUserName);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Reset the page and clear the previous data
                currentPage = 1;
                isLoading = false;
                hasMorePages = true;
                loadColleaguesWithToken(progressBar,accessToken, getContext(), currentUserName);
            }
        });

        colleaguesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && !isLoading && hasMorePages && !recyclerView.canScrollVertically(1)) {
                    // Show SwipeRefreshLayout when loading more data
                    swipeRefreshLayout.setRefreshing(true);
                    loadColleaguesWithToken(progressBar, accessToken, getContext(),currentUserName);
                }
            }
        });
            Log.d("MainThread", "This is running on the main thread.");
        });
        return view;
    }

    private void loadColleaguesWithToken(ProgressBar progressBar, String accessToken, Context context, String currentUserName) {
        if (isLoading || !hasMorePages) return;
        isLoading = true;

        colleagueManager.loadColleagues(currentPage, pageSize, new ColleagueManager.AllColleaguesCallback() {
            @Override
            public void onAllColleaguesLoaded(List<ColleagueAPIItem> colleagues, String colleaguesRawJson) {
                if (colleagues != null && !colleagues.isEmpty()) {
                    colleagueManager.loadChats(currentPage, pageSize, new ColleagueManager.AllChatsCallback() {
                        @Override
                        public void onAllChatsLoaded(List<ChatAPIItem> chats, String rawJson) {
                            progressBar.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            isLoading = false;

                            List<CombinedItem> combinedList = new ArrayList<>();
                            if (chats != null && !chats.isEmpty()) {
                                chatDetailsList = extractChatDetails(rawJson);
                                for (ColleagueAPIItem colleague : colleagues) {
                                    combinedList.add(new CombinedItem(colleague, null));
                                }
                                for (ChatDetails chat : chatDetailsList) {
                                    combinedList.add(new CombinedItem(null, chat));
                                }


                                Log.d("ColleaguesPageFragment", "combinedList size: " + combinedList.size());
                                for (CombinedItem item : combinedList) {
                                    ColleagueAPIItem colleague = item.getColleague();
                                    ChatDetails chat = item.getChat();

                                    if (colleague != null) {
                                        Log.d("ColleaguesPageFragment", "Colleague: " + colleague.getName() + ", ID: " + colleague.getId()+"\n"+"AVATAR : "+colleague.getAvatar_path());
                                    }
                                    if (chat != null) {
                                        Log.d("ColleaguesPageFragment", "Chat: " + chat.getChatName() + ", ID: " + chat.getId()+"\n"+"AVATAR : "+chat.getAvatarPath());
                                    }
                                }


                            }


                            if (colleagueAdapter == null) {
                                colleagueAdapter = new ColleagueAdapter(requireContext(), combinedList, currentUserName, accessToken, chatDetailsList);
                                colleaguesRecyclerView.setAdapter(colleagueAdapter);
                            } else {
                                colleagueAdapter.addMoreItems(combinedList);
                            }

                            currentPage++;
                            hasMorePages = combinedList.size() == pageSize;
                        }

                        @Override
                        public void onError(String errorMessage) {
                            progressBar.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            isLoading = false;
                            hasMorePages = false;
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
                hasMorePages = false;
            }
        });
    }

    private List<ChatDetails> extractChatDetails(String rawJson) {
        List<ChatDetails> chatDetailsList = new ArrayList<>();
        try {
            // Log the raw JSON for debugging
            Log.d("Raw JSON", rawJson);

            // Parse the raw JSON into a JSONObject
            JSONObject jsonObject = new JSONObject(rawJson);

            // Check if the "data" object exists
            if (jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");

                // Check if the "chats" array exists and is not null
                if (dataObject.has("chats") && !dataObject.isNull("chats")) {
                    JSONArray chatsArray = dataObject.getJSONArray("chats");

                    // Log the parsed JSON for debugging purposes
                    Log.d("Parsed JSON", dataObject.toString());

                    // Limit to 10 items if there are more than 10 chats
                    int limit = Math.min(chatsArray.length(), 10);

                    // Process the chat details
                    for (int i = 0; i < limit; i++) {
                        JSONObject chatObject = chatsArray.getJSONObject(i);
                        chatDetailsList.add(extractSingleChatDetails(chatObject));
                    }
                } else {
                    Log.e("ColleaguesPageFragment", "'chats' array is missing or null");
                }
            } else {
                Log.e("ColleaguesPageFragment", "No 'data' object found in JSON");
            }
        } catch (JSONException e) {
            Log.e("ColleaguesPageFragment", "JSON Parsing Error: " + e.getMessage());
        }
        return chatDetailsList;
    }

    private ChatDetails extractSingleChatDetails(JSONObject chatObject) throws JSONException {
        String chatName = chatObject.optString("chat_name", "");
        String id = chatObject.optString("id", "");
        String avatarPath = chatObject.optString("avatar_path", "");
        String online = chatObject.optString("online", "");
        int channel = chatObject.optInt("channel", 0);
        int status = chatObject.optInt("status", 0);
        int channelStatus = chatObject.optInt("channel_status", 0);
        int message_read = chatObject.optInt("message_read", 0);

        List<ChatMessage> messages = new ArrayList<>();

        // Check if "message" is a JSONArray or a String
        if (chatObject.has("message")) {
            // If it's a JSONArray
            if (chatObject.get("message") instanceof JSONArray) {
                JSONArray messagesArray = chatObject.getJSONArray("message");
                for (int j = 0; j < messagesArray.length(); j++) {
                    JSONObject messageObject = messagesArray.getJSONObject(j);
                    int messageId = messageObject.optInt("id", 0);
                    String text = messageObject.optString("text", "");
                    String time = messageObject.optString("time", "");
                    String date = messageObject.optString("date", "");
                    int unread = messageObject.optInt("unread", 0);
                    messages.add(new ChatMessage(messageId, text, time, date, unread));
                }
            }
            // If it's a String, handle it accordingly (you can decide what to do with the string)
            else if (chatObject.get("message") instanceof String) {
                String text = chatObject.optString("message", "");
                messages.add(new ChatMessage(0, text, "", "", 0)); // Create a dummy message
            }
        }

        List<ChatMember> members = new ArrayList<>();
        if (chatObject.has("members")) {
            if (chatObject.get("members") instanceof JSONArray) {
                JSONArray membersArray = chatObject.getJSONArray("members");
                for (int j = 0; j < membersArray.length(); j++) {
                    JSONObject memberObject = membersArray.getJSONObject(j);
                    int memberId = memberObject.optInt("id", 0);
                    String memberName = memberObject.optString("name", "");
                    String memberAvatarPath = memberObject.optString("avatar_path", "");
                    String email = memberObject.optString("email", "");
                    int lastRead = memberObject.optInt("last_read", 0);
                    members.add(new ChatMember(memberId, memberName, memberAvatarPath, email, lastRead));
                }
            }
            // If it's a String, log an error or handle as necessary
            else if (chatObject.get("members") instanceof String) {
                Log.d("ColleaguesPageFragment", "'members' is unexpectedly a string: " + chatObject.get("members"));
                // You could handle this case by either skipping the members or handling it differently
            }
        }

        return new ChatDetails(chatName, id, "", avatarPath, messages, online, channel, status, channelStatus, members, message_read);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (colleagueManager != null) {
            colleagueManager.cancelAllCalls();
        }
    }
}



    /*
    private void loadColleaguesWithToken(ProgressBar progressBar, String accessToken, Context context, String email, String password, String currentUserName) {
        if (isLoading || !hasMorePages) return;
        isLoading = true;
        colleagueManager.loadColleagues(currentPage, pageSize, new ColleagueManager.AllColleaguesCallback() {
            @Override
            public void onAllColleaguesLoaded(List<ColleagueAPIItem> colleagues, String colleaguesRawJson) {
                if (colleagues != null && !colleagues.isEmpty()) {

                    colleagueManager.loadChats(currentPage, pageSize, new ColleagueManager.AllChatsCallback() {
                        @Override
                        public void onAllChatsLoaded(List<ChatAPIItem> chats, String rawJson) {
                            progressBar.setVisibility(View.GONE);
                            isLoading = false;
                            if (chats != null && !chats.isEmpty()) {
                                List<ChatDetails> chatDetailsList = extractChatDetails(rawJson);
                                Log.w("ColleaguesPageFragment", "Extracted chat details: " + chatDetailsList);

                                List<CombinedItem> combinedList = new ArrayList<>();
                                for (ColleagueAPIItem colleague : colleagues) {
                                    combinedList.add(new CombinedItem(colleague, null));
                                }
                                for (ChatDetails chat : chatDetailsList) {
                                    combinedList.add(new CombinedItem(null, chat));
                                }

                                if (colleagueAdapter == null) {
                                    colleagueAdapter = new ColleagueAdapter(requireContext(), combinedList, email, password, currentUserName, accessToken,chatDetailsList);
                                    colleaguesRecyclerView.setAdapter(colleagueAdapter);

                                } else {
                                    colleagueAdapter.addMoreItems(combinedList);
                                }


                                Log.w("ColleaguesPageFragment", "Combined list: " + combinedList);
                            } else {
                                Log.d("ColleaguesPageFragment", "No chats received.");

                            }

                            currentPage++;
                            hasMorePages = false;
                            return;
                        }

                        @Override
                        public void onError(String errorMessage) {
                            progressBar.setVisibility(View.GONE);
                            isLoading = false;
                            hasMorePages = false;
                            Log.e("ColleaguesPageFragment", "Chat Loading Error: " + errorMessage);
                        }
                    });

                } else {
                    Log.e("ColleaguesPageFragment", "No more colleagues.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                Log.e("ColleaguesPageFragment", "Error loading colleagues: " + errorMessage);
                Toast.makeText(getContext(), "Error loading colleagues", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private List<ChatDetails> extractChatDetails(String rawJson) {
        List<ChatDetails> chatDetailsList = new ArrayList<>();
        Log.d("ColleaguesPageFragment", "Raw JSON Input: " + rawJson);

        try {
            JSONObject jsonObject = new JSONObject(rawJson);
            if (jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                if (dataObject.has("chats")) {
                    JSONArray chatsArray = dataObject.getJSONArray("chats");
                    Log.d("ColleaguesPageFragment", "Total Chats Found: " + chatsArray.length());


                    for (int i = 0; i < chatsArray.length(); i++) {
                        JSONObject chatObject = chatsArray.getJSONObject(i);

                        Log.d("ColleaguesPageFragment", "Parsing Chat " + (i + 1) + "/" + chatsArray.length());


                        String chatName = chatObject.optString("chat_name", "");
                        String id = chatObject.optString("id", "");
                        String name = chatObject.optString("name", "");
                        String avatarPath = chatObject.optString("avatar_path", "");
                        String online = chatObject.optString("online", "");
                        int channel = chatObject.optInt("channel", 0);
                        int status = chatObject.optInt("status", 0);
                        int channelStatus = chatObject.optInt("channel_status", 0);

                       int message_read = chatObject.optInt("message_read", 0);

                        Log.d("ColleaguesPageFragment", "Extracted Chat: " +
                                "chatName=" + chatName + ", id=" + id + ", name=" + name +
                                ", avatarPath=" + avatarPath + ", online=" + online +
                                ", channel=" + channel + ", status=" + status +
                                ", channelStatus=" + channelStatus);

                        List<ChatMessage> messages = new ArrayList<>();
                        if (chatObject.has("message")) {
                            try {
                                JSONArray messagesArray = new JSONArray(chatObject.getString("message"));
                                Log.d("ColleaguesPageFragment", "Total Messages Found: " + messagesArray.length());

                                for (int j = 0; j < messagesArray.length(); j++) {
                                    JSONObject messageObject = messagesArray.getJSONObject(j);
                                    //    int messageId = messageObject.optInt("id", 0);

                                    int messageId = messageObject.optInt("id", 0);
                                    String text = messageObject.optString("text", "");
                                    String time = messageObject.optString("time", "");
                                    String date = messageObject.optString("date", "");
                                    int unread = messageObject.optInt("unread", 0);

                                    Log.d("ColleaguesPageFragment", "Message " + (j + 1) + ": " +
                                            "id=" + messageId + ", text=" + text +
                                            ", time=" + time + ", date=" + date + ", unread=" + unread);

                                    messages.add(new ChatMessage(messageId, text, time, date, unread));
                                }
                            } catch (JSONException e) {
                                Log.e("ColleaguesPageFragment", "Error parsing messages: " + e.getMessage());
                            }
                        }

                        List<ChatMember> members = new ArrayList<>();
                        if (chatObject.has("members")) {
                            try {
                                JSONArray membersArray = new JSONArray(chatObject.getString("members"));
                                Log.d("ColleaguesPageFragment", "Total Members Found: " + membersArray.length());

                                for (int j = 0; j < membersArray.length(); j++) {
                                    JSONObject memberObject = membersArray.getJSONObject(j);
                                    int memberId = memberObject.optInt("id", 0);
                                    String memberName = memberObject.optString("name", "");
                                    String memberAvatarPath = memberObject.optString("avatar_path", "");
                                    String email = memberObject.optString("email", "");
                                    int lastRead = memberObject.optInt("last_read", 0);

                                    Log.d("ColleaguesPageFragment", "Member " + (j + 1) + ": " +
                                            "id=" + memberId + ", name=" + memberName +
                                            ", avatarPath=" + memberAvatarPath + ", email=" + email +
                                            ", lastRead=" + lastRead);

                                    members.add(new ChatMember(memberId, memberName, memberAvatarPath, email, lastRead));
                                }
                            } catch (JSONException e) {
                                Log.e("ColleaguesPageFragment", "Error parsing members: " + e.getMessage());
                            }
                        }

                        ChatDetails chatDetails = new ChatDetails(chatName, id, name, avatarPath, messages, online, channel, status, channelStatus, members,message_read);
                        chatDetailsList.add(chatDetails);
                    }
                } else {
                    Log.w("ColleaguesPageFragment", "No 'chats' array found in JSON.");
                }
            } else {
                Log.w("ColleaguesPageFragment", "No 'data' object found in JSON.");
            }
        } catch (JSONException e) {
            Log.e("ColleaguesPageFragment", "JSON Parsing Error: " + e.getMessage());
        }



        return chatDetailsList;
    }

     */



/*
curl -X GET "https://aws.customquoter.co.uk/api/m/chats/chat?id=null&channel=1&unread=null" \
-H "Authorization: Bearer 7898|QVu8LPIEoPkdOLqJToYdAYEoE3ydz1Qu95vx4npS" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0"

 */