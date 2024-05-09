package com.rr.recyclerally.model.user;

import com.rr.recyclerally.model.system.Challenge;

import java.util.ArrayList;
import java.util.List;

public class Admin extends AUser implements IAdmin {
    private List<Challenge> challenges;

    public Admin(String email, String username, String password, EUserType userType) {
        super(email, username, password, userType);
        this.challenges = new ArrayList<>();
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    @Override
    public void registerChallenge(Challenge challenge) {
        challenges.add(challenge);
        System.out.println(challenge);
    }

    @Override
    public String toString() {
        return "Admin{" +
                super.toString() +
                '}';
    }
}
