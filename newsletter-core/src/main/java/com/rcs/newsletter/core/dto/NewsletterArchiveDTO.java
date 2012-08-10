/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.core.dto;

import org.jdto.annotation.DTOTransient;

/**
 *
 * @author marcoslacoste
 */
public class NewsletterArchiveDTO  extends DataTransferObject {
    
    private Long id;
    private String name;
    private String template;
    @DTOTransient
    private int blocks;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * @return the blocks
     */
    public int getBlocks() {
        return blocks;
    }

    /**
     * @param blocks the blocks to set
     */
    public void setBlocks(int blocks) {
        this.blocks = blocks;
    }
    
    
    
}
