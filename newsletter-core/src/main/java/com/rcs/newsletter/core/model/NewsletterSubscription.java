package com.rcs.newsletter.core.model;

import com.rcs.newsletter.core.json.GsonExclude;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cascade;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Entity
@Table(name = "newsletter_subscription")
public class NewsletterSubscription implements Serializable, NewsletterEntity {
    
    public static final String SUBSCRIPTOR = "subscriptor";
    public static final String CATEGORY = "category";
    public static final String CONFIRMATION_KEY = "confirmationKey";
    public static final String CANCELLATION_KEY = "cancellationKey";

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
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
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="category_id")
    private NewsletterCategory category;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

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
