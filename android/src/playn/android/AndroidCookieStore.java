package playn.android;

import java.util.List;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;

public class AndroidCookieStore implements playn.core.CookieStore {

    final private CookieStore mCookieStore = new BasicCookieStore();
    final private HttpContext m_context = new BasicHttpContext();
    
    @Override
    public String get(String cookie) { 
        //return mCookieStore.get(cookie);
        List<Cookie> cookiesList = mCookieStore.getCookies();
        String value = null;
        
        for(int i = 0; i < cookiesList.size(); i++) {
            if(cookiesList.get(i).getName().equals(cookie)) {
                value = cookiesList.get(i).getValue();
            }
        }
        return value; 
    }

    @Override
    public void put(String cookie, String value) {
        mCookieStore.addCookie(new BasicClientCookie(cookie, value));
        m_context.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
        //return "unimplemented"; 
    }

    public CookieStore getCookies() {
        return mCookieStore;
    }

}