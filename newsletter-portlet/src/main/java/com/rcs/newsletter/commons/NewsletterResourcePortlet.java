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
import org.apache.commons.fileupload.FileItem;

public class NewsletterResourcePortlet extends GenericFacesPortlet {

    private static final Log logger = LogFactoryUtil.getLog(NewsletterResourcePortlet.class);

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
                List<FileItem> items = FileUploadUtil.parseRequest(actionRequest);
                for (FileItem fileItem : items) {
                    if (fileItem.getFieldName().equals(ResourceTypeEnum.SUBSCRIPTOR_FROM_EXCEL.toString())) {

                        SubscriptorExportManagedBean subscriptorExportManagedBean =
                                (SubscriptorExportManagedBean) actionRequest.getPortletSession().getAttribute("subscriptorExportManagedBean");

                        if (subscriptorExportManagedBean != null) {
                            SubscriptorsResourceUtil.importSubscriptorsFromExcel(fileItem, subscriptorExportManagedBean);
                        } else {
                            logger.error("Could not retrieve the Export Managed Bean");
                        }
                    }
                }
            } catch (FileUploadException ex) {
                logger.error("Could not parse the request");
            }
        } else {
            super.processAction(actionRequest, actionResponse);
        }
    }
}