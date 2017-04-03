package com.codepath.nytimessearch.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by christine_nguyen on 4/1/17.
 */

@Parcel
public class Media {
    String url;
    String format;
    int height;
    int widgth;
    String type;
    String subtype;
    String caption;
    String copyright;

    public Media() {}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFormat() {
        return format;
    }

    public int getHeight() {
        return height;
    }

    public int getWidgth() {
        return widgth;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getCaption() {
        return caption;
    }

    public String getCopyright() {
        return copyright;
    }

    static Media fromJson(JSONObject multimediaJson) {
        Media m = new Media();

        try {
            m.setUrl(multimediaJson.getString("url"));
            // Don't care about other fields
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return m;
    }
}
