/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package playn.core.analytics;

import playn.core.Analytics;
import playn.core.PlayN;
import playn.core.util.Callback;

/**
 *
 * @author Kevin
 */
public class Kontagent implements Analytics {
    private final String apiKey;
    private int userID;
    
    public Kontagent(String apiKeyInput, int userIDInput)
    {
        this.apiKey = apiKeyInput;
        this.userID = userIDInput;
    }

    @Override
    public void logEvent(Category category, String action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void logEvent(Category category, String action, String label, int value) {
        long timestamp = System.currentTimeMillis()/1000L;
        String kontagentURLString = "http://api.geo.kontagent.net/api/v1/"+ this.apiKey + "/" + category.getCategory().toString() + "/?s=" + this.userID + "&n=" + label;
        
        PlayN.net().get(kontagentURLString, null, 
            new Callback<String>(){

            @Override
            public void onSuccess(String result) {
                PlayN.log().debug("analytics success! :" + result);
            }

            @Override
            public void onFailure(Throwable cause) {
                PlayN.log().debug("analytics failed:" + cause);
            }



            }
        );
    }
                
                
        
}
