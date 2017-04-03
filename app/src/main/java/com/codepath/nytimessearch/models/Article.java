package com.codepath.nytimessearch.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by christine_nguyen on 3/28/17.
 */
@Parcel
public class Article {
    @SerializedName("web_url")
    String webUrl;
    Headline headline;

    ArrayList<Media> multimedia;

    String snippet;
    @SerializedName("news_desk")
    String newsDesk;
    @SerializedName("pub_date")
    String pubDate;

    public Article() {}

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline.getMain();
    }

    public String getThumbnail() {
        int numImages = multimedia == null ? 0 : multimedia.size();
        String thumbnail = null;
        if (thumbnail == null && numImages > 0) {
            int mediaIndex = new Random().nextInt(numImages);
            Media media = multimedia.get(mediaIndex);
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
        if (!(TextUtils.isEmpty(newsDesk) || newsDesk.toLowerCase().equals("null"))) {
            return newsDesk;
        }
        return null;
    }

    public ArrayList<Media> getMultimedia() {
        return multimedia;
    }

    public String getPubDate() {
        return pubDate;
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
                this.multimedia = new ArrayList<Media>();
                for (int x = 0; x < multimedia.length(); x++) {
                    JSONObject multimediaJson = multimedia.getJSONObject(x);
                    Media media = Media.fromJson(multimediaJson);

                    this.multimedia.add(media);
                }
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
