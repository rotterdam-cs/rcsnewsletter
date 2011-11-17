package com.rcs.newsletter.core.service;


import com.rcs.newsletter.core.model.RegistrationConfig;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.Set;
import javax.validation.Validator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Override
    public RegistrationConfig findConfig(String portletId) {
        
        
        RegistrationConfig config = null;
        
        config = template.get(RegistrationConfig.class, portletId);
        
        if (config == null) {
            config = new RegistrationConfig();
            config.setId(portletId);
        }
        
        return config;
    }

    @Override
    public ServiceActionResult updateConfig(String portletId, RegistrationConfig data) {
        
        Set violations = validator.validate(data);
        
        if (violations.isEmpty()) {
            return ServiceActionResult.buildFailure(null);
        }
        
        if (StringUtils.isEmpty(portletId)) {
            throw new IllegalArgumentException("Portlet id = null");
        }
        
        RegistrationConfig config = template.get(RegistrationConfig.class, portletId);
        
        
        boolean isNew = false;
        if (config == null) {
            config = new RegistrationConfig();
            config.setId(portletId);
            isNew = true;
        }
        
        
        config.setConfirmationEmailArticleId(data.getConfirmationEmailArticleId());
        config.setGreetingEmailArticleId(data.getGreetingEmailArticleId());
        config.setListId(data.getListId());
        
        if (isNew) {
            template.save(config);
        }
        
        return ServiceActionResult.buildSuccess(null);
    }
    
}
