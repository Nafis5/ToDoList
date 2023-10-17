package com.notes.keepnotes;

import static android.content.ContentValues.TAG;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataTransfer {


    private Context context;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Activity activity;


    public DataTransfer(Context context,Activity activity) {

        this.context = context;
        this.activity=activity;
    }
    public ArrayList<Note> importDataFromCSV(String fileName) {
        ArrayList<Note> importedDataList = new ArrayList<>();

        // Get app's internal storage directory
        File directory = new File(context.getFilesDir(), "notes_backup_folder");
        File file = new File(directory, fileName + ".csv");

        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;

                // Skip the header line (Title, Content, Date, Time)
                bufferedReader.readLine();

                // Read data from the CSV file and populate the ArrayList
                while ((line = bufferedReader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 4) {
                        String title = data[0];
                        String content = data[1];
                        String date = data[2];
                        String time = data[3];

                        Note note = new Note(title, content, date, time);
                        importedDataList.add(note);
                    }
                }

                bufferedReader.close();

                // Data imported successfully
                showToast("Data import successful");

            } catch (IOException e) {
                e.printStackTrace();
                // Handle file read error
                showToast("Failed to import data");
            }
        } else {
            // Handle the case when the file does not exist
            showToast("File does not exist");
        }

        return importedDataList;
    }




    private File getCSVFile(String fileName) {
        File directory = new File(Environment.getExternalStorageDirectory() + "/notes_backup_folder");

        // Create directory if not exist
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName + ".csv");
        return file;
    }

    public void exportDataAsCSV(ArrayList<Note> noteList, String fileName) {
        // Check if external storage write permission is granted
        if (checkWritePermission()) {


            File directory = new File(Environment.getExternalStorageDirectory() + "/notes_backup_folder");

            // Create directory if not exist
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    // Directory creation failed
                    showToast("Failed to create directory");


                    return;
                }
            }

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName + ".csv");
            if (file.exists()) {
                if (!file.delete()) {
                    // Failed to delete existing file
                    showToast("Failed to delete existing file");
                    return;
                }
            }

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
                showToast("Data exported successfully!");

            } catch (IOException e) {
                e.printStackTrace();
                // Handle file write error
                showToast("file write error");
            }
        } else {
            // Request write external storage permission
            // showToast("you don't have write permisison");
            //    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            showToast("you don't have permission to write");
        }
    }



    private boolean checkWritePermission() {
        int result = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }





    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }



    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
    public boolean isBackupAvailable() {
        File backupFolder = new File(Environment.getExternalStorageDirectory() + "/notes_backup_folder");
        File csvFile = new File(backupFolder, "notes_data.csv");

        return backupFolder.exists() && csvFile.exists();
    }


}
