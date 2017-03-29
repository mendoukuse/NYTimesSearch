package com.codepath.nytimessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by christine_nguyen on 3/28/17.
 */

public class Article implements Serializable {
    String webUrl;
    String headline;
    String thumbnail;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() >= 1) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbnail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                this.thumbnail = "";
            }
        } catch (JSONException e) {

        }
    }

    public static ArrayList<Article> fromJsonArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return results;
    }
}
