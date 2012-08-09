package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.dtos.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
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
    
    @Override
    public ServiceActionResult<NewsletterSubscriptionDTO> findSubscriptionBySubscriptorId(long subscriptorId) {
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
        criteria.createCriteria(NewsletterSubscription.SUBSCRIPTOR).add(Restrictions.idEq(subscriptorId));
        criteria.setMaxResults(1);
        NewsletterSubscription subscription = (NewsletterSubscription) criteria.uniqueResult();
        if (subscription == null){
            return ServiceActionResult.buildFailure(null, "Could not find the subscription");
        }
        return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterSubscriptionDTO.class, subscription));        
    }
    
    @Override
    public NewsletterSubscription findBySubscriptorAndCategory(NewsletterSubscriptor newsletterSubscriptor, NewsletterCategory newsletterCategory) {
        NewsletterSubscription result = null;

        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
            criteria.add(Restrictions.eq(NewsletterSubscription.SUBSCRIPTOR, newsletterSubscriptor));
            criteria.add(Restrictions.eq(NewsletterSubscription.CATEGORY, newsletterCategory));

            result = (NewsletterSubscription) criteria.uniqueResult();

        } catch (NonUniqueResultException ex) {
            String error = "Exists more than unique email";
            logger.error(error);
        } catch (Exception ex) {
            logger.warn("error", ex);
        }

        return result;
    }
}
