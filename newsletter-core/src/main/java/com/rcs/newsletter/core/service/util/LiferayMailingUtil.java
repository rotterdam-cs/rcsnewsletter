package com.rcs.newsletter.core.service.util;

import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journalcontent.util.JournalContentUtil;
import com.liferay.util.mail.MailEngine;
import com.liferay.util.mail.MailEngineException;
import java.io.UnsupportedEncodingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author juan
 */
@Service
@Transactional
public class LiferayMailingUtil {

    private static final Logger log = LoggerFactory.getLogger(LiferayMailingUtil.class);

    public void sendArticleByEmail(JournalArticle ja, ThemeDisplay themeDisplay, String toName, String toMail, String fromName, String fromMail) {
        try {
            String content = ja.getContentByLocale(ja.getDefaultLocale());
            content = JournalContentUtil.getContent(ja.getGroupId(), 
                                                    ja.getArticleId(), 
                                                    ja.getTemplateId(), 
                                                    Constants.PRINT, 
                                                    themeDisplay.getLanguageId(), 
                                                    themeDisplay);        
            
            String title = ja.getTitle();
            sendEmail(fromName, fromMail, toName, toMail, title, content);
        } catch (Exception ex) {
            log.error("Error while trying to read article", ex);
        }
    }
    
    
    /**
     * Send an article to an email address.
     * @param articleId
     * @param to
     * @param from
     * @param subject
     * @param content 
     */
    public void sendArticleByEmail(JournalArticle ja, ThemeDisplay themeDisplay, String to, String from) {
        try {
            String content = ja.getContentByLocale(ja.getDefaultLocale());
            content = JournalContentUtil.getContent(ja.getGroupId(), 
                                                    ja.getArticleId(), 
                                                    ja.getTemplateId(), 
                                                    Constants.PRINT, 
                                                    themeDisplay.getLanguageId(), 
                                                    themeDisplay);        
            
            String title = ja.getTitle();
            sendEmail(from, to, title, content);
        } catch (Exception ex) {
            log.error("Error while trying to read article", ex);
        }
    }
    
    /**
     * Send an article to an email address.
     * @param articleId
     * @param to
     * @param from 
     */
    public void sendArticleByEmail(Long articleId, ThemeDisplay themeDisplay, String to, String from) {
        try {
            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(articleId);
            sendArticleByEmail(ja, themeDisplay, to, from);
        } catch (Exception ex) {
            log.error("Error while trying to read article", ex);
        }
    }

    /**
     * Sends an HTML email using the portal's mail engine
     * @param from the From address
     * @param to the To address
     * @param subject the email subject
     * @param content the email content
     */
    public static boolean sendEmail(String from, String to, String subject, String content) {    
        return sendEmail(null, from, null, to, subject, content);
    }
    
    /**
     * Sends an HTML email using the portal's mail engine
     * @param from the From address
     * @param to the To address
     * @param subject the email subject
     * @param content the email content
     */
    public static boolean sendEmail(String fromName, String fromEmail, String toName, String toMail, String subject, String content) {
        boolean result = false;
        
        try {
            InternetAddress fromIA = fromName != null ? new InternetAddress(fromEmail, fromName) : new InternetAddress(fromEmail);
            InternetAddress toIA = toName != null ? new InternetAddress(toMail, toName) : new InternetAddress(toMail);
            MailEngine.send(fromIA, toIA, subject, content, true);
            result = true;
        } catch(UnsupportedEncodingException ex) {
            log.error("Error building the internet address", ex);            
        } catch (AddressException ex) {
            log.error("Error building the internet address", ex);            
        } catch (MailEngineException ex) {
            log.error("Error sending the email", ex);            
        }
        
        return result;
    }
}
