package com.notes.keepnotes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.util.Calendar;

public class addNote extends AppCompatActivity {
    Toolbar toolbar;
    EditText noteTitle,noteDetails;
    Calendar c;
    String todaysDate;
    String currentTime;
    InterstitialAd adi;
    AdManager adManager;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
          adManager=new AdManager(this);
        //  if(adManager.isAdNULL()) adManager.loadInterstial();

          adi=adManager.getad();
          showInterstial();


        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        noteTitle=findViewById(R.id.noteTitle);
        noteDetails=findViewById(R.id.noteDetails);
        toolbar.setBackgroundColor(Color.parseColor("#000000"));
        getSupportActionBar().setTitle("New Note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        //banner add stufss
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
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
        todaysDate=c.get(Calendar.DAY_OF_MONTH)+"/"+c.get(Calendar.MONTH+1)+"/"+ c.get(Calendar.YEAR);
        currentTime=pad(c.get(Calendar.HOUR))+":"+pad(c.get(Calendar.MINUTE));
       // Log.d("calender","Date and Time"+todaysDate+"and"+currentTime);

    }
    private String pad(int i){
        if(i<10) return "0"+i;
        return String.valueOf(i);

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

            Note note=new Note(noteTitle.getText().toString(),noteDetails.getText().toString(),todaysDate,currentTime);
            NoteDatabase db=new NoteDatabase(this);
            db.addNote(note);
            Toast.makeText(this,"Note saved",Toast.LENGTH_SHORT).show();
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
        startActivity(i);
    }
    private void showInterstial(){
        if(adi!=null){


            adi.show(this);
        }
        else{
           // Toast.makeText(this,"Ad not loaded",Toast.LENGTH_SHORT).show();
            adManager.loadInterstial();
        }

    }
}
