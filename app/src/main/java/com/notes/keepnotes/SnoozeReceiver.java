package com.notes.keepnotes;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.TimeUnit; // For snooze duration clarity

public class SnoozeReceiver extends BroadcastReceiver {

    // Using TimeUnit improves readability for snooze duration
    private static final long SNOOZE_DURATION_MINUTES = 5;
    private static final long SNOOZE_DURATION_MS = TimeUnit.MINUTES.toMillis(SNOOZE_DURATION_MINUTES);

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SnoozeReceiver", "Snooze action received.");

        // Retrieve data passed from ReminderReceiver's snooze action
        int taskId = intent.getIntExtra("TASK_ID", -1);
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        int originalNotificationId = intent.getIntExtra("NOTIFICATION_ID", -1);

        if (taskId == -1 || originalNotificationId == -1 || title == null || content == null) {
            Log.e("SnoozeReceiver", "Missing essential data (taskId, notificationId, title, or content) in intent.");
            return;
        }

        Log.d("SnoozeReceiver", "Snoozing Task ID: " + taskId + ", Original Notification ID: " + originalNotificationId);

        // 1. Cancel the original notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(originalNotificationId);
        Log.d("SnoozeReceiver", "Cancelled original notification ID: " + originalNotificationId);

        // 2. Calculate Snooze Time
        long snoozeUntil = System.currentTimeMillis() + SNOOZE_DURATION_MS;

        // 3. Prepare Intent to re-trigger ReminderReceiver after snooze
        Intent reminderIntent = new Intent(context, ReminderReceiver.class);
        reminderIntent.putExtra("TASK_ID", taskId);
        reminderIntent.putExtra("title", title);
        reminderIntent.putExtra("content", content);

        // Use the original taskId as request code for the rescheduled reminder
        PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                context,
                taskId, // Use original taskId for the PendingIntent request code
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 4. Schedule the Snoozed Reminder using AlarmManager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // --- Permission Check Reminder ---
        // ** IMPORTANT for Android 12+ **
        // As in setReminder, check SCHEDULE_EXACT_ALARM before setting.
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        //     if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
        //         Log.e("SnoozeReceiver", "Cannot schedule snooze - SCHEDULE_EXACT_ALARM permission not granted.");
        //         Toast.makeText(context, "Cannot snooze: Exact alarm permission needed.", Toast.LENGTH_LONG).show();
        //         // Optionally, try to reschedule using a non-exact alarm or notify the user
        //         return;
        //     }
        // }
        // --- End Permission Check Reminder ---

        if (alarmManager != null) {
            try {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeUntil, reminderPendingIntent);
                Log.d("SnoozeReceiver", "Snooze reminder set for Task ID " + taskId + " at approx " + snoozeUntil);
                Toast.makeText(context, title + " snoozed for " + SNOOZE_DURATION_MINUTES + " minutes", Toast.LENGTH_SHORT).show();
            } catch (SecurityException se) {
                Log.e("SnoozeReceiver", "SecurityException setting exact alarm. Check permissions.", se);
                Toast.makeText(context, "Could not set snooze due to permission issue.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("SnoozeReceiver", "Error setting snooze alarm.", e);
                Toast.makeText(context, "Could not set snooze.", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e("SnoozeReceiver", "AlarmManager service not available.");
            Toast.makeText(context, "Could not access Alarm service to set snooze.", Toast.LENGTH_LONG).show();
        }
    }
}
