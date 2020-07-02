package com.swadallail.nileapp.data;

import com.google.gson.annotations.SerializedName;

public class GetOffersResponse {
    @SerializedName("img")
    public String img;
    @SerializedName("description")
    public String description;
    @SerializedName("hours")
    public int hours;
    @SerializedName("fromLat")
    public String fromLat;
    @SerializedName("fromLng")
    public String fromLng;
    @SerializedName("toLat")
    public String toLat;
    @SerializedName("toLng")
    public String toLng;
    @SerializedName("fromAddress")
    public String fromAddress;
    @SerializedName("toAddress")
    public String toAddress;
    @SerializedName("editable")
    public Boolean editable;
    @SerializedName("orderId")
    public String orderId;
    @SerializedName("ownerName")
    public String ownerName;
    @SerializedName("ownerId")
    public String ownerId;
    @SerializedName("represtiveId")
    public String represtiveId;
    @SerializedName("represtiveName")
    public String represtiveName;
    @SerializedName("ownerRate")
    public float ownerRate;
    @SerializedName("represtiveRate")
    public float represtiveRate;
    @SerializedName("status")
    public String status;
    @SerializedName("state")
    public String state;
    @SerializedName("fromDistance")
    public String fromdis;
    @SerializedName("toDistance")
    public String todis;
    @SerializedName("ownerMobile")
    public String ownerMobile;
    @SerializedName("represtiveMobile")
    public String represtiveMobile;
    @SerializedName("ownerTotalRate")
    public int ownerTotalRate;
    @SerializedName("represtiveTotalRate")
    public int represtiveTotalRate;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public String getFromLat() {
        return fromLat;
    }

    public void setFromLat(String fromLat) {
        this.fromLat = fromLat;
    }

    public String getFromLng() {
        return fromLng;
    }

    public void setFromLng(String fromLng) {
        this.fromLng = fromLng;
    }

    public String getToLat() {
        return toLat;
    }

    public void setToLat(String toLat) {
        this.toLat = toLat;
    }

    public String getToLng() {
        return toLng;
    }

    public void setToLng(String toLng) {
        this.toLng = toLng;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getReprestiveId() {
        return represtiveId;
    }

    public void setReprestiveId(String represtiveId) {
        this.represtiveId = represtiveId;
    }

    public String getReprestiveName() {
        return represtiveName;
    }

    public void setReprestiveName(String represtiveName) {
        this.represtiveName = represtiveName;
    }

    public float getOwnerRate() {
        return ownerRate;
    }

    public void setOwnerRate(float ownerRate) {
        this.ownerRate = ownerRate;
    }

    public float getReprestiveRate() {
        return represtiveRate;
    }

    public void setReprestiveRate(float represtiveRate) {
        this.represtiveRate = represtiveRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFromdis() {
        return fromdis;
    }

    public void setFromdis(String fromdis) {
        this.fromdis = fromdis;
    }

    public String getTodis() {
        return todis;
    }

    public void setTodis(String todis) {
        this.todis = todis;
    }

    public String getOwnerMobile() {
        return ownerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile;
    }

    public String getReprestiveMobile() {
        return represtiveMobile;
    }

    public void setReprestiveMobile(String represtiveMobile) {
        this.represtiveMobile = represtiveMobile;
    }

    public int getOwnerTotalRate() {
        return ownerTotalRate;
    }

    public void setOwnerTotalRate(int ownerTotalRate) {
        this.ownerTotalRate = ownerTotalRate;
    }

    public int getReprestiveTotalRate() {
        return represtiveTotalRate;
    }

    public void setReprestiveTotalRate(int represtiveTotalRate) {
        this.represtiveTotalRate = represtiveTotalRate;
    }
}
