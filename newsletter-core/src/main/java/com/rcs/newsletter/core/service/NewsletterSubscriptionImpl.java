package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import java.util.List;
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
    private final static Logger logger = LoggerFactory.getLogger(NewsletterSubscriptionImpl.class);

    @Override
    public List<NewsletterSubscription> findSubscriptionByKey(String s) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NewsletterSubscription.class);

        criteria.add(Restrictions.or(Restrictions.eq(NewsletterSubscription.CONFIRMATION_KEY, s),
                Restrictions.eq(NewsletterSubscription.CANCELLATION_KEY, s)));

        return criteria.list();
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
