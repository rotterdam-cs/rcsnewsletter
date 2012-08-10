package com.rcs.newsletter.core.model;

import javax.persistence.ManyToOne;
//import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author juan
 */
@Entity
@Table(name="newsletter_mailing")
public class NewsletterMailing extends NewsletterEntity {
    private static final long serialVersionUID = 1L;
    
    @NotBlank
    //mailing properties.
    private String name;
        
    @NotNull
    @ManyToOne
    private NewsletterCategory list;
    
    @NotNull
    @ManyToOne
    private NewsletterTemplate template;
    
    @OneToMany
    @JoinColumn(name = "mailing_id")
    @OrderColumn(name = "blockOrder")    
    private List<NewsletterTemplateBlock> blocks;

    public List<NewsletterTemplateBlock> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<NewsletterTemplateBlock> blocks) {
        this.blocks = blocks;
    }
    
    public NewsletterCategory getList() {
        return list;
    }

    public void setList(NewsletterCategory list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NewsletterTemplate getTemplate() {
        return template;
    }

    public void setTemplate(NewsletterTemplate template) {
        this.template = template;
    }
    
}
