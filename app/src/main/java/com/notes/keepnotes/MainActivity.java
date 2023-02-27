package com.notes.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;


import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    boolean is_in_action_mode=false;
    int counter;
    Toolbar toolbar;
    RecyclerView recylerview;
    Adapter adapter;
    List<Note> notes;
    List<Long> deletelist=new ArrayList();
    NoteDatabase db;
    AdManager admanager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        admanager=new AdManager(this);
        Intent intent = getIntent();
        if(intent.getBooleanExtra("adDekhabo?", false)){
            showInterstial();
        }

        admanager.loadInterstial();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#000000"));
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        recylerview=findViewById(R.id.allNotesList);
        recylerview.setLayoutManager(new LinearLayoutManager(this));
        db=new NoteDatabase(this);
        notes=db.getAllNotes();
        adapter=new Adapter(this,notes);
        recylerview.setAdapter(adapter);





    }
    private void showInterstial(){
        InterstitialAd mInterstial=admanager.getad();
        if(mInterstial!=null){


            mInterstial.show(this);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.add_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add){



            Intent i=new Intent(this,addNote.class);
            startActivity(i);

        }
        if(item.getItemId()==R.id.only_delete){
            //revome selected items from db
            //dishablehome buton
            //set title to keep notes
            //bring back old menu
            //updateAdapter

            for(int i=0;i<deletelist.size();i++){

                db.deleteNote(deletelist.get(i));
            }
            Toast.makeText(this,counter+" items deleted",Toast.LENGTH_SHORT).show();
            clearActionMode();

            notes=db.getAllNotes();
            adapter.updateAdapter(notes);





        }
        else if(item.getItemId()==android.R.id.home){

            clearActionMode();
        }
        // return  super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.only_delete_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("0 item selected");
        is_in_action_mode=true;

        adapter.notifyDataSetChanged();
        return true;
    }
    public void prepareSelection(View view,long id){
        if(((CheckBox)view).isChecked()){
            deletelist.add(id);
            counter++;
            updatecounter();
        }
        else{
            deletelist.remove(new Long(id));
            --counter;
            updatecounter();
        }
    }
    public void updatecounter(){
        if(counter==0) toolbar.setTitle("0 item selected");
        else {
            toolbar.setTitle(counter+" item selected");
        }
    }
    public  void clearActionMode(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitle("Simple Notes");
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.add_menu);
        counter=0;
        is_in_action_mode=false;
        adapter.notifyDataSetChanged();
        deletelist.clear();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }



}