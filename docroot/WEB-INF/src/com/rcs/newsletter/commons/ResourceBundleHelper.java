package com.rcs.newsletter.commons;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 * @author chuqui
 */
public class ResourceBundleHelper {
    public static String getKeyLocalizedValue(String key, Locale locale) {
	ResourceBundle res = ResourceBundle.getBundle("Language", locale);
	if (res.containsKey(key)) {
	    return res.getString(key);
	} else {
	    return null;
	}
    }
}