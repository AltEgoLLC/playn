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

public class HtmlImageDownload implements ImageDownload {

    private HtmlPlatform mPlatform;
    
    public HtmlImageDownload(HtmlPlatform platform) {
        mPlatform = platform;
    }

    @Override
    public boolean downloadImage(String strUrl, int intRetryCount, long longDelayMS, ResourceCallback<Image> callback) {
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
