/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playn.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
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
import playn.core.PlayN;
import playn.core.ResourceCallback;
import playn.core.gl.Scale;

public class AndroidImageDownload implements ImageDownload {
    
    private File m_fileCacheDirectory = null;
    private final AndroidPlatform mPlatform;
    private boolean m_instagram = false;
    
    public AndroidImageDownload(AndroidPlatform platform) {
        mPlatform = platform;
    }
    

    
    
    @Override
    public boolean downloadImage(final String strUrl, final int intRetryCount, final long longDelayMS, int downloadFlag, ResourceCallback<Image> callback)
    {
//        System.out.println("*******************");
        DownloadedBitmap bitmapDownloaded = null;
        final int doSaveFile = 1; //android save
        
        File fileLocalImage = null;
        String strFilename = "";
        File filePath = getCacheDirectory();
        int intLastPath = strUrl.lastIndexOf( File.separator );
        if (intLastPath >= 0)
        {
            
            strFilename = strUrl.substring( intLastPath + 1 );
            if (filePath != null)
            {
                fileLocalImage = new File( filePath, strFilename );
                bitmapDownloaded = loadImage( fileLocalImage );
            }
        }
        if ((downloadFlag & doSaveFile) == doSaveFile)
        {
            Log.i("downloadFlag & doSaveFile","TEST0");
            if (bitmapDownloaded != null )
            {

             
                File sdDir = Environment.getExternalStorageDirectory();
                //System.out.println("SD Card Path: " + sdDir.getAbsolutePath());
                String abs = sdDir.getAbsolutePath() + "/PlayN/Champion Select";  
                filePath = new File(abs);

                
                 Log.i("downloadFlag & doSaveFile","TEST1");
                FileOutputStream streamFileOutput2 = null;
                Bitmap downloadBitmap = bitmapDownloaded.getBitmap();
                try
                {
                    if (filePath != null)
                    {
                        if (filePath.exists() || filePath.mkdirs())
                        {       
                            // append "i_" to the start of  saved files
                            String newFilePath = filePath.getAbsolutePath() + "/" + strFilename;

                             Log.i("downloadFlag & doSaveFile","TEST2: "+ newFilePath);
                            File fileLocalImage2 = new File( filePath, strFilename );
                            streamFileOutput2 = new FileOutputStream( fileLocalImage2 );
                            if (downloadBitmap != null)
                            {
                                if (downloadBitmap.compress( Bitmap.CompressFormat.PNG, 100, streamFileOutput2 ))
                                {
                                    //System.out.println( "downloadImage -- Stored downloaded bitmap as: " + fileLocalImage.getAbsolutePath() );
                                }
                            }                            
                            
                        }
                    }

                }
                catch (FileNotFoundException ex)
                {

                }
                finally
                {
                    if (streamFileOutput2 != null)
                    {
                        try
                        {
                            streamFileOutput2.close();
                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger( AndroidImageDownload.class.getName() ).log( Level.SEVERE, null, ex );
                        }
                    }
                }    
            }
        }
        if (m_instagram == true)
        {
            Log.i("INSTAGRAM","TRUE");
            final int instagramSize = 612;
            m_instagram = false;
            if (bitmapDownloaded != null )
            {
                // save as max side = 612
                Bitmap instagramBitmap = bitmapDownloaded.getBitmap();
                int instaW = instagramBitmap.getWidth();
                int instaH = instagramBitmap.getHeight();
                int maxSide = 0;
                if (instaH >= instaW) { maxSide = instaH;} else {maxSide = instaW;}
                if (maxSide > instagramSize)
                {
                    instaH = (instaH * instagramSize)/maxSide;
                    instaW = (instaW * instagramSize)/maxSide;
                }
                Bitmap iSizeBitmap = null;
                try
                {
                    // create an instagramSize bitmap - almost black
                    Bitmap bgBitmap = Bitmap.createBitmap(instagramSize, instagramSize, Bitmap.Config.ARGB_8888);
                    bgBitmap.eraseColor(0xff1d1b1b);
                    // create the scaled poster and BLIT it centered horizontally
                    iSizeBitmap = Bitmap.createScaledBitmap(instagramBitmap, instaW, instaH, false);
                    Canvas canvas = new Canvas(bgBitmap);
                    Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
                    canvas.drawBitmap(iSizeBitmap, (instagramSize - instaW)/2, 0, paint);
                    iSizeBitmap = bgBitmap.copy(Bitmap.Config.ARGB_8888, true);
                }
                catch(OutOfMemoryError E)
                {
    //                    System.out.println( "*******************OutOfMemoryError: " + fileLocalImage.getAbsolutePath() );
                }              
                FileOutputStream streamFileOutput2 = null;
                try
                {
                    // append "i_" to the start of instagram saved files
                    File fileLocalImage2 = new File( getCacheDirectory(), "i_" + strFilename );
                    streamFileOutput2 = new FileOutputStream( fileLocalImage2 );
                    if (iSizeBitmap != null)
                    {
                        if (iSizeBitmap.compress( Bitmap.CompressFormat.PNG, 100, streamFileOutput2 ))
                        {
                            //System.out.println( "downloadImage -- Stored downloaded bitmap as: " + fileLocalImage.getAbsolutePath() );
                        }
                    }
                }
                catch (FileNotFoundException ex)
                {

                }
                finally
                {
                    if (streamFileOutput2 != null)
                    {
                        try
                        {
                            streamFileOutput2.close();
                        }
                        catch (IOException ex)
                        {
                            Logger.getLogger( AndroidImageDownload.class.getName() ).log( Level.SEVERE, null, ex );
                        }
                    }
                }            
            }
        }
        if (bitmapDownloaded == null)
        {
            Log.i("bitmapDownloaded","TRUE");
            Bitmap imageBitmap = null;
//        System.out.println( "*******************bitmapDownloaded == null ");

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
                        //connection.setUseCaches(true);
                        connection.setConnectTimeout( 2000 );
                        connection.connect();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inDither = true;
                        options.inPurgeable = true;
                        System.out.println( "*******************Trying to load from wireless: " + strUrl );
                        
                        InputStream streamIn = connection.getInputStream();
                        imageBitmap = BitmapFactory.decodeStream( streamIn, null, options );

                        if (imageBitmap != null)
                        {
                            break;
                        }
                        else
                        {
                            //AltEgo.LogWarning( "downloadImage", strUrl, " retry: ", Integer.toString( intAttempt ) );
                            //System.err.println("downloadImage: " + strUrl + "\nretry: " + Integer.toString( intAttempt ) );
                            try
                            {
                                Thread.sleep( longDelayMS );
                            }
                            catch (InterruptedException ex)
                            {
                                //AltEgo.LogError( "downloadImage", "Retry thread sleep interrupted: ", ex.getMessage() );
                                //System.out.println( "downloadImage -- Retry thread sleep interrupted:\n" + ex.getMessage() );
                            }
                        }
                    }
                    catch (SocketTimeoutException ex)
                    {
                        //AltEgo.LogError( "downloadImage", "SocketTimeoutException occurred: ", ex.getMessage() );
                        //System.out.println( "downloadImage -- SocketTimeoutException occurred:\n" + ex.getMessage() );
                    }
                    catch (IOException ex)
                    {
                        //AltEgo.LogError( "downloadImage", "Exception occurred: ", ex.getMessage() );
                        //System.out.println( "downloadImage -- Exception Occurred:\n" + ex.getMessage() );
                    }
                    catch (Throwable e)
                    {
                        //AltEgo.LogError( "downloadImage", "Exception occurred: ", ex.getMessage() );
                        //System.out.println( "downloadImage -- Error/Exception Occurred:\n" + e.getMessage() );
                        System.out.println( "*******************OutOfMemoryError: " + strUrl );
                        
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
                    //System.out.println( "downloadImage -- MalformedURLException occurred:\n" + ex.getMessage() );
                }
                catch (IOException ex)
                {
                    //System.out.println( "downloadImage -- IOException occurred:\n" + ex.getMessage() );
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
//                        System.out.println( "*******************Image Path Is File: " + fileLocalImage.isFile() );
                        
                        try { 
                            //System.out.println( "MKDIR Image Path: " + fileLocalImage.mkdir() ); 
                        }
                        catch (SecurityException e) { 
                            //System.out.println("Exception: " + e.toString() + "\n" + e.getMessage()); 
                        }
                        
                        streamFileOutput = new FileOutputStream( fileLocalImage );
                        if (imageBitmap.compress( Bitmap.CompressFormat.PNG, 100, streamFileOutput ))
                        {
                            //AltEgo.LogInfo( "downloadImage", "Stored downloaded bitmap as: " + fileLocalImage.getAbsolutePath() );
                            //System.out.println( "downloadImage -- Stored downloaded bitmap as: " + fileLocalImage.getAbsolutePath() );
                        }
                        else
                        {
                            //AltEgo.LogError( "downloadImage", "Failed to locally store bitmap: " + fileLocalImage.getAbsolutePath() );
                            //System.out.println( "downloadImage -- Failed to locally store bitmap: " + fileLocalImage.getAbsolutePath() );
                        }
                    }
                    catch (FileNotFoundException ex)
                    {
                        //System.out.println( "downloadImage -- Failed to open file: " + fileLocalImage.getAbsolutePath() + 
                        //        "\n" + ex.toString() + 
                        //        "\n" + ex.getMessage());
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
        System.out.println("*********************************callback null? " + (callback == null));

        /*//
        System.out.println("callback null? " + (callback == null));
        System.out.println("mPlatform null? " + (mPlatform == null));
        System.out.println("graphics() null? " + (mPlatform.graphics() == null));
        System.out.println("ctx() null? " + (mPlatform.graphics().ctx() == null));
        System.out.println("bitmap null? " + (bitmapDownloaded == null));
        //*/
        //System.out.println("*******************");
        //if (callback != null && bitmapDownloaded != null) 
        try {
            System.out.println("*******************Android Image Download Successful: bitmapDownloaded= " + (bitmapDownloaded == null) + " url: " + strUrl);
            if (callback != null) {
                callback.done(new AndroidImage(mPlatform.graphics().ctx(), bitmapDownloaded.getBitmap(), Scale.ONE));
            }
            return true;
        }
        catch (Exception ex) {
            PlayN.log().debug("Android Image Download Failed: " + strUrl, ex);
            callback.error(ex);
            return false;
        }
        catch (Error err) {
            PlayN.log().debug("Android Image Download Failed: " + strUrl, err);
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
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inDither = true;    
                    options.inPurgeable = true;
        System.out.println("*********************************trying to load " + fileLocalImage.getAbsolutePath());

                    Bitmap bitmap = BitmapFactory.decodeStream( input, null, options );
                    if (bitmap != null)
                    {
                        bitmapDownloaded = new DownloadedBitmap( fileLocalImage, bitmap ); 
                    }
                }
                catch(OutOfMemoryError E)
                {
                        System.out.println( "*******************OutOfMemoryError: " + fileLocalImage.getAbsolutePath() );
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
                //System.out.println( "loadImage -- Failed to load image from local storage:\n" + fileLocalImage.getAbsolutePath() );
            }
        }
        
        return bitmapDownloaded;
    }
    
    public void setCacheDirectory(File fileCacheDirectory)
    {
        try {
            if (fileCacheDirectory != null)
            {
                if (fileCacheDirectory.exists() || fileCacheDirectory.mkdirs())
                {
                    m_fileCacheDirectory = fileCacheDirectory;
                }
                else
                {
                    //AltEgo.LogWarning( "setCacheDirectory", "Failed to find accessible cache path: ", fileCacheDirectory.getAbsolutePath() );
                    //System.out.println( "setCacheDirectory -- Failed to find accessible cache path:\n" + fileCacheDirectory.getAbsolutePath() );
                }
            }
        } catch (ExceptionInInitializerError e) {
            PlayN.log().debug("Set Cache Directory: ", e);
        }
    }
     
    public File getCacheDirectory() {
        return m_fileCacheDirectory;
    }
    
    public void setInstagramMode() {
        m_instagram = true;
    }
}
