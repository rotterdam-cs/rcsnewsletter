package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.util.PortalUtil;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.util.FacesUtil;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.annotation.Scope;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.Log;

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
    private long categoryId;

    public void generateReport() {

        ServiceActionResult<NewsletterCategory> serviceActionResult = categoryService.findById(categoryId);

        if (serviceActionResult.isSuccess()) {
            NewsletterCategory category = serviceActionResult.getPayload();
            String fileName = category.getName() != null ? category.getName() : "subscribers";

            List<NewsletterSubscriptor> subscriptors = subscriptorService.findByCategory(category);
            System.out.println("Total: " + subscriptors.size());

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

            FacesContext facesContext = FacesContext.getCurrentInstance();
            PortletResponse portletResponse = (PortletResponse) facesContext.getExternalContext().getResponse();
            HttpServletResponse response = PortalUtil.getHttpServletResponse(portletResponse);
            
            byte[] bin = workbook.getBytes();

            response.addHeader("Content-Length", "" + bin.length);
            response.addHeader("Content-Disposition", "inline; filename=" + fileName + ".xls");
            response.addHeader("Expires", "0");
            response.addHeader("Pragma", "cache");
            response.addHeader("Cache-Control", "private");
            response.setContentType("application/vnd.ms-excel");            

            try {
                workbook.write(response.getOutputStream());
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } catch (Exception e) {
                log.error("Could not write the Excel file", e);
            }
            facesContext.responseComplete();
        } else {
            FacesUtil.errorMessage("Could not obtain the Category");
        }
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}
