/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package playn.core;

/**
 * Interface for platform specific Cookie Store
 * @author scs
 */
public interface CookieStore {
    public String get(String cookie);
    public void put(String cookie, String value);
}
