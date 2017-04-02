package com.codepath.nytimessearch.models;

import com.codepath.nytimessearch.R;

/**
 * Created by christine_nguyen on 4/1/17.
 */

public enum NewsDesk {
    ART_AND_DESIGN("Arts / Art & Design", R.color.tagColorOne),
    ARTS_AND_LEISURE("Arts&Leisure", R.color.tagColorFive),
    FASHION_AND_SYTLE("Fashion & Style", R.color.tagColorSix),
    SPORTS("Sports", R.color.tagColorTwo),
    // Generic category
    ART("Art", R.color.tagColorFour);

    private String label;
    private int color;

    NewsDesk(String label, int color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public int getColor() {
        return color;
    }

    public static int getColorForNewsDesk(String label) {
        NewsDesk newsDesk = null;
        for (NewsDesk n : values()) {
            if (label.toLowerCase().contains(n.getLabel().toLowerCase())) {
                newsDesk = n;
                break;
            }
        }
        if (newsDesk == null) {
            return R.color.colorPrimaryDark;
        }
        return newsDesk.getColor();
    }
}
