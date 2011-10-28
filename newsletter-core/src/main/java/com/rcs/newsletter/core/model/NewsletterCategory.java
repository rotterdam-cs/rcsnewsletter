package com.rcs.newsletter.core.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Entity
@Table(name = "newsletter_category")
public class NewsletterCategory implements Serializable, NewsletterEntity {

    public static final String ID = "id";
    public static final String CATEGORY_KEY = "categoryKey";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;    
    private boolean active;
    private String name;
    private String description;
    private long articleId;    
    @OneToMany
    @JoinColumn(name = "category_id")
    @Cascade(CascadeType.DELETE)
    private Set<NewsletterSubscription> subscriptions;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getArticleId() {
        return articleId;
    }

    public void setArticleId(long articleId) {
        this.articleId = articleId;
    }

    public Set<NewsletterSubscription> getSubscriptions() {
        if (subscriptions == null) {
            subscriptions = new HashSet<NewsletterSubscription>();
        }
        return subscriptions;
    }

    public void setSubscriptions(Set<NewsletterSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}