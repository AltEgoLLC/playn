/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playn.flash;

import java.io.IOException;
import java.net.MalformedURLException;
import playn.core.Image;
import playn.core.ImageDownload;
import playn.core.ResourceCallback;

public class FlashImageDownload implements ImageDownload {
    
    public FlashImageDownload() {
        
    }

    @Override
    public boolean downloadImage(String strUrl, int intRetryCount, long longDelayMS, int downloadFlag, ResourceCallback<Image> callback) {
        return false;
    }
}
