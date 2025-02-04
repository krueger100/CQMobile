package com.example.cq_mobile.ui.chat;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.cq_mobile.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.cq_mobile.HelperManagers.SharedPreffFolder.SharedPrefManager;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.ui.chat.ColleagueFolder.ColleagueAPIItem;
import com.example.cq_mobile.ui.chat.ColleagueFolder.ColleagueAdapter;
import com.example.cq_mobile.ui.chat.ColleagueFolder.ColleagueManager;


import java.util.List;

public class ColleaguesPageFragment extends Fragment {

    private ColleagueManager colleagueManager;
    private String accessToken;
    private RecyclerView colleaguesRecyclerView;
    private ColleagueAdapter colleagueAdapter;
    private int currentPage = 1;
    private final int pageSize = 20;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_colleagues_page, container, false);

        SharedPrefManager sharedPrefManager = new SharedPrefManager(requireContext());
        String email = sharedPrefManager.getEmail();
        String password = sharedPrefManager.getPassword();

        Log.d("ColleaguesPageFragment", "Email: " + email);
        Log.d("ColleaguesPageFragment", "Password: " + password);

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        colleaguesRecyclerView = view.findViewById(R.id.colleaguesRecyclerView);
        colleaguesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        colleagueManager = new ColleagueManager(getContext());

        if (email != null && password != null) {
            AccessTokenRequest tokenRequest = new AccessTokenRequest(email, password);
            getAccessTokenAndLoadColleagues(tokenRequest, progressBar);
        } else {
            Log.e("ColleaguesPageFragment", "Email or Password is null.");
            progressBar.setVisibility(View.GONE);
        }

        return view;
    }

    private void getAccessTokenAndLoadColleagues(AccessTokenRequest tokenRequest, ProgressBar progressBar) {
        colleagueManager.getAccessToken(tokenRequest, new ColleagueManager.AccessTokenCallback() {
            @Override
            public void onAccessTokenReceived(String token) {
                accessToken = token;
                Log.d("ColleaguesPageFragment", "Access Token received: " + token);
                loadColleaguesWithToken(progressBar,token);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("ColleaguesPageFragment", "Error fetching access token: " + errorMessage);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to get access token", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadColleaguesWithToken(ProgressBar progressBar, String accessToken) {
        if (isLoading || accessToken == null) return;
        isLoading = true;

        colleagueManager.loadColleagues(currentPage, pageSize, new ColleagueManager.AllColleaguesCallback() {
            @Override
            public void onAllColleaguesLoaded(List<ColleagueAPIItem> colleagues, String rawJson) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;

                Log.d("ColleaguesPageFragment", "Received Colleagues: " + rawJson);

                if (colleagues != null && !colleagues.isEmpty()) {
                    displayColleagues(colleagues, accessToken,progressBar);
                    currentPage++;
                } else {
                    Log.e("ColleaguesPageFragment", "No colleagues found.");
                    Toast.makeText(getContext(), "No colleagues found.", Toast.LENGTH_SHORT).show();
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

    private void displayColleagues(List<ColleagueAPIItem> colleagues, String accessToken, ProgressBar progressBar) {
        if (colleagueAdapter == null) {
            colleaguesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            colleagueAdapter = new ColleagueAdapter(requireContext(), colleagues);
            colleaguesRecyclerView.setAdapter(colleagueAdapter);
        } else {
            colleagueAdapter.addColleagues(colleagues);
        }

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
curl -X GET "https://aws.customquoter.co.uk/api/m/chats/chat?id=null&channel=1&unread=null" \
-H "Authorization: Bearer 7898|QVu8LPIEoPkdOLqJToYdAYEoE3ydz1Qu95vx4npS" \
-H "x-api-key: BLSNDC1Blc29jhd4jJ898FPrIS1s6YE2" \
-H "Accept: application/json" \
-H "Content-Type: application/json" \
-H "User-Agent: PostmanRuntime/7.43.0"

 */