package com.notes.keepnotes;

public class CommonAttributes {
    private static boolean isDarkThemeOn=false;


    public CommonAttributes(){

    }
    void setDarkThemeStatus(boolean isDarkThemeOn){
        this.isDarkThemeOn=isDarkThemeOn;
    }
    public boolean getThemeStatus(){
        return isDarkThemeOn;
    }
}
