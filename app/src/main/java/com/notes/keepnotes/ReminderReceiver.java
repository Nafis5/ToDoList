package com.notes.keepnotes;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings; // Needed for potential permission request intent
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String ACTION_SNOOZE = "com.notes.keepnotes.ACTION_SNOOZE";
    private static final int NOTIFICATION_SPACING_MINUTES = 5; // 5-minute gap between reminders

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ReminderReceiver", "Received intent action: " + intent.getAction());

        // Retrieve data
        int taskId = intent.getIntExtra("TASK_ID", -1);
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        int reminderIndex = intent.getIntExtra("REMINDER_INDEX", 0); // 0=first, 1=second, 2=third

        if (taskId == -1 || title == null || content == null) {
            Log.e("ReminderReceiver", "Missing essential data in intent.");
            return;
        }

        // Check if task is already completed (optional safety check)
        if (isTaskCompleted(context, taskId)) {
            Log.d("ReminderReceiver", "Task " + taskId + " already completed. Skipping notification.");
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {


            return;
        }

        showNotification(context, taskId, title, content);

        // Schedule next reminder if not the last one
        if (reminderIndex < 2) { // 0=first, 1=second, 2=third (no more after)
            scheduleNextReminder(context, taskId, title, content, reminderIndex + 1);
        }
    }

    private boolean isTaskCompleted(Context context, int taskId) {
        // Query your database/task list to check if task is completed
        // Return true if completed (skip notification)
        NoteDatabase db = new NoteDatabase(context);
        if(db.doesIdExist(taskId)) return false;
        else return true;


    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void showNotification(Context context, int taskId, String title, String content) {
        // Intent for clicking notification (opens task details)
        Intent detailIntent = new Intent(context, MainActivity.class);
        detailIntent.putExtra("TASK_ID", taskId);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context,
                taskId,
                detailIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Snooze action (optional)
       /* Intent snoozeIntent = new Intent(context, SnoozeReceiver.class);
        snoozeIntent.putExtra("TASK_ID", taskId);
        snoozeIntent.putExtra("title", title);
        snoozeIntent.putExtra("content", content);
        snoozeIntent.putExtra("NOTIFICATION_ID", taskId);

        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                taskId + 1000, // Different requestCode
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );*/

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TaskReminderChannel")
                .setSmallIcon(R.drawable.todo_list_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true);
              //  .addAction(R.drawable.add_vector, "Snooze 5 min", snoozePendingIntent);

        // Show notification
        NotificationManagerCompat.from(context).notify(taskId, builder.build());
    }

    private void scheduleNextReminder(Context context, int taskId, String title, String content, int nextReminderIndex) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("TASK_ID", taskId);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("REMINDER_INDEX", nextReminderIndex);

        long triggerTime = System.currentTimeMillis() + (NOTIFICATION_SPACING_MINUTES * 60 * 1000);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId * 10 + nextReminderIndex, // Unique requestCode per reminder
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }
}