package com.rr.recyclerally.model.system;

import java.util.Date;

public class SuperChallenge extends Challenge {
    private static final int SUPER_MAX_ITEMS = 30;

    public SuperChallenge(EItemType itemType, Date startDate, Date endDate) {
        super(itemType, startDate, endDate);
    }

    @Override
    public int getMaxItems() {
        return SUPER_MAX_ITEMS;
    }

    @Override
    public String toString() {
        return "Super{" +
                super.toString() +
                '}';
    }
}
