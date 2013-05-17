/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playn.html;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import java.io.IOException;
import java.net.MalformedURLException;
import playn.core.Image;
import playn.core.ImageDownload;
import playn.core.PlayN;
import playn.core.ResourceCallback;
import playn.core.util.Callback;


public class HtmlImageDownload implements ImageDownload {

    private HtmlPlatform mPlatform;
    private String SERVER_URL = "http://slipstream-live.altego.com/" + "downloadImage.php";
    public HtmlImageDownload(HtmlPlatform platform) {
        mPlatform = platform;
    }

    @Override
    public boolean downloadImage(String strUrl, int intRetryCount, long longDelayMS, int downloadFlag, ResourceCallback<Image> callback) {
        final int doSaveFile = 1;
        if ((downloadFlag & doSaveFile) == doSaveFile)
        {
           String url = (new StringBuilder("")).append(SERVER_URL)
                            .append("?url=").append(strUrl).toString();
           
        PlayN.log().debug("Auth URL: " + url);
        PlayN.openWebView(url, "");           
            
        }
        ImageElement img = Document.get().createImageElement();
                            
//        if (img.hasAttribute("crossOrigin"))
        img.setAttribute("crossOrigin", "anonymous");

        img.setSrc(strUrl);
        try {
            HtmlGLContext ctx = mPlatform.graphics().ctx();

            HtmlImage hi = new HtmlImage(ctx, img);
            hi.addCallback(callback);
//            callback.done(new HtmlImage(ctx, img));
            return true;
        }
        catch (Exception ex) {
            callback.error(ex);
            return false;
        }
        catch (Error err) {
            callback.error(err);
            return false;
        }
    }
}
