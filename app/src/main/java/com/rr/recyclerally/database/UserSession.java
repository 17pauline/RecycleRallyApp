package com.rr.recyclerally.database;

import com.rr.recyclerally.model.user.AUser;
import com.rr.recyclerally.model.user.Recycler;

public class UserSession {
    private volatile static UserSession INSTANCE; // thread safe
    private AUser user;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (INSTANCE == null) {
            synchronized (UserSession.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserSession();
                }
            }
        }
        return INSTANCE;
    }

    public AUser getUser() {
        return user;
    }

    public void setUser(AUser user) {
        this.user = user;
    }

    public void clear() {
        this.user = null;
    }

    public boolean isRecycler() {
        return user instanceof Recycler;
    }

    public Recycler getRecycler() {
        if (isRecycler()) {
            return (Recycler) user;
        }
        return null;
    }
}
