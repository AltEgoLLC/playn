/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package playn.ios;

import playn.core.CookieStore;

/**
 *
 * @author scs
 */
public class IOSCookieStore implements CookieStore {

    IOSStorage mStorage;
    
    public IOSCookieStore(IOSStorage storage) {
        mStorage = storage;
    }
    
    @Override
    public String get(String cookie) {
        String result = null;
        // Unimplemented
        if (result == null) {
            result = mStorage.getItem(cookie);
        }
        return result;
    }

    @Override
    public void put(String cookie, String value) {
        // Unimplemented
        mStorage.setItem(cookie, value);
    }
    
}
