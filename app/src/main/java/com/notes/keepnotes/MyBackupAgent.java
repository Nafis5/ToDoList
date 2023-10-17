package com.notes.keepnotes;


import android.provider.Settings.Secure;
import android.content.Context;
import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MyBackupAgent extends BackupAgentHelper {

    private static final String TAG = "MyBackupAgent";
    private static final String DATABASE_NAME = "notedb";
    private static final String BACKUP_FOLDER = "database_backups";
    private static final String BACKUP_FILENAME = "backup.csv";
    // private FirebaseAuth mAuth;
    String userUID = "";
    Context context;
    ArrayList<Note> noteList;
    NoteDatabase db;


    @Override
    public void onCreate() {
        FileBackupHelper databaseBackupHelper = new FileBackupHelper(this, "../databases/" + DATABASE_NAME);
        addHelper("database", databaseBackupHelper);
    }
    public String getAndroidId() {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

    }

    void setUserId( Context context, ArrayList<Note> notes) {
        noteList = notes;

        this.context = context;
        db=new NoteDatabase(context);
        this.userUID = getAndroidId();
        //  Toast.makeText(context,userUID,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        super.onBackup(oldState, data, newState);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference backupRef = storageRef.child(BACKUP_FOLDER + "/" + userUID + "/" + BACKUP_FILENAME);

        // Delete the existing backup file if it exists
        backupRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Old backup deleted successfully");

                    // Proceed to create the new backup
                    backupDatabaseToStorage(backupRef);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting old backup", e);

                    // Proceed to create the new backup even if deletion fails
                    backupDatabaseToStorage(backupRef);
                });
    }



    File getDatbaseFileAsCsv() {

        //copy data to a sav file in internal storage
        File file = new File(context.getFilesDir(), BACKUP_FILENAME);
        try {
            FileWriter writer = new FileWriter(file);

            // Write CSV header
            writer.append("Title,Content,Date,Time\n");

            // Write data to the CSV file
            for (Note note : noteList) {
                writer.append(note.getTitle())
                        .append(",")
                        .append(note.getContent())
                        .append(",")
                        .append(note.getDate())
                        .append(",")
                        .append(note.getTime())
                        .append("\n");
            }

            writer.flush();
            writer.close();

            // File saved successfully


        } catch (IOException e) {
            e.printStackTrace();
            // Handle file write error

        }
        return file;
    }

    ArrayList<Note> getCsvFileAsArrayList(File file) {
        ArrayList<Note> importedDataList = new ArrayList<>();
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;

                // Skip the header line (ID, Title, Content, Date, Time)
                bufferedReader.readLine();

                // Read data from the CSV file and populate the ArrayList
                while ((line = bufferedReader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 4) {

                        String title = "" + data[0];
                        String content = "" + data[1];
                        String date = "" + data[2];
                        String time = "" + data[3];

                        Note note = new Note(title, content, date, time);
                        importedDataList.add(note);
                    }
                }

                bufferedReader.close();

                // Data imported successfully
                // You can add your own success message or actions here


            } catch (IOException e) {
                e.printStackTrace();
                // Handle file read error
                // You can add your own error handling here


            }
        } else {
            // Handle the case when the file does not exist
            // You can add your own error handling here
            //  showToast("file does not exist");
        }
        return importedDataList;

    }

    private void backupDatabaseToStorage(StorageReference backupRef) {
        //    File originalDatabaseFile = context.getDatabasePath(DATABASE_NAME);
        File data = Environment.getDataDirectory();
        //  String currentDBPath = context.getDatabasePath(DATABASE_NAME).getAbsoluteFile();
        File originalDatabaseFile = context.getDatabasePath(DATABASE_NAME).getAbsoluteFile();

        // Create a copy of the database file in a temporary location
        File temporaryCopy = getDatbaseFileAsCsv();
        // copyDatabaseFile(originalDatabaseFile, temporaryCopy);

        // Upload the temporary copy to Firebase Cloud Storage
        backupRef.putFile(Uri.fromFile(temporaryCopy))
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "Database backup successful");
                    // Clean up the temporary copy

                    temporaryCopy.delete();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Database backup failed", e);
                    // Clean up the temporary copy in case of failure
                    //  temporaryCopy.delete();
                });
    }


    public void restoreDatabaseFromStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference backupFileRef = storageRef.child(BACKUP_FOLDER + "/" + userUID + "/" + BACKUP_FILENAME);

        // Check if the backup file exists in Firebase Cloud Storage
        backupFileRef.getMetadata()
                .addOnSuccessListener(storageMetadata -> {
                    // Backup file exists, proceed with download and restore
                    File localBackupFile = new File(context.getFilesDir(), BACKUP_FILENAME);

                    // Download the backup file from Firebase Cloud Storage to local storage
                    backupFileRef.getFile(localBackupFile)
                            .addOnSuccessListener(taskSnapshot -> {
                                boolean restoreSuccess = restoreDatabase(localBackupFile);
                                if (restoreSuccess) {
                                    Log.d(TAG, "Database restore successful");
                                } else {
                                    Log.e(TAG, "Database restore failed");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error downloading backup file", e);
                            });
                })
                .addOnFailureListener(e -> {
                    // Backup file doesn't exist, show toast message
                    Log.e(TAG, "No backup file in cloud", e);
                });
    }


    private boolean restoreDatabase(File file) {


        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;

                // Skip the header line (ID, Title, Content, Date, Time)
                bufferedReader.readLine();


                // Read data from the CSV file and populate the ArrayList
                while ((line = bufferedReader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 4) {

                        String title = "" + data[0];
                        String content = "" + data[1];
                        String date = "" + data[2];
                        String time = "" + data[3];

                        Note note = new Note(title, content, date, time);
                        db.addNote(note);
                    }
                }

                bufferedReader.close();

                // Data imported successfully
                // You can add your own success message or actions here
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                // Handle file read error
                // You can add your own error handling here


            }
        } else {
            // Handle the case when the file does not exist
            // You can add your own error handling here

        }

        return false;
        //ends here
    }
}





