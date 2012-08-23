/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package playn.flash;

import playn.core.CookieStore;

/**
 *
 * @author scs
 */
public class FlashCookieStore implements CookieStore {

    FlashStorage mStorage;
    
    public FlashCookieStore(FlashStorage storage) {
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
