package com.rcs.newsletter.core.model.commons;

import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
import java.util.Comparator;

public class TemplateBlockComparator implements Comparator<NewsletterTemplateBlock> {

    @Override
    public int compare(NewsletterTemplateBlock a1, NewsletterTemplateBlock a2) {
        int result = 0;

        if (a1.getBlockOrder() < (a2.getBlockOrder())) {
            result = -1;
        } else if (a1.getBlockOrder() > (a2.getBlockOrder())) {
            result = 1;
        }

        return result;
    }
}
