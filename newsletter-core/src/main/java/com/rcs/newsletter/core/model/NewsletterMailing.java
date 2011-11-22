package com.rcs.newsletter.core.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author juan
 */
@Entity
@Table(name="newsletter_mailing")
public class NewsletterMailing extends NewsletterEntity{
    private static final long serialVersionUID = 1L;
    
    @NotBlank
    //mailing properties.
    private String name;
        
    @NotNull
    @ManyToOne
    private NewsletterCategory list;
    
    @NotNull
    @Min(1)
    private Long articleId;

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public NewsletterCategory getList() {
        return list;
    }

    public void setList(NewsletterCategory list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
