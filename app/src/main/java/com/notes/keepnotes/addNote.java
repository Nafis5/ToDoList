package com.notes.keepnotes;

import static android.service.controls.ControlsProviderService.TAG;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class addNote extends AppCompatActivity {
    Toolbar toolbar;
    EditText noteTitle,noteDetails;
    String selectedDate,selectedTime;
    Calendar c;
    String todaysDate;
    String currentTime;
    InterstitialAd adi;
    AdManager adManager;
    private AdView mAdView;
    AdManager admanager;
    AdRequest adRequest;
    private Button dateButton, timeButton;
    NoteDatabase db;
    private boolean isEdit;
    Note note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandleNightMode();
        setContentView(R.layout.activity_add_note);


        admanager=new AdManager(this);

        admanager.loadInterstial();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        noteTitle=findViewById(R.id.noteTitle);
        noteDetails=findViewById(R.id.noteDetails);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
       // toolbar.setBackgroundColor(Color.parseColor("#000000"));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        Long id = i.getLongExtra("ID", -1);
        isEdit = i.getBooleanExtra("isEdit", false);
        db= new NoteDatabase(this);
        //Toast.makeText(this,""+id,Toast.LENGTH_SHORT).show();
        if(id!=-1){
            db = new NoteDatabase(this);
            note = db.getNote(id);
            if(note.getTitle().length()>0) noteTitle.setText(note.getTitle());
            if( note.getContent().length()>0 ) noteDetails.setText(note.getContent());
            if(note.getTime().length()>0){
                timeButton.setText(note.getTime());
                selectedTime= note.getTime();
            }
            if(note.getDate().length()>0) {
                dateButton.setText(note.getDate());
                selectedDate= note.getDate();
            }
        }

      //  toolbar.setTitleTextColor(Color.parseColor("#000000"));
        //banner add stufss
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //banner stuff end here

        noteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0){
                    //getSupportActionBar().setTitle(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //get time and date
        c=Calendar.getInstance();

        currentTime=pad(c.get(Calendar.HOUR))+":"+pad(c.get(Calendar.MINUTE));
        todaysDate=c.get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR);
        dateButton.setOnClickListener(v -> showDatePicker());
        timeButton.setOnClickListener(v -> showTimePicker());
         Log.d("calender","Date and Time"+todaysDate+"and"+currentTime);
        noteTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,TextSizeUtil.getEditTextSizeBasedOnScreen(this));
        noteDetails.setTextSize(TypedValue.COMPLEX_UNIT_SP,TextSizeUtil.getEditTextSizeBasedOnScreen(this));
        createNotificationChannel();


    }
    private void HandleNightMode() {
        CommonAttributes commonAttributes=new CommonAttributes();
        if(commonAttributes.getThemeStatus()){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
           // getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private String pad(int i){
        if(i<10) return "0"+i;
        return String.valueOf(i);

    }
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
            dateButton.setText(selectedDate); // Update button text
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
            timeButton.setText(selectedTime); // Update button text
        }, hour, minute, false);

        timePickerDialog.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);
        return  true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.save) {

            if(noteTitle.getText().toString().length()==0 || noteTitle.getText().toString()==null ) {
                Toast.makeText(this,"Please Enter the task name",Toast.LENGTH_LONG).show();

            }
            if(selectedDate==null) Toast.makeText(this,"Please Select a date",Toast.LENGTH_LONG).show();
            if(selectedDate!=null & noteTitle.getText().toString().length()>0 ){
                if(selectedTime==null) selectedTime="";
                if (noteDetails==null) noteDetails.setText("");
                if(isEdit) {
                    note.setTitle(noteTitle.getText().toString());
                    note.setContent(noteDetails.getText().toString());
                    note.setDate(selectedDate);
                    note.setTime(selectedTime);
                    db.editnote(note);
                    Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
                    setReminder(selectedDate,selectedTime,(int)note.getId(),note.getTitle(),note.getContent());
                    goToMain();
                }
                else{
                    Note note = new Note(noteTitle.getText().toString(), noteDetails.getText().toString(), selectedDate, selectedTime);



                    db.addNote(note);
                    Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
                    setReminder(selectedDate,selectedTime,(int)db.getLastNoteId(),note.getTitle(),note.getContent());
                    goToMain();
                }


            }


        }
        if(item.getItemId()==R.id.only_delete){
            Note note = new Note(noteTitle.getText().toString(), noteDetails.getText().toString(), todaysDate, currentTime);
            NoteDatabase db = new NoteDatabase(this);
            db.deleteNote(note.getId());
            goToMain();
        }

        return  super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public void goToMain(){
        Intent i=new Intent(this,MainActivity.class);
        i.putExtra("adDekhabo?",true);
        startActivity(i);
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        admanager.loadInterstial();


    }

    @Override
    protected void onStart() {
        super.onStart();

        admanager.loadInterstial();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAdView.loadAd(adRequest);
        admanager.loadInterstial();

    }
    // Assumed to be inside an Activity or Service context
// Make sure to pass the unique taskId for the reminder
    private void setReminder(String date, String time, int taskId, String title, String content) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);

        // Pass data + first reminder index (0)
        intent.putExtra("TASK_ID", taskId);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("REMINDER_INDEX", 0); // First of 3 reminders

        // Parse date/time (your existing code)
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateTime = date + " " + (time.isEmpty() ? "09:00" : time);
            calendar.setTime(dateFormat.parse(dateTime));

            // Ensure future time
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                Toast.makeText(this, "Reminder time must be in the future.", Toast.LENGTH_LONG).show();
                return;
            }

            // Schedule first reminder
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    taskId * 10, // Base requestCode for first reminder
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (alarmManager != null) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Log.d("SetReminder", "First reminder set for Task ID " + taskId);
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date/time format.", Toast.LENGTH_SHORT).show();
        }
    }
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TaskReminderChannel";
            String description = "Channel For ToDoList";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("TaskReminderChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


    }
}