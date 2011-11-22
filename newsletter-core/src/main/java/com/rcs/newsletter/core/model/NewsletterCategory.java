package com.rcs.newsletter.core.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Entity
@Table(name = "newsletter_category")
public class NewsletterCategory extends NewsletterEntity {
    
    public static final String CATEGORY_KEY = "categoryKey";
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private String fromName;
    
    @NotNull
    @NotEmpty
    @Email    
    private String fromEmail;
    
    @OneToMany
    @JoinColumn(name = "category_id")
    @Cascade(CascadeType.DELETE)
    private List<NewsletterSubscription> subscriptions;    
    
    private String subscriptionEmail;
    
    private String unsubscriptionEmail;
    
    private String greetingEmail;
        
    @OneToMany(mappedBy = "list", orphanRemoval=true)    
    private List<NewsletterMailing> mailings;

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

    public List<NewsletterSubscription> getSubscriptions() {
        if (subscriptions == null) {
            subscriptions = new ArrayList<NewsletterSubscription>();
        }
        return subscriptions;
    }

    public void setSubscriptions(List<NewsletterSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<NewsletterMailing> getMailings() {
        return mailings;
    }

    public void setMailings(List<NewsletterMailing> mailings) {
        this.mailings = mailings;
    }

    public String getGreetingEmail() {
        return greetingEmail;
    }

    public void setGreetingEmail(String greetingEmail) {
        this.greetingEmail = greetingEmail;
    }

    public String getSubscriptionEmail() {
        return subscriptionEmail;
    }

    public void setSubscriptionEmail(String subscriptionEmail) {
        this.subscriptionEmail = subscriptionEmail;
    }

    public String getUnsubscriptionEmail() {
        return unsubscriptionEmail;
    }

    public void setUnsubscriptionEmail(String unsubscriptionEmail) {
        this.unsubscriptionEmail = unsubscriptionEmail;
    }
}