package com.rr.recyclerally.model.system;

import com.rr.recyclerally.utils.DateConverter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Challenge implements Serializable {
    private int itemsNumber;
    private EItemType itemType;
    private String startDate;
    private String endDate;
    private Map<String, Integer> participants;

    public Challenge() {
    }

    public Challenge(int itemsNumber, EItemType itemType, String endDate) {
        this.itemsNumber = itemsNumber;
        this.itemType = itemType;
        this.startDate = DateConverter.fromDate(new Date());
        this.endDate = endDate;
        this.participants = new HashMap<>();
    }

    public int getItemsNumber() {
        return itemsNumber;
    }

    public void setItemsNumber(int itemsNumber) {
        this.itemsNumber = itemsNumber;
    }

    public EItemType getItemType() {
        return itemType;
    }

    public void setItemType(EItemType itemType) {
        this.itemType = itemType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Map<String, Integer> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Integer> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "itemsNumber=" + itemsNumber +
                ", itemType=" + itemType +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", participants=" + participants +
                '}';
    }
}
