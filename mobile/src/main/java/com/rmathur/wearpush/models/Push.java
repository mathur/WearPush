package com.rmathur.wearpush.models;

import com.orm.SugarRecord;

import java.io.File;
import java.util.Date;

public class Push extends SugarRecord implements Comparable<Push> {

    String title;
    Date date;

    public Push() {

    }

    public Push(String text, Date date){
        this.title = text;
        this.date = date;
    }

    public Push(File file, Date time){
        // ayy lmao
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(Push another) {
        // reverse ordering
        return -1 * this.getDate().compareTo(another.getDate());
    }
}