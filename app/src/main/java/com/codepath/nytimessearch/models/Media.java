package com.codepath.nytimessearch.models;

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
}
