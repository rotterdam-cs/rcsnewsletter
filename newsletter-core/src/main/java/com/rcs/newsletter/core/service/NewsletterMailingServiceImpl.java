package com.rcs.newsletter.core.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.util.LiferayMailingUtil;
import com.rcs.newsletter.core.service.util.EmailFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.InternetAddress;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author juan
 */
@Service
@Transactional
class NewsletterMailingServiceImpl extends CRUDServiceImpl<NewsletterMailing> implements NewsletterMailingService {   
    private static Log logger = LogFactoryUtil.getLog(NewsletterMailingServiceImpl.class);
    
    @Autowired
    private LiferayMailingUtil mailingUtil;
        
    @Value("${newsletter.mail.from}")
    private String fromEmailAddress;
    @Value("${newsletter.admin.name}")
    private String fromName;    

    @Async
    @Override
    public void sendTestMailing(Long mailingId, String testEmail, ThemeDisplay themeDisplay) {        
        try {
            
            NewsletterMailing mailing = findById(mailingId).getPayload();
            String content = EmailFormat.getEmailFromTemplate(mailing, themeDisplay);
                        
            //Add full path to images
            content = EmailFormat.fixImagesPath(content, themeDisplay);
            
            //Replace User Info
            content = EmailFormat.replaceUserInfo(content, null, themeDisplay);
            
            String title = mailing.getName();
            
            InternetAddress fromIA = new InternetAddress(fromEmailAddress);
            InternetAddress toIA = new InternetAddress(testEmail);
            MailMessage message = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIA, title, content);            
            mailingUtil.sendEmail(message);
            
            //mailingUtil.sendArticleByEmail(mailing.getArticleId(), themeDisplay, testEmail, fromEmailAddress);
            
        } catch (PortalException ex) {
            logger.error("Error while trying to read article", ex);
        } catch (SystemException ex) {
            logger.error("Error while trying to read article", ex);
        } catch (Exception ex) {
            logger.error("Error while trying to read article", ex);
        }      
    }

    @Async
    @Override
    public void sendMailing(Long mailingId, ThemeDisplay themeDisplay, Long archiveId) {
        try {
            NewsletterMailing mailing = findById(mailingId).getPayload();
            String content = EmailFormat.getEmailFromTemplate(mailing, themeDisplay);
            
            //Add full path to images
            content = EmailFormat.fixImagesPath(content, themeDisplay);            
            
            String title = mailing.getName();
            
            InternetAddress fromIA = new InternetAddress(fromEmailAddress, fromName);
            InternetAddress toIA = new InternetAddress();
            MailMessage message = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIA, title, content);
            String bodyContent = message.getBody();
            
            //sendEmail(fromName, fromMail, toName, toMail, title, content);
            
            //if the content is personalizable a different message will be send for each user
            //if (EmailFormat.contentPersonalizable(content)){
                logger.error("Sending personalizable conent");
                for (NewsletterSubscription newsletterSubscription : mailing.getList().getSubscriptions()) {
                    if(newsletterSubscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
                        NewsletterSubscriptor subscriptor = newsletterSubscription.getSubscriptor();
                        String name = subscriptor.getFirstName() + " " + subscriptor.getLastName();                    
                        
                        MailMessage personalMessage = message;
                        
                        toIA = new InternetAddress(subscriptor.getEmail(), name);
                        logger.error("Sending to " + name + "<" + subscriptor.getEmail() + ">");
                        personalMessage.setTo(toIA);

                        //Replace User Info
                        String tmpContent = EmailFormat.replaceUserInfo(bodyContent, newsletterSubscription, themeDisplay, archiveId);
                        personalMessage.setBody(tmpContent);

                        mailingUtil.sendEmail(personalMessage);
                        //mailingUtil.sendArticleByEmail(ja, themeDisplay, name, subscriptor.getEmail(), fromName, fromEmailAddress);
                    }
                }
                
            //if the content is NOT personalizable the same message will be send for all users
            //@@To Improve
            /*
            } else {
                List<InternetAddress> ial = new ArrayList<InternetAddress>();                
                for (NewsletterSubscription newsletterSubscription : mailing.getList().getSubscriptions()) {
                    if(newsletterSubscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
                        NewsletterSubscriptor subscriptor = newsletterSubscription.getSubscriptor();
                        String name = subscriptor.getFirstName() + " " + subscriptor.getLastName();                                                
                        ial.add( new InternetAddress(subscriptor.getEmail(), name) );                        
                    }
                }                
                int numSubscribtors = ial.size();                
                InternetAddress[] ia;
                ia = new InternetAddress[numSubscribtors];
                ia = ial.toArray(ia);                
                message.setBCC(ia);
                InternetAddress toIAt = new InternetAddress("to@to.com");
                message.setTo(toIAt);
                mailingUtil.sendEmail(message);
            }
            */
            logger.error("End Sending personalizable conent");
        } catch (Exception ex) {
            logger.error("Error while trying to read article", ex);
        }
    }
    
    /**
     * 
     * @param mailingId
     * @param themeDisplay
     * @return 
     */
    @Override
    public String getEmailFromTemplate(Long mailingId, ThemeDisplay themeDisplay){
        String content = "";
        try {
            NewsletterMailing mailing = findById(mailingId).getPayload();
            content = EmailFormat.getEmailFromTemplate(mailing, themeDisplay);            
        } catch (Exception ex) {
            Logger.getLogger(NewsletterMailingServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return content;
    }
    
    /**
     * Validates the template format
     * @param mailingId
     * @return 
     */
    @Override
    public boolean validateTemplateFormat(Long mailingId) {
        boolean result = true;
        NewsletterMailing mailing = findById(mailingId).getPayload();
        String templateContent = mailing.getTemplate().getTemplate();
        String content = EmailFormat.validateTemplateFormat(templateContent);
        if (content == null || content.isEmpty()) {
            result = false;
        }
        return result;
    }
    
}
