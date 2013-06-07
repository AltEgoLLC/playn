/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package playn.android;

import java.net.URLEncoder;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.provider.Settings;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 *
 * @author joey
 */
public class MdotmReceiver extends BroadcastReceiver {
    


    public String postBackUrl = "";
    public String deviceId = "0";
    public String androidId = "0";
    @Override
    public void onReceive( Context context, Intent intent ) {
      String referrer = "";
      
      try {
        referrer = intent.getStringExtra( "referrer" );
        if ( referrer == null ) {
	  referrer = "null_referrer_found";
        }
      } catch(Exception e) {
        referrer = "exception_found_retrieving_referrer";
      }

      try {
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
        if( deviceId == null ) deviceId = "0";
      } catch(Exception e) {
        deviceId = "0";
      }
        try
        {
            MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
            byte[] bytes = deviceId.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for( byte b : bytes )
            {
                sb.append( String.format("%02X", b) );
            }
            deviceId = sb.toString();
        }
        catch( NoSuchAlgorithmException e )
        {
//            e.printStackTrace();
        }
        catch( UnsupportedEncodingException e )
        {
//            e.printStackTrace();
        }
      try {
        androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if( androidId == null ) androidId = "0";
      } catch(Exception e) {
        androidId = "0";
      }

      String packageName = "";
      Context applicationContext = context.getApplicationContext();
      if( applicationContext == null ) {
        packageName = "null_package";
      } else {
        packageName = applicationContext.getPackageName();
      }

      postBackUrl = "http://ads.mdotm.com/ads/receiver.php?referrer=" + URLEncoder.encode( referrer ) + "&package=" + URLEncoder.encode( packageName ) + "&sha1deviceid=" + URLEncoder.encode(deviceId) + "&androidid=" + URLEncoder.encode(androidId);

      makePostBack.start();
    }

    private Thread makePostBack = new Thread() {
      public void run() {
        try{
          HttpClient httpClient = new DefaultHttpClient();
          HttpGet httpGet = new HttpGet( postBackUrl ); 
          httpClient.execute(httpGet);
        } catch(Exception e) {
          return;
        }
      }
   };
}
    