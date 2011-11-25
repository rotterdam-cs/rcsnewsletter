package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.rcs.newsletter.commons.ResourceTypeEnum;
import javax.annotation.PostConstruct;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("request")
public class SubscriptorExportManagedBean {

    private static Log log = LogFactoryUtil.getLog(SubscriptorExportManagedBean.class);
    @Inject
    NewsletterSubscriptorService subscriptorService;
    @Inject
    NewsletterCategoryService categoryService;    

    private ResourceTypeEnum resourceTypeEnum;
    
    @PostConstruct
    public void init() {
        resourceTypeEnum = ResourceTypeEnum.SUBSCRIPTOR_TO_EXCEL;
    }

    public String getResourceType() {
        return resourceTypeEnum.toString();
    }
}
