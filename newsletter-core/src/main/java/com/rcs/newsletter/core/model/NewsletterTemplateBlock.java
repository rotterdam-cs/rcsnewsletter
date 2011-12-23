package com.rcs.newsletter.core.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author pablo
 */
@Entity
@Table(name="newsletter_template_block")
public class NewsletterTemplateBlock extends NewsletterEntity {     
    public static final String MAILING = "mailing";
    public static final String BLOCK_ORDER_COLUMN = "blockorder";    
    private static final long serialVersionUID = 1L;        
    

    private Long articleId;
    
    private int blockOrder;
    
    @ManyToOne
    @JoinColumn(name="mailing_id")
    private NewsletterMailing mailing;

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public int getBlockOrder() {
        return blockOrder;
    }

    public void setBlockOrder(int blockOrder) {
        this.blockOrder = blockOrder;
    }

    public NewsletterMailing getMailing() {
        return mailing;
    }

    public void setMailing(NewsletterMailing mailing) {
        this.mailing = mailing;
    }
    
}