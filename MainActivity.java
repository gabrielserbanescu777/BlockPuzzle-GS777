package com.gs777.blockpuzzle;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class MainActivity extends Activity {
 private static final String INTERSTITIAL="ca-app-pub-6031328027408355/6106490687";
 private static final String REWARDED="ca-app-pub-6031328027408355/5723347302";
 private static final String APP_OPEN="ca-app-pub-6031328027408355/3302117965";
 private static final String TEST_INTERSTITIAL="ca-app-pub-3940256099942544/1033173712";
 private static final String TEST_REWARDED="ca-app-pub-3940256099942544/5224354917";
 private static final String TEST_APP_OPEN="ca-app-pub-3940256099942544/9257395921";
 private final Handler handler=new Handler(Looper.getMainLooper());
 private WebView web;
 private InterstitialAd interstitial;
 private RewardedAd rewarded;
 private AppOpenAd appOpen;
 private int completedGames=0;
 private boolean initialAppOpenShown=false, adsReady=false, fullscreen=false;
 private long lastInterstitialAt=0;
 private String adUnit(String live,String test){return BuildConfig.REAL_ADS?live:test;}
 @Override public void onCreate(Bundle b){super.onCreate(b);
  getWindow().setStatusBarColor(0xff0c1424);getWindow().setNavigationBarColor(0xff0c1424);
  web=new WebView(this);setContentView(web,new ViewGroup.LayoutParams(-1,-1));
  WebSettings settings=web.getSettings();settings.setJavaScriptEnabled(true);settings.setDomStorageEnabled(true);
  settings.setAllowFileAccess(true);settings.setAllowFileAccessFromFileURLs(false);settings.setAllowUniversalAccessFromFileURLs(false);
  web.addJavascriptInterface(new Bridge(),"AndroidAds");
  web.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest req){Uri uri=req.getUrl();if("https".equals(uri.getScheme())&&"gabrielserbanescu777.com".equals(uri.getHost())){startActivity(new Intent(Intent.ACTION_VIEW,uri));return true;}return !"file".equals(uri.getScheme());}});
  web.loadUrl("file:///android_asset/index.html");requestConsent();
 }
 private void requestConsent(){ConsentInformation info=UserMessagingPlatform.getConsentInformation(this);
  ConsentRequestParameters params=new ConsentRequestParameters.Builder().build();
  info.requestConsentInfoUpdate(this,params,()->UserMessagingPlatform.loadAndShowConsentFormIfRequired(this,error->{if(info.canRequestAds())initAds();}),error->{if(info.canRequestAds())initAds();});
  if(info.canRequestAds())initAds();
 }
 private synchronized void initAds(){if(adsReady)return;adsReady=true;MobileAds.initialize(this,status->{});loadInterstitial();loadRewarded();}
 private void loadInterstitial(){InterstitialAd.load(this,adUnit(INTERSTITIAL,TEST_INTERSTITIAL),new AdRequest.Builder().build(),new InterstitialAdLoadCallback(){
  @Override public void onAdLoaded(InterstitialAd ad){interstitial=ad;}
  @Override public void onAdFailedToLoad(LoadAdError error){interstitial=null;}
 });}
 private void loadRewarded(){RewardedAd.load(this,adUnit(REWARDED,TEST_REWARDED),new AdRequest.Builder().build(),new RewardedAdLoadCallback(){
  @Override public void onAdLoaded(RewardedAd ad){rewarded=ad;}
  @Override public void onAdFailedToLoad(LoadAdError error){rewarded=null;}
 });}
 private void loadAppOpen(){AppOpenAd.load(this,adUnit(APP_OPEN,TEST_APP_OPEN),new AdRequest.Builder().build(),new AppOpenAd.AppOpenAdLoadCallback(){
  @Override public void onAdLoaded(AppOpenAd ad){appOpen=ad;if(!initialAppOpenShown && !fullscreen){initialAppOpenShown=true;appOpen=null;fullscreen=true;ad.setFullScreenContentCallback(new FullScreenContentCallback(){@Override public void onAdDismissedFullScreenContent(){fullscreen=false;}@Override public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError error){fullscreen=false;}});ad.show(MainActivity.this);}}
  @Override public void onAdFailedToLoad(LoadAdError error){appOpen=null;}
 });}
 private void gameOver(){completedGames++;}
 private void newGame(){if(completedGames>0 && completedGames%5==0 && interstitial!=null && !fullscreen && System.currentTimeMillis()-lastInterstitialAt>300000){InterstitialAd ad=interstitial;interstitial=null;fullscreen=true;ad.setFullScreenContentCallback(new FullScreenContentCallback(){@Override public void onAdDismissedFullScreenContent(){fullscreen=false;lastInterstitialAt=System.currentTimeMillis();loadInterstitial();}@Override public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError error){fullscreen=false;loadInterstitial();}});ad.show(this);}}
 private void showRewarded(){if(fullscreen)return;if(rewarded==null){loadRewarded();web.evaluateJavascript("(function(){var e=document.getElementById('hint');if(e)e.textContent='Reclama nu este disponibilă. Încearcă mai târziu.'})()",null);return;}
  RewardedAd ad=rewarded;rewarded=null;fullscreen=true;ad.setFullScreenContentCallback(new FullScreenContentCallback(){@Override public void onAdDismissedFullScreenContent(){fullscreen=false;loadRewarded();}@Override public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError error){fullscreen=false;loadRewarded();}});
  ad.show(this,reward->web.evaluateJavascript("window.gs777RewardEarned()",null));
 }
 public class Bridge {@JavascriptInterface public void onGameOver(){handler.post(()->gameOver());}@JavascriptInterface public void showRewarded(){handler.post(()->showRewarded());}@JavascriptInterface public void onNewGame(){handler.post(()->newGame());}}
 @Override protected void onDestroy(){if(web!=null){web.removeJavascriptInterface("AndroidAds");web.destroy();}super.onDestroy();}
}
