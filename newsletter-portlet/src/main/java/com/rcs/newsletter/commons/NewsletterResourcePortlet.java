package com.rcs.newsletter.commons;

import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.portlets.admin.SubscriberAdminManagedBean;
import com.rcs.newsletter.util.ExcelExporterUtil;
import com.rcs.newsletter.util.FileUploadUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.portletfaces.bridge.GenericFacesPortlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

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
            if (type.equals(ResourceTypeEnum.SUBSCRIPTOR_TO_EXCEL.toString())) {
                //Export subscriptors
                writeSubscriptorsExcel(request, response);
            } else {
                super.serveResource(request, response);
            }
        } else {
            super.serveResource(request, response);
        }
    }

    @Override
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException {
        
        ActionRequest myRequest = actionRequest;
        
        if (FileUploadUtil.isMultipart(actionRequest)) {
            try {
                myRequest = new FileUploadActionRequestWrapper(actionRequest);
            } catch (FileUploadException e) {
            }
        }
        super.processAction(myRequest, actionResponse);
    }

    private void writeSubscriptorsExcel(ResourceRequest request, ResourceResponse response) {

        SubscriberAdminManagedBean subscriberAdminManagedBean =
                (SubscriberAdminManagedBean) request.getPortletSession().getAttribute("subscriberAdminManagedBean");

        if (subscriberAdminManagedBean != null) {

            List<NewsletterSubscriptor> subscriptors = subscriberAdminManagedBean.getSubscriptorsByFilterCategory();
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            HSSFRow row = sheet.createRow((short) 0);
            HSSFCellStyle cellStyle = ExcelExporterUtil.createHeaderStyle(workbook);

            HSSFCell cell1 = row.createCell((short) 0);
            cell1.setCellValue("Id");
            cell1.setCellStyle(cellStyle);

            HSSFCell cell2 = row.createCell((short) 1);
            cell2.setCellValue("Name");
            cell2.setCellStyle(cellStyle);

            HSSFCell cell3 = row.createCell((short) 2);
            cell3.setCellValue("Last Name");
            cell3.setCellStyle(cellStyle);

            HSSFCell cell4 = row.createCell((short) 3);
            cell4.setCellValue("Email");
            cell4.setCellStyle(cellStyle);

            int index = 1;
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
                String fileName = "subscribers";
                if (subscriberAdminManagedBean.getCategoryId() != 0) {
                    NewsletterCategory category = subscriberAdminManagedBean.getFilterCategory();
                    fileName = category != null ? category.getName() : "subscribers";
                }

                response.setContentType(ContentTypes.TEXT_XML_UTF8);
                response.addProperty(HttpHeaders.CACHE_CONTROL, "must-revalidate, post-check=0, pre-check=0");
                response.addProperty(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + ".xls\"");
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