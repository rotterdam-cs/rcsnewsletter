package com.rcs.newsletter.portlets.admin;

import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.util.bridges.jsf.common.JSFPortletUtil;
import com.rcs.newsletter.commons.ResourceTypeEnum;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import org.apache.commons.fileupload.FileItem;

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

    @PostConstruct
    public void init() {
    }

    public String getExportResourceType() {
        return ResourceTypeEnum.SUBSCRIPTOR_TO_EXCEL.toString();
    }
    
    public String redirectImportSubscriptors() {
        return "importSubscriptors";
    }

    public void submitApply() {
        System.out.println("Joyaaa");
        
        FacesContext facesContext = FacesContext.getCurrentInstance();

        // JSFPortletUtil - Liferay provided class from util-bridges.jar
        PortletRequest request = JSFPortletUtil.getPortletRequest(facesContext);
        System.out.println("2 " + request.getAttribute("resumeFile"));
        // FileItem is from commons-fileupload.jar
        FileItem item = (FileItem) request.getAttribute("resumeFile");
        
        System.out.println("File " + item.getName());
    }
}
