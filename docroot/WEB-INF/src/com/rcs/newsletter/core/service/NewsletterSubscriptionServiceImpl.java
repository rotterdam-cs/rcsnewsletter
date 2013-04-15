package com.rcs.newsletter.core.service;

import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.NewsletterConstants;
import com.rcs.newsletter.core.dto.CreateMultipleSubscriptionsResult;
import com.rcs.newsletter.core.dto.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.core.service.util.EmailFormat;
import com.rcs.newsletter.core.service.util.LiferayMailingUtil;
import com.rcs.newsletter.core.service.util.SubscriptionUtil;
import java.util.List;
import java.util.ResourceBundle;
import javax.mail.internet.InternetAddress;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jdto.DTOBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Service
@Transactional
public class NewsletterSubscriptionServiceImpl extends CRUDServiceImpl<NewsletterSubscription> implements NewsletterSubscriptionService {

    @Autowired
    private DTOBinder binder;
    
    @Autowired
    private NewsletterCategoryService categoryService;
    
    public ServiceActionResult<List<NewsletterSubscriptionDTO>> findSubscriptionsBySubscriptorId(long subscriptorId) {
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
        criteria.createCriteria(NewsletterSubscription.SUBSCRIPTOR).add(Restrictions.idEq(subscriptorId));
        List<NewsletterSubscription> subscriptions = criteria.list();
        if (subscriptions == null){
            return ServiceActionResult.buildFailure(null, "Could not find subscriptions");
        }
        return ServiceActionResult.buildSuccess(binder.bindFromBusinessObjectList(NewsletterSubscriptionDTO.class, subscriptions));        
    }
    
    private NewsletterSubscription findByEmailAndCategory(ThemeDisplay themeDisplay, String email, long newsletterCategoryId) throws HibernateException, Exception {
    	NewsletterSubscription nls = null;
    	Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
        criteria.add(Restrictions.eq(NewsletterSubscription.COMPANYID, themeDisplay.getCompanyId()));
        criteria.add(Restrictions.eq(NewsletterSubscription.GROUPID, themeDisplay.getScopeGroupId()));        
        criteria.createCriteria(NewsletterSubscription.SUBSCRIPTOR).add(Restrictions.ilike(NewsletterSubscriptor.EMAIL, email));
        criteria.createCriteria(NewsletterSubscription.CATEGORY).add(Restrictions.idEq(newsletterCategoryId));
        criteria.setMaxResults(1);
        nls = (NewsletterSubscription) criteria.uniqueResult();

        return nls;
    }

    public NewsletterSubscriptor subscriptorForEmail(String email, ThemeDisplay themeDisplay) throws HibernateException, Exception {
    	NewsletterSubscriptor nls = null;
    	
    	Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NewsletterSubscriptor.class);
        criteria.add(Restrictions.eq(NewsletterSubscriptor.COMPANYID, themeDisplay.getCompanyId()));
        criteria.add(Restrictions.eq(NewsletterSubscriptor.GROUPID, themeDisplay.getScopeGroupId()));
        criteria.add(Restrictions.ilike(NewsletterSubscriptor.EMAIL, email));
        criteria.setMaxResults(1);
        nls = (NewsletterSubscriptor) criteria.uniqueResult();

	    return nls;
    }    
    
    public CreateMultipleSubscriptionsResult createSubscriptionsForCategory(CreateMultipleSubscriptionsResult result, ThemeDisplay themeDisplay, long categoryId, List<NewsletterSubscriptionDTO> newSubscriptions) {
        if (newSubscriptions == null){
            result.setSuccess(false);
            logger.error("newSubscriptions null");
            return result;
        }
        if (newSubscriptions.isEmpty()){
            result.setSuccess(false);
            logger.error("newSubscriptions empty");
            return result; 
        }
        
        ServiceActionResult<NewsletterCategory> sarCategory = categoryService.findById(categoryId);
        if (!sarCategory.isSuccess()){
            result.setSuccess(false);
            logger.error("sarCategory not Success");
            return result;
        }
        
        long omitted = 0;
        long created = 0;
        for (NewsletterSubscriptionDTO subscriptionData : newSubscriptions){     
        	String subscriptorEmail = subscriptionData.getSubscriptorEmail();
        	try {            	
	            NewsletterSubscription subscription = findByEmailAndCategory(themeDisplay, subscriptorEmail, categoryId);
	            if (subscription != null){
	                logger.warn(String.format("Email address %s is already subscribed to the list", subscriptorEmail));
	                omitted++;
	                continue;
	            }
	            if (subscriptorEmail.isEmpty()) {
	            	logger.warn(String.format("Empty Email address %s", subscriptorEmail));
	                omitted++;
	                continue;
	            }
	            NewsletterSubscriptor subscriptor = subscriptorForEmail(subscriptorEmail, themeDisplay);
	            if (subscriptor == null) {
	                subscriptor = new NewsletterSubscriptor();
	                subscriptor.setEmail(subscriptorEmail);
	                subscriptor.setFirstName(subscriptionData.getSubscriptorFirstName());
	                subscriptor.setLastName(subscriptionData.getSubscriptorLastName());
	                subscriptor.setCompanyid(themeDisplay.getCompanyId());
	                subscriptor.setGroupid(themeDisplay.getScopeGroupId());
	                sessionFactory.getCurrentSession().save(subscriptor);
	            }	            
	            subscription = new NewsletterSubscription();
	            subscription.setSubscriptor(subscriptor);
	            subscription.setCategory(sarCategory.getPayload());
	            subscription.setStatus(SubscriptionStatus.ACTIVE);
	            subscription.setGroupid(themeDisplay.getScopeGroupId());
	            subscription.setCompanyid(themeDisplay.getCompanyId());
	            subscription.setActivationKey(SubscriptionUtil.getUniqueKey());
	            subscription.setDeactivationKey(SubscriptionUtil.getUniqueKey());
	            sessionFactory.getCurrentSession().save(subscription);
	            created++;
	            logger.warn("subscriptor:" + subscriptor.getEmail() + "-" + subscriptor.getFirstName() + "-" + subscriptor.getLastName());
        	}catch (HibernateException e) {
        		omitted++;
            	logger.error("error with email " + subscriptorEmail );            	
                continue;
        	} catch (Exception e) {
            	omitted++;
            	logger.error("error with email " + subscriptorEmail );            	
                continue;
			}
        }

        result.setSuccess(true);       
        result.setRowsOmitted(result.getRowsOmitted() + omitted);
        result.setSubscriptionsCreated(created);
        logger.warn("Finishing " + result.isSuccess()); 
        return result;
    }

    public ServiceActionResult createSubscription(NewsletterSubscriptionDTO subscriptionDTO, ThemeDisplay themeDisplay) {
        ResourceBundle bundle = ResourceBundle.getBundle("Language", themeDisplay.getLocale());
        
        // validate category exists
        ServiceActionResult<NewsletterCategory> findCategoryResult = categoryService.findById(Long.valueOf(subscriptionDTO.getCategoryId()));
        if (!findCategoryResult.isSuccess() || findCategoryResult.getPayload() == null){
        	return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.registration.register.error.categorydoesnotexist"));
        }
        
        // check if the subscription already exists
        NewsletterSubscription subscription = null;
		try {
			subscription = findByEmailAndCategory(themeDisplay, subscriptionDTO.getSubscriptorEmail(), Long.valueOf(subscriptionDTO.getCategoryId()));
		} catch (NumberFormatException e1) {
			logger.error(e1);
		} catch (HibernateException e1) {		
			logger.error(e1);
		} catch (Exception e1) {
			logger.error(e1);
		}
        if (subscription != null){
            String error = bundle.getString("newsletter.registration.register.error.alreadyregisterd");
            error = error.replace("{EMAIL_ADDRESS}", subscriptionDTO.getSubscriptorEmail());
            error = error.replace("{LIST_NAME}", subscription.getCategory().getName());
            return ServiceActionResult.buildFailure(null, error);
        }
        
        NewsletterCategory category = categoryService.findById(Long.valueOf(subscriptionDTO.getCategoryId())).getPayload();
        
        
        NewsletterSubscriptor subscriptor = null;
		try {
			subscriptor = subscriptorForEmail(subscriptionDTO.getSubscriptorEmail(), themeDisplay);
		} catch (HibernateException e1) {
			logger.error(e1);
		} catch (Exception e1) {
			logger.error(e1);
		}
            if (subscriptor == null){
                subscriptor = new NewsletterSubscriptor();
                subscriptor.setEmail(subscriptionDTO.getSubscriptorEmail());
                subscriptor.setFirstName(subscriptionDTO.getSubscriptorFirstName());
                subscriptor.setLastName(subscriptionDTO.getSubscriptorLastName());
                subscriptor.setCompanyid(themeDisplay.getCompanyId());
                subscriptor.setGroupid(themeDisplay.getScopeGroupId());
                sessionFactory.getCurrentSession().save(subscriptor);
            }
            
        subscription = new NewsletterSubscription();
        subscription.setSubscriptor(subscriptor);
        subscription.setCategory(category);
        subscription.setStatus(SubscriptionStatus.INACTIVE); // wait for confirmation
        subscription.setGroupid(themeDisplay.getScopeGroupId());
        subscription.setCompanyid(themeDisplay.getCompanyId());
        subscription.setActivationKey(SubscriptionUtil.getUniqueKey());
        subscription.setDeactivationKey(SubscriptionUtil.getUniqueKey());
        sessionFactory.getCurrentSession().save(subscription);
        

        // send confirmation email
        try{
            String content = category.getSubscriptionEmail();
            String subject = bundle.getString("newsletter.subscription.mail.subject");
            content = EmailFormat.replaceUserInfo(content, subscription, themeDisplay);
            content = EmailFormat.fixImagesPath(content, themeDisplay);                            
            InternetAddress fromIA = new InternetAddress(category.getFromEmail(), category.getFromName());
            InternetAddress toIA = new InternetAddress(subscriptor.getEmail());
            MailMessage message = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIA, subject, content);
            LiferayMailingUtil.sendEmail(message);
        }catch(Exception e){
            logger.error("An error occurred when trying to send confirmation email. Exception: " + e.getMessage(), e);
            return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.registration.register.error.sendconfirmation"));
        }

        
        // send feedback message to UI
        String resultMessage = bundle.getString("newsletter.registration.register.message.emailregistered");
        resultMessage = resultMessage.replace("{EMAIL_ADDRESS}", subscriptionDTO.getSubscriptorEmail());
        resultMessage = resultMessage.replace("{LIST_NAME}", category.getName());

        return ServiceActionResult.buildSuccess(null, resultMessage);
    }
    
    

    public ServiceActionResult activateSubscription(Long subscriptionId, String activationKey, ThemeDisplay themeDisplay) {
        ResourceBundle bundle = ResourceBundle.getBundle("Language", themeDisplay.getLocale());
        
        // validate subscription
        ServiceActionResult<NewsletterSubscription> result = findById(subscriptionId);
        if (!result.isSuccess()){
            return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.confirmation.error.subscriptionnotfound"));
        }
        NewsletterSubscription subscription = result.getPayload();
        
        
        // validate key
        if (!subscription.getActivationKey().equals(activationKey)){
            return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.confirmation.error.invalidactivationkey"));
        }
        
        // activate account
        boolean alreadyActive = subscription.getStatus().equals(SubscriptionStatus.ACTIVE);
        
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        
        // send greetings email if the subscription was not activated before
        if (!alreadyActive){
            try{
                String content = subscription.getCategory().getGreetingEmail();
                String subject = bundle.getString("newsletter.subscription.mail.greetings.subject");
                content = EmailFormat.replaceUserInfo(content, subscription, themeDisplay);
                content = EmailFormat.fixImagesPath(content, themeDisplay);                            
                InternetAddress fromIA = new InternetAddress(subscription.getCategory().getFromEmail(), subscription.getCategory().getFromName());
                InternetAddress toIA = new InternetAddress(subscription.getSubscriptor().getEmail());
                MailMessage message = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIA, subject, content);
                LiferayMailingUtil.sendEmail(message);
            }catch(Exception e){
                logger.error("An error occurred when trying to send greeting email. Exception: " + e.getMessage(), e);
                return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.registration.register.error.sendgreetings"));
            }
        }
        
        String successMessage = bundle.getString("newsletter.confirmation.message.activated");
        successMessage = successMessage.replace("{LIST_NAME}", subscription.getCategory().getName());
        return ServiceActionResult.buildSuccess( successMessage, successMessage);
        
    }
    
    
    public ServiceActionResult deactivateSubscription(Long subscriptionId, String deactivationKey, ThemeDisplay themeDisplay) {
        ResourceBundle bundle = ResourceBundle.getBundle("Language", themeDisplay.getLocale());
        
        // validate subscription
        ServiceActionResult<NewsletterSubscription> result = findById(subscriptionId);
        if (!result.isSuccess()){
            return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.confirmation.error.subscriptionnotfound"));
        }
        NewsletterSubscription subscription = result.getPayload();
        
        
        // validate key
        if (!subscription.getDeactivationKey().equals(deactivationKey)){
            return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.confirmation.error.invaliddeactivationkey"));
        }
        
        // deactivate account
        String listName =  subscription.getCategory().getName();
        delete(subscription);
        
        
        String successMessage = bundle.getString("newsletter.confirmation.message.deactivated");
        successMessage = successMessage.replace("{LIST_NAME}", listName);
        return ServiceActionResult.buildSuccess(null, successMessage);
        
    }

    public ServiceActionResult removeSubscription(NewsletterSubscriptionDTO subscriptionDTO, ThemeDisplay themeDisplay) {
        ResourceBundle bundle = ResourceBundle.getBundle("Language", themeDisplay.getLocale());
        
         // validate subscription
        NewsletterSubscription subscription = null;
		try {
			subscription = findByEmailAndCategory(themeDisplay, subscriptionDTO.getSubscriptorEmail(), Long.valueOf(subscriptionDTO.getCategoryId()));
		} catch (NumberFormatException e1) {
			logger.error(e1);
		} catch (HibernateException e1) {
			logger.error(e1);
		} catch (Exception e1) {
			logger.error(e1);
		}
        if (subscription == null){
            return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.confirmation.error.subscriptionnotfound"));
        }
        
        
        // send greetings email if the subscription was not activated before
        try{
            String content = subscription.getCategory().getUnsubscriptionEmail();
            String subject = bundle.getString("newsletter.subscription.mail.unsubscription.subject");
            content = EmailFormat.replaceUserInfo(content, subscription, themeDisplay);
            content = EmailFormat.fixImagesPath(content, themeDisplay);                            
            InternetAddress fromIA = new InternetAddress(subscription.getCategory().getFromEmail(), subscription.getCategory().getFromName());
            InternetAddress toIA = new InternetAddress(subscription.getSubscriptor().getEmail());
            MailMessage message = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIA, subject, content);
            LiferayMailingUtil.sendEmail(message);
        }catch(Exception e){
            logger.error("An error occurred when trying to send unsubscription email. Exception: " + e.getMessage(), e);
            return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.registration.register.error.sendingunsubscription"));
        }
        
        
        String successMessage = bundle.getString("newsletter.confirmation.message.removedfromlist");
        successMessage = successMessage.replace("{EMAIL_ADDRESS}", subscription.getSubscriptor().getEmail());
        successMessage = successMessage.replace("{LIST_NAME}", subscription.getCategory().getName());
        return ServiceActionResult.buildSuccess(null, successMessage);
        
    }
    
    
     
}
