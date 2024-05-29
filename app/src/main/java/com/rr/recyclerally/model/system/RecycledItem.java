package com.rr.recyclerally.model.system;

import java.util.Date;

public class RecycledItem {
    private String imageURL;
    private EItemType itemType;
    private boolean algorithmFeedback;
    private Date datePosted;
    private boolean registeredInChallenge;

    public RecycledItem(String imageURL, EItemType itemType) {
        this.imageURL = imageURL;
        this.itemType = itemType;
        this.datePosted = new Date();
        this.registeredInChallenge = false;
    }

    public RecycledItem(String imageURL, EItemType itemType, boolean algorithmFeedback, Date datePosted, boolean registeredInChallenge) {
        this.imageURL = imageURL;
        this.itemType = itemType;
        this.algorithmFeedback = algorithmFeedback;
        this.datePosted = datePosted;
        this.registeredInChallenge = registeredInChallenge;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public EItemType getItemType() {
        return itemType;
    }

    public void setItemType(EItemType itemType) {
        this.itemType = itemType;
    }

    public boolean isAlgorithmFeedback() {
        return algorithmFeedback;
    }

    public void setAlgorithmFeedback(boolean algorithmFeedback) {
        this.algorithmFeedback = algorithmFeedback;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public boolean isRegisteredInChallenge() {
        return registeredInChallenge;
    }

    public void setRegisteredInChallenge(boolean registeredInChallenge) {
        this.registeredInChallenge = registeredInChallenge;
    }

    @Override
    public String toString() {
        return "RecycledItem{" +
                ", itemType=" + itemType +
                ", algorithmFeedback=" + algorithmFeedback +
                ", datePosted=" + datePosted +
                ", registeredInChallenge=" + registeredInChallenge +
                '}';
    }
}
