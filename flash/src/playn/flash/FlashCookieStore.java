/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package playn.flash;

import java.util.Set;
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
    
    @Override
    public void clear() {
        /*//
        Set<String> keyset = mCookies.keySet();
        for (String key : keyset) {
            mCookies.remove(key);
            mStorage.removeItem(key);
        }
        //*/
    }
    
}
