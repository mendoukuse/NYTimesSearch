package com.codepath.nytimessearch.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by christine_nguyen on 4/1/17.
 */
@Parcel
public class Filters {
    String sortOrder;
    Calendar beginDate;
    ArrayList<String> categories;

    public Filters() {}

    public static Filters createNewFilters() {
        return new Filters(SortOrder.NEWEST.getName(), null, new ArrayList());
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
