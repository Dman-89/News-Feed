package com.example.android.newsfeed;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class News {

    private String mSectionName;
    private String mTitle;
    private String mAuthor = null;
    private String mDatePublished;
    private String mUrl;


    public News(String sectionName, String title, String url, String datePublished) {
        this.mSectionName = sectionName;
        this.mTitle = title;
        this.mUrl = url;
        this.mDatePublished = datePublished;
    }

    public News(String sectionName, String title, String url, String datePublished, String author) {
        this.mSectionName = sectionName;
        this.mTitle = title;
        this.mUrl = url;
        this.mDatePublished = datePublished;
        this.mAuthor = author;
    }


    public String getSectionName() {
        return mSectionName;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDatePublished() {
        return mDatePublished;
    }

}