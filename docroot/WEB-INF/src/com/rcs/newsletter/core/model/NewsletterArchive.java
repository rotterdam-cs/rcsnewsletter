package com.rcs.newsletter.core.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.annotations.Type;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Entity
@Table(name = "newsletter_archive")
public class NewsletterArchive extends NewsletterEntity {

    private static final long serialVersionUID = 1L;    
        
    private String name;
    private String categoryName;
    private String articleTitle;
    @Type(type="text")
    private String emailBody;    
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="archive_date")
    private Date date;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
