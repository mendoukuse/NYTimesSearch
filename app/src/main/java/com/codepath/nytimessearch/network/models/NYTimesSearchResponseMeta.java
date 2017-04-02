package com.codepath.nytimessearch.network.models;

/**
 * Created by christine_nguyen on 4/1/17.
 */

public class NYTimesSearchResponseMeta {
    int hits;
    int time;
    int offset;

    public int getHits() {
        return hits;
    }

    public int getTime() {
        return time;
    }

    public int getOffset() {
        return offset;
    }
}
