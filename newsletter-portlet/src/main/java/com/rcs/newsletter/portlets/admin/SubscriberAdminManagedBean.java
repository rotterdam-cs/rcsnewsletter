package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.theme.ThemeDisplay;
import java.io.IOException;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.PortletPreferences;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.NewsletterConstants;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.portlet.ValidatorException;
import org.springframework.context.annotation.Scope;

import static com.rcs.newsletter.NewsletterConstants.*;

/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
@Named
@Scope("session")
public class SubscriberAdminManagedBean extends PaginationManagedBean {

    private static Log log = LogFactoryUtil.getLog(SubscriberAdminManagedBean.class);
    private NewsletterCategory filterCategory;
    private int categoryId = 0;
    private Boolean onlyInactive = false;
    private SubscriptionStatus status = null;
    private ResourceBundle messageBundle;
    private String importResult = "";
    @Inject
    NewsletterSubscriptorService subscriptorService;
    @Inject
    NewsletterCategoryService categoryService;
    @Inject
    private UserUiStateManagedBean uiState;
    @Inject
    NewsletterSubscriptionService subscriptionService;
    List<NewsletterSubscriptor> subscribers;

    @PostConstruct
    public void init() throws IOException, ValidatorException {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        try {
            Object a = facesContext.getExternalContext().getRequest();
            if (a instanceof RenderRequest) {
                RenderRequest renderRequest = (RenderRequest) a;
                PortletPreferences prefs = renderRequest.getPreferences();
                prefs.setValue("importresult", "");                
                prefs.setValue("importresultDetails", "");
                prefs.store();
            }
        } catch (ReadOnlyException ex) {
            log.error(ex);
        }

        setPaginationStart(NewsletterConstants.PAGINATION_DEFAULT_START);
        setPaginationLimit(NewsletterConstants.PAGINATION_DEFAULT_LIMIT);
        updateSubscriptors();

        messageBundle = ResourceBundle.getBundle(LANGUAGE_BUNDLE, facesContext.getViewRoot().getLocale());
    }

    public ResourceBundle getMessageBundle() {
        return messageBundle;
    }

    public void setMessageBundle(ResourceBundle messageBundle) {
        this.messageBundle = messageBundle;
    }

    public List<NewsletterSubscriptor> getSubscribers() {
        updateSubscriptors();
        return subscribers;
    }

    public NewsletterCategory getFilterCategory() {
        return filterCategory;
    }

    public void setFilterCategory(NewsletterCategory filterCategory) {
        this.filterCategory = filterCategory;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getOnlyInactive() {
        return onlyInactive;
    }

    public void setOnlyInactive(Boolean onlyInactive) {
        this.onlyInactive = onlyInactive;
    }

    public String getCategoryIdAsString() {
        return String.valueOf(categoryId);
    }

    public String getImportResult() {

        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Object a = facesContext.getExternalContext().getRequest();
            String importResultBridge = "";
            String importResultBridgeDetails = "";
            if (a instanceof RenderRequest) {
                RenderRequest renderRequest = (RenderRequest) a;
                PortletPreferences prefs = renderRequest.getPreferences();
                importResultBridge = (String) prefs.getValue("importresult", "");
                importResultBridgeDetails = (String) prefs.getValue("importresultDetails", "");
                prefs.setValue("importresult", "");
                prefs.setValue("importresultDetails", "");
                prefs.store();
            }
            ResourceBundle newsletterMessageBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());

            if (importResultBridge.equals("0")) {
                importResult = newsletterMessageBundle.getString("newsletter.admin.subscribers.import.unsuccess");

            } else if (importResultBridge.equals("1")) {
                importResult = newsletterMessageBundle.getString("newsletter.admin.subscribers.import.success");
            } else if (importResultBridge.equals("2")) {
                importResult = newsletterMessageBundle.getString("newsletter.admin.subscribers.import.partial");
                importResult += " " + importResultBridgeDetails;
            }else{
                importResult = "";
            }
        } catch (Exception ex) {
            log.error(ex);
        }

        return importResult;
    }

    public void setImportResult(String importResult) {
        this.importResult = importResult;
    }

    public void changeCategory(AjaxBehaviorEvent event) {
        this.gotoFirstPage();
    }

    private void updateResults() {
        uiState.setAdminActiveTabIndex(UserUiStateManagedBean.SUBSCRIBERS_TAB_INDEX);
        updateSubscriptors();
    }

    private void updateSubscriptors() {
        try {
            if (onlyInactive) {
                status = null;
            } else {
                status = SubscriptionStatus.ACTIVE;
            }
            if (getCategoryId() == 0) {
                subscribers = subscriptorService.findAllByStatus(uiState.getThemeDisplay(), getPaginationStart(), getPaginationLimit(), "id", NewsletterConstants.ORDER_BY_ASC, status);
                setPaginationTotal(subscriptorService.findAllByStatusCount(uiState.getThemeDisplay(), status));

            } else {
                filterCategory = categoryService.findById(categoryId).getPayload();
                subscribers = subscriptorService.findByCategoryAndStatus(filterCategory, getPaginationStart(), getPaginationLimit(), "id", NewsletterConstants.ORDER_BY_ASC, status);
                setPaginationTotal(subscriptorService.findByCategoryAndStatusCount(filterCategory, status));
            }
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    public List<NewsletterSubscriptor> getSubscriptorsByFilterCategory(ThemeDisplay themeDisplay) {

        List<NewsletterSubscriptor> result = null;
        if (getCategoryId() == 0) {
            result = subscriptorService.findAllByStatus(themeDisplay, SubscriptionStatus.ACTIVE);
        } else {
            result = subscriptorService.findByCategoryAndStatus(getFilterCategory(), SubscriptionStatus.ACTIVE);
        }

        return result;
    }

    @Override
    public void gotoPage() {
        super.gotoPage();
        updateResults();
    }

    @Override
    public void nextPage() {
        super.nextPage();
        updateResults();
    }

    @Override
    public void prevPage() {
        super.prevPage();
        updateResults();
    }

    @Override
    public void gotoFirstPage() {
        super.gotoFirstPage();
        updateResults();
    }

    @Override
    public void gotoLastPage() {
        super.gotoLastPage();
        updateResults();
    }
}
