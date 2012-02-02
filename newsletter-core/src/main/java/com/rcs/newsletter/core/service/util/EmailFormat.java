package com.rcs.newsletter.core.service.util;

import java.util.ResourceBundle;
import com.rcs.newsletter.core.model.commons.TemplateBlockComparator;
import java.util.Collections;
import com.rcs.newsletter.core.model.NewsletterMailing;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.rcs.newsletter.core.service.NewsletterTemplateBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
import java.util.List;
import java.net.MalformedURLException;
import org.apache.commons.lang.StringEscapeUtils;
import javax.mail.internet.InternetAddress;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.NewsletterTemplate;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static com.rcs.newsletter.NewsletterConstants.*;

public class EmailFormat {
    private static Log log = LogFactoryUtil.getLog(EmailFormat.class);
    
    //Boundle Keys
    private static final String TEMPLATE_BLOCK_EMPTY_SELECTOR = "newsletter.admin.mailing.template.select.article";
    private static final String DEFAULT_REPLACEMENT_FIRST_NAME_TOKEN = "newsletter.admin.mailing.default.replacement.firstname";    
    private static final String DEFAULT_REPLACEMENT_LAST_NAME_TOKEN = "newsletter.admin.mailing.default.replacement.lastname";
    
    @Autowired
    private static NewsletterTemplateBlockService templateBlockService;
    
        
    /**
     * To determine if the content is personalizable or not
     * @param content
     * @return 
     */
    public static boolean contentPersonalizable(String content) {
        boolean result = false;        
        if (content.contains(CONFIRMATION_LINK_TOKEN)
         || content.contains(LIST_NAME_TOKEN)
         || content.contains(FIRST_NAME_TOKEN)
         || content.contains(LAST_NAME_TOKEN)             
         || content.contains(ONLINE_ARTICLE_LINK) 
        ){
            result = true;
        }        
        return result;
    }
    
    
    /**
     * Replace special tags with user information
     * @param content
     * @param subscription
     * @param themeDisplay
     * @return 
     */
    public static String replaceUserInfo(String content, NewsletterSubscription subscription, ThemeDisplay themeDisplay) {
        return replaceUserInfo(content, subscription, themeDisplay, null);
    }
    
    /**
     * Replace special tags with user information Including the OnLine Article Viewer
     * @param content
     * @param subscription
     * @param themeDisplay
     * @param articleId
     * @return 
     */
    public static String replaceUserInfo(String content, NewsletterSubscription subscription, ThemeDisplay themeDisplay, Long archiveId) {        
        ResourceBundle serverMessageBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, themeDisplay.getLocale());
        
        //Default Replacement information
        String subscriptorFirstName = serverMessageBundle.getString(DEFAULT_REPLACEMENT_FIRST_NAME_TOKEN);
        String subscriptorLastName = serverMessageBundle.getString(DEFAULT_REPLACEMENT_LAST_NAME_TOKEN);
        String categoryName = "";        
        String confirmationLinkToken = "";
        String confirmationUnregisterLinkToken = "";
        String onlineArticleLink = "";
        
        String portalUrl = themeDisplay.getPortalURL();
        if (subscription != null) {
            //Replace Subscriptor Information
            NewsletterSubscriptor subscriptor = subscription.getSubscriptor();
            subscriptorFirstName = subscriptor.getFirstName() != null ? subscriptor.getFirstName() : "";
            subscriptorLastName = subscriptor.getLastName() != null ? subscriptor.getLastName() : "";
            
            //Replace Category Information
            NewsletterCategory category = subscription.getCategory();
            categoryName = category.getName();
            
            //Replace Confirmation Link Information
            StringBuilder stringBuilder = new StringBuilder(portalUrl);
            stringBuilder.append(ONLINE_NEWSLETTER_CONFIRMATION_PAGE);
            stringBuilder.append("?subscriptionId=");
            stringBuilder.append(subscription.getId());
            stringBuilder.append("&activationkey=");
            stringBuilder.append(subscription.getActivationKey());       
            
            String confirmationLinkTokenTmp = stringBuilder.toString();            
            StringBuilder stringBuilderconfirmationLinkToken = new StringBuilder("<a href=\"");
            stringBuilderconfirmationLinkToken.append(confirmationLinkTokenTmp);
            stringBuilderconfirmationLinkToken.append("\">");
            stringBuilderconfirmationLinkToken.append(confirmationLinkTokenTmp);            
            stringBuilderconfirmationLinkToken.append("</a>");
            confirmationLinkToken = stringBuilderconfirmationLinkToken.toString();
            
            //Replace UNREGISTER Confirmation Link Information
            StringBuilder stringBuilderu = new StringBuilder(portalUrl);
            stringBuilderu.append(ONLINE_NEWSLETTER_CONFIRMATION_PAGE);
            stringBuilderu.append("?unsubscriptionId=");
            stringBuilderu.append(subscription.getId());
            stringBuilderu.append("&deactivationkey=");
            stringBuilderu.append(subscription.getDeactivationKey());       
            
            String confirmationLinkTokenTmpu = stringBuilderu.toString();            
            StringBuilder stringBuilderconfirmationLinkTokenu = new StringBuilder("<a href=\"");
            stringBuilderconfirmationLinkTokenu.append(confirmationLinkTokenTmpu);
            stringBuilderconfirmationLinkTokenu.append("\">");
            stringBuilderconfirmationLinkTokenu.append(confirmationLinkTokenTmpu);            
            stringBuilderconfirmationLinkTokenu.append("</a>");
            confirmationUnregisterLinkToken = stringBuilderconfirmationLinkTokenu.toString();
            
            //Replace Confirmation Link Information
            if (archiveId != null) {            
                StringBuilder stringBuilderol = new StringBuilder(portalUrl);
                stringBuilderol.append(ONLINE_NEWSLETTER_VIEWER_PAGE);
                stringBuilderol.append("?nlid=");
                stringBuilderol.append(archiveId);
                stringBuilderol.append("&sid=");
                stringBuilderol.append(subscription.getId());
                
                String stringBuilderolTmp = stringBuilderol.toString();            
                StringBuilder stringBuilderollink = new StringBuilder("<a href=\"");
                stringBuilderollink.append(stringBuilderolTmp);
                stringBuilderollink.append("\">");
                stringBuilderollink.append(stringBuilderolTmp);            
                stringBuilderollink.append("</a>");
                onlineArticleLink = stringBuilderollink.toString();
            }
            
        }        
        content = content.replace(FIRST_NAME_TOKEN, subscriptorFirstName);
        content = content.replace(LAST_NAME_TOKEN, subscriptorLastName);
        content = content.replace(LIST_NAME_TOKEN, categoryName);
        content = content.replace(CONFIRMATION_LINK_TOKEN, confirmationLinkToken);
        content = content.replace(CONFIRMATION_UNREGISTER_LINK_TOKEN, confirmationUnregisterLinkToken);        
        content = content.replace(ONLINE_ARTICLE_LINK, onlineArticleLink);
        return content;
    }
    
    
    
    /**
     * Fix the relative Paths to Absolute Paths on images
     */
    public static String fixImagesPath(String emailBody, ThemeDisplay themeDisplay) {
        String siteURL = getUrl(themeDisplay);
        String result = emailBody.replaceAll("src=\"/", "src=\" " + siteURL);
        result = result.replaceAll("&amp;", "&");
        return result;
    }

    /**
     * Returns the base server URL
     */
    public static String getUrl(ThemeDisplay themeDisplay) {
        StringBuilder result = new StringBuilder();
        String[] toReplaceTmp = themeDisplay.getURLHome().split("/");
        for (int i = 0; i < toReplaceTmp.length; i++) {
            if (i < 3) {
                result.append(toReplaceTmp[i]);
                result.append("/");
            }
        }
        return result.toString();
    }

    /**
     * 
     * @param u
     * @return
     * @throws Exception 
     */
    public static File getFile(URL u) throws Exception {
        URLConnection uc = u.openConnection();
        String contentType = uc.getContentType();
        int contentLength = uc.getContentLength();
        if (contentType.startsWith("text/") || contentLength == -1) {
            throw new IOException("This is not a binary file.");
        }
        InputStream raw = uc.getInputStream();
        InputStream is = new BufferedInputStream(raw);

        File tmp = null;
        OutputStream output = null;
        try {
            log.error("ContenType: " + contentType);
            String fileExt = "";
            if (contentType.endsWith("png")){
                fileExt = ".png";
            } else if (contentType.endsWith("jpg")){
                fileExt = ".jpg";
            } else if (contentType.endsWith("jpeg")){
                fileExt = ".jpeg";
            } else if (contentType.endsWith("jpe")){
                fileExt = ".jpe";
            } else if (contentType.endsWith("gif")){
                fileExt = ".gif";
            }
            tmp = File.createTempFile("image", fileExt);
            output = new FileOutputStream(tmp);
            int val;  
            while ((val = is.read()) != -1) {
                output.write(val);
            }            
        } catch (IOException e) {
            log.error(e);
        } finally {
            try {
                is.close();
                output.flush();
                output.close();
            } catch (Exception e) {
                log.error(e);
            }
        }        
        return tmp;
    }

    /**
     * Get the message with attached images
     * @param fromIA
     * @param toIA
     * @param subject
     * @param content
     * @return
     * @throws Exception 
     */
    public static MailMessage getMailMessageWithAttachedImages(InternetAddress fromIA, InternetAddress toIA, String subject, String content) throws Exception {
        
        ArrayList images = getImagesPathFromHTML(content);
        
        MailMessage message = new MailMessage(fromIA, toIA, subject, content, true);
            
        // embed the images into the multipart
        for (int i = 0; i < images.size(); i++) {
            String image = (String) images.get(i);
            URL imageUrl = null;
            try {
                String imagePathOriginal = (String) images.get(i);                    
                String imagePath = StringEscapeUtils.unescapeHtml(image);
                imageUrl = new URL(imagePath);                    
                File tempF = getFile(imageUrl);//To Improve probably add Cache
                content = StringUtils.replace(content, imagePathOriginal, "cid:" + tempF.getName());
                message.addFileAttachment(tempF);
            } catch (MalformedURLException ex) {
                log.error("problem with image url " + image, ex);
            }
        }
        message.setBody(content);
        
        return message;
    }
    
    /**
     * Method imported from COPS (com.rcs.community.common.MimeMail)
     * Returns an ArrayList with all the different images paths. Duplicated paths are deleted.
     *
     * NB! the returned images urls may have html encoding included.
     * 
     * @param htmltext a String HTML with content
     * @return an ArrayList with the images paths
     */
    public static ArrayList getImagesPathFromHTML(String htmltext) {

        ArrayList imagesList = new ArrayList();
        try {
            // get everything that is inside the <img /> tag
            String[] imagesTag = StringUtils.substringsBetween(htmltext, "<img ", ">");

            if (imagesTag != null) { // if there are images

                for (int i = 0; i < imagesTag.length; i++) {
                    // get what is in the src attribute
                    String imagePath = StringUtils.substringBetween(imagesTag[i], "src=\"", "\"");
                    if (imagePath == null) {
                        imagePath = StringUtils.substringBetween(imagesTag[i], "src='", "'");
                    }

                    if (!imagesList.contains(imagePath)) { // don't save the duplicated images
                        imagesList.add(imagePath);
                    }
                }
            }


            /// and now for the background images only one style of typing is allowed for now!!!
            imagesTag = StringUtils.substringsBetween(htmltext, "background=\"", "\"");


            if (imagesTag != null) {
                for (int i = 0; i < imagesTag.length; i++) {
                    // get what is in the src attribute
                    String imagePath = imagesTag[i].trim();
                    log.error("processing: " + imagePath);
                    if (!imagesList.contains(imagePath)) { // don't save the duplicated images
                        imagesList.add(imagePath);
                    }
                }
            }
            imagesTag = StringUtils.substringsBetween(htmltext, "background=\'", "\'");
            if (imagesTag != null) {
                for (int i = 0; i < imagesTag.length; i++) {
                    // get what is in the src attribute
                    String imagePath = imagesTag[i].trim();
                    log.error("processing: " + imagePath);
                    if (!imagesList.contains(imagePath)) { // don't save the duplicated images
                        imagesList.add(imagePath);
                    }
                }
            }



        } catch (Exception ex) {
            log.error("error in getImagesPathFromHTML: ", ex);
        }
        return imagesList;
    }
    

    /**
     * Get the email content based on the template
     * @param mailing
     * @param themeDisplay
     * @return 
     */
    public static String getEmailFromTemplate(NewsletterMailing mailing, ThemeDisplay themeDisplay) throws Exception { 
        NewsletterTemplate template = mailing.getTemplate();
        String result = template.getTemplate();
        String fTagBlockOpen = fixTagsToRegex(TEMPLATE_TAG_BLOCK_OPEN);
        String fTagBlockClose = fixTagsToRegex(TEMPLATE_TAG_BLOCK_CLOSE);
        String fTagBlockTitle = fixTagsToRegex(TEMPLATE_TAG_TITLE);
        String fTagBlockContent = fixTagsToRegex(TEMPLATE_TAG_CONTENT);
        
        result = result.replace(TEMPLATE_TAG_BLOCK_OPEN, fTagBlockOpen)
                .replace(TEMPLATE_TAG_BLOCK_CLOSE, fTagBlockClose)
                .replace(TEMPLATE_TAG_TITLE, fTagBlockTitle)
                .replace(TEMPLATE_TAG_CONTENT, fTagBlockContent);
        String resulttmp = result;
        
        List<NewsletterTemplateBlock> ntb = mailing.getBlocks();
        Collections.sort(ntb, new TemplateBlockComparator());
        Pattern patternBlock = Pattern.compile(fTagBlockOpen + ".*?" + fTagBlockClose, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = patternBlock.matcher(result);
        String toReplaceTmp = "";
        int count = 0;
        
        //Iterate each Block
        while(m.find()) {             
            try {               
               String toReplace = result.substring(m.start(), m.end() );               
               toReplaceTmp  = result.substring(m.start()+fTagBlockOpen.length(), m.end()-fTagBlockClose.length() );  
              
               //If there is a content related to this block
               if (ntb.size() > count) {                   
                   if (ntb.get(count).getArticleId() != null && ntb.get(count).getArticleId() != UNDEFINED) {                   
                        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(ntb.get(count).getArticleId());
                        String content = ja.getContentByLocale(ja.getDefaultLocale());
                        content = ArticleUtils.getArticleContent(ja, themeDisplay.getLocale().toString());
                        toReplaceTmp = toReplaceTmp.replace(fTagBlockTitle, ja.getTitle());
                        toReplaceTmp = toReplaceTmp.replace(fTagBlockContent, content); 
                        resulttmp = resulttmp.replaceFirst(toReplace, toReplaceTmp);      
                        
                   //If there is a NOT content related to this block the block is deleted
                   } else {
                    resulttmp = resulttmp.replaceFirst(toReplace, "");
                   }
                   
                //If there is a NOT content related to this block the block is deleted
                } else {
                    resulttmp = resulttmp.replaceFirst(toReplace, "");
                }
               
            } catch (PortalException ex) {
                log.error("Error while trying to read article", ex);
            } catch (SystemException ex) {
                log.error("Error while trying to read article", ex);
            }           
           count++;
        }
        return resulttmp;
    }
    
    /**
     * Get the edit code that allow the selection of articles on each block
     * @param templateContent
     * @return 
     */
    public static String parseTemplateEdit(String incomingTemplateContent, String newsletterArticleType, String newsletterArticleCategory, String newsletterArticleTag, ThemeDisplay themeDisplay) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String result = ""; 
        
        ResourceBundle newsletterMessageBundle = ResourceBundle.getBundle(NEWSLETTER_BUNDLE, themeDisplay.getLocale());
        String emptySelectorMessage = newsletterMessageBundle.getString(TEMPLATE_BLOCK_EMPTY_SELECTOR);               
        String fTagBlockOpen = fixTagsToRegex(TEMPLATE_TAG_BLOCK_OPEN);
        String fTagBlockClose = fixTagsToRegex(TEMPLATE_TAG_BLOCK_CLOSE);
        String templateContent = validateTemplateFormat(incomingTemplateContent);
        //If the template contains at least one block with one title or content
        if (templateContent != null && !templateContent.isEmpty()) {
                String templateContentTmp = templateContent;
                Pattern patternBlock = Pattern.compile(fTagBlockOpen + ".*?" + fTagBlockClose, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
                Matcher m = patternBlock.matcher(templateContent);
                int count = 0;                
                
                //Get all newsletter articles to create the selectors
                HashMap<String, JournalArticle> resultArticleNewsletter = new HashMap<String, JournalArticle>();
                try {
                    //Search Articles by Type
                    List<JournalArticle> articlesByType = ArticleUtils.findArticlesByType(newsletterArticleType);
                    for (JournalArticle article : articlesByType) {
                        if (!resultArticleNewsletter.containsKey(article.getArticleId())) {                            
                            resultArticleNewsletter.put(article.getArticleId(), article);
                        }
                    }
                    //Search Articles by Category
                    List<JournalArticle> articlesByCategory = ArticleUtils.findArticlesByCategory(newsletterArticleCategory);
                    for (JournalArticle article : articlesByCategory) {
                        if (!resultArticleNewsletter.containsKey(article.getArticleId())) {                            
                            resultArticleNewsletter.put(article.getArticleId(), article);
                        }
                    }
                    //Search Articles by Tag
                    List<JournalArticle> articlesByTag = ArticleUtils.findArticlesByTag(themeDisplay, newsletterArticleTag);
                    for (JournalArticle article : articlesByTag) {
                        if (!resultArticleNewsletter.containsKey(article.getArticleId())) {                            
                            resultArticleNewsletter.put(article.getArticleId(), article);
                        }
                    }                    
                } catch (SystemException ex) {
                    log.error("Could not filter the articles by this category, type, or tag", ex);
                } catch (PortalException ex) {
                    log.error("Could not filter the articles by this category, type, or tag", ex);
                }
                List<JournalArticle> newsletterArticles = new ArrayList<JournalArticle>(resultArticleNewsletter.values());
                
                //Create HTML select option with all newsletter articles
                StringBuilder selectHTMLOptionsSB = new StringBuilder("<option value=\"");
                selectHTMLOptionsSB.append(String.valueOf(UNDEFINED));
                selectHTMLOptionsSB.append("\">");
                selectHTMLOptionsSB.append(emptySelectorMessage);
                selectHTMLOptionsSB.append("</option>");                
                for (JournalArticle journalArticle : newsletterArticles) {
                    selectHTMLOptionsSB.append("<option value=\"");
                    selectHTMLOptionsSB.append(journalArticle.getId());
                    selectHTMLOptionsSB.append("\">");
                    selectHTMLOptionsSB.append(journalArticle.getTitle());
                    selectHTMLOptionsSB.append("</option>");
                }
                
                //Iterate each Block and replace by the HTML select
                while (m.find()) {
                    StringBuilder selectHTMLItemSB = new StringBuilder("<div id=\"blockSelector");
                    selectHTMLItemSB.append(count);
                    selectHTMLItemSB.append("\">");
                    selectHTMLItemSB.append("<select class=\"blockSelectorSelect\"");
                    selectHTMLItemSB.append(" id=\"blockSelectorSelect");
                    selectHTMLItemSB.append(count);
                    selectHTMLItemSB.append("\" class=\"newsletter-forms-input-text\" onchange=\"selectArticlesToTemplate() \">");
                    selectHTMLItemSB.append(selectHTMLOptionsSB.toString());
                    selectHTMLItemSB.append("</select></div>");
                    String toReplace = templateContent.substring(m.start(), m.end());
                    templateContentTmp = templateContentTmp.replaceFirst(toReplace, selectHTMLItemSB.toString());
                    count++;
                }
                
                StringBuilder resultSB = new StringBuilder("<div class=\"templateArticleSelectorEditor\">");
                resultSB.append(templateContentTmp);
                resultSB.append("</div>");
                result = resultSB.toString();            
        } else {
            log.error("Invalid Template Format");
        }

        return result;
    }
    
    /**
     * Validate the template format
     * @param templateContent
     * @return 
     */
    public static String validateTemplateFormat(String templateContent) {
        String result = null;
        String fTagBlockOpen = fixTagsToRegex(TEMPLATE_TAG_BLOCK_OPEN);
        String fTagBlockClose = fixTagsToRegex(TEMPLATE_TAG_BLOCK_CLOSE);
        String fTagBlockTitle = fixTagsToRegex(TEMPLATE_TAG_TITLE);
        String fTagBlockContent = fixTagsToRegex(TEMPLATE_TAG_CONTENT);
        
        templateContent = templateContent.replace(TEMPLATE_TAG_BLOCK_OPEN, fTagBlockOpen)
                .replace(TEMPLATE_TAG_BLOCK_CLOSE, fTagBlockClose)
                .replace(TEMPLATE_TAG_TITLE, fTagBlockTitle)
                .replace(TEMPLATE_TAG_CONTENT, fTagBlockContent);
        
        //If the template contains at least one block with one title or content
        if (templateContent.contains(fTagBlockOpen) 
                && templateContent.contains(fTagBlockClose) 
                && ( templateContent.contains(fTagBlockTitle) || templateContent.contains(fTagBlockContent) ) 
        ){
            result = templateContent;
        }
        return result;
    }
    
    /**
     * Fix tags to allow replacements using common regular Expressions
     * @param tag
     * @return 
     */
    public static String fixTagsToRegex(String tag){
        tag = tag.replace("[", "<");
        tag = tag.replace("]", ">");
        return tag;
    }
    
    
}
