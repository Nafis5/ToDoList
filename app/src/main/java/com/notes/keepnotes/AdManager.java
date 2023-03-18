package com.notes.keepnotes;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdManager {
    static InterstitialAd lowInterstitialAd;
    static InterstitialAd highInterstitialAd;
    private Context ctx;

    private  final String highAdunit1="ca-app-pub-3940256099942544/8691691433";
    private  final String highAdunit2="ca-app-pub-3940256099942544/8691691433";
    private  final String highAdunit3="ca-app-pub-3940256099942544/8691691433";
    private  final String highAdunit4="ca-app-pub-3940256099942544/1033173712";
    static InterstitialAd interstitialAd1;
    static InterstitialAd interstitialAd2;
    static InterstitialAd interstitialAd3;
    static InterstitialAd interstitialAd4;

    public AdManager(Context ctx) {
        this.ctx = ctx;


    }

    /* public static InterstitialAd getad() {
         if (highInterstitialAd != null) return highInterstitialAd;
         return lowInterstitialAd;
     }*/
    public static InterstitialAd getad(){
        if(interstitialAd1 !=null) return interstitialAd1;
        else if(interstitialAd2 !=null) return interstitialAd2;
        else if(interstitialAd3 !=null) return interstitialAd3;
        else return interstitialAd4;

    }


    public void loadInterstial() {
        //   if(highInterstitialAd==null) loadAdHigh();
        // if(lowInterstitialAd==null) loadAdLow();
        if(isAllHighAdsNull()) {

            loadAds1(highAdunit1);
            loadAds2(highAdunit2);
            loadAds3(highAdunit3);
            if(highAdunit4==null) loadAds4(highAdunit4);


        }



    }






    private boolean isAllHighAdsNull(){
        return interstitialAd1 == null & interstitialAd2==null & interstitialAd3==null;
    }



    private void loadAds1(String adUnit) {
        if (interstitialAd1 == null) {


            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(ctx, adUnit, adRequest, new InterstitialAdLoadCallback() {

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    interstitialAd1 = null;


                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    interstitialAd1 = interstitialAd;
                    interstitialAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            interstitialAd1 = null;

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            interstitialAd1 = null;


                        }
                    });
                }
            });


        }
    }



    private void loadAds2(String adUnit) {
        if (interstitialAd2 == null) {


            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(ctx, adUnit, adRequest, new InterstitialAdLoadCallback() {

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    interstitialAd2 = null;


                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    interstitialAd2 = interstitialAd;
                    interstitialAd2.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            interstitialAd2 = null;

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            interstitialAd2 = null;


                        }
                    });
                }
            });


        }
    }
    private void loadAds3(String adUnit) {
        if (interstitialAd3 == null) {


            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(ctx, adUnit, adRequest, new InterstitialAdLoadCallback() {

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    interstitialAd3 = null;


                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    interstitialAd3 = interstitialAd;
                    interstitialAd3.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            interstitialAd3 = null;

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            interstitialAd3 = null;


                        }
                    });
                }
            });


        }
    }

    private void loadAds4(String adUnit) {
        if (interstitialAd4 == null) {


            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(ctx, adUnit, adRequest, new InterstitialAdLoadCallback() {

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    interstitialAd4 = null;


                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    interstitialAd4 = interstitialAd;
                    interstitialAd4.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            interstitialAd4 = null;

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            interstitialAd4 = null;


                        }
                    });
                }
            });


        }
    }

}