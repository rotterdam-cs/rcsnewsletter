package com.rcs.newsletter.core.model;

import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Entity
@Table(name = "newsletter_subscription")
public class NewsletterSubscription extends NewsletterEntity {
    
    public static final String SUBSCRIPTOR = "subscriptor";
    public static final String CATEGORY = "category";

    private static final long serialVersionUID = 1L;
        
    @NotNull
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
    
    @NotNull
    @ManyToOne(cascade= CascadeType.PERSIST)
    @JoinColumn(name="subscriptor_id")
    private NewsletterSubscriptor subscriptor;
    
    @ManyToOne
    @JoinColumn(name="category_id")
    private NewsletterCategory category;

    private String activationKey;
    private String deactivationKey;

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getDeactivationKey() {
        return deactivationKey;
    }

    public void setDeactivationKey(String deactivationKey) {
        this.deactivationKey = deactivationKey;
    }
    
    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public NewsletterCategory getCategory() {
        return category;
    }

    public void setCategory(NewsletterCategory category) {
        this.category = category;
    }

    public NewsletterSubscriptor getSubscriptor() {
        return subscriptor;
    }

    public void setSubscriptor(NewsletterSubscriptor subscriptor) {
        this.subscriptor = subscriptor;
    }
}
