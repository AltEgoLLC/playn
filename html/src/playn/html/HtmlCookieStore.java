/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package playn.html;

import playn.core.CookieStore;
import java.util.TreeMap;

public class HtmlCookieStore implements CookieStore {

    private TreeMap<String,String> mCookies = new TreeMap<String,String>();
    private HtmlStorage mStorage;
    
    public HtmlCookieStore(HtmlStorage storage) {
        mStorage = storage;
    }
    
    @Override
    public String get(String cookie) 
    {
        //returns cookie or null
        String result = mCookies.get(cookie);
        if (result == null) {
            mStorage.getItem(cookie);
        }
        return result;
    }

    @Override
    public void put(String cookie, String value) { 
        mCookies.put( cookie, value);
        mStorage.setItem(cookie, value);
    }

}