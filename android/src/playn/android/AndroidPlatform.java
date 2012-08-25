/**
 * Copyright 2011 The PlayN Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package playn.android;

import android.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import playn.core.*;
import playn.core.json.JsonImpl;
import playn.core.util.RunQueue;

public class AndroidPlatform implements Platform {

  public static final boolean DEBUG_LOGS = true;

  public static AndroidPlatform register(AndroidGL20 gl20, GameActivity activity) {
    AndroidPlatform platform = new AndroidPlatform(activity, gl20);
    PlayN.setPlatform(platform);
    return platform;
  }

  Game game;
  GameActivity activity;

  private final AndroidAnalytics analytics;
  private final AndroidAssets assets;
  private final AndroidAudio audio;
  private final AndroidGraphics graphics;
  private final AndroidKeyboard keyboard;
  private final AndroidLog log;
  private final AndroidNet net;
  private final AndroidPointer pointer;
  private final AndroidStorage storage;
  private final AndroidImageDownload imageDownload;
  private final TouchImpl touch;
  private final AndroidTouchEventHandler touchHandler;
  private final Json json;
  private final RunQueue runQueue;
  private final AndroidCookieStore cookieStore;
  
  protected AndroidPlatform(GameActivity activity, AndroidGL20 gl20) {
    this.activity = activity;

    log = new AndroidLog();
    audio = new AndroidAudio(this);
    graphics = new AndroidGraphics(this, gl20, activity.scaleFactor());
    analytics = new AndroidAnalytics();
    assets = new AndroidAssets(this);
    json = new JsonImpl();
    keyboard = new AndroidKeyboard(this);
    net = new AndroidNet(this);
    pointer = new AndroidPointer();
    storage = new AndroidStorage(activity);
    touch = new TouchImpl();
    touchHandler = new AndroidTouchEventHandler(graphics, activity.gameView());
    runQueue = new RunQueue(log);
    cookieStore = new AndroidCookieStore(storage);
    
    imageDownload = new AndroidImageDownload(this);
  }

  @Override
  public AndroidAssets assets() {
    return assets;
  }

  @Override
  public AndroidAnalytics analytics() {
    return analytics;
  }

  @Override
  public AndroidAudio audio() {
    return audio;
  }

  @Override
  public AndroidGraphics graphics() {
    return graphics;
  }

  @Override
  public Json json() {
    return json;
  }

  @Override
  public AndroidKeyboard keyboard() {
    return keyboard;
  }

  @Override
  public AndroidLog log() {
    return log;
  }

  @Override
  public AndroidNet net() {
    return net;
  }

  @Override
  public void openURL(String url) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    activity.startActivity(browserIntent);
  }
  
  @Override
  public void openWebView(String url){
      
      PlayN.log().debug("Entering openUrlWithCallback");
      /*
      LinearLayout layout = activity.viewLayout();
      WebView webView = new WebView(activity);
      
      webView.getSettings().setJavaScriptEnabled(true);
      webView.setVisibility(View.VISIBLE);
      webView.loadUrl(url); 
      layout.addView(webView);
      
      webView.setWebViewClient(new WebViewClient() {            

          @Override 
          public void onPageFinished(WebView view, String url) {
              view.setVisibility(View.GONE);
          }
      });
      */
      activity.showWebView(url);
      PlayN.log().debug("Leaving openUrlWithCallback");
  }
  
    @Override
    public void closeWebView() {
        activity.hideWebView();
    }

  @Override
  public void invokeLater(Runnable runnable) {
    runQueue.add(runnable);
  }

  @Override
  public Mouse mouse() {
    return new MouseStub();
  }

  @Override
  public TouchImpl touch() {
    return touch;
  }

  public AndroidTouchEventHandler touchEventHandler() {
    return touchHandler;
  }

  @Override
  public AndroidPointer pointer() {
    return pointer;
  }

  @Override
  public float random() {
    return (float) Math.random();
  }

  @Override
  public AndroidRegularExpression regularExpression() {
    return new AndroidRegularExpression();
  }

  @Override
  public void run(Game game) {
    this.game = game;
    game.init();
  }

  @Override
  public AndroidStorage storage() {
    return storage;
  }

  @Override
  public CookieStore cookieStore() {
    return cookieStore;
  }
  
  @Override
  public double time() {
    return System.currentTimeMillis();
  }

  @Override
  public Type type() {
    return Type.ANDROID;
  }
  
    @Override
    public boolean downloadImage(String strUrl, int intRetryCount, long longDelayMS, ResourceCallback<Image> callback) {
        return imageDownload.downloadImage(strUrl, intRetryCount, longDelayMS, callback);
    }

  void update(float delta) {
    runQueue.execute();
    if (game != null) {
      game.update(delta);
    }
  }
  
    @Override
  public void setPlatformCache(String location) {
      if (imageDownload != null) {
          System.out.println("****************");
          File sdDir = Environment.getExternalStorageDirectory();
          System.out.println("SD Card Path: " + sdDir.getAbsolutePath());
          String abs = sdDir.getAbsolutePath() + "/PlayN/" + location;
          sdDir = new File(abs);
          imageDownload.setCacheDirectory(sdDir);
          System.out.println("SD Card Final: " + sdDir.getAbsolutePath());
          System.out.println("****************");
      }
      else
          System.out.println("****************\nNo SD Card\n****************");
  }
}
