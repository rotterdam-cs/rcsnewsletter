package com.rcs.newsletter.portlets.newsletteradmin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.commons.NewsletterResourcePortlet;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.CreateMultipleSubscriptionsResult;
import com.rcs.newsletter.core.dto.NewsletterCategoryDTO;
import com.rcs.newsletter.core.dto.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.NewsletterCategoryService;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.util.ExcelExporterUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Component
public class SubscriptorsResourceUtil {

    @Autowired
    private NewsletterSubscriptorService subscriptorService;
    
    @Autowired
    private NewsletterSubscriptionService subscriptionService;
    
    @Autowired
    private NewsletterCategoryService categoryService;

    private static final Log logger = LogFactoryUtil.getLog(NewsletterResourcePortlet.class);

    private static final String ID_COLUMN = "Id";

    private static final String NAME_COLUMN = "Name";

    private static final String LAST_NAME_COLUMN = "Last Name";

    private static final String EMAIL_COLUMN = "Email";

    private static final String LIST_COLUMN = "List";

    private static final int ID_INDEX = 0;

    private static final int NAME_INDEX = 1;

    private static final int LAST_NAME_INDEX = 2;

    private static final int EMAIL_INDEX = 3;

    private static final int LIST_INDEX = 4;
    
    private static final int PAGE_SIZE = 5000;

    public void writeSubscriptorsExcel(ResourceRequest request, long categoryId, ThemeDisplay themeDisplay, ResourceResponse response) {

        ResourceBundle messageBundle = ResourceBundle.getBundle("Language", Utils.getCurrentLocale(request));
        String fileName = messageBundle.getString("newsletter.admin.subscribers");
        
        int recordsCount = subscriptorService.findAllByStatusAndCategoryCount(themeDisplay, SubscriptionStatus.ACTIVE, categoryId);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        HSSFRow row = sheet.createRow((short) 0);
        HSSFCellStyle cellStyle = ExcelExporterUtil.createHeaderStyle(workbook);

        HSSFCell cell1 = row.createCell(ID_INDEX);
        cell1.setCellValue(ID_COLUMN);
        cell1.setCellStyle(cellStyle);

        HSSFCell cell2 = row.createCell(NAME_INDEX);
        cell2.setCellValue(NAME_COLUMN);
        cell2.setCellStyle(cellStyle);

        HSSFCell cell3 = row.createCell(LAST_NAME_INDEX);
        cell3.setCellValue(LAST_NAME_COLUMN);
        cell3.setCellStyle(cellStyle);

        HSSFCell cell4 = row.createCell(EMAIL_INDEX);
        cell4.setCellValue(EMAIL_COLUMN);
        cell4.setCellStyle(cellStyle);

        HSSFCell cell5;
        String categoryName = messageBundle.getString("newsletter.admin.general.undefined");
        if (categoryId != 0) {
            cell5 = row.createCell(LIST_INDEX);
            cell5.setCellValue(LIST_COLUMN);
            cell5.setCellStyle(cellStyle);
            ServiceActionResult<NewsletterCategoryDTO> sarCategoryDTO = categoryService.getCategoryDTO(categoryId);
            if (sarCategoryDTO.isSuccess()){
                categoryName = sarCategoryDTO.getPayload().getName();
            }
        }

        int start = 0;
        int excelRow = 1;
        while (start < recordsCount){
            ServiceActionResult<ListResultsDTO<NewsletterSubscriptionDTO>> sarSubscriptions = 
                    subscriptorService.findAllByStatusAndCategory(themeDisplay, start, PAGE_SIZE, "subscriptorId", "asc", SubscriptionStatus.ACTIVE, categoryId);

            start += sarSubscriptions.getPayload().getResult().size();
            
            for (NewsletterSubscriptionDTO subscription : sarSubscriptions.getPayload().getResult()) {
                row = sheet.createRow(excelRow);
                row.createCell(0).setCellValue(subscription.getSubscriptorId());
                row.createCell(1).setCellValue(subscription.getSubscriptorFirstName());
                row.createCell(2).setCellValue(subscription.getSubscriptorLastName());
                row.createCell(3).setCellValue(subscription.getSubscriptorEmail());

                if (categoryId != 0) {
                    row.createCell(4).setCellValue(categoryName);
                }
                excelRow++;
            }
        }        
        

        try {
            response.reset();
            response.setContentType("application/excel");
            response.setProperty(HttpHeaders.CACHE_CONTROL, "must-revalidate, post-check=0, pre-check=0");
            response.setProperty(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + ".xls\"");
            response.setProperty(HttpHeaders.PRAGMA, "public");
            response.setProperty(HttpHeaders.EXPIRES, "0");

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);
            
            OutputStream out = response.getPortletOutputStream();
            out.write(output.toByteArray());
            out.flush();
            out.close();

        } catch (IOException ex) {
        }
    }

    public static boolean isValidEmailAddress(String email) {
        if (!StringUtils.hasText(email)){
            return false;
        }

        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            return true;
        } catch (AddressException ex) {
            return false;
        }
    }

    public CreateMultipleSubscriptionsResult importSubscriptorsFromExcel(InputStream inputStream, long categoryId, ThemeDisplay themeDisplay) {
        CreateMultipleSubscriptionsResult result = new CreateMultipleSubscriptionsResult();
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

            if (workbook == null){
                result.setSuccess(false);
                logger.warn("Error loading the workbook");
                return result;
            }

            if (categoryId == 0) {
                result.setSuccess(false);
                logger.warn("Error loading the list");
                return result;
            }

            HSSFSheet sheet = workbook.getSheetAt(0);
            List<NewsletterSubscriptionDTO> newSubscriptions = new LinkedList<NewsletterSubscriptionDTO>();
            
            long processed = 0;
            long omitted = 0;
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                HSSFRow row = sheet.getRow(i);
                processed++;
                
                String firstName;
                String lastName;
                String email;

                HSSFCell nameCell = row.getCell(NAME_INDEX);
                if (nameCell != null && nameCell.getCellType() == HSSFCell.CELL_TYPE_STRING && StringUtils.hasText(nameCell.getStringCellValue())) {
                    firstName = nameCell.getStringCellValue();
                }else{
                    logger.warn(String.format("Error loading first name from row %d", i + 1));
                    omitted++;
                    continue;
                }

                HSSFCell lastNameCell = row.getCell(LAST_NAME_INDEX);
                if (lastNameCell != null && lastNameCell.getCellType() == HSSFCell.CELL_TYPE_STRING && StringUtils.hasText(lastNameCell.getStringCellValue())) {
                    lastName = lastNameCell.getStringCellValue();
                }else{
                    logger.warn(String.format("Error loading last name from row %d", i + 1));
                    omitted++;
                    continue;
                }
                
                HSSFCell emailCell = row.getCell(EMAIL_INDEX);
                if (emailCell != null && emailCell.getCellType() == HSSFCell.CELL_TYPE_STRING && isValidEmailAddress(emailCell.getStringCellValue().trim())) {
                    email = emailCell.getStringCellValue().trim();
                }else{
                    logger.warn(String.format("Error loading email address from row %d", i + 1));
                    omitted++;
                    continue;                    
                }
                NewsletterSubscriptionDTO newSubscription = new NewsletterSubscriptionDTO();
                newSubscription.setSubscriptorFirstName(firstName);
                newSubscription.setSubscriptorLastName(lastName);
                newSubscription.setSubscriptorEmail(email);
                newSubscriptions.add(newSubscription);
            }
            result.setRowsProcessed(processed);
            result.setRowsOmitted(omitted);
            subscriptionService.createSubscriptionsForCategory(result, themeDisplay, categoryId, newSubscriptions);
            return result;
        } catch (IOException ex) {
            logger.error("Error in importSubscriptorsFromExcel " + ex);
            return null;
        }
    }
}
