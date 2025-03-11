package com.example.cq_mobile.ui.chat.InnerChatsFolder;

import android.content.Context;
import android.util.Log;

import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenApiService;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenRequest;
import com.example.cq_mobile.HelperManagers.getAccessToken.AccessTokenResponse;
import com.example.cq_mobile.HelperManagers.getAccessToken.RetrofitClientAccessToken;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageManager {
    private final String baseUrl = "https://aws.customquoter.co.uk/";
    private String accessToken;
    private final Context context;
    Call<AccessTokenResponse> call;
    Call<MessageAPIResponse> call2;

    public MessageManager(Context context) {
        this.context = context;
    }

    public void getAccessToken(AccessTokenRequest request, final AccessTokenCallback callback) {
        AccessTokenApiService apiService = RetrofitClientAccessToken.getRetrofitInstance().create(AccessTokenApiService.class);
        call = apiService.AccessTokenUser(request);

        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    accessToken = response.body().getAccessToken();
                    if (accessToken != null) {
                        Log.d("MessageManager", "Access Token: " + accessToken);
                        callback.onAccessTokenReceived(accessToken);
                    } else {
                        callback.onError("Access token not received.");
                    }
                } else {
                    callback.onError("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                callback.onError("Failure: " + t.getMessage());
            }
        });
    }

    public void loadMessages(int messageID, int channel, final AllChatsCallback callback) {
        if (accessToken == null) {
            callback.onError("Access token is missing.");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MessageApi chatApi = retrofit.create(MessageApi.class);

        call2 = chatApi.getChats(messageID, channel, "", "Bearer " + accessToken);

        call2.enqueue(new Callback<MessageAPIResponse>() {
            @Override
            public void onResponse(Call<MessageAPIResponse> call2, Response<MessageAPIResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String rawJson = new Gson().toJson(response.body());
                    Log.d("MessageManager", "Raw JSON Response: " + rawJson);

                    if (response.body().getData() == null) {
                        callback.onError("API data is null.");
                        return;
                    }

                    Contact contact = response.body().getData().getContact();
                    if (contact == null) {
                        callback.onError("Contact data is missing.");
                        return;
                    }

                    Map<String, List<InnerChatMessage>> messages = contact.getMessages();
                    if (messages == null || messages.isEmpty()) {
                        callback.onError("No messages found.");
                        return;
                    }

                    // Convert messages to a List<InnerChatAPIItem>
                    List<InnerChatAPIItem> chatItems = new ArrayList<>();
                    for (Map.Entry<String, List<InnerChatMessage>> entry : messages.entrySet()) {
                        for (InnerChatMessage innerChatMessage : entry.getValue()) {
                            InnerChatAPIItem chatItem = new InnerChatAPIItem();
                            chatItem.setId(innerChatMessage.getId());
                            chatItem.setSender(innerChatMessage.getMessage().getSender());  // Extract sender from nested message object
                            chatItem.setMessage(innerChatMessage.getMessage().getText());  // Extract text from nested message object
                            chatItem.setMessage_read(innerChatMessage.getMessage().getTime()); // Extract time
                            chatItem.setMessage_read(innerChatMessage.getMessage().getAvatar()); // Extract Avatar
                            chatItem.setMessage_read(innerChatMessage.getMessage().getName()); // Extract Avatar
                            chatItem.setMessage_read(innerChatMessage.getMessage().getDate());


                            chatItems.add(chatItem);
                        }
                    }

                    callback.onAllChatsLoaded(chatItems, rawJson);
                } else {
                    callback.onError("API error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<MessageAPIResponse> call2, Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
        });
    }

    public void cancelAccessTokenCall() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }
    }

    public void cancelChatLoadingCall() {
        if (call2 != null && !call2.isCanceled()) {
            call2.cancel();
        }
    }

    public void cancelAllCalls() {
        cancelAccessTokenCall();
        cancelChatLoadingCall();
    }



    public interface AccessTokenCallback {
        void onAccessTokenReceived(String accessToken);
        void onError(String errorMessage);
    }

    public interface AllChatsCallback {
        void onAllChatsLoaded(List<InnerChatAPIItem> chats, String rawJson);
        void onError(String errorMessage);
    }


}

//      MessageApi chatApi = retrofit.create(MessageApi.class);
//        call2 = chatApi.getChats(page, pageSize, accessToken, "Bearer " + accessToken);
