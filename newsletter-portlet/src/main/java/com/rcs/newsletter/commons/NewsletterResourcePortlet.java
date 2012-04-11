package com.rcs.newsletter.commons;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.portlets.admin.SubscriptorExportManagedBean;
import com.rcs.newsletter.util.FileUploadUtil;
import java.io.IOException;
import java.util.*;
import javax.portlet.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.portletfaces.bridge.GenericFacesPortlet;

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
                PortletPreferences prefs = actionRequest.getPreferences();

                List<FileItem> items = FileUploadUtil.parseRequest(actionRequest);
                for (FileItem fileItem : items) {
                    if (fileItem.getFieldName().equals(ResourceTypeEnum.SUBSCRIPTOR_FROM_EXCEL.toString())) {

                        SubscriptorExportManagedBean subscriptorExportManagedBean =
                                (SubscriptorExportManagedBean) actionRequest.getPortletSession().getAttribute("subscriptorExportManagedBean");

                        int result = 0;
                        String resultRowProblems = "";
                        if (subscriptorExportManagedBean != null) {
                            
                            ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);                            
                            HashMap<Integer, String> resultHM = SubscriptorsResourceUtil.importSubscriptorsFromExcel(fileItem, subscriptorExportManagedBean, themeDisplay);
                            Set set = resultHM.entrySet();
                            Iterator i = set.iterator();
                            while(i.hasNext()){
                              Map.Entry me = (Map.Entry)i.next();
                              result = Integer.parseInt(me.getKey().toString());
                              resultRowProblems = me.getValue().toString();
                            }                            
                            if (result == 0) {
                                prefs.setValue("importresult", "0");
                                prefs.setValue("importresultDetails", resultRowProblems);
                            } else if (result == 1) {
                                prefs.setValue("importresult", "1");
                                prefs.setValue("importresultDetails", resultRowProblems);
                            } else {
                                prefs.setValue("importresult", "2");
                                prefs.setValue("importresultDetails", resultRowProblems);
                            }
                        } else {
                            prefs.setValue("importresult", "0");
                            prefs.setValue("importresultDetails", resultRowProblems);
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