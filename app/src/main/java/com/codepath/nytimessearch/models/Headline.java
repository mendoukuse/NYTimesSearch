package com.codepath.nytimessearch.models;

import org.parceler.Parcel;

/**
 * Created by christine_nguyen on 4/1/17.
 */

@Parcel
public class Headline {
    String main;

    public Headline() {}

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }
}
