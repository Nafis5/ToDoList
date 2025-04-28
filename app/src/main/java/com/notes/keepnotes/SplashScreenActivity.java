package com.notes.keepnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;


public class SplashScreenActivity extends AppCompatActivity {
    ImageView imageView;
    private static int SPLASH = 3000;
    private static final String PREF_FIRST_APP_LAUNCH = "FirstAppLaunch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageView = findViewById(R.id.imageView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if(isFirstTimeLaunch(SplashScreenActivity.this)) startActivity(new Intent(SplashScreenActivity.this,ReminderActivity.class));

                else{
                    startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));

                }

                finish();
            }
        }, SPLASH);

    }
    public static boolean isFirstTimeLaunch(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppUsagePrefs", Context.MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTimeLaunch", true);

        if (isFirstTime) {
            // It's the first time launching the app, update the flag
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstTimeLaunch", false);
            editor.apply();

            return true;
        }


        else{

            return false;
        }
    }

}