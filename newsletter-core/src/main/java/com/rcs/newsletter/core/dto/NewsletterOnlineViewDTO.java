/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.core.dto;

/**
 *
 * @author marcoslacoste
 */
public class NewsletterOnlineViewDTO {
    
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
