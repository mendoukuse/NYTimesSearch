package com.codepath.nytimessearch.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

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
}
