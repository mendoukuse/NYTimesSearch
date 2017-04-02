package com.codepath.nytimessearch.network.models;

import com.codepath.nytimessearch.models.Headline;
import com.google.gson.annotations.SerializedName;

/**
 * Created by christine_nguyen on 4/1/17.
 */

// To delete

public class NYTimesArticle {
    @SerializedName("web_url")
    String webUrl;
    Headline headline;
    String thumbnail;
    String snippet;
    @SerializedName("news_desk")
    String newsDesk;

    public NYTimesArticle() {}

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline.getMain();
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

}
