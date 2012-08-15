package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.CreateMultipleSubscriptionsResult;
import com.rcs.newsletter.core.dto.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.core.service.util.SubscriptionUtil;
import java.util.List;
import java.util.ResourceBundle;
import org.hibernate.Criteria;
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
public class NewsletterSubscriptionImpl extends CRUDServiceImpl<NewsletterSubscription> implements NewsletterSubscriptionService {

    @Autowired
    private DTOBinder binder;
    
    @Autowired
    private NewsletterCategoryService categoryService;
    
    @Override
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
    
    private NewsletterSubscription findByEmailAndCategory(ThemeDisplay themeDisplay, String email, long newsletterCategoryId) {
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
        criteria.add(Restrictions.eq(NewsletterSubscription.COMPANYID, themeDisplay.getCompanyId()));
        criteria.add(Restrictions.eq(NewsletterSubscription.GROUPID, themeDisplay.getScopeGroupId()));        
        criteria.createCriteria(NewsletterSubscription.SUBSCRIPTOR).add(Restrictions.ilike(NewsletterSubscriptor.EMAIL, email));
        criteria.createCriteria(NewsletterSubscription.CATEGORY).add(Restrictions.idEq(newsletterCategoryId));
        criteria.setMaxResults(1);

        return (NewsletterSubscription) criteria.uniqueResult();
    }

    public NewsletterSubscriptor subscriptorForEmail(String email, ThemeDisplay themeDisplay) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NewsletterSubscriptor.class);
        criteria.add(Restrictions.eq(NewsletterSubscriptor.COMPANYID, themeDisplay.getCompanyId()));
        criteria.add(Restrictions.eq(NewsletterSubscriptor.GROUPID, themeDisplay.getScopeGroupId()));
        criteria.add(Restrictions.ilike(NewsletterSubscriptor.EMAIL, email));
        criteria.setMaxResults(1);
        return (NewsletterSubscriptor) criteria.uniqueResult();
    }    
    
    @Override
    public void createSubscriptionsForCategory(CreateMultipleSubscriptionsResult result, ThemeDisplay themeDisplay, long categoryId, List<NewsletterSubscriptionDTO> newSubscriptions) {
        if (newSubscriptions == null){
            result.setSuccess(false);
            return;
        }
        if (newSubscriptions.isEmpty()){
            result.setSuccess(false);
            return; 
        }
        
        ServiceActionResult<NewsletterCategory> sarCategory = categoryService.findById(categoryId);
        if (!sarCategory.isSuccess()){
            result.setSuccess(false);
            return;
        }
        
        long omitted = 0;
        long created = 0;
        for (NewsletterSubscriptionDTO subscriptionData : newSubscriptions){
            String subscriptorEmail = subscriptionData.getSubscriptorEmail();
            
            NewsletterSubscription subscription = findByEmailAndCategory(themeDisplay, subscriptorEmail, categoryId);
            if (subscription != null){
                logger.warn(String.format("Email address %s is already subscribed to the list", subscriptorEmail));
                omitted++;
                continue;
            }
            
            NewsletterSubscriptor subscriptor = subscriptorForEmail(subscriptorEmail, themeDisplay);
            if (subscriptor == null){
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
        }
        result.setSuccess(true);
        result.setRowsOmitted(result.getRowsOmitted() + omitted);
        result.setSubscriptionsCreated(created);
    }

    @Override
    public ServiceActionResult createSubscription(NewsletterSubscriptionDTO subscriptionDTO, ThemeDisplay themeDisplay) {
        ResourceBundle bundle = ResourceBundle.getBundle("Language", themeDisplay.getLocale());
        
        // check if the subscription already exists
        NewsletterSubscription subscription = findByEmailAndCategory(themeDisplay, subscriptionDTO.getSubscriptorEmail(), Long.valueOf(subscriptionDTO.getCategoryId()));
        if (subscription != null){
            String error = bundle.getString("newsletter.registration.register.error.alreadyregisterd");
            error = error.replace("{EMAIL_ADDRESS}", subscriptionDTO.getSubscriptorEmail());
            error = error.replace("{LIST_NAME}", subscription.getCategory().getName());
            return ServiceActionResult.buildFailure(null, error);
        }
        
        NewsletterCategory category = categoryService.findById(Long.valueOf(subscriptionDTO.getCategoryId())).getPayload();
        
        
        NewsletterSubscriptor subscriptor = subscriptorForEmail(subscriptionDTO.getSubscriptorEmail(), themeDisplay);
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
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setGroupid(themeDisplay.getScopeGroupId());
        subscription.setCompanyid(themeDisplay.getCompanyId());
        subscription.setActivationKey(SubscriptionUtil.getUniqueKey());
        subscription.setDeactivationKey(SubscriptionUtil.getUniqueKey());
        sessionFactory.getCurrentSession().save(subscription);
        
        
        String message = bundle.getString("newsletter.registration.register.message.emailregistered");
        message = message.replace("{EMAIL_ADDRESS}", subscriptionDTO.getSubscriptorEmail());
        message = message.replace("{LIST_NAME}", category.getName());

        return ServiceActionResult.buildSuccess(null, message);
    }
}
