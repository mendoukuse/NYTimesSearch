package com.codepath.nytimessearch.network.models;

/**
 * Created by christine_nguyen on 4/1/17.
 */

public class NYTimesApiResponse {
    NYTimesSearchResponse response;
    String status;
    String copyright;
    String message;

    public NYTimesSearchResponse getResponse() {
        return response;
    }

    public String getStatus() {
        return status;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getMessage() {
        return message;
    }
}
