/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playn.core;

import java.io.IOException;
import java.net.MalformedURLException;

public interface ImageDownload {
    public void downloadImage(final String strUrl, final int intRetryCount, final long longDelayMS, ResourceCallback<Image> callback) throws MalformedURLException, IOException;
}