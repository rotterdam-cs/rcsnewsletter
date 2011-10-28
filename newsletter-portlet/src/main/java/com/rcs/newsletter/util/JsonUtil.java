
package com.rcs.newsletter.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class JsonUtil {
    
    /**
     * Greate a Gson object with an exclusion strategy
     * @param exs
     * @return 
     */
    public static Gson createGsonFromBuilder(ExclusionStrategy exs) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.setExclusionStrategies(exs);
        
        return gsonbuilder.serializeNulls().create();
    }
    
}
