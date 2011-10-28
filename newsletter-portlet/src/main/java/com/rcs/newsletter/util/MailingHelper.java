package com.rcs.newsletter.util;

import com.liferay.mail.service.MailServiceUtil;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journalcontent.util.JournalContentUtil;
import javax.mail.internet.InternetAddress;

/**
 * Utility class used to send Emails
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class MailingHelper {

    /**
     * Send an email using an Journal Article. The title will be the subject
     * and the content the email body
     * @param to
     * @param from
     * @param articleId
     * @param themeDisplay 
     */
    public static void sendEmail(String to, String from, long articleId, ThemeDisplay themeDisplay) {        
        String content = "";
        String title = "";
        try {
            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(themeDisplay.getScopeGroupId(), String.valueOf(articleId));
            content = JournalContentUtil.getContent(ja.getGroupId(), ja.getArticleId(), ja.getTemplateId(), Constants.PRINT, themeDisplay.getLanguageId(), themeDisplay);
            title = ja.getTitle();
        } catch (Exception e) {
        }
        
        sendEmail(to, from, title, content, true);

    }

    public static void sendEmail(String to, String from, String subject, String body, boolean sendHtml) {

        try {
            MailMessage mailMessage = new MailMessage();
            mailMessage.setBody(body);
            mailMessage.setHTMLFormat(sendHtml);
            mailMessage.setFrom(new InternetAddress(from));
            mailMessage.setTo(new InternetAddress(to));
            mailMessage.setSubject(subject);
            MailServiceUtil.sendEmail(mailMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}