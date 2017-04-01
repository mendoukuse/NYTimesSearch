package com.codepath.nytimessearch.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by christine_nguyen on 4/1/17.
 */

public class Filters implements Serializable {
    String sortOrder;
    Calendar beginDate;
    ArrayList<String> categories;

    public Filters() {
        sortOrder = SortOrder.NEWEST.getName();
        categories = new ArrayList<>();
    }

    public Filters(String sortOrder, Calendar beginDate, ArrayList<String> categories) {
        this.sortOrder = sortOrder;
        this.beginDate = beginDate;
        this.categories = categories;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Calendar getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Calendar beginDate) {
        this.beginDate = beginDate;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    public void removeCategory(String category) {
        categories.remove(category);
    }
}
