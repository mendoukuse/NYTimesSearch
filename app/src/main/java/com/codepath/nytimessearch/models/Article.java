package com.codepath.nytimessearch.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName("web_url")
    String webUrl;
    Headline headline;

    ArrayList<Media> multimedia;

    String thumbnail;
    String snippet;
    @SerializedName("news_desk")
    String newsDesk;

    public Article() {}

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline.getMain();
    }

    public String getThumbnail() {
        if (thumbnail == null && multimedia != null && multimedia.size() > 0) {
            Media media = multimedia.get(0);
            thumbnail = "http://www.nytimes.com/" + media.getUrl();
        } else {
            thumbnail = "";
        }
        return thumbnail;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getNewsDesk() {
        if (!(TextUtils.isEmpty(newsDesk) || newsDesk.toLowerCase().equals("null") ||
                newsDesk.toLowerCase().equals("none"))) {
            return newsDesk;
        }
        return null;
    }

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = new Headline();
            this.headline.setMain(jsonObject.getJSONObject("headline").getString("main"));
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
