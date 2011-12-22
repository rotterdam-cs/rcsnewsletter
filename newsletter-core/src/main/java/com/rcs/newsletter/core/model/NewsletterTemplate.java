package com.rcs.newsletter.core.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
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
    
    
    @OneToMany
    @JoinColumn(name = "template_id")
    @OrderColumn(name = "blockOrder")
    @Cascade(CascadeType.DELETE)    
    private List<NewsletterTemplateBlock> blocks;   
    

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

    public List<NewsletterTemplateBlock> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<NewsletterTemplateBlock> blocks) {
        this.blocks = blocks;
    }

}