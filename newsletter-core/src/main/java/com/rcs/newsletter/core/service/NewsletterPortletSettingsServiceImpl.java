package com.rcs.newsletter.core.service;


import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.theme.ThemeDisplay;
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
    private static Log log = LogFactoryUtil.getLog(NewsletterPortletSettingsServiceImpl.class);
    
    @Autowired
    private HibernateTemplate template;
    
    @Autowired
    private Validator validator;
    
    @Override
    public RegistrationConfig findConfig(ThemeDisplay themeDisplay, String portletId) {        
        RegistrationConfig config = null;
        
        config = template.get(RegistrationConfig.class, portletId);
        
        if (config == null) {
            //log.error("Configuration not found "); TODO ARIEL
            config = new RegistrationConfig();       
            config.setGroupid(themeDisplay.getScopeGroupId());
            config.setCompanyid(themeDisplay.getCompanyId());
            config.setId(portletId);
        }
        
        return config;
    }

    @Override
    public ServiceActionResult updateConfig(ThemeDisplay themeDisplay, String portletId, RegistrationConfig data) {  
        
        Set violations = validator.validate(data);
        
        if (!violations.isEmpty()) {
            log.error("violation " + violations.toString());
            return ServiceActionResult.buildFailure(null);
        }
        
        if (StringUtils.isEmpty(portletId)) {
            log.error("Portlet id = null ");
            throw new IllegalArgumentException("Portlet id = null");
        }
        
        RegistrationConfig config = template.get(RegistrationConfig.class, portletId);
                
        boolean isNew = false;
        if (config == null) {
            config = new RegistrationConfig();         
            config.setGroupid(themeDisplay.getScopeGroupId());
            config.setCompanyid(themeDisplay.getCompanyId());
            config.setId(portletId);
            isNew = true;
        }        
        
        config.setConfirmationEmailArticleId(data.getConfirmationEmailArticleId());
        config.setListId(data.getListId());
        config.setDisableName(data.isDisableName());
        
        if (isNew) {
            Object a = template.save(config);
        }            
        return ServiceActionResult.buildSuccess(null);
    }
    
}
