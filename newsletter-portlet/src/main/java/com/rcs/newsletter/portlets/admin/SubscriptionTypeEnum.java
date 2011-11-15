
package com.rcs.newsletter.portlets.admin;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public enum SubscriptionTypeEnum {
    SUBSCRIBE("newsletter.admin.category.subscribe")
    ,UNSUBSCRIBE("newsletter.admin.category.unsubscribe");
    
    private String key;

    private SubscriptionTypeEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
