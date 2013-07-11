package com.rcs.newsletter.core.service;


import java.util.ResourceBundle;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.NewsletterConstants;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.service.common.ServiceActionResult;

/**
 *
 * @author juan
 */
@Repository
@Transactional
public class NewsletterPortletSettingsServiceImpl implements NewsletterPortletSettingsService {    
    
    @Autowired
    private HibernateTemplate template;
    
    @Autowired
    private Validator validator;
    
   
    @Autowired
    private NewsletterCategoryService categoryService;
    
    
    public ServiceActionResult<Void> validateSettings(Long categoryId, ThemeDisplay themeDisplay){
        ResourceBundle bundle = ResourceBundle.getBundle("Language", themeDisplay.getLocale());
    	
    	ServiceActionResult<NewsletterCategory> findCategoryResult = categoryService.findById(categoryId);
        NewsletterCategory category = findCategoryResult.getPayload();
        
        // validate the link token has been set for Subscription Email
        if (category.getSubscriptionEmail() == null || !category.getSubscriptionEmail().contains(NewsletterConstants.CONFIRMATION_LINK_TOKEN)){
        	return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.registration.register.error.linktokenmissing"));
        }
        
        return ServiceActionResult.buildSuccess(null);
        

    }
    
}
