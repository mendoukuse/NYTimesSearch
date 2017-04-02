package com.codepath.nytimessearch.network.models;

import com.codepath.nytimessearch.models.Article;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 * Created by christine_nguyen on 4/1/17.
 */

public class NYTimesSearchResponse {
    NYTimesSearchResponseMeta meta;
    ArrayList<Article> docs;

    public NYTimesSearchResponseMeta getMeta() {
        return meta;
    }

    public ArrayList<Article> getDocs() {
        return docs;
    }

    // public constructor is necessary for collections
    public NYTimesSearchResponse() {
        docs = new ArrayList<>();
    }

    public static NYTimesSearchResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        NYTimesSearchResponse nytSearchResponse = gson.fromJson(response, NYTimesSearchResponse.class);
        return nytSearchResponse;
    }
}
