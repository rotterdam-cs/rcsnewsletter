package com.rcs.newsletter.core.service.util;

import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.util.mail.MailEngine;
import com.liferay.util.mail.MailEngineException;
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

    /**
     * Send an article to an email address.
     * @param articleId
     * @param to
     * @param from
     * @param subject
     * @param content 
     */
    public void sendArticleByEmail(JournalArticle ja, String to, String from) {
        try {
            String content = ja.getContentByLocale(ja.getDefaultLocale());
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
    public void sendArticleByEmail(Long articleId, String to, String from) {
        try {
            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(articleId);
            sendArticleByEmail(ja, to, from);
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
        try {
            InternetAddress fromIA = new InternetAddress(from);
            InternetAddress toIA = new InternetAddress(to);
            MailEngine.send(fromIA, toIA, subject, content, true);
            return true;
        } catch (AddressException ex) {
            log.error("Error building the internet address", ex);
            return false;
        } catch (MailEngineException ex) {
            log.error("Error sending the email", ex);
            return false;
        }
    }
}
