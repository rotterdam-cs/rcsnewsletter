
package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterSubscriptor;
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
public class NewsletterSubscriptorImpl extends CRUDServiceImpl<NewsletterSubscriptor> implements NewsletterSubscriptorService {

    @Autowired
    private SessionFactory sessionFactory;
    
    private final static Logger logger = LoggerFactory.getLogger(NewsletterSubscriptionImpl.class);

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
