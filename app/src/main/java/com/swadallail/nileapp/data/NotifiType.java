package com.swadallail.nileapp.data;

import com.google.gson.annotations.SerializedName;

public class NotifiType {
    @SerializedName("message")
    public int message;
    @SerializedName("newOrder")
    public int newOrder;
    @SerializedName("newOffer")
    public int newOffer;
    @SerializedName("offerAccepted")
    public int offerAccepted;
}
