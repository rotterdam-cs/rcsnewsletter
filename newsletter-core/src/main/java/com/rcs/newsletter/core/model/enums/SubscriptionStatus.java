package com.rcs.newsletter.core.model.enums;

/**
 * Flow of this Status:
 * INVITED -> ACTIVE
 * ACTIVE -> INACTIVE
 * INACTIVE -> INVITED
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public enum SubscriptionStatus {

    ACTIVE("subscriptionstatus.activekey"),
    INACTIVE("subscriptionstatus.inactivekey"),
    INVITED("subscriptionstatus.invitedkey");
    
    private String key;

    private SubscriptionStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
