package com.codepath.nytimessearch.models;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by christine_nguyen on 3/28/17.
 */
@Parcel
public class Article {
    String webUrl;
    String headline;
    String thumbnail;
    String snippet;
    String newsDesk;

    public Article() {}

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getNewsDesk() {
        return newsDesk;
    }

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");
            this.snippet = jsonObject.getString("snippet");

            String newsDesk = jsonObject.getString("news_desk");
            if (!(TextUtils.isEmpty(newsDesk) || newsDesk.toLowerCase().equals("null") ||
                    newsDesk.toLowerCase().equals("none"))) {
                this.newsDesk = newsDesk;
            }

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
