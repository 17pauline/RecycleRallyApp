package com.rr.recyclerally.database;

public interface Callback<R> {
    void runResultOnUiThread(R result);
}
