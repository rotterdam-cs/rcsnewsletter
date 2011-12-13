package com.rcs.newsletter.portlets.onlineviewer;

import com.rcs.newsletter.portlets.admin.UserUiStateManagedBean;
import com.rcs.newsletter.core.service.util.EmailFormat;
import com.rcs.newsletter.core.model.NewsletterArchive;
import com.rcs.newsletter.core.service.NewsletterArchiveService;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.faces.context.FacesContext;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.io.Serializable;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import static com.rcs.newsletter.NewsletterConstants.*;

/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
@Named
@Scope("request")
public class OnlineViewerManagedBean implements Serializable {

    private static Log log = LogFactoryUtil.getLog(OnlineViewerManagedBean.class);
    private static final long serialVersionUID = 1L;    
    private Long requestedNewsletterId = null;
    private String requestedsubscriptionId = null;
    private NewsletterArchive article = null;    
    
    
    @Inject
    private NewsletterArchiveService archiveService;   
    @Inject
    private NewsletterSubscriptionService subscriptionService;
    @Inject
    private UserUiStateManagedBean uiStateManagedBean;

    
    
    public Long getRequestedNewsletterId() {
        return requestedNewsletterId;
    }

    public void setRequestedNewsletterId(Long requestedNewsletterId) {
        this.requestedNewsletterId = requestedNewsletterId;
    }    
    
    public String getRequestedsubscriptionId() {
        return requestedsubscriptionId;
    }

    public void setRequestedsubscriptionId(String requestedsubscriptionId) {
        this.requestedsubscriptionId = requestedsubscriptionId;
    }
        
    public NewsletterArchive getArticle() {
        return article;
    }

    public void setArticle(NewsletterArchive article) {
        this.article = article;
    }

    
    
    /**
     * Get the article and user params from the URL and return the article content with replacements
     * @return 
     */
    public String getNewsletterArticle() {
        String result = "";
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, Object> map = facesContext.getExternalContext().getRequestMap();
        if (map != null) {
            for (String key : map.keySet()) {
                if (map.get(key) instanceof HttpServletRequestWrapper) {
                    HttpServletRequest request = (HttpServletRequest) ((HttpServletRequestWrapper) map.get(key)).getRequest();
                    setRequestedNewsletterId(Long.parseLong((String) request.getParameter("nlid")));
                    setRequestedsubscriptionId((String) request.getParameter("sid"));
                    break;
                }
            }
        }
        if(getRequestedNewsletterId() != null && getRequestedsubscriptionId() != null) {
            ServiceActionResult<NewsletterArchive> sar = archiveService.findById( getRequestedNewsletterId() );
            if (sar.isSuccess()) {
                long subscriptionId = Long.parseLong(getRequestedsubscriptionId());
                ServiceActionResult<NewsletterSubscription> subscriptionResult = subscriptionService.findById(subscriptionId);
                result = sar.getPayload().getEmailBody();
                if (subscriptionResult.isSuccess()) {
                    NewsletterSubscription subscription = subscriptionResult.getPayload();
                    result = EmailFormat.replaceUserInfo(result, subscription, uiStateManagedBean.getThemeDisplay(), getRequestedNewsletterId());
                }
            }                        
        }        
        return result;
    }    
    
    
    
}
