package com.rcs.newsletter.portlets.admin;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import org.primefaces.event.TabChangeEvent;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author juan
 */
@Named
@Scope("session")
public class UserUiStateManagedBean {
    
    private int adminActiveTabIndex;
    
    @PostConstruct
    public void init() {
        adminActiveTabIndex = 0;
    }

    public int getAdminActiveTabIndex() {
        return adminActiveTabIndex;
    }

    public void setAdminActiveTabIndex(int adminActiveTabIndex) {
        this.adminActiveTabIndex = adminActiveTabIndex;
    }
    
    public void onTabsUpdated(TabChangeEvent event) {
        //DUMMY METHOD TO MAKE THE MAGIC HAPPEN
        System.out.println(adminActiveTabIndex);
    }
    
}
