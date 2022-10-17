package com.notes.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    boolean is_in_action_mode=false;
    int counter;
    Toolbar toolbar;
    RecyclerView recylerview;
    Adapter adapter;
    List<Note> notes;
    List<Long> deletelist=new ArrayList();
    NoteDatabase db;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    MenuInflater inflater;
    public NavigationView nav_view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AdManager admanager=new AdManager(this);
        admanager.loadInterstial();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#000000"));
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        nav_view = (NavigationView) findViewById(R.id.nav_view);

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recylerview=findViewById(R.id.allNotesList);
        recylerview.setLayoutManager(new LinearLayoutManager(this));
        db=new NoteDatabase(this);
        notes=db.getAllNotes();
        adapter=new Adapter(this,notes);
        recylerview.setAdapter(adapter);



        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.backupId){
                    Toast.makeText(MainActivity.this,"backup",Toast.LENGTH_SHORT).show();
                }
                if(id==R.id.restoreId){
                    Toast.makeText(MainActivity.this,"restore",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });


    }





    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if(item.getItemId()==R.id.only_delete) {

            deleteNotes();
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
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        toolbar.inflateMenu(R.menu.only_delete_menu);
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
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        toolbar.setTitle("Simple Notes");
        toolbar.getMenu().clear();
        counter=0;
        is_in_action_mode=false;
        adapter.notifyDataSetChanged();
        deletelist.clear();




    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            finishAffinity();
            System.exit(0);
        }

    }



    public void goToAddNote(View view) {
        Intent i=new Intent(this,addNote.class);
        startActivity(i);
    }
    void deleteNotes(){
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
}
