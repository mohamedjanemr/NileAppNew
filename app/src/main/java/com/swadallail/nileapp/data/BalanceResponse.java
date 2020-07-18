package com.swadallail.nileapp.data;

import com.google.gson.annotations.SerializedName;

public class BalanceResponse {
    @SerializedName("balance")
    public double balance;

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
