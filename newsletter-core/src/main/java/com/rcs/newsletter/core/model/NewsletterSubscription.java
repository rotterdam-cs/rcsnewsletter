package com.rcs.newsletter.core.model;

import com.rcs.newsletter.core.json.GsonExclude;
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
    public static final String CONFIRMATION_KEY = "confirmationKey";
    public static final String CANCELLATION_KEY = "cancellationKey";

    private static final long serialVersionUID = 1L;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
    private String confirmationKey;
    private String cancellationKey;
    
    @NotNull
    @ManyToOne(cascade= CascadeType.PERSIST)
    @JoinColumn(name="subscriptor_id")
    private NewsletterSubscriptor subscriptor;
    
    @GsonExclude
    @ManyToOne
    @JoinColumn(name="category_id")
    private NewsletterCategory category;

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public String getCancellationKey() {
        return cancellationKey;
    }

    public void setCancellationKey(String cancellationKey) {
        this.cancellationKey = cancellationKey;
    }

    public NewsletterCategory getCategory() {
        return category;
    }

    public void setCategory(NewsletterCategory category) {
        this.category = category;
    }

    public String getConfirmationKey() {
        return confirmationKey;
    }

    public void setConfirmationKey(String confirmationKey) {
        this.confirmationKey = confirmationKey;
    }

    public NewsletterSubscriptor getSubscriptor() {
        return subscriptor;
    }

    public void setSubscriptor(NewsletterSubscriptor subscriptor) {
        this.subscriptor = subscriptor;
    }
}
