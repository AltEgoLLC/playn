/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playn.html;

import playn.core.CookieStore;
import java.util.TreeMap;

public class HtmlCookieStore implements CookieStore {

    private TreeMap<String,String> cookies = new TreeMap<String,String>();
    
    @Override
    public String get(String cookie) 
    {
        //returns cookie or null
        return cookies.get(cookie);
    }

    @Override
    public void put(String cookie, String value) { cookies.put( cookie, value);}

}