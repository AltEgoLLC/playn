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
  
  void openWebView(String url);
  
  void closeWebView();

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
}
