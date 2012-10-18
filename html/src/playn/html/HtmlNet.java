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
package playn.html;

import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

import playn.core.Net;
import playn.core.CookieStore;
import playn.core.Platform;
import playn.core.PlayN;
import playn.core.util.Callback;
import playn.html.XDomainRequest.Handler;

public class HtmlNet implements Net {

  @Override
  public WebSocket createWebSocket(String url, WebSocket.Listener listener) {
    return new HtmlWebSocket(url, listener);
  }

    @Override
  public void get(String url, CookieStore cs, final Callback<String> callback) {
    try 
    {
        if (PlayN.assets().getPlatform().contains("MSIE"))
        {


            XDomainRequest xdrCall = XDomainRequest.create();
            xdrCall.open("GET", url);
            //PlayN.log().debug("URL STRING DEBUG: " + url);            
            xdrCall.setHandler(new Handler(){
            
                @Override
                public void onError(XDomainRequest xdr) {
                    PlayN.log().debug("GET CALL FAILED STRING:" + xdr.getStatus());
                    callback.onFailure(new RuntimeException("Get call failed. Response: " + xdr.getResponseText()));
                }

                @Override
                public void onLoad(XDomainRequest xdr) {
                    callback.onSuccess(xdr.getResponseText());
                    //throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void onProgress(XDomainRequest xdr) {
                    PlayN.log().debug("IN PROGRESS");
                   // throw new UnsupportedOperationException("Not supported yet.");
                }

                @Override
                public void onTimeout(XDomainRequest xdr) {
                    PlayN.log().debug("TIMEOUT");
                   // throw new UnsupportedOperationException("Not supported yet.");
                }

            });

            xdrCall.send();  
        }       
        else
        {

            XMLHttpRequest xhr = XMLHttpRequest.create();
            xhr.open("GET", url);            
            xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
                @Override
                public void onReadyStateChange(XMLHttpRequest xhr) {
                if (xhr.getReadyState() == XMLHttpRequest.DONE) {
                    if (xhr.getStatus() >= 400) {
                    callback.onFailure(new RuntimeException("Bad HTTP status code: " + xhr.getStatus()));
                    } else {
                    callback.onSuccess(xhr.getResponseText());
                    }
                }
                }
            });
            xhr.send();
        }
    } catch (Exception e) {
      callback.onFailure(e);
    }
  }

  @Override
  public void post(String url, CookieStore cs, String data, final Callback<String> callback) {
    try {
      XMLHttpRequest xhr = XMLHttpRequest.create();
      xhr.open("POST", url);
      xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
        @Override
        public void onReadyStateChange(XMLHttpRequest xhr) {
          if (xhr.getReadyState() == XMLHttpRequest.DONE) {
            if (xhr.getStatus() >= 400) {
              callback.onFailure(new RuntimeException("Bad HTTP status code: " + xhr.getStatus()));
            } else {
              callback.onSuccess(xhr.getResponseText());
            }
          }
        }
      });
      xhr.send(data);
    } catch (Exception e) {
      callback.onFailure(e);
    }
  }
}
