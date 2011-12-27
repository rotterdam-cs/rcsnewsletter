package com.rcs.newsletter.commons;

import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.NewsletterSubscriptorService;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.portlets.admin.SubscriberAdminManagedBean;
import com.rcs.newsletter.portlets.admin.SubscriptorExportManagedBean;
import com.rcs.newsletter.util.ExcelExporterUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.NewsletterConstants;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class SubscriptorsResourceUtil {

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

    /**
     * Method that create the excel from the subscribers list and
     * writes to the response
     * @param request
     * @param response 
     */
    public static void writeSubscriptorsExcel(ResourceRequest request, ResourceResponse response) {

        SubscriberAdminManagedBean subscriberAdminManagedBean =
                (SubscriberAdminManagedBean) request.getPortletSession().getAttribute("subscriberAdminManagedBean");

        if (subscriberAdminManagedBean != null) {

            ResourceBundle messageBundle = subscriberAdminManagedBean.getMessageBundle();

            List<NewsletterSubscriptor> subscriptors = subscriberAdminManagedBean.getSubscriptorsByFilterCategory();
            int categoryId = subscriberAdminManagedBean.getCategoryId();
            String categoryName = messageBundle.getString("newsletter.admin.general.undefined");
            String fileName = messageBundle.getString("newsletter.admin.subscribers");

            if (subscriberAdminManagedBean.getCategoryId() != 0) {
                NewsletterCategory category = subscriberAdminManagedBean.getFilterCategory();

                categoryName = category.getName() != null && !category.getName().isEmpty() ? category.getName() : categoryName;
                fileName = category.getName() != null && !category.getName().isEmpty() ? categoryName : fileName;
            }

            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            HSSFRow row = sheet.createRow((short) 0);
            HSSFCellStyle cellStyle = ExcelExporterUtil.createHeaderStyle(workbook);

            HSSFCell cell1 = row.createCell((short) ID_INDEX);
            cell1.setCellValue(ID_COLUMN);
            cell1.setCellStyle(cellStyle);

            HSSFCell cell2 = row.createCell((short) NAME_INDEX);
            cell2.setCellValue(NAME_COLUMN);
            cell2.setCellStyle(cellStyle);

            HSSFCell cell3 = row.createCell((short) LAST_NAME_INDEX);
            cell3.setCellValue(LAST_NAME_COLUMN);
            cell3.setCellStyle(cellStyle);

            HSSFCell cell4 = row.createCell((short) EMAIL_INDEX);
            cell4.setCellValue(EMAIL_COLUMN);
            cell4.setCellStyle(cellStyle);

            HSSFCell cell5 = null;
            NewsletterCategory category = null;
            if (categoryId != 0) {
                cell5 = row.createCell((short) LIST_INDEX);
                cell5.setCellValue(LIST_COLUMN);
                cell5.setCellStyle(cellStyle);
            }

            int index = 1;
            for (NewsletterSubscriptor subscriptor : subscriptors) {
                row = sheet.createRow((short) index);
                row.createCell((short) 0).setCellValue(subscriptor.getId());
                row.createCell((short) 1).setCellValue(subscriptor.getFirstName());
                row.createCell((short) 2).setCellValue(subscriptor.getLastName());
                row.createCell((short) 3).setCellValue(subscriptor.getEmail());

                if (categoryId != 0) {
                    row.createCell((short) 4).setCellValue(categoryName);
                }

                index++;
            }

            OutputStream os = null;
            try {
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

    /**
     * Import subscribers from excel file
     * @param fileItem
     * @param exportManagedBean
     * @return 
     */
    public static String importSubscriptorsFromExcel(FileItem fileItem, SubscriptorExportManagedBean exportManagedBean) {
        logger.error("DENTRO!!*****************");
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(fileItem.getInputStream());
            NewsletterCategory category = exportManagedBean.getFilterCategory();
            if (workbook != null && category != null) {
                HSSFSheet sheet = workbook.getSheetAt(0);

                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    HSSFRow row = sheet.getRow(i);
                    String firstName = "";
                    String lastName = "";
                    String email = "";

                    HSSFCell nameCell = row.getCell(NAME_INDEX);
                    if (nameCell != null && nameCell.getCellType() == HSSFCell.CELL_TYPE_STRING && nameCell.getStringCellValue() != null) {
                        firstName = nameCell.getStringCellValue();
                    }

                    HSSFCell lastNameCell = row.getCell(LAST_NAME_INDEX);
                    if (lastNameCell != null && lastNameCell.getCellType() == HSSFCell.CELL_TYPE_STRING && lastNameCell.getStringCellValue() != null) {
                        lastName = lastNameCell.getStringCellValue();
                    }

                    HSSFCell emailCell = row.getCell(EMAIL_INDEX);
                    if (emailCell != null && emailCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        if (emailCell.getStringCellValue() != null) {
                            email = emailCell.getStringCellValue();
                        } else {

                            FacesContext facesContext = FacesContext.getCurrentInstance();
                            ResourceBundle serverMessageBundle = ResourceBundle.getBundle(NewsletterConstants.NEWSLETTER_BUNDLE, facesContext.getViewRoot().getLocale());
                            Object[] messageArguments = {row.getRowNum()};
                            MessageFormat formatter = new MessageFormat("");

                            formatter.setLocale(facesContext.getViewRoot().getLocale());
                            formatter.applyPattern(serverMessageBundle.getString("newsletter.admin.subscribers.import.failure.email"));

                            String output = formatter.format(messageArguments);
                            logger.error(output);
                            return "2";
//
//                            FacesUtil.errorMessage(output);
                        }
                    }

                    NewsletterSubscriptorService subscriptorService = exportManagedBean.getSubscriptorService();
                    NewsletterSubscriptionService subscriptionService = exportManagedBean.getSubscriptionService();

                    ServiceActionResult<NewsletterSubscriptor> subscriptorResult = subscriptorService.findByEmail(email);
                    NewsletterSubscriptor subscriptor = null;
                    NewsletterSubscription subscription = null;

                    if (subscriptorResult.isSuccess()) {
                        subscriptor = subscriptorResult.getPayload();
                        subscription = subscriptionService.findBySubscriptorAndCategory(subscriptor, category);
                        /**
                         * If the subscriptor exists and he hasn't belong
                         * to the current category, we create the subscription
                         */
                        if (subscription == null) {
                            subscription = new NewsletterSubscription();
                            subscription.setCategory(category);
                            subscription.setSubscriptor(subscriptor);
                            subscription.setStatus(SubscriptionStatus.ACTIVE);

                            subscriptionService.save(subscription);

                            logger.warn("Associated the existing mail: " + email
                                    + " to the category with id: " + category.getId());
                        } else {
                            logger.warn("The mail: " + email
                                    + " already belong to the category with id: " + category.getId());
                        }
                    } else {
                        subscriptor = new NewsletterSubscriptor();
                        subscriptor.setEmail(email);
                        subscriptor.setFirstName(firstName);
                        subscriptor.setLastName(lastName);

                        subscriptorResult = subscriptorService.save(subscriptor);

                        if (subscriptorResult.isSuccess()) {
                            subscription = new NewsletterSubscription();
                            subscription.setCategory(category);
                            subscription.setSubscriptor(subscriptor);
                            subscription.setStatus(SubscriptionStatus.ACTIVE);

                            subscriptionService.save(subscription);

                            logger.debug("Associated the new mail: " + email
                                    + " to the category with id: " + category.getId());
                        } else {
                            logger.error("we could not save the subscriptor of row "+ row.getRowNum());
                            return "2";
                        }
                    }
                }
                return "1";
            } else {
                return "0";
            }

        } catch (IOException ex) {
            return "0";
        }
    }
}
