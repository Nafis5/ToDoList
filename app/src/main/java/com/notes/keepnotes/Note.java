package com.notes.keepnotes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Note {
    private long id;
   private  String title;
   private String content;
   private String date;
   private String time;

    Note(String title,String content,String date, String time){
        this.title = title;
        if(content==null){
            this.content="";
        }
        else{
            this.content = content;
        }

        this.date = date;
        this.time = time;
    }

    Note(long id,String title,String content,String date, String time){
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.time = time;
    }
    Note(){
        // empty constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        if(content==null)content="";
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }
    public String getDayOfWeekFromDate() {
        // Get the current date string in the format "day/month/year"
        String dateString = getDate(); // Assuming getDate() returns a date string like "2/9/2024"

        // Define the date format
        SimpleDateFormat format = new SimpleDateFormat("d/M/yyyy", Locale.ENGLISH);
        String dayOfWeek = "";

        try {
            // Parse the date string into a Date object
            Date date = format.parse(dateString);

            // Format the date to get the abbreviated day of the week in uppercase
            dayOfWeek = new SimpleDateFormat("EEE", Locale.ENGLISH).format(date).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(dayOfWeek.equals("")) return "SUN";

        else return dayOfWeek;
    }
    public String getDayOfMonthFromDate() {
        // Get the current date string in the format "day/month/year"
        String dateString = getDate(); // Assuming getDate() returns a date string like "09/11/2019"

        try {
            // Split the date string using "/" as the delimiter
            String[] dateParts = dateString.split("/");

            // The first part of the array will be the day of the month
            // Parsing it as an integer will remove any leading zeros
            int dayOfMonth = Integer.parseInt(dateParts[0]);

            // Convert the day back to a string
            return String.valueOf(dayOfMonth);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return an empty string if there's an issue parsing the date
        return "1";
    }


    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}


