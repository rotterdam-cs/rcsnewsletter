package com.rcs.newsletter.commons;

import com.rcs.newsletter.util.FileUploadUtil;
import java.io.IOException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.commons.fileupload.FileUploadException;
import org.portletfaces.bridge.GenericFacesPortlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.portlets.admin.SubscriptorExportManagedBean;
import java.util.List;
import javax.portlet.PortletPreferences;
import org.apache.commons.fileupload.FileItem;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.rcs.newsletter.portlets.admin.UserUiStateManagedBean;
import javax.inject.Inject;

public class NewsletterResourcePortlet extends GenericFacesPortlet {

    private static final Log logger = LogFactoryUtil.getLog(NewsletterResourcePortlet.class);

    @Inject
    private UserUiStateManagedBean uiState;
    
    public NewsletterResourcePortlet() {
        super();
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response)
            throws PortletException, IOException {

        String type = request.getParameter("type");
        if (type != null) {
            ResourceTypeEnum resourceType = ResourceTypeEnum.valueOf(type);

            switch (resourceType) {
                case SUBSCRIPTOR_TO_EXCEL:
                    SubscriptorsResourceUtil.writeSubscriptorsExcel(request, response);
                    break;
                default:
                    super.serveResource(request, response);
                    break;
            }
        } else {
            super.serveResource(request, response);
        }
    }

    @Override
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException {
        if (FileUploadUtil.isMultipart(actionRequest)) {
            try {
                PortletPreferences prefs = actionRequest.getPreferences();

                List<FileItem> items = FileUploadUtil.parseRequest(actionRequest);
                for (FileItem fileItem : items) {
                    if (fileItem.getFieldName().equals(ResourceTypeEnum.SUBSCRIPTOR_FROM_EXCEL.toString())) {

                        SubscriptorExportManagedBean subscriptorExportManagedBean =
                                (SubscriptorExportManagedBean) actionRequest.getPortletSession().getAttribute("subscriptorExportManagedBean");

                        if (subscriptorExportManagedBean != null) {
                            logger.debug("LLAMANDO!!*****************");
                            String result = SubscriptorsResourceUtil.importSubscriptorsFromExcel(fileItem, subscriptorExportManagedBean, uiState);

                            if (result.equals("0")) {
                                prefs.setValue("importresult", "0");
                            } else if (result.equals("1")) {
                                prefs.setValue("importresult", "1");
                            } else {
                                prefs.setValue("importresult", "2");
                            }

                        } else {
                            prefs.setValue("importresult", "0");
                            logger.error("Could not retrieve the Export Managed Bean");
                        }
                    }
                }
                prefs.store();
                SessionMessages.add(actionRequest, "success");
            } catch (FileUploadException ex) {
                logger.error("Could not parse the request");
            }
        } else {
            super.processAction(actionRequest, actionResponse);
        }
    }
}