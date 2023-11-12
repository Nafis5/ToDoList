package com.notes.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Calendar;

public class Edit extends AppCompatActivity {
    Toolbar toolbar;
    EditText noteTitle,noteDetails;
    Calendar c;
    String todaysDate;
    String currentTime;
    NoteDatabase db;
    Note note;
    private AdView mAdView;
    AdManager admanager;
    AdRequest adRequest;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String CLICK_COUNT_KEY = "clickCount";
    private SharedPreferences sharedPreferences;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Intent i=getIntent();
        Long id=i.getLongExtra("ID",0);
        db=new NoteDatabase(this);
        note=db.getNote(id);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        noteTitle=findViewById(R.id.noteTitle);
        noteDetails=findViewById(R.id.noteDetails);
        toolbar.setBackgroundColor(Color.parseColor("#000000"));
        getSupportActionBar().setTitle(note.getTitle());

        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        noteTitle.setText(note.getTitle());

        noteDetails.setText(note.getContent());
        c=Calendar.getInstance();
        todaysDate =c.get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR);

        currentTime=pad(c.get(Calendar.HOUR))+":"+pad(c.get(Calendar.MINUTE));
        admanager=new AdManager(this);
        if((getClickCounter()+1)%3==0)   admanager.loadInterstial();



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
        builder = new AlertDialog.Builder(this);



    }
    private void increaseClickCounter(){
        int currentClickCount = sharedPreferences.getInt(CLICK_COUNT_KEY, 0);
        currentClickCount++;
        sharedPreferences.edit().putInt(CLICK_COUNT_KEY, currentClickCount).apply();
    }
    private int getClickCounter(){
        return sharedPreferences.getInt(CLICK_COUNT_KEY, 0);
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
            increaseClickCounter();
            if(noteTitle.getText().toString().length()>0 || noteDetails.getText().toString().length()>0 ){
                note.setTitle(noteTitle.getText().toString());
                note.setContent(noteDetails.getText().toString());
                note.setDate(todaysDate);
                note.setTime(currentTime);
                db.editnote(note);
                Toast.makeText(this,"Note updated",Toast.LENGTH_SHORT).show();

            }
            else {
                db.deleteNote(note.getId());
                Toast.makeText(this,"Empty notes can't be saved",Toast.LENGTH_SHORT).show();
            }
            goToMain();

        }
        if(item.getItemId()==R.id.only_delete){
            OpenDialogue();

        }
        if(item.getItemId() == android.R.id.home){
            increaseClickCounter();

            goToMain();
            return true;
        }

        return  super.onOptionsItemSelected(item);
    }
    public void goToMain(){
        Intent i=new Intent(this,MainActivity.class);
        if( (getClickCounter())%3==0) i.putExtra("adDekhabo?",true);
        else  i.putExtra("adDekhabo?",false);

        startActivity(i);
    }
    private String pad(int i){
        if(i<10) return "0"+i;
        return String.valueOf(i);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        admanager.loadInterstial();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdView.loadAd(adRequest);
        admanager.loadInterstial();
    }
    void OpenDialogue() {
        // builder.setMessage(R.string.dialog_message).setTitle("delete");

        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to delete this note ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        db.deleteNote(note.getId());
                        goToMain();



                        Toast.makeText(getApplicationContext(), "Note Deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();

        alert.setTitle("Delete");
        alert.show();
    }
    @Override
    public void onBackPressed() {
        increaseClickCounter();
        super.onBackPressed();

    }

}
