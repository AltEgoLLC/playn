/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playn.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import playn.android.altego.DownloadedBitmap;
import playn.core.Image;
import playn.core.ImageDownload;
import playn.core.ResourceCallback;
import playn.core.gl.Scale;

public class AndroidImageDownload implements ImageDownload {
    
    private File m_fileCacheDirectory = null;
    private final AndroidPlatform mPlatform;
    
    public AndroidImageDownload(AndroidPlatform platform) {
        mPlatform = platform;
    }

    @Override
    public boolean downloadImage(final String strUrl, final int intRetryCount, final long longDelayMS, ResourceCallback<Image> callback)
    {
        System.out.println("*******************");
        DownloadedBitmap bitmapDownloaded = null;

        File fileLocalImage = null;
        int intLastPath = strUrl.lastIndexOf( File.separator );
        if (intLastPath >= 0)
        {
            String strFilename = strUrl.substring( intLastPath + 1 );
            if (getCacheDirectory() != null)
            {
                fileLocalImage = new File( getCacheDirectory(), strFilename );
                bitmapDownloaded = loadImage( fileLocalImage );
            }
        }
        
        System.out.println( "File Image Url: " + strUrl);
        if (fileLocalImage != null) {
            System.out.println( "File Image Path: " + fileLocalImage.toString());
            System.out.println( "Image Path Is File: " + fileLocalImage.isFile() );
            System.out.println( "Image Path Exists: " + fileLocalImage.exists());
        }

        if (bitmapDownloaded == null)
        {
            Bitmap imageBitmap = null;

            for (int intAttempt = 0; intAttempt < intRetryCount; ++intAttempt)
            {
                try
                {
                    URL url = new URL( strUrl );

                    HttpURLConnection connection = null;
                    try
                    {
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput( true );
                        connection.setConnectTimeout( 2000 );
                        connection.connect();

                        InputStream streamIn = connection.getInputStream();
                        imageBitmap = BitmapFactory.decodeStream( streamIn );

                        if (imageBitmap != null)
                        {
                            break;
                        }
                        else
                        {
                            //AltEgo.LogWarning( "downloadImage", strUrl, " retry: ", Integer.toString( intAttempt ) );
                            System.err.println("downloadImage: " + strUrl + "\nretry: " + Integer.toString( intAttempt ) );
                            try
                            {
                                Thread.sleep( longDelayMS );
                            }
                            catch (InterruptedException ex)
                            {
                                //AltEgo.LogError( "downloadImage", "Retry thread sleep interrupted: ", ex.getMessage() );
                                System.out.println( "downloadImage -- Retry thread sleep interrupted:\n" + ex.getMessage() );
                            }
                        }
                    }
                    catch (SocketTimeoutException ex)
                    {
                        //AltEgo.LogError( "downloadImage", "SocketTimeoutException occurred: ", ex.getMessage() );
                        System.out.println( "downloadImage -- SocketTimeoutException occurred:\n" + ex.getMessage() );
                    }
                    catch (IOException ex)
                    {
                        //AltEgo.LogError( "downloadImage", "Exception occurred: ", ex.getMessage() );
                        System.out.println( "downloadImage -- Exception Occurred:\n" + ex.getMessage() );
                    }
                    finally
                    {
                        if (connection != null)
                        {
                            connection.disconnect();
                        }
                    }
                }
                catch (MalformedURLException ex)
                {
                    System.out.println( "downloadImage -- MalformedURLException occurred:\n" + ex.getMessage() );
                }
                catch (IOException ex)
                {
                    System.out.println( "downloadImage -- IOException occurred:\n" + ex.getMessage() );
                }
            }
            
            if (imageBitmap != null)
            {
                bitmapDownloaded = new DownloadedBitmap( fileLocalImage, imageBitmap );

                // Write the bitmap to SD card.
                if (fileLocalImage != null && !fileLocalImage.exists())
                {
                    FileOutputStream streamFileOutput = null;
                    try
                    {
                        System.out.println( "Image Path Is File: " + fileLocalImage.isFile() );
                        
                        try { System.out.println( "MKDIR Image Path: " + fileLocalImage.mkdir() ); }
                        catch (SecurityException e) { System.out.println("Exception: " + e.toString() + "\n" + e.getMessage()); }
                        
                        streamFileOutput = new FileOutputStream( fileLocalImage );
                        if (imageBitmap.compress( Bitmap.CompressFormat.PNG, 100, streamFileOutput ))
                        {
                            //AltEgo.LogInfo( "downloadImage", "Stored downloaded bitmap as: " + fileLocalImage.getAbsolutePath() );
                            System.out.println( "downloadImage -- Stored downloaded bitmap as: " + fileLocalImage.getAbsolutePath() );
                        }
                        else
                        {
                            //AltEgo.LogError( "downloadImage", "Failed to locally store bitmap: " + fileLocalImage.getAbsolutePath() );
                            System.out.println( "downloadImage -- Failed to locally store bitmap: " + fileLocalImage.getAbsolutePath() );
                        }
                    }
                    catch (FileNotFoundException ex)
                    {
                        System.out.println( "downloadImage -- Failed to open file: " + fileLocalImage.getAbsolutePath() + 
                                "\n" + ex.toString() + 
                                "\n" + ex.getMessage());
                    }
                    finally
                    {
                        if (streamFileOutput != null)
                        {
                            try
                            {
                                streamFileOutput.close();
                            }
                            catch (IOException ex)
                            {
                                Logger.getLogger( AndroidImageDownload.class.getName() ).log( Level.SEVERE, null, ex );
                            }
                        }
                    }
                }
            }
        }

        /*//
        System.out.println("callback null? " + (callback == null));
        System.out.println("mPlatform null? " + (mPlatform == null));
        System.out.println("graphics() null? " + (mPlatform.graphics() == null));
        System.out.println("ctx() null? " + (mPlatform.graphics().ctx() == null));
        System.out.println("bitmap null? " + (bitmapDownloaded == null));
        //*/
        System.out.println("*******************");
        //if (callback != null && bitmapDownloaded != null) 
        try {
            callback.done(new AndroidImage(mPlatform.graphics().ctx(), bitmapDownloaded.getBitmap(), Scale.ONE));
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
        //return bitmapDownloaded;
    }
    
    private DownloadedBitmap loadImage(final File fileLocalImage)
    {
        DownloadedBitmap bitmapDownloaded = null;
        
        if (fileLocalImage.isFile())
        {
            try 
            {
                FileInputStream input = null;
                try
                {
                    input = new FileInputStream( fileLocalImage.getAbsolutePath() );
                    Bitmap bitmap = BitmapFactory.decodeStream( input );
                    if (bitmap != null)
                    {
                        bitmapDownloaded = new DownloadedBitmap( fileLocalImage, bitmap ); 
                    }
                }
                finally
                {
                    if (input != null)
                    {
                        input.close();
                    }
                }
            } 
            catch (Exception ex)
            {
                //AltEgo.LogError( "loadImage", "Failed to load image from local storage: ", fileLocalImage.getAbsolutePath() );
                System.out.println( "loadImage -- Failed to load image from local storage:\n" + fileLocalImage.getAbsolutePath() );
            }
        }
        
        return bitmapDownloaded;
    }
    
    public void setCacheDirectory(File fileCacheDirectory)
    {
        if (fileCacheDirectory != null)
        {
            if (fileCacheDirectory.exists() || fileCacheDirectory.mkdirs())
            {
                m_fileCacheDirectory = fileCacheDirectory;
            }
            else
            {
                //AltEgo.LogWarning( "setCacheDirectory", "Failed to find accessible cache path: ", fileCacheDirectory.getAbsolutePath() );
                System.out.println( "setCacheDirectory -- Failed to find accessible cache path:\n" + fileCacheDirectory.getAbsolutePath() );
            }
        }
    }
    
    public File getCacheDirectory() {
        return m_fileCacheDirectory;
    }
}
