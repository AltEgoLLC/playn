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
import playn.android.altego.DownloadedBitmap;
import playn.core.Image;
import playn.core.ImageDownload;
import playn.core.ResourceCallback;
import playn.core.gl.Scale;

public class AndroidImageDownload implements ImageDownload {
    
    private File m_fileCacheDirectory = null;

    public void downloadImage(final String strUrl, final int intRetryCount, final long longDelayMS, ResourceCallback<Image> callback) throws MalformedURLException, IOException
    {
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
                    throw ex;
                }
                catch (IOException ex)
                {
                    throw ex;
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
                    finally
                    {
                        if (streamFileOutput != null)
                        {
                            streamFileOutput.close();
                        }
                    }
                }
            }
        }

        callback.done(new AndroidImage(, bitmapDownloaded.getBitmap(), Scale.ONE));
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
