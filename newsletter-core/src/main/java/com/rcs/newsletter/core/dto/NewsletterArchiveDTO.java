/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.core.dto;

import java.util.Date;

/**
 *
 * @author marcoslacoste
 */
public class NewsletterArchiveDTO  extends DataTransferObject {
    
    private Long id;
    private String name;
    private String categoryName;
    private String articleTitle;
    private String emailBody;    
    private Date date;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @return the articleTitle
     */
    public String getArticleTitle() {
        return articleTitle;
    }

    /**
     * @param articleTitle the articleTitle to set
     */
    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    /**
     * @return the emailBody
     */
    public String getEmailBody() {
        return emailBody;
    }

    /**
     * @param emailBody the emailBody to set
     */
    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    
    
    
    
}
