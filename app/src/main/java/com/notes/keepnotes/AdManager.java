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
    static InterstitialAd mInterstitialAd;
    private Context ctx;
    private String adUnitID="ca-app-pub-3103198316569371/8307918328";
    public AdManager(Context ctx){
        this.ctx=ctx;


    }

    public static InterstitialAd getad(){

        return mInterstitialAd;
    }


    public  void loadInterstial(){
        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.load(ctx,adUnitID, adRequest, new InterstitialAdLoadCallback() {

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd=null;


            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mInterstitialAd=null;

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        mInterstitialAd=null;


                    }
                });
            }
        });



    }


}
