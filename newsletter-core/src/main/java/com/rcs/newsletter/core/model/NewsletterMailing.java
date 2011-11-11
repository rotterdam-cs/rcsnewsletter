package com.rcs.newsletter.core.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 *
 * @author juan
 */
@Entity
public class NewsletterMailing extends NewsletterEntity{
    private static final long serialVersionUID = 1L;
    
    //mailing properties.
    private String nombre;
    
    @ManyToOne
    private NewsletterCategory list;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
}
