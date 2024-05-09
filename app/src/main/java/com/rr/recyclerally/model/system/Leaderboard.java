package com.rr.recyclerally.model.system;

import com.rr.recyclerally.model.user.Recycler;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class Leaderboard {
    private static TreeMap<Recycler, Integer> leaderboard = new TreeMap<>(Comparator.comparingInt((Recycler recycler) -> recycler.getNumberOfPoints()).reversed());

    public static void updateLeaderboard(Recycler recycler) {
        leaderboard.put(recycler, recycler.getNumberOfPoints());
    }
    public static void displayLeaderboard() {
        System.out.println("-- Leaderboard --");
        for (Map.Entry<Recycler, Integer> entry : leaderboard.entrySet()) {
            System.out.println(entry.getKey().getUsername() + " - " + entry.getValue() + " points");
        }
    }
}
