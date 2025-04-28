package com.notes.keepnotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class NoteDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=2;
    private static final String DATABASE_NAME="notedb";
    private static final String DATABASE_TABLE="notetable";
    private static final String KEY_ID="id";
    private static final String KEY_TITLE="title";
    private static final String KEY_CONTENT="content";
    private static final String KEY_DATE="date";
    private static final String KEY_TIME="time";
    NoteDatabase(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="CREATE TABLE "+DATABASE_TABLE+" ("+
                KEY_ID+" INTEGER PRIMARY KEY,"+
                KEY_TITLE+" TEXT,"+
                KEY_CONTENT+" TEXT,"+
                KEY_DATE+" TEXT,"+
                KEY_TIME+" TEXT"
                +" )";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion>=newVersion) return;
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
        onCreate(db);

    }
    public long addNote (Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues c=new ContentValues();
        if(note.getTitle().length()>0){
            c.put(KEY_TITLE,note.getTitle());
        }
        else {
            c.put(KEY_TITLE,"untitled");
        }
        c.put(KEY_CONTENT,note.getContent());
        c.put(KEY_DATE,note.getDate());
        c.put(KEY_TIME,note.getTime());
        long ID=db.insert(DATABASE_TABLE,null,c);
        return ID;
    }
    public Note getNote(long id){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=  db.query(DATABASE_TABLE,new String[]{KEY_ID,KEY_TITLE,KEY_CONTENT,KEY_DATE,KEY_TIME},KEY_ID+"=?",new String[]{String.valueOf(id)},null,null,null,null);
        if(cursor != null)
            cursor.moveToFirst();

        Note note= new Note(Long.parseLong(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
       return note;
    }
    public boolean doesIdExist(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                DATABASE_TABLE,
                new String[]{KEY_ID},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null
        );

        boolean exists = (cursor != null && cursor.getCount() > 0);

        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return exists;
    }
    public List<Note> getAllNotes(){
        List<Note> allNotes = new ArrayList<>();
        String query = "SELECT * FROM " +DATABASE_TABLE+" ORDER BY "+KEY_ID+" DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                Note note = new Note();
                note.setId(Long.parseLong(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setContent(cursor.getString(2));
                note.setDate(cursor.getString(3));
                note.setTime(cursor.getString(4));
                allNotes.add(note);
            }while (cursor.moveToNext());
        }

        return allNotes;

    }
    void deleteNote(long id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(DATABASE_TABLE,KEY_ID+"=?",new String[]{String.valueOf(id)});
        db.close();
    }
    public void editnote(Note note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();

       if(note.getTitle().length()>0) c.put(KEY_TITLE,note.getTitle());
       else c.put(KEY_TITLE,"untitled");
        c.put(KEY_CONTENT,note.getContent());
        c.put(KEY_DATE,note.getDate());
        c.put(KEY_TIME,note.getTime());
        db.update(DATABASE_TABLE,c,KEY_ID+"=?",new String[]{String.valueOf(note.getId())});

    }
    public List<Note> getNotesByDate(String selectedDate) {
        List<Note> notesByDate = new ArrayList<>();
        // SQL query to select notes for the given date
        String query = "SELECT * FROM " + DATABASE_TABLE + " WHERE " + KEY_DATE + " = ? ORDER BY " + KEY_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, new String[]{selectedDate});



        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(Long.parseLong(cursor.getString(0)));   // ID
                note.setTitle(cursor.getString(1));                // Title
                note.setContent(cursor.getString(2));              // Content
                note.setDate(cursor.getString(3));                 // Date
                note.setTime(cursor.getString(4));                 // Time
                notesByDate.add(note);
            } while (cursor.moveToNext());
        }

        cursor.close(); // Always close the cursor
        db.close();     // Always close the database connection
        return notesByDate;
    }
}