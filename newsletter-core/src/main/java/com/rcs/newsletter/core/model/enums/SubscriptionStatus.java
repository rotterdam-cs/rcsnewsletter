package com.rcs.newsletter.core.model.enums;

/**
 * Status of the subscription
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public enum SubscriptionStatus {

    ACTIVE("subscriptionstatus.activekey"),
    INACTIVE("subscriptionstatus.inactivekey");
    
    private String key;

    private SubscriptionStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
