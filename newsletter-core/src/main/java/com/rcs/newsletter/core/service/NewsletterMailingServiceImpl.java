package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.util.LiferayMailingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${newsletter.mail.from}")
    private String fromEmailAddress;
    private static final Logger logger = LoggerFactory.getLogger(NewsletterMailingServiceImpl.class);

    @Async
    @Override
    public void sendTestMailing(Long mailingId, String testEmail, ThemeDisplay themeDisplay) {
        NewsletterMailing mailing = findById(mailingId).getPayload();
        mailingUtil.sendArticleByEmail(mailing.getArticleId(), themeDisplay, testEmail, fromEmailAddress);
    }

    @Async
    @Override
    public void sendMailing(Long mailingId, ThemeDisplay themeDisplay) {
        try {
            NewsletterMailing mailing = findById(mailingId).getPayload();
            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(mailing.getArticleId()); 
            
            for (NewsletterSubscription newsletterSubscription : mailing.getList().getSubscriptions()) {
                if(newsletterSubscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
                    mailingUtil.sendArticleByEmail(ja, themeDisplay, newsletterSubscription.getSubscriptor().getEmail(), fromEmailAddress);
                }
            }
        } catch (Exception ex) {
            logger.error("Error while trying to read article", ex);
        }
    }
    
    
}
