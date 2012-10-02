/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package playn.core.analytics;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private long timestamp;
    
    public Kontagent(String apiKeyInput, int userIDInput)
    {
        this.apiKey = apiKeyInput;
        this.userID = userIDInput;
    }
    
    @Override
    public void logEvent(Category category, String label) {
        
        //timestamp = System.currentTimeMillis()/1000L;
        timestamp = System.currentTimeMillis();
        //timestamp = new Timestamp(System.currentTimeMillis());
        String kontagentURLString = "http://api.geo.kontagent.net/api/v1/"+ this.apiKey + "/" + category.getCategory().toString() + "/?s=" + this.userID + "&ts=" + timestamp;
        kontagentURLString = kontagentURLString.replace(" ", "_");
        
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

    @Override
    public void logEvent(Category category, String action, String label, int value) {
//        long timestamp = System.currentTimeMillis()/1000L;
        timestamp = System.currentTimeMillis();
        String kontagentURLString = "http://api.geo.kontagent.net/api/v1/"+ this.apiKey + "/" + category.getCategory().toString() + "/?s=" + this.userID + "&n=" + label + "&ts=" + timestamp;
        kontagentURLString = kontagentURLString.replace(" ", "_");
        
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
    //For Kontagent custom events, here's parameter contrains
    // n < 32 chars
    // l = 0 to 255
    // st1, st2, st3 < 32 chars
    //Example1 : Ahri&st1=Pax&st2=Android&st3=Champion_Save
    //Example2 :    ViewerScreen_sess_Ahri&st1=PAX&st2=Android&v=46&l=10
    private static String trimParamters(String label)
    {
        StringBuilder sb = new StringBuilder();
        String[] params = label.split("&");
        String output = "";
        if (params[0].length() > 32)
        {
           sb.append(params[0]); 
           output += sb.substring(0,31);
           //clear StringBuilder
           sb.delete(0,sb.length()-1);
        }
        else
            output+=params[0];
        String[] values = params[1].split("=");
        if (values[0].equals("st1") && values[1].length() > 32)
        {
           sb.append(params[1]); 
           output += "&" + sb.substring(0,31);
           sb.delete(0,sb.length()-1);
        }
        else
            output+="&" + params[1];
        values = params[2].split("=");
        if (values[0].equals("st2") && values[1].length() > 32)
        {
           sb.append(params[2]); 
           output += "&" + sb.substring(0,31);
        }
        else
            output+= "&" + params[2];
//        for (String x : params)
//        {
//            output+=x + "\n";
//        }
        return output;
    }
    
public static void main(String[] args)
    {
//        System.out.println(Kontagent.trimParamters("Ahri&st1=Pax&st2=Android&st3=Champion_Save"));
        System.out.println(Kontagent.trimParamters("ViewerScreen_sess_Ahri_reallyLongStringTooLong&st1=PAX&st2=Android&v=46&l=10"));
//        ViewerScreen_sess_Ahri&st1=PAX&st2=Android&v=46&l=10
        
    }
    
    
}
