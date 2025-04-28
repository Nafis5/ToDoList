package com.notes.keepnotes;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private Button setReminderButton, skipButton;
    private TextView reminderTextView;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Switch reminderSwitch;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);



        timePicker = findViewById(R.id.timePicker);
        setReminderButton = findViewById(R.id.setReminderButton);
        skipButton = findViewById(R.id.skipButton);
        reminderTextView = findViewById(R.id.reminderTextView);
        reminderSwitch = findViewById(R.id.reminderSwitch);
        if(getReminderFromSharedPreferences()) reminderSwitch.setChecked(true);
        else reminderSwitch.setChecked(false);
        reminderTextView.setText(getReminderTimeFromSharedPreferences());



        createNotificationChannel();
        if(!checkPermissions()) requestPermissions();



        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDailyReminder();
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 // Go back to the main activity without setting a reminder
                startActivity(new Intent(ReminderActivity.this,MainActivity.class));
            }
        });
        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Enable the reminder

                setDailyReminder();
                setReminderInSharedPreferences(true);
            } else {
                // Disable the reminder

                cancelDailyReminder();
                setReminderInSharedPreferences(false);
            }
        });

    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED;
    }


    private void setDailyReminder() {
        try {
            // Get the selected time from the TimePicker
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            calendar.set(Calendar.SECOND, 0);

            // Create an intent to trigger the reminder
            Intent intent = new Intent(ReminderActivity.this, ReminderReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(
                    ReminderActivity.this, 0, intent, PendingIntent.FLAG_MUTABLE);

            // Set up the alarm manager for daily repetition
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                );

                reminderTextView.setText("Reminder set for " + timePicker.getHour() + ":" + timePicker.getMinute());
                setReminderTimeInSharedPreferences("Reminder set for " + timePicker.getHour() + ":" + timePicker.getMinute());
                Toast.makeText(this,"Daily Reminder set to "+timePicker.getHour() + ":" + timePicker.getMinute(),Toast.LENGTH_LONG).show();
                Log.d("SetReminderActivity", "Reminder successfully set for " + timePicker.getHour() + ":" + timePicker.getMinute());
            } else {
                Log.e("SetReminderActivity", "AlarmManager is null");
            }
        } catch (Exception e) {
            // Log the error with the exception message and stack trace
            Log.e("SetReminderActivity", "Error setting reminder: " + e.getMessage());
            e.printStackTrace(); // Optionally, print the stack trace for more detailed logs
        }
        startActivity(new Intent(ReminderActivity.this,MainActivity.class));
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "DairyReminderChannelName";
            String description = "Channel For Dairy Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("DairyReminderChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


    }


    private void requestPermissions() {
        // Request both read and write permissions
        String[] permissions = new String[]{
                Manifest.permission.POST_NOTIFICATIONS,

        };

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Check if all permissions are granted
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                // Permission granted, you can proceed with file operations.

            } else {
                // Permission denied. Handle the situation accordingly.
                Toast.makeText(this, "Notification permission is needed inorder to remind you", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void cancelDailyReminder() {

          if (alarmManager != null) {
              alarmManager.cancel(pendingIntent);
              Log.e("SetReminderActivity", "Reminder turned off succesfully ");
          }

      //  reminderTextView.setText("Reminder is OFF");


      //  Toast.makeText(this, "Reminder is OFF", Toast.LENGTH_SHORT).show();
    }
    // Method to set reminder state in SharedPreferences
    private void setReminderInSharedPreferences(boolean isReminderEnabled) {
        SharedPreferences sharedPreferences = getSharedPreferences("ReminderPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("ReminderEnabled", isReminderEnabled);
        editor.apply();
    }
    // Method to get reminder state from SharedPreferences
    private boolean getReminderFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReminderPrefs", MODE_PRIVATE);
        // Return the stored value, default to true if not set
        return sharedPreferences.getBoolean("ReminderEnabled", true);
    }
    // Method to set reminder time in SharedPreferences
    private void setReminderTimeInSharedPreferences(String time) {
        SharedPreferences sharedPreferences = getSharedPreferences("ReminderPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ReminderTime", time);
        editor.apply();


    }

    // Method to get reminder time from SharedPreferences
    private String getReminderTimeFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReminderPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("ReminderTime", "No reminder set");  // Default to empty string if no time is saved
    }



}