package com.example.cq_mobile.ui.chat.ChatFolder;

import java.util.List;

public class ChatAPIResponse {
    private Data data;


    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private List<ChatAPIItem> chats;

        public List<ChatAPIItem> getChats() {
            return chats;
        }

        public void setChats(List<ChatAPIItem> chats) {
            this.chats = chats;
        }
    }


}
