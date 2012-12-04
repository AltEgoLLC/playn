package playn.android.gcm;

import android.content.Context;
import android.content.Intent;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

    @Override
    protected void onMessage(Context cntxt, Intent intent) {
        // Called when your server sends a message to GCM, and GCM 
        // delivers it to the device. If the message has a payload, 
        // its contents are available as extras in the intent.
    }

    @Override
    protected void onError(Context cntxt, String string) {
        // Called when the device tries to register or unregister, but 
        // GCM returned an error. There is nothing to be done other than 
        // evaluating the error (returned by errorId) and trying to fix 
        // the problem.
    }

    @Override
    protected void onRegistered(Context cntxt, String regid) {
        // Called after a registration intent is received, passes the 
        // registration ID assigned by GCM to that device/application 
        // pair as parameter. You should send the regid to your server 
        // so it can use it to send messages to this device.
    }

    @Override
    protected void onUnregistered(Context cntxt, String string) {
        // Called after the device has been unregistered from GCM. You
        // should send the regid to the server so it unregisters the device.
    }
    
    //@Override
    //protected void onRecoverableError(Context context, String errorId) {
        // Called when the device tries to register or unregister, but the 
        // GCM servers are unavailable. The GCM library will retry the 
        // operation using exponential backup, unless this method is overridden 
        // and returns false. This method is optional and should be overridden 
        // only if you want to display the message to the user or cancel the 
        // retry attempts.
    //}
}
