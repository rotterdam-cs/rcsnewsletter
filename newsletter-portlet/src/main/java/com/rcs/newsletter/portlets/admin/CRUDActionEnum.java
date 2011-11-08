
package com.rcs.newsletter.portlets.admin;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public enum CRUDActionEnum {
    
    CREATE("Save")
    ,UPDATE("Update")
    ,DELETE("Delete");
    
    private String key;

    private CRUDActionEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
    
}
