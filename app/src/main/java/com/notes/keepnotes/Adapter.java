package com.notes.keepnotes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    LayoutInflater layoutInflater;
    List<Note> notes;
    MainActivity mainActivity;
    NoteDatabase db;




    Adapter(Context context,List<Note> notes){
        this.layoutInflater=LayoutInflater.from(context);
        this.notes=notes;
        mainActivity=(MainActivity) context;
        db = new NoteDatabase(mainActivity);

    }
    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.custom_list_view,parent,false);


        if(TextSizeUtil.getScreenSize(mainActivity)>7.0){
            view=layoutInflater.inflate(R.layout.custom_list_view_large,parent,false);
        }




        return new ViewHolder(view,mainActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        String title=notes.get(position).getTitle();
        String date=notes.get(position).getDate();
        String time=notes.get(position).getTime();
        String daysOfWeek=notes.get(position).getDayOfWeekFromDate();
        String daysOfMonth=notes.get(position).getDayOfMonthFromDate();
        if(hasTaskExpired(date,time)){
            holder.ntitle.setTextColor(Color.parseColor("#D32F2F"));
            holder.nDate.setTextColor(Color.parseColor("#D32F2F"));
            holder.nTime.setTextColor(Color.parseColor("#D32F2F"));
        }
        holder.ntitle.setText(title);
        holder.nDate.setText(date);
        holder.nTime.setText(time);

        holder.todoCheckbox.setChecked(false);
        if(!mainActivity.is_in_action_mode){
            holder.checkBox.setVisibility(View.GONE);

        }
        else{
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }


    }

    @Override
    public int getItemCount() {
        try {
            return notes.size();
        } catch (Exception e) {
            return 0;
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView ntitle,nDate,nTime;
        CheckBox checkBox;
        MainActivity mainActivity;
        CheckBox todoCheckbox;

        public ViewHolder(@NonNull View itemView, final MainActivity mainActivity) {
            super(itemView);
            ntitle=itemView.findViewById(R.id.nTitle);
            nDate=itemView.findViewById(R.id.nDate);
            nTime=itemView.findViewById(R.id.nTime);

            checkBox=(CheckBox) itemView.findViewById(R.id.check);
            todoCheckbox=(CheckBox) itemView.findViewById(R.id.check_todo);
            this.mainActivity=mainActivity;
            checkBox.setOnClickListener(this);

            itemView.setOnLongClickListener(mainActivity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(!mainActivity.is_in_action_mode) {



                        Intent i = new Intent(v.getContext(), addNote.class);
                        i.putExtra("ID", notes.get(getAdapterPosition()).getId());
                        i.putExtra("isEdit", true);
                        v.getContext().startActivity(i);
                    }
                }
            });
            todoCheckbox.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Note note = notes.get(position);

                if (todoCheckbox.isChecked()) {
                    // Set fade effect
                    cancelReminders(mainActivity,(int) note.getId());
                    itemView.setAlpha(0.5f); // Fade out the item view

                    // Delay the deletion
                    new Handler().postDelayed(() -> {

                        db.deleteNote(note.getId());; // Call delete method
                        notes.remove(position); // Remove from the list
                       // updateAdapter(notes); // Notify adapter

                        notifyDataSetChanged();
                        itemView.setAlpha(1f);
                    }, 2000); // 2 seconds delay
                } else {
                    // Reset alpha if unchecked
                    itemView.setAlpha(1f); // Reset to fully visible
                }
            });
        }


        @Override
        public void onClick(View v) {

            mainActivity.prepareSelection(v,notes.get(getAdapterPosition()).getId());
        }

    }
    public void updateAdapter(List<Note> updateslist){
       this.notes=updateslist;
        notifyDataSetChanged();

    }

    private boolean hasTaskExpired(String date, String time) {
        // Define the date and time format
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        try {
            // Parse the input date and time into a single Date object
            Date taskDateTime = dateTimeFormat.parse(date + " " + time);

            // Get the current date and time
            Calendar calendar = Calendar.getInstance();
            Date currentDateTime = calendar.getTime();

            // Compare the task's date/time with the current date/time
            if (taskDateTime != null && taskDateTime.before(currentDateTime)) {
                // Task has expired
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Task is still valid (or invalid date/time format)
        return false;
    }
    public static void cancelReminders(Context context, int taskId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel all 3 possible reminders (indices 0, 1, 2)
        for (int i = 0; i < 3; i++) {
            Intent intent = new Intent(context, ReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    taskId * 10 + i, // Same requestCode used in scheduling
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            Log.d("AlarmDebug", "method dhukse taskId=" + taskId + ", index=" + i);

            if (alarmManager != null) {
                Log.d("AlarmDebug", "Attempting to cancel PendingIntent for taskId=" + taskId + ", index=" + i);
                alarmManager.cancel(pendingIntent);
                Log.d("AlarmDebug", "PendingIntent canceled: " + pendingIntent);
            }
            pendingIntent.cancel();
        }

        // Also cancel any active notification
        NotificationManagerCompat.from(context).cancel(taskId);
    }



}
