package com.codepath.nytimessearch.models;

import java.util.ArrayList;

/**
 * Created by christine_nguyen on 4/1/17.
 */

public enum SortOrder {
    NEWEST("Newest"),
    OLDEST("Oldest");

    private final String name;

    SortOrder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public static ArrayList<String> getNames() {
        ArrayList arr = new ArrayList();

        for (SortOrder s : values()) {
            arr.add(s.getName());
        }

        return arr;
    }

    public static SortOrder getFromName(String name) {
        SortOrder order = NEWEST;
        for (SortOrder s : values()) {
            if (s.getName() == name) {
                order = s;
                break;
            }
        }
        return order;
    }
}
