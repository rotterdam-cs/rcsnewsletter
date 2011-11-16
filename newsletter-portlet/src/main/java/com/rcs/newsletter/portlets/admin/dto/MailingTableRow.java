package com.rcs.newsletter.portlets.admin.dto;

import com.rcs.newsletter.core.model.NewsletterMailing;
import java.io.Serializable;

/**
 *
 * @author juan
 */
public class MailingTableRow implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private NewsletterMailing mailing;
    private String articleTitle;

    public MailingTableRow() {
    }

    public MailingTableRow(NewsletterMailing mailing, String articleTitle) {
        this.mailing = mailing;
        this.articleTitle = articleTitle;
    }
    
    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public NewsletterMailing getMailing() {
        return mailing;
    }

    public void setMailing(NewsletterMailing mailing) {
        this.mailing = mailing;
    }
    
}
