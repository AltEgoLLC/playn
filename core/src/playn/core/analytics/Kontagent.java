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
        String kontagentURLString;
        //timestamp = new Timestamp(System.currentTimeMillis());
        if (label == null)
        {
            kontagentURLString = "http://api.geo.kontagent.net/api/v1/"+ this.apiKey + "/" + category.getCategory().toString() + "/?s=" + this.userID + "&ts=" + timestamp;
        }
        else
            kontagentURLString = "http://api.geo.kontagent.net/api/v1/"+ this.apiKey + "/" + category.getCategory().toString() + "/?s=" + this.userID + label + "&ts=" + timestamp;
            
        kontagentURLString = kontagentURLString.replace(" ", "_");
        
        if (this.userID != 9999 && this.userID != 0 && kontagentURLString != null)
        {
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

    @Override
    public void logEvent(Category category, String action, String label, int value) {
//        long timestamp = System.currentTimeMillis()/1000L;
        timestamp = System.currentTimeMillis();
        String kontagentURLString = "http://api.geo.kontagent.net/api/v1/"+ this.apiKey + "/" + category.getCategory().toString() + "/?s=" + this.userID + "&n=" + label + "&ts=" + timestamp;
        kontagentURLString = kontagentURLString.replace(" ", "_");
        
        if (this.userID != 9999 && this.userID != 0)
        {
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
    
    //For Kontagent custom events, here's parameter contrains
    // n < 32 chars
    // l = 0 to 255
    // st1, st2, st3 < 32 chars
    //Example1 : Ahri&st1=Pax&st2=Android&st3=Champion_Save
    //Example2 :    ViewerScreen_sess_Ahri&st1=PAXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXWERWERSDFSDFWEDFGDSFGAERDSFGDHF&st2=Android&v=46&l=10
    private static String trimParamters(String label)
    {
        StringBuilder sb = new StringBuilder(label);
        String[] params = label.split("&");
        String output = "";
        String eventName = "";
        String st1 = "";
        String st2 = "";
        String st3 = "";
        String v = "";
        String l = "";
        String tmp;
        int index;
        
        index = sb.indexOf("&");
        
        if (index>30)
        {
            eventName = params[0];
            eventName = eventName.substring(0, 30);
            sb.delete(0,index);
            output = eventName+sb.toString();
        }
        else 
            output = label;
        
        //TODO - validate other optional parameters
        
        
//        eventName = params[0];
//        if (eventName.length() > 30)
//        {
//            eventName = eventName.substring(0, 30);
//        }  
        
        
//        index = label.lastIndexOf("&st1");
//        if (index != -1)
//        {
//            tmp = label.substring(index);
//            String[] tmpStrings = tmp.split("&");
//            st1 = tmpStrings[1];
//            tmpStrings = st1.split("=");
//            st1 = tmpStrings[1];
//        
//            if (st1.length() > 30)
//            {
//                st1 = st1.substring(0, 30);
//                st1 = "&st1=" + st1;
//            }  
//            else
//                st1 = "&st1=" + st1;
//        }
//        
//        index = label.lastIndexOf("&st2");
//        if (index != -1)
//        {
//            tmp = label.substring(index);
//            String[] tmpStrings = tmp.split("&");
//            st2 = tmpStrings[1];
//            tmpStrings = st2.split("=");
//            st2 = tmpStrings[1];
//        
//            if (st2.length() > 30)
//            {
//                st2 = st2.substring(0, 30);
//                st2 = "&st2=" + st2;
//            }  
//            else
//                st2 = "&st2=" + st2;
//        }
//        
//        index = label.lastIndexOf("&st3");
//        if (index != -1)
//        {
//            tmp = label.substring(index);
//            String[] tmpStrings = tmp.split("&");
//            st3 = tmpStrings[1];
//            tmpStrings = st3.split("=");
//            st3 = tmpStrings[1];
//        
//            if (st3.length() > 30)
//            {
//                st3 = st3.substring(0, 30);
//                st3 = "&st3=" + st3;
//            }  
//            else
//                st3 = "&st3=" + st3;
//        }
//        //not currently validated here.....v must be a signed int
//        index = label.lastIndexOf("&v");
//        if (index != -1)
//        {
//            tmp = label.substring(index);
//            String[] tmpStrings = tmp.split("&");
//            v = tmpStrings[1];
//            tmpStrings = v.split("=");
//            v = "&v=" + tmpStrings[1];
//        }
//        //not currently validated here....l must be 
//        index = label.lastIndexOf("&l");
//        if (index != -1)
//        {
//            tmp = label.substring(index);
//            String[] tmpStrings = tmp.split("&");
//            l = tmpStrings[1];
//            tmpStrings = l.split("=");
//            l = "&l=" + tmpStrings[1];
//        }
        
        
//        output = eventName+params[1];
        return output;
    }
    
public static void main(String[] args)
    {
        System.out.println(Kontagent.trimParamters("Ahri&st1=Pax&st2=Android&st3=Champion_Save&v=14&l=3"));
        System.out.println(Kontagent.trimParamters("PAXXXXXXXXXXXXXXWERWERWERWERWEXUSDFLKJSDF&st1=PAXXXXXXXXXXXXXXWERWERWERWERWERWERXXWERWERSDFSDFWEDFGDSFGAERDSFGDHF&st2=PAXXXXXXXXXXXXXXWERWERWERWERWERWERXXWERWERSDFSDFWEDFGDSFGAERDSFGDHF&st3=PAXXXXXXXXXXXXXXWERWERWERWERWERWERXXWERWERSDFSDFWEDFGDSFGAERDSFGDHF&v=46&l=10"));
        
    }
    
    
}
