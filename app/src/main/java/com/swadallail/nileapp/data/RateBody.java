package com.swadallail.nileapp.data;

public class RateBody {
    public int rate ;
    public String  userId ;
    public int  orderId ;

    public RateBody(int rate, String userId , int orderId) {
        this.rate = rate;
        this.userId = userId;
        this.orderId = orderId;
    }
}
