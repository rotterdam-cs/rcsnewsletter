
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.dtos.NewsletterCategoryDTO;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterCategoryService extends CRUDService<NewsletterCategory> {
    
    NewsletterCategory findByKey(String categoryKey);
    
    ServiceActionResult<ListResultsDTO<NewsletterCategoryDTO>> findAllNewsletterCategories(ThemeDisplay themeDisplay, int start, int limit);
    
    List<NewsletterCategory> findNewsletterCategorysBySubscriber(NewsletterSubscriptor subscriptor);
    
}
