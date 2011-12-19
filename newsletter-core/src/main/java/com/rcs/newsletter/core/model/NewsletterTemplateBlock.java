package com.rcs.newsletter.core.model;

import javax.persistence.Entity;
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
    private static final long serialVersionUID = 1L;
    
    @NotNull
    @Min(1)
    private Long articleId;
    
    private int blockOrder;
    
    @NotNull
    @ManyToOne
    private NewsletterTemplate template;

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

    public NewsletterTemplate getTemplate() {
        return template;
    }

    public void setTemplate(NewsletterTemplate template) {
        this.template = template;
    }
    
    
    
}
