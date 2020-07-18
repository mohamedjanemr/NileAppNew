package com.swadallail.nileapp.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ChatResponse<T> {
    @SerializedName("chatId")
    public int chatId;
    @SerializedName("users")
    public ArrayList<T> users;
    @SerializedName("messages")
    public T messages;

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }
}
