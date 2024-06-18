package com.rr.recyclerally.model.system;

import com.rr.recyclerally.utils.DateConverter;

import java.io.Serializable;
import java.util.Date;

public class RecycledItem implements Serializable {
    private String imageURL;
    private String algorithmClassification;
    private EItemType itemType;
    private String datePosted;
    private boolean registeredInChallenge;
    private boolean verifiedByAdmin;

    public RecycledItem() {
    }

    public RecycledItem(String imageURL, EItemType itemType, String algorithmClassification) {
        this.imageURL = imageURL;
        this.itemType = itemType;
        this.algorithmClassification = algorithmClassification;
        this.datePosted = DateConverter.fromDate(new Date());
        this.registeredInChallenge = false;
        this.verifiedByAdmin = false;
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

    public String getAlgorithmClassification() {
        return algorithmClassification;
    }

    public void setAlgorithmClassification(String algorithmClassification) {
        this.algorithmClassification = algorithmClassification;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public boolean isRegisteredInChallenge() {
        return registeredInChallenge;
    }

    public void setRegisteredInChallenge(boolean registeredInChallenge) {
        this.registeredInChallenge = registeredInChallenge;
    }

    public boolean isVerifiedByAdmin() {
        return verifiedByAdmin;
    }

    public void setVerifiedByAdmin(boolean verifiedByAdmin) {
        this.verifiedByAdmin = verifiedByAdmin;
    }

    @Override
    public String toString() {
        return "RecycledItem{" +
                "imageURL='" + imageURL + '\'' +
                ", algorithmClassification='" + algorithmClassification + '\'' +
                ", itemType=" + itemType +
                ", datePosted='" + datePosted + '\'' +
                ", registeredInChallenge=" + registeredInChallenge +
                ", verifiedByAdmin=" + verifiedByAdmin +
                '}';
    }
}
