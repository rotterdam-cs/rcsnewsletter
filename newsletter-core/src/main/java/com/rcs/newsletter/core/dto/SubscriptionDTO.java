package com.rcs.newsletter.core.dto;

import org.hibernate.validator.constraints.Email;

/**
 * Data transfer object using on subscriptions
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class SubscriptionDTO extends DataTransferObject {

    private long categoryId;
    private String categoryName;
    private String firstName;
    private String lastName;
    @Email
    private String email;
    private String confirmationKey;
    private String cancellationKey;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }    

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmationKey() {
        return confirmationKey;
    }

    public void setConfirmationKey(String confirmationKey) {
        this.confirmationKey = confirmationKey;
    }

    public String getCancellationKey() {
        return cancellationKey;
    }

    public void setCancellationKey(String cancellationKey) {
        this.cancellationKey = cancellationKey;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
