/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playn.android.altego;

import android.graphics.Bitmap;
import java.io.File;

/**
 *
 * @author dmccartney
 */
public class DownloadedBitmap
{
    final private File m_fileBitmap;
    final private Bitmap m_bitmap;
    
    public DownloadedBitmap(final File fileBitmap, final Bitmap bitmap)
    {
        m_fileBitmap = fileBitmap;
        m_bitmap = bitmap;
    }
    
    public File getLocalFile()
    {
        return m_fileBitmap;
    }
    
    public Bitmap getBitmap()
    {
        return m_bitmap;
    }

    @Override
    public String toString()
    {
        return new StringBuilder( m_fileBitmap.getAbsolutePath() ).append( " [width: ").append( m_bitmap.getWidth() ).append( ", height: ").append( m_bitmap.getHeight() ).append( "]").toString();
    }
}