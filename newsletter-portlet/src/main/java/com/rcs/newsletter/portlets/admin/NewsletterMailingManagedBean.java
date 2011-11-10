package com.rcs.newsletter.portlets.admin;

import edu.emory.mathcs.backport.java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author juan
 */
@Named
@Scope("session")
public class NewsletterMailingManagedBean {
    
    @Inject
    private UserUiStateManagedBean uiState;
    
    
    private List mailingList;
    
    @PostConstruct
    public void init() {
        mailingList = Collections.emptyList();
    }
    
    //the mailing list.
    public List getMailingList() {
        return mailingList;
    }
    
    
    public String addMailing() {
        uiState.setAdminActiveTabIndex(2);
        return "addmailing";
    }
    
}
