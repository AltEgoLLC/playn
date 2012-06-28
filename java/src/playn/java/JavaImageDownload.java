/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playn.java;

import java.io.IOException;
import java.net.MalformedURLException;
import playn.core.Image;
import playn.core.ImageDownload;
import playn.core.ResourceCallback;

public class JavaImageDownload implements ImageDownload {

    public JavaImageDownload() {
        
    }
    
    @Override
    public void downloadImage(String strUrl, int intRetryCount, long longDelayMS, ResourceCallback<Image> callback) throws MalformedURLException, IOException {
        
    }

}
