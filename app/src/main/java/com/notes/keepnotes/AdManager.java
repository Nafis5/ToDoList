package com.notes.keepnotes;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdManager {
    static InterstitialAd lowInterstitialAd;
    static  InterstitialAd highInterstitialAd;
    private Context ctx;
    private final String adUnitIDLow="ca-app-pub-3940256099942544/8691691433";
    private final String adUnitIdHigh="ca-app-pub-3940256099942544/1033173712";

    public AdManager(Context ctx){
        this.ctx=ctx;


    }

    public static InterstitialAd getad(){

        if(highInterstitialAd!=null) return highInterstitialAd;
        return lowInterstitialAd;
    }


    public void loadInterstial() {
        if(highInterstitialAd==null) loadAdHigh();
        if(lowInterstitialAd==null) loadAdLow();



    }

    private void loadAdLow() {
        if (lowInterstitialAd == null) {


            AdRequest adRequest = new AdRequest.Builder().build();

            lowInterstitialAd.load(ctx,adUnitIDLow, adRequest, new InterstitialAdLoadCallback() {

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    lowInterstitialAd = null;


                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    lowInterstitialAd = interstitialAd;
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            lowInterstitialAd = null;

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            lowInterstitialAd = null;


                        }
                    });
                }
            });
        }
    }

    private void loadAdHigh() {
        if (highInterstitialAd == null) {


            AdRequest adRequest = new AdRequest.Builder().build();

            highInterstitialAd.load(ctx,adUnitIdHigh, adRequest, new InterstitialAdLoadCallback() {

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    highInterstitialAd = null;


                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    highInterstitialAd = interstitialAd;
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            highInterstitialAd = null;

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            highInterstitialAd = null;


                        }
                    });
                }
            });
        }
    }







}
