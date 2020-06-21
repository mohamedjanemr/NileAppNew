package com.swadallail.nileapp.data;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationView<T>  {
    @SerializedName("body")
    public String body;
    @SerializedName("title")
    public String title;
    @SerializedName("refId")
    public String refId;
    @SerializedName("type")
    public int type;
}
