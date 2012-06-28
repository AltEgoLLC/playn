/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playn.java;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import playn.core.Image;
import playn.core.ImageDownload;
import playn.core.ResourceCallback;
import playn.core.gl.Scale;

public class JavaImageDownload implements ImageDownload {

    private JavaPlatform mPlatform;
    
    public JavaImageDownload(JavaPlatform platform) {
        mPlatform = platform;
    }
    
    @Override
    public void downloadImage(String strUrl, int intRetryCount, long longDelayMS, ResourceCallback<Image> callback) throws MalformedURLException, IOException {
        BufferedImage img = null;
        try {
            URL url = new URL(strUrl);
            img = ImageIO.read(url);
            JavaImage jImg = new JavaImage(mPlatform.graphics().ctx(), img, Scale.ONE) {
                @Override
                public void addCallback(ResourceCallback<? super Image> callback) {}
            };
            
            callback.done(jImg);
            
        } catch (IOException e) {
            callback.error(e);
        }
    } // end downloadImage
}
