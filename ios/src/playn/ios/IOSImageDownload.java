/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playn.ios;

import java.io.IOException;
import java.net.MalformedURLException;
import playn.core.Image;
import playn.core.ImageDownload;
import playn.core.ResourceCallback;

public class IOSImageDownload implements ImageDownload {

    public IOSImageDownload() {
        
    }

    @Override
    public void downloadImage(String strUrl, int intRetryCount, long longDelayMS, ResourceCallback<Image> callback) throws MalformedURLException, IOException {
        
    }
}
