package com.rcs.newsletter.core.dto;

import java.io.Serializable;

/**
 *
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
public class NewsletterCategoryDTO implements Serializable {

    private Long id;

    private String name;

    private String description;

    private String fromName;

    private String adminEmail;

    private String fromEmail;

    private String subscriptionEmail;

    private String unsubscriptionEmail;

    private String greetingEmail;

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getGreetingEmail() {
        return greetingEmail;
    }

    public void setGreetingEmail(String greetingEmail) {
        this.greetingEmail = greetingEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
