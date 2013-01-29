/**
 * Copyright 2010 The PlayN Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package playn.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import playn.core.util.Callback;

/**
 * Generic platform interface. New platforms are defined as implementations of this interface.
 */
public interface Platform {
  public enum Type { JAVA, HTML, ANDROID, IOS, FLASH }

  void run(Game game);

  Platform.Type type();

  double time();

  float random();

  void openURL(String url);
  
  void openWebView(String url, String callback_url);
  
  void closeWebView();
  
  boolean isWebViewVisible();

  void invokeLater(Runnable runnable);

  Audio audio();

  Graphics graphics();

  Assets assets();

  Json json();

  Keyboard keyboard();

  Log log();

  Net net();

  Pointer pointer();

  Mouse mouse();

  Touch touch();

  Storage storage();

  Analytics analytics();
  
  CookieStore cookieStore();
  
  RegularExpression regularExpression();
  
  public boolean downloadImage(String strUrl, int intRetryCount, long longDelayMS, ResourceCallback<Image> callback);
  
  //public void setCacheDirectory(File file);
  
  public void setPlatformCache(String location);
  
  public void showAlertDialog(String message, String accept);
  
  public void showAlertDialog(String message, String accept, Callback callback);
  
  public void showSoftKeyboard();
  
  public void hideSoftKeyboard();
  
  public String[] getPlatformInfo();
  
  public String[] getPlatformInfo(String[] ra);
  
  public String urlEncode(String str);
  
  public void doPayment(String externalTransID, int uid, String paymentSystem, String description, String Payment, int[] items, String productNumber, String SERVER_URL, Callback callback);
  
  public void addCallback(Callback callback);
  
  public void showEditText();
  
  public void hideEditText();
  
  public void showEditText(final int w, final int h, final int x, final int y, final float s, final int[] types);
  
  public String getEditText();
  
  public void setEditTextCallback(final Callback<String> callback);
}
