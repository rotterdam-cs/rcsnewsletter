
package com.rcs.newsletter.core.service;

import com.rcs.newsletter.NewsletterConstants;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterEntity;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
    public ServiceActionResult<NewsletterSubscriptor> findByEmail(String email) {
        boolean success = true;
        List<String> validationKeys = new ArrayList<String>();
        NewsletterSubscriptor newsletterSubscriptor = null;
        
        try {            
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscriptor.class);
            criteria.add(Restrictions.eq(NewsletterSubscriptor.EMAIL, email));            
            
            Object uniqueObject = criteria.uniqueResult();
            
            if(uniqueObject != null) {
                newsletterSubscriptor = (NewsletterSubscriptor) uniqueObject;
            } else {
                success = false;
            }
        } catch (NonUniqueResultException ex) {
            String error = "Exists more than unique email";
            logger.error(error);
            success = false;
        }        
        
        ServiceActionResult<NewsletterSubscriptor> result = 
                new ServiceActionResult<NewsletterSubscriptor>(success, newsletterSubscriptor, validationKeys);
        
        return result;
    }

    @Override
    public List<NewsletterSubscriptor> findByCategory(NewsletterCategory newsletterCategory) {
       return findByCategory(newsletterCategory, -1, -1);
    }    
    
    @Override
    public List<NewsletterSubscriptor> findByCategory(NewsletterCategory newsletterCategory, int start, int limit) {
       return findByCategory(newsletterCategory, -1, -1, "", "");
    }
    
    @Override
    public List<NewsletterSubscriptor> findByCategory(NewsletterCategory newsletterCategory, int start, int limit, String ordercrit, String order) {
       return findByCategoryAndStatus(newsletterCategory, -1, -1, "", "", null);
    }
    
    @Override
    public List<NewsletterSubscriptor> findByCategoryAndStatus(NewsletterCategory newsletterCategory, int start, int limit, String ordercrit, String order, SubscriptionStatus status) {
        List <NewsletterSubscriptor> result = new ArrayList<NewsletterSubscriptor>();        
        List<NewsletterSubscription> newsletterSubscription = new ArrayList<NewsletterSubscription>();
        try {            
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
            criteria.add(Restrictions.eq(NewsletterSubscription.CATEGORY, newsletterCategory));
            
            if (status != null) {
                criteria.add(Restrictions.sqlRestriction("status = '" + status.toString() + "'"));
            }
            
            if (start != -1) {
                criteria.setFirstResult(start);
            }
            if (limit != -1) {
                criteria.setMaxResults(limit);
            }
            if (!ordercrit.isEmpty()) {                
                if (NewsletterConstants.ORDER_BY_DESC.equals(order)) {
                    criteria.addOrder(Order.desc(ordercrit)); 
                } else {
                    criteria.addOrder(Order.asc(ordercrit)); 
                }
            }
            
            newsletterSubscription = criteria.list();                    
            for (NewsletterSubscription sls : newsletterSubscription) {
                result.add(sls.getSubscriptor());
            }            
        } catch (NonUniqueResultException ex) {
            String error = "Error loading Subscriptor by Category" + ex;
            logger.error(error);
        }   
        
        return result;
    }

    @Override
    public int findByCategoryCount(NewsletterCategory newsletterCategory) {
        int result = 0;
        try {            
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
            criteria.add(Restrictions.eq(NewsletterSubscription.CATEGORY, newsletterCategory));            
            result = criteria.list().size();                    
                        
        } catch (NonUniqueResultException ex) {
            String error = "Error loading Subscriptor by Category Count" + ex;
            logger.error(error);
        }   
        return result;
    }
    
    @Override
    public int findByCategoryAndStatusCount(NewsletterCategory newsletterCategory, SubscriptionStatus status) {
        int result = 0;
        try {            
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
            criteria.add(Restrictions.eq(NewsletterSubscription.CATEGORY, newsletterCategory)); 
            if (status != null) {
                criteria.add(Restrictions.sqlRestriction("status = '" + status.toString() + "'"));
            }
            result = criteria.list().size();                    
                        
        } catch (NonUniqueResultException ex) {
            String error = "Error loading Subscriptor by Category Count" + ex;
            logger.error(error);
        }   
        return result;
    }
    
    @Override
    public List<NewsletterSubscriptor> findAllByStatus(int start, int limit, String ordercrit, String order, SubscriptionStatus status) {        
        List <NewsletterSubscriptor> result = new ArrayList<NewsletterSubscriptor>();        
        List<NewsletterSubscription> newsletterSubscription = new ArrayList<NewsletterSubscription>();
        try {            
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
            
            if (status != null) {
                criteria.add(Restrictions.sqlRestriction("status = '" + status.toString() + "'"));
            }
            
            if (start != -1) {
                criteria.setFirstResult(start);
            }
            if (limit != -1) {
                criteria.setMaxResults(limit);
            }
            if (!ordercrit.isEmpty()) {                
                if (NewsletterConstants.ORDER_BY_DESC.equals(order)) {
                    criteria.addOrder(Order.desc(ordercrit)); 
                } else {
                    criteria.addOrder(Order.asc(ordercrit)); 
                }
            }
            
            newsletterSubscription = criteria.list();                    
            for (NewsletterSubscription sls : newsletterSubscription) {
                result.add(sls.getSubscriptor());
            }            
        } catch (NonUniqueResultException ex) {
            String error = "Error loading Subscriptor by Category" + ex;
            logger.error(error);
        }   
        
        return result;
        
    }

    @Override
    public int findAllByStatusCount(SubscriptionStatus status) {
        int result = 0;
        try {            
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
            if (status != null) {
                criteria.add(Restrictions.sqlRestriction("status = '" + status.toString() + "'"));
            }
            result = criteria.list().size();                    
                        
        } catch (NonUniqueResultException ex) {
            String error = "Error in findAllCount" + ex;
            logger.error(error);
        }   
        return result;
    }
    
    
}
