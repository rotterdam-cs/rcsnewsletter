package com.rcs.newsletter.util;

import com.google.gson.Gson;
import com.rcs.newsletter.core.json.GsonExclusionStrategy;
import com.rcs.newsletter.core.model.NewsletterEntity;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class JsonEntityBuilder extends JsonBuilder {

    private NewsletterEntity newsletterEntity;

    public NewsletterEntity getNewsletterEntity() {
        return newsletterEntity;
    }

    public void setNewsletterEntity(NewsletterEntity newsletterEntity) {
        this.newsletterEntity = newsletterEntity;
    }

    @Override
    public String toString() {
        Gson gson;
        NewsletterEntity entity = getNewsletterEntity();

        if (entity != null) {
            gson = JsonUtil.createGsonFromBuilder(
                    new GsonExclusionStrategy(getNewsletterEntity().getClass()));
        } else {
            gson = new Gson();
        }

        return gson.toJson(this);
    }
}
