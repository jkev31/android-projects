package com.example.peoplecounter;

import android.provider.Contacts;
public class PeopleControl {
    private int lCounter;
    private int pCounter;

    public void add(boolean p) {
        if (p) {
            lCounter++;
        } else {
            pCounter++;
        }

    }

    public int getTotal() {
        return lCounter + pCounter;
    }

    public PeopleControl() {
        lCounter = 0;
        pCounter = 0;
    }

    public int getLCounter() {
        return lCounter;
    }

    public int getPCounter() {
        return pCounter;
    }
}
