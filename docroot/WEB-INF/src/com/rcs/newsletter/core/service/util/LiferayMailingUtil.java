package com.rcs.newsletter.core.service.util;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
//import com.liferay.util.mail.MailEngine;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.util.mail.MailEngineException;

/**
 *
 * @author juan
 */
@Service
@Transactional
public class LiferayMailingUtil {
 
    private static Log log = LogFactoryUtil.getLog(LiferayMailingUtil.class);

    /**
     * Sends an HTML email using the portal's mail engine
     * @param from the From address
     * @param to the To address
     * @param subject the email subject
     * @param content the email content
     */
    public static boolean sendEmail(String from, String to, String subject, String content) throws Exception {    
        return sendEmail(null, from, null, to, subject, content);
    }
    
    /**
     * Sends an HTML email using the portal's mail engine
     * @param from the From address
     * @param to the To address
     * @param subject the email subject
     * @param content the email content
     */
    public static boolean sendEmail(String fromName, String fromEmail, String toName, String toMail, String subject, String content) throws Exception {
        boolean result = false;
        
        try {           
            InternetAddress fromIA = fromName != null ? new InternetAddress(fromEmail, fromName) : new InternetAddress(fromEmail);
            InternetAddress toIA = toName != null ? new InternetAddress(toMail, toName) : new InternetAddress(toMail);
            
            MailMessage message = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIA, subject, content);                        
            message.setBody(content);
            
            log.error("***** content:");            
            log.error(content);
            log.error("***********************");
            
            MailEngineNL.send(message);
                        
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
    
    /**
     * 
     * @param message
     * @return
     * @throws Exception 
     */
    public static boolean sendEmail(MailMessage message) throws Exception {
        boolean result = false;        
        try {           
            MailEngineNL.send(message);                        
            result = true;       
        } catch (MailEngineException ex) {
            log.error("Error sending the email", ex);            
        }
        
        return result;
    }
    
}
