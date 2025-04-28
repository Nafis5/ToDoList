package com.notes.keepnotes;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;

public class TextSizeUtil {

    public static int getEditTextSizeBasedOnScreen(Context context) {
        // Get screen dimensions
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        // Determine screen size category
        int screenSize = context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        // Define default text size and adjust based on screen size
        int defaultTextSize = 18; // default size in sp
        double screenInches=getScreenSize(context);

        // editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultTextSize);
        if (screenInches < 5.7) {// editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultTextSize - 2);
            defaultTextSize -= 2;
            return defaultTextSize;
        } else if (screenInches< 7.0 ) {// editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultTextSize);
           // defaultTextSize = 18;
            return  defaultTextSize;
        } else if (screenInches< 9.0) {// editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultTextSize + 2);
            defaultTextSize += 2;
            return  defaultTextSize;
        } else if (screenInches >= 9.0) {//  editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultTextSize + 4);
            defaultTextSize += 4;
        }
        return  defaultTextSize;
    }
    static double getScreenSize(Context context){
        double screenInches=6.0;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        // Calculate screen size in inches
        double x = Math.pow(metrics.widthPixels / metrics.xdpi, 2);
        double y = Math.pow(metrics.heightPixels / metrics.ydpi, 2);
        screenInches = Math.sqrt(x + y);

        return  screenInches;
    }
}