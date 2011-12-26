package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.rcs.newsletter.NewsletterConstants;
import com.rcs.newsletter.commons.ResourceTypeEnum;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import javax.annotation.PostConstruct;
import javax.faces.event.AjaxBehaviorEvent;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("session")
public class SubscriptorExportManagedBean {

    private static Log log = LogFactoryUtil.getLog(SubscriptorExportManagedBean.class);
    private NewsletterCategory filterCategory;
    private int categoryId = NewsletterConstants.UNDEFINED;
    @Inject
    NewsletterSubscriptorService subscriptorService;
    @Inject
    NewsletterCategoryService categoryService;
    @Inject
    NewsletterSubscriptionService subscriptionService;

    @PostConstruct
    public void init() {
    }

    public int getCategoryId() {
        return categoryId;
    }

    public NewsletterSubscriptorService getSubscriptorService() {
        return subscriptorService;
    }

    public NewsletterCategoryService getCategoryService() {
        return categoryService;
    }

    public NewsletterSubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public NewsletterCategory getFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(NewsletterCategory filterCategory) {
        this.filterCategory = filterCategory;
    }

    public String getExportResourceType() {
        return ResourceTypeEnum.SUBSCRIPTOR_TO_EXCEL.toString();
    }

    public String getImportResourceType() {
        return ResourceTypeEnum.SUBSCRIPTOR_FROM_EXCEL.toString();
    }

    public void changeCategory(AjaxBehaviorEvent event) {
        if (getCategoryId() != 0) {
            filterCategory = categoryService.findById(categoryId).getPayload();
        }
    }

    public String redirectImportSubscriptors() {
        return "importSubscriptors";
    }

    public void submitApply() {
    }
}
