/**
 * Copyright 2010 The PlayN Authors
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
package playn.core;

import playn.core.util.Callback;

/**
 * The main PlayN interface. The static methods in this class provide access to
 * the various available subsystems.
 *
 * <p>
 * You must register a {@link Platform} before calling any of these methods. For
 * example, <code>JavaPlatform.register();</code>.
 * </p>
 */
public class PlayN {

  private static Platform platform;
  private static Analytics analytics;
  private static String name;
  private static int appIcon = 0;

  /**
   * Call this method to start your {@link Game}. It must be called only once,
   * and all work after this call is made will be performed in {@link Game}'s
   * callback methods.
   */
  public static void run(Game game) {
    platform.run(game);
  }

  /**
   * Returns the platform {@link Platform.Type}.
   */
  public static Platform.Type platformType() {
    return platform.type();
  }

  /**
   * Returns the current time, as a double value in millis since January 1, 1970, 00:00:00 GMT.
   *
   * <p> This is equivalent to the standard JRE {@code new Date().getTime();}, but it slightly
   * terser, and avoids the use of {@code long} values, which are best avoided when translating to
   * JavaScript. </p>
   */
  public static double currentTime() {
    return platform.time();
  }

  /**
   * Gets a random floating-point value in the range [0, 1).
   */
  public static float random() {
    return platform.random();
  }

  /**
   * Opens the given URL in the default browser.
   */
  public static void openURL(String url) {
    platform.openURL(url);
  }
  
  public static void openWebView(String url, String callback_url) {
    platform.openWebView(url, callback_url);  
  }
  
    public static void closeWebView() {
        platform.closeWebView();
    }
    
    public static boolean webViewIsVisible() {
        return platform.isWebViewVisible();
    }
    
    public static void showAlertDialog(String message, String accept) {
        platform.showAlertDialog(message, accept);
    }
    
    public static void showAlertDialog(String message, String accept, Callback callback) {
        platform.showAlertDialog(message, accept, callback);
    }
    
    public static String[] getPlatformInfo() {
        return platform.getPlatformInfo();
    }
    
    public static String[] getPlatformInfo(String[] ra) {
        return platform.getPlatformInfo(ra);
    }
    
    public static String urlEncode(String str) {
        return platform.urlEncode(str);
    }

  /**
   * Queues the supplied runnable for invocation on the game thread prior to the next call to
   * {@link Game#update}.
   */
  public static void invokeLater(Runnable runnable) {
    platform.invokeLater(runnable);
  }

  /**
   * Returns the {@link Audio} service.
   */
  public static Audio audio() {
    return platform.audio();
  }

  /**
   * Returns the {@link Graphics} service.
   */
  public static Graphics graphics() {
    return platform.graphics();
  }

  /**
   * Returns the {@link Assets} service.
   */
  public static Assets assets() {
    return platform.assets();
  }

  /**
   * Returns the {@link Json} service.
   */
  public static Json json() {
    return platform.json();
  }

  /**
   * Returns the {@link Keyboard} input service.
   */
  public static Keyboard keyboard() {
    return platform.keyboard();
  }

  /**
   * Returns the {@link Log} service.
   */
  public static Log log() {
    return platform.log();
  }

  /**
   * Returns the {@link Net} service.
   */
  public static Net net() {
    return platform.net();
  }

  /**
   * Returns the {@link Pointer} input service.
   */
  public static Pointer pointer() {
    return platform.pointer();
  }

  /**
   * Returns the {@link Mouse} input service.
   */
  public static Mouse mouse() {
    return platform.mouse();
  }

  /**
   * Returns the {@link RegularExpression} service.
   */
  public static RegularExpression regularExpression() {
    return platform.regularExpression();
  }

  /**
   * Returns the {@link Touch} input service.
   */
  public static Touch touch() {
    return platform.touch();
  }

  /**
   * Returns the {@link Storage} storage service.
   */
  public static Storage storage() {
      if (platform != null) {
        return platform.storage();
      }
      else {
          return null;
      }
  }

  /**
   * Returns the {@link Analytics} analytics service.
   */
  public static Analytics analytics() {
      if(analytics != null)
      {
          return analytics;
      }
    return platform.analytics();
  }
  
  public static void clearCookies() {
      platform.cookieStore().clear();
  }

  /**
   * Adds new cookie to the cookieStore
   * @param cookie the key
   * @param value the value to add
   */
  public static void addCookie(String cookie, String value){
      platform.cookieStore().put(cookie, value);
  }
  
  /**
   * Gets a cookie to the cookieStore
   * @param cookie the key of what to get
   * @return the value of that cookie (null if it doesn't exist)
   */
  public static String getCookie(String cookie) {
      return platform.cookieStore().get(cookie);
  }
  
  public static void clearStorage() {
      platform.storage().clear();
  }
  
  /**
   * Configures the current {@link Platform}. Do not call this directly unless you're implementing
   * a new platform.
   */
  public static void setPlatform(Platform platform) {
    PlayN.platform = platform;
  }
  
  /*//
  public static Platform getPlatform() {
      return platform;
  }
  //*/
  
  public static void downloadImage(String url, int intRetryCount, long longDelayMS, int downloadFlag, ResourceCallback callback) {
      platform.downloadImage(url, intRetryCount, longDelayMS, downloadFlag, callback);
  }
  
  public static void openUrl(String url) {
      platform.openURL(url);
  }
  
  public static void setPlatformCache(String location) {
      platform.setPlatformCache(location);
  }

  public static void setAnalytics(Analytics newAnalytics)
  {
      analytics = newAnalytics;
  }
  
  public static void showSoftKeyboard() {
      platform.showSoftKeyboard();
  }
  
  public static void hideSoftKeyboard() {
      platform.hideSoftKeyboard();
  }
  
  public static void doPayment(String externalTransID, int uid, String paymentSystem, String description, String price, int[] items, String productNumber, String SERVER_URL, Callback callback)
  {
      platform.doPayment(externalTransID, uid, paymentSystem, description, price, items, productNumber, SERVER_URL, callback);
  }
  public static void addCallback(Callback callback)
  {
    platform.addCallback(callback);
      
  }          
  
  public static void setAppName(String appName) {
      name = appName;
  } 
  
  public static String getAppName() {
      return name;
  }
  
  public static void setAppIcon(int icon) {
      appIcon = icon;
  }
  
  public static int getAppIcon() {
      return appIcon;
  }
  
  public static void showEditText() {
      if (platform.type().equals(Platform.Type.ANDROID)) {
          platform.showEditText();
      }
  }

  public static void setEditText(String s) {
      if (platform.type().equals(Platform.Type.ANDROID)) {
          platform.setEditText(s);
      }
  }  
  
  public static void showEditText(int w, int h, int x, int y, float s, int[] types) {
      if (platform.type().equals(Platform.Type.ANDROID)) {
          platform.showEditText(w, h, x, y, s, types);
      }
  }
  
  public static void hideEditText() {
      if (platform.type().equals(Platform.Type.ANDROID)) {
          platform.hideEditText();
      }
  }
  
    public static void setEditTextCallback(final Callback<String> callback) {
        platform.setEditTextCallback(callback);
    }

    public static boolean doInstagram(String imageUrl) {
        return platform.doInstagram(imageUrl);
    }

    public static boolean doGooglePlayRedirect(){
        return platform.doGooglePlayRedirect();
    }
    
    public static boolean getDoneFlag()
    {
        return platform.getDoneFlag();
    }
    
    public static void setDoneFlag(boolean flag)
    {
        platform.setDoneFlag(flag);
    }
    public  static void showBigAlertDialog(final String message, final String accept, final String cancel, final Callback callback)
    {
        platform.showBigAlertDialog(message, accept, cancel, callback);
    }
      public static void scanMedia(String path){
          platform.scanMedia(path);
      } 
  // Non-instantiable
  private PlayN() {
  }
}
