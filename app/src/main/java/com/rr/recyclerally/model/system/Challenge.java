package com.rr.recyclerally.model.system;

import java.util.Date;

public class Challenge {
    private static final int MAX_ITEMS = 10;
    private EItemType itemType;
    private Date startDate;
    private Date endDate;

    public Challenge(EItemType itemType, Date startDate, Date endDate) {
        this.itemType = itemType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public EItemType getItemType() {
        return itemType;
    }

    public void setItemType(EItemType itemType) {
        this.itemType = itemType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getMaxItems() {
        return MAX_ITEMS;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "itemType=" + itemType +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
