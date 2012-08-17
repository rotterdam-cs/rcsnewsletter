package com.rcs.newsletter.core.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author pablo
 */
@Entity
@Table(name="newsletter_template")
public class NewsletterTemplate extends NewsletterEntity {    
    private static final long serialVersionUID = 1L;
    
    @NotBlank
    //mailing properties.
    private String name;
    
    @Type(type="text")
    private String template;    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
    

}