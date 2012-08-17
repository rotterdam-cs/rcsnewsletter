package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.NewsletterCategoryDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterCategoryService extends CRUDService<NewsletterCategory> {

    NewsletterCategory findByKey(String categoryKey);

    ServiceActionResult<ListResultsDTO<NewsletterCategoryDTO>> findAllNewsletterCategories(ThemeDisplay themeDisplay, GridForm gridForm);
    List<NewsletterCategoryDTO> findAllNewsletterCategories(ThemeDisplay themeDisplay);

    List<NewsletterCategory> findNewsletterCategorysBySubscriber(NewsletterSubscriptor subscriptor);

    ServiceActionResult<NewsletterCategoryDTO> getCategoryDTO(long categoryId);
    
    ServiceActionResult<NewsletterCategoryDTO> createCategory(long groupId, long companyId, String name, String description, String fromname, String fromemail, String adminemail);
    ServiceActionResult<NewsletterCategoryDTO> editCategory(long categoryId, String name, String description, String fromname, String fromemail, String adminemail);
    ServiceActionResult deleteCategory(long categoryId);
    
    ServiceActionResult setCategoryGreetingEmailContent(long categoryId, String content);
    ServiceActionResult setCategorySubscribeEmailContent(long categoryId, String content);
    ServiceActionResult setCategoryUnsubscribeEmailContent(long categoryId, String content);
}
