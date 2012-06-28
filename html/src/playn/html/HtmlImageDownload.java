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
import playn.core.ResourceCallback;

public class HtmlImageDownload implements ImageDownload {

    private HtmlPlatform mPlatform;
    
    public HtmlImageDownload(HtmlPlatform platform) {
        mPlatform = platform;
    }

    @Override
    public void downloadImage(String strUrl, int intRetryCount, long longDelayMS, ResourceCallback<Image> callback) throws MalformedURLException, IOException {
        ImageElement img = Document.get().createImageElement();
        
        if (img.hasAttribute("crossOrigin"))
            img.setAttribute("crossOrigin", "anonymous");
        
        img.setSrc(strUrl);
        callback.done(new HtmlImage(mPlatform.graphics().ctx(), img));
    }
}
