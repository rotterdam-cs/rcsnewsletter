package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.service.util.LiferayMailingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author juan
 */
@Service
@Transactional
class NewsletterMailingServiceImpl extends CRUDServiceImpl<NewsletterMailing> implements NewsletterMailingService {

    @Autowired
    private LiferayMailingUtil mailingUtil;
    //TODO - INJECT PROPERTY
    private String fromEmailAddress = "nobody@rotterdam-cs.com";
    private static final Logger logger = LoggerFactory.getLogger(NewsletterMailingServiceImpl.class);

    @Async
    @Override
    public void sendTestMailing(Long mailingId, String testEmail) {
        NewsletterMailing mailing = findById(mailingId).getPayload();
        mailingUtil.sendArticleByEmail(mailing.getArticleId(), testEmail, fromEmailAddress);
    }

    @Async
    @Override
    public void sendMailing(Long mailingId) {
        NewsletterMailing mailing = findById(mailingId).getPayload();
        for (NewsletterSubscription newsletterSubscription : mailing.getList().getSubscriptions()) {
            mailingUtil.sendArticleByEmail(mailing.getArticleId(), newsletterSubscription.getSubscriptor().getEmail(), fromEmailAddress);
        }
    }
}
