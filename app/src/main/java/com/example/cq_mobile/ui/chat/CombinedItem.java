package com.example.cq_mobile.ui.chat;

import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.example.cq_mobile.ui.chat.ColleagueFolder.ColleagueAPIItem;

public class CombinedItem {
    private ColleagueAPIItem colleague;
    private ChatDetails chat;

    public CombinedItem(ColleagueAPIItem colleague, ChatDetails chat) {
        this.colleague = colleague;
        this.chat = chat;
    }

    public ColleagueAPIItem getColleague() {
        return colleague;
    }

    public ChatDetails getChat() {
        return chat;
    }
}
