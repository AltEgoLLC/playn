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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import playn.core.*;
import playn.core.json.JsonImpl;
import playn.core.util.Callback;
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
    activity.setEditTextDone();
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
  public void openWebView(final String url, final String callback_url){
      
      //PlayN.log().debug("Entering openUrlWithCallback");
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
      //activity.showWebView(url, callback_url);
      //PlayN.log().debug("Leaving openUrlWithCallback");
      activity.runOnUiThread(new Runnable() {
            public void run () {
                activity.showWebView(url, callback_url);
            }
        });
  }
  
    @Override
    public void closeWebView() {
        //activity.hideWebView();
        activity.runOnUiThread(new Runnable() {
            public void run () {
                activity.hideWebView();
            }
        });
    }
    
    @Override
    public boolean isWebViewVisible() {
        return activity.isWebViewVisible();
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
    public boolean downloadImage(final String strUrl, final int intRetryCount, final long longDelayMS, final ResourceCallback<Image> callback) {
        /*//
        AsyncTask<Void, Void, Void> mRegisterTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                imageDownload.downloadImage(strUrl, intRetryCount, longDelayMS, callback);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {}

        };
        mRegisterTask.execute(null, null, null);
        /*/
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                AsyncTask<Void, Void, Void> mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        imageDownload.downloadImage(strUrl, intRetryCount, longDelayMS, callback);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {}

                };
                mRegisterTask.execute(null, null, null);
            }
        });
        //*/
        
        return true;
    } // end downloadImage()
    
    @Override
    public void showAlertDialog(String message, String accept) {
        showAlertDialog(message, accept, null);
    }
    
    @Override
    public void showAlertDialog(final String message, final String accept, final Callback callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                String alertAccept = accept;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                if (message != null && !message.equals("")) {
                    builder.setTitle(message);
                }
                else {
                    builder.setTitle("AltEgo broke something!");
                    alertAccept = "OK";
                }
                builder.setNeutralButton(alertAccept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (callback != null) {
                            callback.onSuccess("success");
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    
    public void showBigAlertDialog(final String message, final String accept, final String cancel, final Callback callback)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                String alertAccept = accept;
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                if (message != null && !message.equals("")) {
                    builder.setTitle(message);
                }
                else {
                    builder.setTitle("AltEgo broke something!");
                    alertAccept = "OK";
                }

                builder.setPositiveButton(alertAccept, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                        if (callback != null) {
                            callback.onSuccess("success");
                        }
                }
                });

                builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                        if (callback != null) {
                            //callback.onSuccess("fail");
                        }
                }
                });              
                /*
                builder.setNeutralButton(alertAccept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (callback != null) {
                            callback.onSuccess("success");
                        }
                    }
                });*/
                AlertDialog alert = builder.create();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alert.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.FILL_PARENT;
                lp.height = WindowManager.LayoutParams.FILL_PARENT;
    
                alert.show();
                alert.getWindow().setAttributes(lp);
            }
        });        
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
          //System.out.println("****************");
          File sdDir = Environment.getExternalStorageDirectory();
          //System.out.println("SD Card Path: " + sdDir.getAbsolutePath());
          String abs = sdDir.getAbsolutePath() + "/PlayN/" + location;
          sdDir = new File(abs);
          try {
            imageDownload.setCacheDirectory(sdDir);
          } catch (ExceptionInInitializerError e) {
            PlayN.log().debug("Set Cache Directory: ", e);
          }
          //System.out.println("SD Card Final: " + sdDir.getAbsolutePath());
          //System.out.println("****************");
      }
      else {}
          //System.out.println("****************\nNo SD Card\n****************");
  }
    
    @Override
    public void showSoftKeyboard() {
        
        PlayN.log().debug("SHOW SOFT KEYBOARD");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(activity.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
        });
    }
    
    @Override
    public void hideSoftKeyboard() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(activity.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS, 0);
                activity.hideEditText();
            }
        });
    }
    
    @Override
    public void showEditText() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                activity.showEditText();
            }
        });
    }
    
    @Override
    public void showEditText(final int w, final int h, final int x, final int y, final float s, final int types[]) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                activity.showEditText(InputType.TYPE_CLASS_TEXT, "", w, h, x, y, s, types);
            }
        });
    }
    
    @Override
    public void hideEditText() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run () {
                activity.hideEditText();
            }
        });
    }
    
    public String getEditText() {
        return activity.getEditText();
    }
    
    public void setEditTextCallback(final Callback<String> callback) {
        activity.setEditTextCallback(callback);
    }
    
    @Override
    public String[] getPlatformInfo() {
        String[] info = {
            System.getProperty("os.version"),
            android.os.Build.VERSION.INCREMENTAL,
            android.os.Build.VERSION.SDK,
            android.os.Build.DEVICE,
            android.os.Build.MODEL,
            android.os.Build.PRODUCT,
            android.os.Build.MANUFACTURER
        };
        return info;
    }
    
    @Override
    public String[] getPlatformInfo(String[] ra) {
        return null;
    }
    
    @Override
    public String urlEncode(String str) {
        String url = str;
        
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            PlayN.log().debug("Url Encode Exception: ", e);
            url = str;
        }
        
        return url;
    }
    @Override
    public void addCallback(Callback callback)
    {
        activity.getBilling().addCallback(callback);
    }
    @Override  
    public void doPayment(String externalTransID, int uid, String paymentSystem, String description, String price, int[] items, String productNumber, String SERVER_URL, Callback callback)
    {
        activity.getBilling().buyObject(uid, productNumber, SERVER_URL);
    }    
    @Override
    public boolean doInstagram(final String imageUrl) {     
        if(appInstalledOrNot("com.instagram.android") == true)
        {
             PlayN.log().debug("doInstagram imageUrl: " + imageUrl);
            imageDownload.setInstagramMode();
            imageDownload.downloadImage(imageUrl, 10, 100, new ResourceCallback<Image>() {
               
                @Override
                public void done(Image resource) {
                    
                    int intLastPath = imageUrl.lastIndexOf( File.separator );
                    if (intLastPath >= 0)
                    {
                        
                        String strFilename = imageUrl.substring( intLastPath + 1 );
                        PlayN.log().debug("strFilename: " + strFilename);
                        shareInstagram(Uri.parse("file://" + imageDownload.getCacheDirectory() +"/i_"+strFilename));
                    }
                    
                    
                    
                }

                @Override
                public void error(Throwable err) {
                    
                    
                    
                }
            });
            return true;
        }
        else
        {
        return false;
        }
    }    

    
    @Override
    public boolean doGooglePlayRedirect() {     
        if(appInstalledOrNot("com.android.vending") == true)
        {
             //PlayN.log().debug("doInstagram imageUrl: " + imageUrl);
            openGooglePlay();
            return true;
        }
        else
        {
        return false;
        }
    }    
        
    
    private boolean appInstalledOrNot(String appname) 
    {
        boolean app_installed = false;
        try {
        ApplicationInfo info = activity.getPackageManager().getApplicationInfo(appname, 0);
        app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
        app_installed = false;
        }
        return app_installed;
    }    
    private void shareInstagram(Uri uri)
    {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/*"); // set mime type
        shareIntent.putExtra(Intent.EXTRA_STREAM,uri); // set uri
        shareIntent.setPackage("com.instagram.android");
        activity.startActivity(shareIntent);        
    }
    
    private void openGooglePlay()
    {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+"com.altego.riot.android")));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+"com.altego.riot.android")));
        }
        //activity.startActivity(shareIntent);             
    }
    @Override
    public boolean getDoneFlag()
    {
        return activity.getDoneFlag();
    }
    @Override
    public void setDoneFlag(boolean flag)
    {
        activity.setDoneFlag(flag);
    }
    
    
}
