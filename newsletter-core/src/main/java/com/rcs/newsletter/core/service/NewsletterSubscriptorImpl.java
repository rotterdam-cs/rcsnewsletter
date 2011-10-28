
package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterSubscriptor;
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
public class NewsletterSubscriptorImpl implements NewsletterSubscriptorService {

    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private Validator validator;
    
    private final static Logger logger = LoggerFactory.getLogger(NewsletterSubscriptionImpl.class);
    
    @Override
    public boolean addNewsletterSubscriptor(NewsletterSubscriptor newsletterSubscriptor) {
        boolean result = true;
        Set violations = validator.validate(newsletterSubscriptor);

        if (!violations.isEmpty()) {
            return false;
        }

        sessionFactory.getCurrentSession().save(newsletterSubscriptor);

        return result;
    }

    @Override
    public boolean updateNewsletterSubscriptor(NewsletterSubscriptor newsletterSubscriptor) {
        boolean result = true;
        Set violations = validator.validate(newsletterSubscriptor);

        if (!violations.isEmpty()) {
            return false;
        }

        sessionFactory.getCurrentSession().update(newsletterSubscriptor);

        return result;
    }

    @Override
    public NewsletterSubscriptor findByEmail(String email) {
        NewsletterSubscriptor result = null;
        
        try {
            
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscriptor.class);
            criteria.add(Restrictions.eq(NewsletterSubscriptor.EMAIL, email));            
            
            result = (NewsletterSubscriptor) criteria.uniqueResult();
            
        } catch (NonUniqueResultException ex) {
            String error = "Exists more than unique email";
            logger.error(error);
        }        
        
        return result;
    }
    
}
