package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import java.util.List;
import java.util.Set;
import javax.validation.Validator;
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
public class NewsletterSubscriptionImpl implements NewsletterSubscriptionService {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private Validator validator;
    
    private final static Logger logger = LoggerFactory.getLogger(NewsletterSubscriptionImpl.class);

    @Override
    public boolean addNewsletterSubscription(NewsletterSubscription newsletterSubscription) {
        boolean result = true;
        Set violations = validator.validate(newsletterSubscription);

        if (!violations.isEmpty()) {
            return false;
        }

        sessionFactory.getCurrentSession().save(newsletterSubscription);

        return result;
    }
    
    @Override
    public boolean updateNewsletterSubscription(NewsletterSubscription newsletterSubscription) {
        boolean result = true;
        Set violations = validator.validate(newsletterSubscription);

        if (!violations.isEmpty()) {
            return false;
        }

        sessionFactory.getCurrentSession().update(newsletterSubscription);

        return result;
    }
    
    @Override
    public List<NewsletterSubscription> findSubscriptionByKey(String s) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NewsletterSubscription.class);
        
        criteria.add(Restrictions.or(Restrictions.eq(NewsletterSubscription.CONFIRMATION_KEY, s),
                Restrictions.eq(NewsletterSubscription.CANCELLATION_KEY, s)));
        
        return criteria.list();
    }

    @Override
    public List<NewsletterSubscription> findAllNewsletterSubscriptions() {
        return sessionFactory.getCurrentSession().createCriteria(NewsletterSubscription.class).list();
    }

    @Override
    public boolean deleteNewsletterSubscription(long newsletterSubscriptionId) {
        NewsletterSubscription newsletterSubscription = 
                (NewsletterSubscription) sessionFactory.getCurrentSession().get(
                NewsletterSubscription.class, newsletterSubscriptionId);
        
        if (newsletterSubscription == null) {
            return false;
        }
        
        sessionFactory.getCurrentSession().delete(newsletterSubscription);

        return true;
    }

    @Override
    public NewsletterSubscription findById(long newsletterSubscriptionId) {
        NewsletterSubscription result = null;
        
        return result;
    }

    @Override
    public NewsletterSubscription findBySubscriptor(NewsletterSubscriptor newsletterSubscriptor) {
        NewsletterSubscription result = null;
        
        try {
            
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
            criteria.add(Restrictions.eq(NewsletterSubscription.SUBSCRIPTOR, newsletterSubscriptor));
            result = (NewsletterSubscription) criteria.uniqueResult();
            
        } catch (NonUniqueResultException ex) {
            String error = "Exists more than unique email";
            logger.error(error);
        }        
        
        return result;
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
