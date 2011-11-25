package com.rcs.newsletter.commons;

import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.portlets.admin.SubscriberAdminManagedBean;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.portletfaces.bridge.GenericFacesPortlet;

public class NewsletterResourcePortlet extends GenericFacesPortlet {

    @Inject
    NewsletterCategoryService categoryService;

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response)
            throws PortletException, IOException {

        String type = request.getParameter("type");        
        if (type != null) {
            
            if(type.equals(ResourceTypeEnum.SUBSCRIPTOR_TO_EXCEL.toString())) {
                writeSubscriptorsExcel(request, response);
            }            
        } else {
            super.serveResource(request, response);
        }
    }

    private void writeSubscriptorsExcel(ResourceRequest request, ResourceResponse response) {
        
        SubscriberAdminManagedBean subscriberAdminManagedBean =
                (SubscriberAdminManagedBean) request.getPortletSession().getAttribute("subscriberAdminManagedBean");
                    
        if (subscriberAdminManagedBean != null) {
            
            List<NewsletterSubscriptor> subscriptors = subscriberAdminManagedBean.getSubscribers();
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            HSSFRow row;
            row = sheet.createRow((short) 0);
            row.createCell((short) 0).setCellValue("Id");
            row.createCell((short) 1).setCellValue("Name");
            row.createCell((short) 2).setCellValue("Last Name");
            row.createCell((short) 3).setCellValue("Email");
            int index = 0;
            for (NewsletterSubscriptor subscriptor : subscriptors) {
                row = sheet.createRow((short) index);
                row.createCell((short) 0).setCellValue(subscriptor.getId());
                row.createCell((short) 1).setCellValue(subscriptor.getFirstName());
                row.createCell((short) 2).setCellValue(subscriptor.getLastName());
                row.createCell((short) 3).setCellValue(subscriptor.getEmail());
                index++;
            }                
            
            OutputStream os = null;
            try {
                response.setContentType(ContentTypes.TEXT_XML_UTF8);
                response.addProperty(HttpHeaders.CACHE_CONTROL, "must-revalidate, post-check=0, pre-check=0");
                response.addProperty(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"subscribers.xls\"");
                response.addProperty(HttpHeaders.PRAGMA, "public");
                response.addProperty(HttpHeaders.EXPIRES, "0");
                
                os = response.getPortletOutputStream();
                workbook.write(os);
            } catch (IOException ex) {
                
            } finally {
                try {
                    os.close();
                } catch (IOException ex) {
                    
                }
            }
        }
    }
}