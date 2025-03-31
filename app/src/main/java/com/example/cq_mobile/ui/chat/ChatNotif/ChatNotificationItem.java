package com.example.cq_mobile.ui.chat.ChatNotif;
import com.example.cq_mobile.ui.chat.ChatFolder.ChatDetails;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ChatNotificationItem {
    private String sender;
    private String text;
    private String avatar;
    private String initials;
    private int channel;
    @SerializedName("channelname")
    private String channelName;
    private String date;
    private String time;
    private String files;

    // Constructor
    public ChatNotificationItem(String sender, String text, String avatar, String initials, int channel,
                                String channelName, String date, String time, String files) {
        this.sender = sender;
        this.text = text;
        this.avatar = avatar;
        this.initials = initials;
        this.channel = channel;
        this.channelName = channelName;
        this.date = date;
        this.time = time;
        this.files = files;
    }

    // Getters
    public String getSender() { return sender; }
    public String getText() { return text; }
    public String getAvatar() { return avatar; }
    public String getInitials() { return initials; }
    public int getChannel() { return channel; }
    public String getChannelName() { return channelName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getFiles() { return files; }

    public static List<Integer> getChannels(List<ChatDetails> chatDetailsList) {
        List<Integer> channels = new ArrayList<>();
        for (ChatDetails chatDetails : chatDetailsList) {
            channels.add(chatDetails.getChannel());
        }
        return channels;
    }

    public static class ChatNotificationItemDeserializer implements JsonDeserializer<ChatNotificationItem> {
        @Override
        public ChatNotificationItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String sender = obj.has("sender") && !obj.get("sender").isJsonNull() ? obj.get("sender").getAsString() : "";
            String text = obj.has("text") && !obj.get("text").isJsonNull() ? obj.get("text").getAsString() : "";
            String avatar = obj.has("avatar") && !obj.get("avatar").isJsonNull() ? obj.get("avatar").getAsString() : "";
            String initials = obj.has("initials") && !obj.get("initials").isJsonNull() ? obj.get("initials").getAsString() : "";
            int channel = obj.has("channel") && obj.get("channel").isJsonPrimitive() ? obj.get("channel").getAsInt() : 0;
            String channelName = obj.has("channelname") && !obj.get("channelname").isJsonNull() ? obj.get("channelname").getAsString() : "";
            String date = obj.has("date") && !obj.get("date").isJsonNull() ? obj.get("date").getAsString() : "";
            String time = obj.has("time") && !obj.get("time").isJsonNull() ? obj.get("time").getAsString() : "";

            // Handle 'files' field that can be String, Array, or Object
            JsonElement filesElement = obj.get("files");
            String files = "";
            if (filesElement != null && !filesElement.isJsonNull()) {
                if (filesElement.isJsonArray()) {
                    List<String> fileList = new ArrayList<>();
                    for (JsonElement element : filesElement.getAsJsonArray()) {
                        if (element.isJsonPrimitive()) {
                            fileList.add(element.getAsString());
                        }
                    }
                    files = String.join(",", fileList);
                } else if (filesElement.isJsonPrimitive()) {
                    files = filesElement.getAsString();
                } else if (filesElement.isJsonObject()) {
                    files = filesElement.toString();
                }
            }

            return new ChatNotificationItem(sender, text, avatar, initials, channel, channelName, date, time, files);
        }
    }
}
