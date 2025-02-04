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
import com.example.cq_mobile.ui.chat.ChatFolder.ChatManager;

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_page, container, false);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(requireContext());
        String email = sharedPrefManager.getEmail();
        String password = sharedPrefManager.getPassword();

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
                setProgressBarVisibility(false,progressBar);
                isLoading = false;

                if (chats != null && !chats.isEmpty()) {
                    displayChats(chats, token,progressBar);
                    currentPage++;  // Increment page after successful load
                } else {
                    Log.d("ChatPageFragment", "No chats received.");
                }
            }

            @Override
            public void onError(String errorMessage) {
                setProgressBarVisibility(false,progressBar);

                isLoading = false;
                Log.e("ChatPageFragment", "Chat Loading Error: " + errorMessage);
            }
        });
    }

    private void displayChats(List<ChatAPIItem> chats, String token, ProgressBar progressBar) {
        if (chatAdapter == null) {
          chatRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            chatAdapter = new ChatAdapter(requireContext(), chats);
            chatRecyclerView.setAdapter(chatAdapter);
        } else {
            chatAdapter.addChats(chats);
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