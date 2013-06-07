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
package playn.flash;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.JavaScriptObject;

import flash.events.Event;
import flash.display.Sprite;
import flash.events.EventType;

import java.io.IOException;
import java.net.MalformedURLException;
import playn.core.*;
import playn.core.json.JsonImpl;
import playn.core.util.Callback;
import playn.core.util.RunQueue;
import playn.html.HtmlRegularExpression;

public class FlashPlatform implements Platform {

  static final int DEFAULT_WIDTH = 640;
  static final int DEFAULT_HEIGHT = 480;

  private static final int FPS_COUNTER_MAX = 300;
  private static final int LOG_FREQ = 2500;
  private static final float MAX_DELTA = 100;
  private static FlashPlatform platform;

  public static FlashPlatform register() {
    if (platform == null)
      platform = new FlashPlatform();
    PlayN.setPlatform(platform);
    return platform;
  }

  static native void addEventListener(JavaScriptObject target, String name, EventHandler<?> handler, boolean capture) /*-{
    target.addEventListener(name, function(e) {
      handler.@playn.flash.EventHandler::handleEvent(Lflash/events/Event;)(e);
    }, capture);
  }-*/;

  private final FlashAssets assets = new FlashAssets();
  private final FlashAudio audio;
  private final HtmlRegularExpression regularExpression;
  private final FlashGraphics graphics;
  private final Json json;
  private final FlashKeyboard keyboard;
  private final FlashLog log;
  private final FlashNet net;
  private final FlashPointer pointer;
  private final FlashMouse mouse;
  private final RunQueue runQueue;
  private final FlashImageDownload imageDownload;
  private final FlashCookieStore cookieStore;

  private Game game;
  private TimerCallback paintCallback;
  private TimerCallback updateCallback;
  private Storage storage;
  private Analytics analytics;

  protected FlashPlatform() {
    log = new FlashLog();
    regularExpression = new HtmlRegularExpression();
    net = new FlashNet();
    audio = new FlashAudio();
    keyboard = new FlashKeyboard();
    pointer = new FlashPointer();
    mouse = new FlashMouse();
    json = new JsonImpl();
    graphics = new FlashGraphics();
    storage = new FlashStorage();
    analytics = new FlashAnalytics();
    runQueue = new RunQueue(log);
    cookieStore = new FlashCookieStore((FlashStorage) storage);
    
    imageDownload = new FlashImageDownload();
  }

  @Override
  public Analytics analytics() {
    return analytics;
  }

  @Override
  public FlashAssets assets() {
    return assets;
  }

  @Override
  public Audio audio() {
    return audio;
  }

  @Override
  public Graphics graphics() {
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
    public boolean downloadImage(String strUrl, int intRetryCount, long longDelayMS, int downloadFlag, ResourceCallback<Image> callback) {
        return imageDownload.downloadImage(strUrl, intRetryCount, longDelayMS, downloadFlag, callback);
    }
  
    @Override
    public void setPlatformCache(String location) {}

  @Override
  public Touch touch() {
    // TODO(pdr): need to implement this.
    return new TouchStub();
  }

  @Override
  public float random() {
    return (float) Math.random();
  }

  @Override
  public RegularExpression regularExpression() {
    return regularExpression;
  }

  @Override
  public Storage storage() {
    return storage;
  }

  @Override
  public void openURL(String url) {
      //TODO: implement
  }
  
  @Override
  public void openWebView(String url, String callback_url){
      this.openURL(url);
  }

  @Override
  public void invokeLater(Runnable runnable) {
    runQueue.add(runnable);
  }

  @Override
  public void run(final Game game) {
    final int updateRate = game.updateRate();
    this.game = game;

    // Game loop.
    paintCallback = new TimerCallback() {
      private float accum = updateRate;
      private double lastTime;
      int frameCounter = 0;
      private double frameCounterStart = 0;

      @Override
      public void fire() {
        // process pending actions
        runQueue.execute();

        double now = time();
        if (frameCounter == 0) {
          frameCounterStart = now;
        }

        float delta = (float)(now - lastTime);
        if (delta > MAX_DELTA) {
          delta = MAX_DELTA;
        }
        lastTime = now;

        if (updateRate == 0) {
          game.update(delta);
          accum = 0;
        } else {
          accum += delta;
          while (accum > updateRate) {
            game.update(updateRate);
            accum -= updateRate;
          }
        }

        game.paint(accum / updateRate);

        graphics.updateLayers();
        frameCounter++;
        if (frameCounter == FPS_COUNTER_MAX) {
          double frameRate = frameCounter /
            ((time() - frameCounterStart) / 1000.0);
          PlayN.log().info("FPS: " + frameRate);
          frameCounter = 0;
        }
      }
    };
    game.init();
    requestAnimationFrame(paintCallback);
  }

  @Override
  public double time() {
    return Duration.currentTimeMillis();
  }

  @Override
  public Type type() {
    return Type.FLASH;
  }

  private void requestAnimationFrame(final TimerCallback callback) {
    //  http://help.adobe.com/en_US/FlashPlatform/reference/actionscript/3/flash/display/DisplayObject.html#event:enterFrame
    FlashPlatform.captureEvent(Sprite.ENTERFRAME, new EventHandler<Event>() {
      @Override
      public void handleEvent(Event evt) {
        evt.preventDefault();
        callback.fire();
      }
    });
  }

  private native int setInterval(TimerCallback callback, int ms) /*-{
    return $wnd.setInterval(function() { callback.@playn.flash.TimerCallback::fire()(); }, ms);
  }-*/;

  private native int setTimeout(TimerCallback callback, int ms) /*-{
    return $wnd.setTimeout(function() { callback.@playn.flash.TimerCallback::fire()(); }, ms);
  }-*/;

  public static native void captureEvent(EventType eventType, EventHandler<?> eventHandler) /*-{
    $root.stage.addEventListener(eventType, function(arg) {
      eventHandler.@playn.flash.EventHandler::handleEvent(Lflash/events/Event;)(arg);
    });
  }-*/;

    @Override
    public CookieStore cookieStore() {
        return cookieStore;
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
    public void showAlertDialog(String message, String accept) {
        // unimplemented
    }
    
    @Override
    public void showAlertDialog(String message, String accept, Callback callback) {
        // unimplemented
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
        
        return url;
    }
    
    @Override
    public void showEditText() {}

    @Override
    public void hideEditText() {}

    @Override
    public void showEditText(int w, int h, int x, int y, float s, int[] types) {}

    @Override
    public String getEditText() {
        return "";
    }

    @Override
    public void setEditTextCallback(Callback<String> callback) {}

    @Override
    public void doPayment(String externalTransID, int uid, String paymentSystem, String description, String Payment, int[] items, String productNumber, String SERVER_URL, Callback callback) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addCallback(Callback callback) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean doInstagram(String imageUrl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getDoneFlag() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setDoneFlag(boolean flag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean doGooglePlayRedirect() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void showBigAlertDialog(String message, String accept, String cancel, Callback callback) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void scanMedia(String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void setEditText(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }       
}
