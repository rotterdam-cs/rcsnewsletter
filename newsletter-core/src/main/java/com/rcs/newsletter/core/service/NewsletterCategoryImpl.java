
package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Service
@Transactional
public class NewsletterCategoryImpl extends CRUDServiceImpl<NewsletterCategory> implements NewsletterCategoryService {

    @Autowired
    private SessionFactory sessionFactory;
    
    private final static Logger logger = LoggerFactory.getLogger(NewsletterSubscriptionImpl.class);
    
    @Override
    public NewsletterCategory findByKey(String categoryKey) {
        NewsletterCategory result = null;
        
        try {            
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterCategory.class);
            criteria.add(Restrictions.eq(NewsletterCategory.CATEGORY_KEY, categoryKey));
            result = (NewsletterCategory) criteria.uniqueResult();
            
        } catch (NonUniqueResultException ex) {
            String error = "Exists more than unique id";
            logger.error(error);
        }        
        
        return result;
    }

    @Override
    public List<NewsletterCategory> findAllNewsletterCategorys(boolean fetchSubscriptors) {
        List<NewsletterCategory> result = sessionFactory.getCurrentSession().createCriteria(NewsletterCategory.class).list();
        
        if(fetchSubscriptors) {
            for(NewsletterCategory newsletterCategory : result) {
                newsletterCategory.setSubscriptions(getNewsletterSubscriptionsByCategoryId(newsletterCategory));
            }
        }
        
        return result;
    }
    
    /**
     * Obtain all the subscriptors for the specified category id
     * @param categoryId
     * @return 
     */
    private Set<NewsletterSubscription> getNewsletterSubscriptionsByCategoryId(NewsletterCategory newsletterCategory) {
        Criteria subscriptionCriteria = sessionFactory.getCurrentSession().createCriteria(NewsletterSubscription.class);
        subscriptionCriteria.add(Restrictions.eq(NewsletterSubscription.CATEGORY, newsletterCategory));

        List<NewsletterSubscription> subscriptions = subscriptionCriteria.list();

        return new HashSet(subscriptions);
    }
}