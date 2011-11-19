package com.rcs.newsletter.core.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Settings for the registration portlet.
 * @author juan
 */
@Entity
@Table(name="newsletter_registration_portlet_config")
public class RegistrationConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;
    
    @NotNull
    private Long listId;
    
    private Long confirmationEmailArticleId;
    
    //@NotNull  TODO ARIEL
    private Long greetingEmailArticleId;
    
    
    private boolean disableName;
        
        
    public Long getConfirmationEmailArticleId() {
        return confirmationEmailArticleId;
    }

    public void setConfirmationEmailArticleId(Long confirmationEmailArticleId) {
        this.confirmationEmailArticleId = confirmationEmailArticleId;
    }

    public Long getGreetingEmailArticleId() {
        return greetingEmailArticleId;
    }

    public void setGreetingEmailArticleId(Long greetingEmailArticleId) {
        this.greetingEmailArticleId = greetingEmailArticleId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public boolean isDisableName() {
        return disableName;
    }

    public void setDisableName(boolean disableName) {
        this.disableName = disableName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RegistrationConfig other = (RegistrationConfig) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
}
