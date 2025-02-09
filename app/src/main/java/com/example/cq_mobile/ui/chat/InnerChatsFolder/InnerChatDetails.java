package com.example.cq_mobile.ui.chat.InnerChatsFolder;



import java.util.List;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InnerChatDetails {

    @SerializedName("chat_name")
    private String chatName;

    private String id;
    private String name;

    @SerializedName("avatar_path")
    private String avatarPath;

    private List<InnerChatMessage> messages;
    private String online;
    private int channel;
    private int status;

    @SerializedName("channel_status")
    private int channelStatus;

    private int unread;
    private int currentPage;
    private Integer blockStatus;
    private int lastPage;

    private List<InnerChatMember> members;



    public InnerChatDetails(String chatName, String id, String name, String avatarPath, List<InnerChatMessage> messages, String online, int channel, int status, int channelStatus, int unread, int currentPage, Integer blockStatus, int lastPage, List<InnerChatMember> members) {
        this.chatName = chatName;
        this.id = id;
        this.name = name;
        this.avatarPath = avatarPath;
        this.messages = messages;
        this.online = online;
        this.channel = channel;
        this.status = status;
        this.channelStatus = channelStatus;
        this.unread = unread;
        this.currentPage = currentPage;
        this.blockStatus = blockStatus;
        this.lastPage = lastPage;
        this.members = members;
    }

    public String getChatName() {
        return chatName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public List<InnerChatMessage> getMessages() {
        return messages;
    }

    public String getOnline() {
        return online;
    }

    public int getChannel() {
        return channel;
    }

    public int getStatus() {
        return status;
    }

    public int getChannelStatus() {
        return channelStatus;
    }

    public int getUnread() {
        return unread;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public Integer getBlockStatus() {
        return blockStatus;
    }

    public int getLastPage() {
        return lastPage;
    }

    public List<InnerChatMember> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return "InnerChatDetails{" +
                "chatName='" + chatName + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                ", messages=" + messages +
                ", online='" + online + '\'' +
                ", channel=" + channel +
                ", status=" + status +
                ", channelStatus=" + channelStatus +
                ", unread=" + unread +
                ", currentPage=" + currentPage +
                ", blockStatus=" + blockStatus +
                ", lastPage=" + lastPage +
                ", members=" + members +
                '}';
    }

    // Method to parse JSON members string
    public static List<InnerChatMember> parseMembersFromJson(String membersJson) {
        Gson gson = new Gson();
        return List.of(gson.fromJson(membersJson, InnerChatMember[].class));
    }
}
