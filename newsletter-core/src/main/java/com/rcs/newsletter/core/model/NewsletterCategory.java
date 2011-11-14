package com.rcs.newsletter.core.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Entity
@Table(name = "newsletter_category")
public class NewsletterCategory extends NewsletterEntity {
    
    public static final String CATEGORY_KEY = "categoryKey";
    private static final long serialVersionUID = 1L;
    
    private boolean active;
    private String name;
    private String description;
    private String fromName;
    private String fromEmail;
    
    @OneToMany
    @JoinColumn(name = "category_id")
    @Cascade(CascadeType.DELETE)
    private Set<NewsletterSubscription> subscriptions;    
    
    @Column(columnDefinition="bigint default -1")
    private long subscriptionArticleId;    
    
    @Column(columnDefinition="bigint default -1")
    private long unsubscriptionArticleId;
    
    @OneToOne
    private NewsletterMailing mailing;
    
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

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
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

    public long getSubscriptionArticleId() {
        return subscriptionArticleId;
    }

    public void setSubscriptionArticleId(long subscriptionArticleId) {
        this.subscriptionArticleId = subscriptionArticleId;
    }

    public long getUnsubscriptionArticleId() {
        return unsubscriptionArticleId;
    }

    public void setUnsubscriptionArticleId(long unsubscriptionArticleId) {
        this.unsubscriptionArticleId = unsubscriptionArticleId;
    }
}