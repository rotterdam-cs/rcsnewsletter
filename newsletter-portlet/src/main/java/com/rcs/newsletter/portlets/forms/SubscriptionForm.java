
package com.rcs.newsletter.portlets.forms;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class SubscriptionForm {
    
    private long categoryId;    
    private String firstName;
    private String lastName;
    private String email;
    private String confirmationKey;
    private String cancellationKey;

    public String getCancellationKey() {
        return cancellationKey;
    }

    public void setCancellationKey(String cancellationKey) {
        this.cancellationKey = cancellationKey;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getConfirmationKey() {
        return confirmationKey;
    }

    public void setConfirmationKey(String confirmationKey) {
        this.confirmationKey = confirmationKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
