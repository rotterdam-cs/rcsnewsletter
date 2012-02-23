package com.rcs.newsletter.util;

import java.util.UUID;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class SubscriptionUtil {

    /**
     * Generate a unique key
     * @param email
     * @return 
     */
    public static String getUniqueKey(String email) {
        String result;
        UUID uuid = UUID.fromString(email);
        
        result = uuid.toString();
        
        return result;
    }
    
    /**
     * Obtain a unique key
     * @return 
     */
    public static String getUniqueKey() {
        String result = "";
        UUID uuid = UUID.randomUUID();
        
        result = uuid.toString();
        
        return result;
    }
}
