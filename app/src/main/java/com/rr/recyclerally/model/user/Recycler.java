package com.rr.recyclerally.model.user;

import com.rr.recyclerally.model.system.RecycledItem;

import java.util.ArrayList;
import java.util.List;

public class Recycler extends AUser implements IRecycler{
    private int numberOfPoints;
    private List<RecycledItem> recycledItems;

    public Recycler(String email, String username, String password, EUserType userType) {
        super(email, username, password, userType);
        this.numberOfPoints++;
        this.recycledItems = new ArrayList<>();
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public List<RecycledItem> getRecycledItems() {
        return recycledItems;
    }

    public void setRecycledItems(List<RecycledItem> recycledItems) {
        this.recycledItems = recycledItems;
    }

    @Override
    public void postItem(RecycledItem recycledItem) {
        recycledItems.add(recycledItem);
        this.numberOfPoints+=5;
        System.out.println(recycledItem);
    }

    @Override
    public String toString() {
        return "Recycler{" +
                super.toString() +
                "numberOfPoints=" + numberOfPoints +
                ", recycledItems=" + recycledItems +
                '}';
    }
}
