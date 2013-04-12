package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
    private SessionFactory sessionFactory;
    private final static Log logger = LogFactoryUtil.getLog(NewsletterSubscriptionImpl.class);

    @Override
    public List<NewsletterSubscription> findBySubscriptor(NewsletterSubscriptor newsletterSubscriptor) {
        List<NewsletterSubscription> result = new ArrayList<NewsletterSubscription>();
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
            criteria.add(Restrictions.eq(NewsletterSubscription.SUBSCRIPTOR, newsletterSubscriptor));
            result = criteria.list();

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
