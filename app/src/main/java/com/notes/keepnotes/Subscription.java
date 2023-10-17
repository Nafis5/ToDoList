package com.notes.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Subscription extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    AlertDialog.Builder builder;
    List<Note> notes;
    NoteDatabase db;
    private MyBackupAgent backupAgent;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        db=new NoteDatabase(this);
        notes=db.getAllNotes();
        builder = new AlertDialog.Builder(this);
        backupAgent = new MyBackupAgent();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


    }


    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    void backUpData(){

        backupAgent.setUserId(  this,(ArrayList<Note>) db.getAllNotes());
        try {
            backUpToCloud();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    void goToPro(){
        openAppInGalaxyAppStore("com.pro.keepnotes");
      //  Toast.makeText(this,"Go to pro",Toast.LENGTH_LONG).show();
    }

    private void backUpToCloud() throws IOException {
        if(notes.size()>0) backupAgent.onBackup(null, null, null); // Trigger the backup process
    }





    public void onBuyNowClicked(View view) {
        if (isInternetConnected(Subscription.this)) {
            backUpData();
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "buyNow_button");
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Subscription button clicked"); // Replace with your button's name
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button_click");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            try {
                Thread.sleep(1000); // Sleep for 1500 milliseconds (1.5 seconds)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            goToPro();


        }
        else{
            Toast.makeText(this,"Please connect to the internet",Toast.LENGTH_SHORT).show();
        }
    }

    public void onCrossClicked(View view) {
       // startActivity(new Intent(Subscription.this,MainActivity.class));
        goToMain();
    }
    private void openAppInGalaxyAppStore(String packageName) {
    /*    try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("samsungapps://ProductDetail/" + packageName));
            intent.setPackage("com.sec.android.app.samsungapps"); // Package name of the Galaxy App Store
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://galaxystore.samsung.com/detail/com.notes.keepnotes"));
            startActivity(intent);
        }*/
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://galaxystore.samsung.com/detail/com.pro.keepnotes"));
        startActivity(intent);
    }
    void goToMain(){
        Intent i=new Intent(this,MainActivity.class);
        i.putExtra("ComingFromSubscription",true);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
     //   super.onBackPressed();
        goToMain();
    }
}