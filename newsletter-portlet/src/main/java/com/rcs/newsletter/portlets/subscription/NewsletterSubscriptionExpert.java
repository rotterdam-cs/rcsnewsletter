package com.rcs.newsletter.portlets.subscription;

import com.rcs.newsletter.core.service.NewsletterSubscriptionService;
import com.rcs.newsletter.core.service.util.LiferayMailingUtil;
import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.rcs.newsletter.NewsletterConstants.*;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Component
public class NewsletterSubscriptionExpert {

    @Autowired
    private NewsletterSubscriptionService subscriptionService;

    /**
     * Obtain the unique key for the confirmation email in subscriptions
     * @param email
     * @return 
     */
    public String getUniqueKey(String email) {
        String result = null;

        if (email != null) {
            //generate random registration key
            MessageDigest md;
            try {
                md = MessageDigest.getInstance(ALGORITHM_SHA1);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            result = HexBin.encode(md.digest((email + System.currentTimeMillis() + Math.random()).getBytes())).toUpperCase();

            if (!subscriptionService.findSubscriptionByKey(result).isEmpty()) {
                return getUniqueKey(email);
            }
        }

        return result;
    }

    /**
     * Method that send the notification mail depending if its a subscription
     * or unsubscription
     * @param email
     * @param key
     * @param categoryName
     * @param url
     * @param confirmation
     * @param locale 
     */
    public void sendNotificationEmail(String email, String key, String categoryName, boolean confirmation, Locale locale) {
        String titleKey = "";
        String bodyKey = "";

        if (confirmation) {
            titleKey = "newsletter.subscription.confirmation.email.title";
            bodyKey = "newsletter.subscription.confirmation.email.body";
        } else {
            titleKey = "newsletter.subscription.cancellation.email.title";
            bodyKey = "newsletter.subscription.cancellation.email.body";
        }
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_BASENAME, locale);
        
        String title = resourceBundle.getString(titleKey);
        String body = resourceBundle.getString(bodyKey);        
        
        title = title.replace("{categoryName}", categoryName);
        body = body.replace("{categoryName}", categoryName);
        body = body.replace("{key}", key);
        body = body.replace("{email}", email);
        
        String from = NEWSLETTER_ADMIN;

        LiferayMailingUtil.sendEmail(from, email, title, body);        
    }
}
