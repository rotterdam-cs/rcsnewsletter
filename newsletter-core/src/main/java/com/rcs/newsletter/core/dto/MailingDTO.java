/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.core.dto;

import java.util.List;
import org.jdto.annotation.DTOTransient;
import org.jdto.annotation.Source;

/**
 *
 * @author marcoslacoste
 */
public class MailingDTO  extends DataTransferObject {
    
    private Long id;
    private String name;
    
    @Source("list.id")
    private Long listId;
    
    @Source("list.name")
    private String listName;
    
    @Source("template.id")
    private Long templateId;
    
    @Source("template.name")
    private String templateName;

    @DTOTransient
    private List<ArticleDTO> articles;
    
    @DTOTransient
    private Long[] articleIds;
    
    
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
     * @return the listId
     */
    public Long getListId() {
        return listId;
    }

    /**
     * @param listId the listId to set
     */
    public void setListId(Long listId) {
        this.listId = listId;
    }

    /**
     * @return the listName
     */
    public String getListName() {
        return listName;
    }

    /**
     * @param listName the listName to set
     */
    public void setListName(String listName) {
        this.listName = listName;
    }

    /**
     * @return the templateId
     */
    public Long getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId the templateId to set
     */
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    /**
     * @return the templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * @param templateName the templateName to set
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * @return the articles
     */
    public List<ArticleDTO> getArticles() {
        return articles;
    }

    /**
     * @param articles the articles to set
     */
    public void setArticles(List<ArticleDTO> articles) {
        this.articles = articles;
    }

    /**
     * @return the articleIds
     */
    public Long[] getArticleIds() {
        return articleIds;
    }

    /**
     * @param articleIds the articleIds to set
     */
    public void setArticleIds(Long[] articleIds) {
        this.articleIds = articleIds;
    }

    
    
    
}
