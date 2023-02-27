package com.notes.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
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
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        noteTitle.setText(note.getTitle());
        noteDetails.setText(note.getContent());
        c=Calendar.getInstance();
        todaysDate=c.get(Calendar.DAY_OF_MONTH)+"/"+c.get(Calendar.MONTH+1)+"/"+ c.get(Calendar.YEAR);
        currentTime=pad(c.get(Calendar.HOUR))+":"+pad(c.get(Calendar.MINUTE));

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
          note.setTitle(noteTitle.getText().toString());
          note.setContent(noteDetails.getText().toString());
          note.setDate(todaysDate);
          note.setTime(currentTime);
          db.editnote(note);

          goToMain();
         /* Intent i=new Intent(getApplicationContext(),Details.class);
          i.putExtra("ID",note.getId());
          startActivity(i);*/
            Toast.makeText(this,"Note updated",Toast.LENGTH_SHORT).show();
        }

        return  super.onOptionsItemSelected(item);
    }
    public void goToMain(){
        Intent i=new Intent(this,MainActivity.class);
        i.putExtra("adDekhabo?",true);
        startActivity(i);
    }
    private String pad(int i){
        if(i<10) return "0"+i;
        return String.valueOf(i);

    }
}
