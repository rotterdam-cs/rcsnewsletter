package com.rcs.newsletter.core.dto;

import java.io.Serializable;

/**
 *
 * @author marcoslacoste
 */
public class NewsletterOnlineViewDTO implements Serializable{
    
	private static final long serialVersionUID = 1L;
	
    private String listName;
    private String newsletterBody;

    /**
     * @return the newsletterBody
     */
    public String getNewsletterBody() {
        return newsletterBody;
    }

    /**
     * @param newsletterBody the newsletterBody to set
     */
    public void setNewsletterBody(String newsletterBody) {
        this.newsletterBody = newsletterBody;
    }

    /**
     * @return the listName
     */
    public String getListName() {
        return listName;
    }

    /**
     * @param listName the listName to set
     */
    public void setListName(String listName) {
        this.listName = listName;
    }
    
}
