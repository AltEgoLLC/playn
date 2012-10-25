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
package playn.java;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import playn.core.*;
import playn.core.json.JsonImpl;
import playn.core.util.Callback;
import playn.core.util.RunQueue;

public class JavaPlatform implements Platform {

  // Maximum delta time to consider between update() calls (in milliseconds). If the delta between
  // two update()s is greater than MAX_DELTA, we clamp to MAX_DELTA.
  private static final float MAX_DELTA = 100;

  // Minimum time between any two paint() calls (in milliseconds). We will paint every
  // FRAME_TIME ms, which is equivalent to (1000 * 1 / FRAME_TIME) frames per second.
  // TODO(pdr): this is set ridiculously low because we're using Java's software renderer which
  // causes the paint loop to be quite slow. Setting this to 10 prevents hitching that occurs when
  // we try to squeeze a paint() near max bound of FRAME_TIME.
  private static final float FRAME_TIME = 10;

  private static JavaPlatform instance;

  public static JavaPlatform register() {
    String sfprop = System.getProperty("playn.scaleFactor");
    if (sfprop != null) {
      try {
        return register(Float.parseFloat(sfprop));
      } catch (Exception e) {
        System.err.println("Invalid scaleFactor supplied '" + sfprop + "': " + e);
        // fall through and register with scale factor 1
      }
    }
    return register(1);
  }

  public static JavaPlatform register(float scaleFactor) {
    // Guard against multiple-registration. This can happen when running tests in maven.
    if (instance != null) {
      return instance;
    }

    instance = new JavaPlatform(scaleFactor);
    PlayN.setPlatform(instance);
    instance.init();
    return instance;
  }

  private final JavaAnalytics analytics = new JavaAnalytics();
  private final JavaAudio audio = new JavaAudio();
  private final JavaLog log = new JavaLog();
  private final JavaNet net = new JavaNet(this);
  private final JavaRegularExpression regex = new JavaRegularExpression();
  private final JavaStorage storage = new JavaStorage();
  private final JsonImpl json = new JsonImpl();
  private final JavaKeyboard keyboard = new JavaKeyboard();
  private final JavaPointer pointer = new JavaPointer();
  private final JavaGraphics graphics;
  private final JavaMouse mouse;
  private final JavaAssets assets;
  private final RunQueue runQueue = new RunQueue(log);
  private final JavaImageDownload imageDownload = new JavaImageDownload(this);
  private final JavaCookieStore cookieStore = new JavaCookieStore(storage);
  
  private int updateRate = 0;
  private float accum = updateRate;
  private double lastUpdateTime;
  private double lastPaintTime;

  public JavaPlatform(float scaleFactor) {
    graphics = new JavaGraphics(this, scaleFactor);
    mouse = new JavaMouse(graphics);
    assets = new JavaAssets(graphics, audio);
  }

  /**
   * Sets the title of the window.
   *
   * @param title the window title
   */
  public void setTitle(String title) {
    Display.setTitle(title);
  }

  @Override
  public Type type() {
    return Type.JAVA;
  }

  @Override
  public Audio audio() {
    return audio;
  }

  @Override
  public JavaGraphics graphics() {
    return graphics;
  }

  @Override
  public Json json() {
    return json;
  }

  @Override
  public Keyboard keyboard() {
    return keyboard;
  }

  @Override
  public Log log() {
    return log;
  }

  @Override
  public Net net() {
    return net;
  }

  @Override
  public Pointer pointer() {
    return pointer;
  }

  @Override
  public Mouse mouse() {
    return mouse;
  }

  @Override
  public Touch touch() {
    return new TouchStub();
  }

  @Override
  public Storage storage() {
    return storage;
  }

  @Override
  public Analytics analytics() {
    return analytics;
  }

  @Override
  public CookieStore cookieStore() {
    return cookieStore;
  }
  
  @Override
  public JavaAssets assets() {
    return assets;
  }

  @Override
  public RegularExpression regularExpression() {
    return regex;
  }

  @Override
  public float random() {
    return (float) Math.random();
  }

  @Override
  public double time() {
    return System.currentTimeMillis();
  }

  @Override
  public void openURL(String url) {
    System.out.println("Opening url: " + url);
    String browser = "chrome ";
    if (System.getProperty("os.name", "-").contains("indows"))
      browser = "rundll32 url.dll,FileProtocolHandler ";
    try {
      Runtime.getRuntime().exec(browser + url);
    } catch (IOException e) {
    }
  }

  @Override
  public void openWebView(String url, String callback_url){
      this.openURL(url);
  }
  
  @Override
    public void closeWebView() {
        // unimplemented
    }
  
  @Override
    public boolean isWebViewVisible() {
        return false;
    }
  
  @Override
  public void invokeLater(Runnable runnable) {
    runQueue.add(runnable);
  }

  @Override
  public void run(final Game game) {
    this.updateRate = game.updateRate();

    try {
      // initialize LWJGL (and show the display) now that the game has been initialized
      graphics.init();
      // now that the display is initialized we can init our mouse and keyboard
      mouse.init();
      keyboard.init();
    } catch (LWJGLException e) {
      throw new RuntimeException("Unrecoverable initialization error", e);
    }

    game.init();

    while (!Display.isCloseRequested()) {
      // Event handling.
      mouse.update();
      keyboard.update();
      pointer.update();
      net.update();

      // Execute any pending runnables.
      runQueue.execute();

      // Game loop.
      double now = time();
      float updateDelta = (float) (now - lastUpdateTime);
      if (updateDelta > 1) {
        updateDelta = updateDelta > MAX_DELTA ? MAX_DELTA : updateDelta;
        lastUpdateTime = now;

        if (updateRate == 0) {
          game.update(updateDelta);
          accum = 0;
        } else {
          accum += updateDelta;
          while (accum > updateRate) {
            game.update(updateRate);
            accum -= updateRate;
          }
        }
      }

      float paintDelta = (float) (now - lastPaintTime);
      if (paintDelta > FRAME_TIME) {
        graphics.paint(game, updateRate == 0 ? 0 : accum / updateRate);
        lastPaintTime = now;
      }

      Display.sync(60);
      Display.update();
    }

    System.exit(0);
  }

  protected void init() {
    storage.init();
  }
  
    @Override
    public boolean downloadImage(String strUrl, int intRetryCount, long longDelayMS, ResourceCallback<Image> callback) {
        return imageDownload.downloadImage(strUrl, intRetryCount, longDelayMS, callback);
    }
    
    @Override
    public void setPlatformCache(String location) {}

    @Override
    public void showAlertDialog(String message, String accept) {
        //JFrame frame = new JFrame("Alert");
        //JOptionPane.showMessageDialog(frame, message);
        
        String[] choices = { accept };
        
        JOptionPane.showOptionDialog(
                null                       // Center in window.
                , message        // Message
                , "Alert"               // Title in titlebar
                , JOptionPane.YES_NO_OPTION  // Option type
                , JOptionPane.PLAIN_MESSAGE  // messageType
                , null                       // Icon (none)
                , choices                    // Button text as above.
                , accept    // Default button's label
            );
    }
    
    @Override
    public void showAlertDialog(String message, String accept, Callback callback) {
        /*//
        JFrame frame = new JFrame("Alert");
        //JOptionPane.showMessageDialog(frame, message);
        int j = JOptionPane.YES_NO_CANCEL_OPTION;
        int k = JOptionPane.YES_OPTION;
        int l = JOptionPane.NO_OPTION;
        int m = JOptionPane.YES_NO_OPTION;
        int n = 0;
        int i = JOptionPane.showConfirmDialog(null, message, "Alert", 1);
        if (i > 0 || i <= 0) {
            callback.onSuccess("success");
        }
        //*/
        
        String[] choices = { accept };
        
        int response = JOptionPane.showOptionDialog(
            null                       // Center in window.
            , message        // Message
            , "Alert"               // Title in titlebar
            , JOptionPane.YES_NO_OPTION  // Option type
            , JOptionPane.PLAIN_MESSAGE  // messageType
            , null                       // Icon (none)
            , choices                    // Button text as above.
            , accept    // Default button's label
        );
        if (response > 0 || response <= 0) {
            callback.onSuccess("success");
        }
    }
    
    @Override
    public void showSoftKeyboard() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void hideSoftKeyboard() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String[] getPlatformInfo() {
        return null;
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
            //URL
        } catch (UnsupportedEncodingException ex) {
            PlayN.log().error("Url Encoding Exception: ", ex);
            url = URLEncoder.encode(str);
        }
        return url;
    }
}