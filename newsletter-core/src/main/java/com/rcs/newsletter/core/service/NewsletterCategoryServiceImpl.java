
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterEntity;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.dtos.NewsletterCategoryDTO;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jdto.DTOBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Service
@Transactional
public class NewsletterCategoryServiceImpl extends CRUDServiceImpl<NewsletterCategory> implements NewsletterCategoryService {
    
    @Autowired
    private DTOBinder binder;
    
    @Override
    public NewsletterCategory findByKey(String categoryKey) {
        NewsletterCategory result = null;
        
        try {            
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterCategory.class);
            criteria.add(Restrictions.eq(NewsletterCategory.CATEGORY_KEY, categoryKey));
            result = (NewsletterCategory) criteria.uniqueResult();
            
        } catch (NonUniqueResultException ex) {
            String error = "Exists more than unique id";
            logger.error(error);
        }        
        
        return result;
    }

    private Criteria createCriteriaForCategories(ThemeDisplay themeDisplay){
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterCategory.class);
        criteria.add(Restrictions.eq(NewsletterEntity.COMPANYID, themeDisplay.getCompanyId()));        
        criteria.add(Restrictions.eq(NewsletterEntity.GROUPID, themeDisplay.getScopeGroupId()));
        return criteria;
    }
    
    @Override
    public ServiceActionResult<ListResultsDTO<NewsletterCategoryDTO>> findAllNewsletterCategories(ThemeDisplay themeDisplay, int start, int limit) {

        Criteria criteriaForCount = createCriteriaForCategories(themeDisplay);
        criteriaForCount.setProjection(Projections.rowCount());
        criteriaForCount.setMaxResults(1);
        int count = ((Long)criteriaForCount.uniqueResult()).intValue();
        
        Criteria criteria = createCriteriaForCategories(themeDisplay);
        
        List<NewsletterCategory> result = criteria.list();

        ListResultsDTO<NewsletterCategoryDTO> payload = 
                new ListResultsDTO(limit, start, count, 
                    binder.bindFromBusinessObjectList(NewsletterCategoryDTO.class, result));
        return ServiceActionResult.buildSuccess(payload);
    }
    
    @Override
    public List<NewsletterCategoryDTO> findAllNewsletterCategories(ThemeDisplay themeDisplay) {
        Criteria criteria = createCriteriaForCategories(themeDisplay);
        criteria.addOrder(Order.asc("name"));
        List<NewsletterCategory> result = criteria.list();
        List<NewsletterCategoryDTO> listDTO = binder.bindFromBusinessObjectList(NewsletterCategoryDTO.class, result);
        return listDTO;
    }
    
    @Override
    public List<NewsletterCategory> findNewsletterCategorysBySubscriber(NewsletterSubscriptor subscriptor) {
        List<NewsletterCategory> result = new ArrayList<NewsletterCategory>();
        List<NewsletterSubscription> newsletterSubscription;
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscription.class);
            criteria.add(Restrictions.eq(NewsletterSubscription.SUBSCRIPTOR, subscriptor));
            newsletterSubscription = criteria.list();
            for (NewsletterSubscription nls : newsletterSubscription) {
                result.add(nls.getCategory());
            }
        } catch (NonUniqueResultException ex) {
            String error = "Error loading categories by subscriber " + ex;
            logger.error(error);
        }
        
        return result;
    }

    @Override
    public ServiceActionResult<NewsletterCategoryDTO> createCategory(long groupId, long companyId, String name, String description, String fromname, String fromemail, String adminemail) {
        NewsletterCategory newsletterCategory = new NewsletterCategory();
        newsletterCategory.setGroupid(groupId);
        newsletterCategory.setCompanyid(companyId);
        fillCategoryData(newsletterCategory, name, description, fromname, fromemail, adminemail);
        
        Set violations = validator.validate(newsletterCategory);
        if (violations.isEmpty()){
            ServiceActionResult saveResult = save(newsletterCategory);
            return saveResult;
        }else{
            List<String> violationsKeys = new LinkedList<String>();
            fillViolations(violations, violationsKeys);
            return ServiceActionResult.buildFailure(null, violationsKeys);
        }
    }

    private void fillCategoryData(NewsletterCategory newsletterCategory, String name, String description, String fromname, String fromemail, String adminemail){
        newsletterCategory.setName(name);
        newsletterCategory.setDescription(description);
        newsletterCategory.setFromName(fromname);
        newsletterCategory.setFromEmail(fromemail);
        newsletterCategory.setAdminEmail(adminemail);        
    }
    
    @Override
    public ServiceActionResult<NewsletterCategoryDTO> editCategory(long categoryId, String name, String description, String fromname, String fromemail, String adminemail) {
        ServiceActionResult<NewsletterCategory> sarCategory = findById(categoryId);
        if (!sarCategory.isSuccess()){
            return ServiceActionResult.buildFailure(null,sarCategory.getValidationKeys());
        }
        NewsletterCategory newsletterCategory = sarCategory.getPayload();
        
        NewsletterCategory dummyNewsletterCategory = new NewsletterCategory();
        dummyNewsletterCategory.setGroupid(newsletterCategory.getGroupid());
        dummyNewsletterCategory.setCompanyid(newsletterCategory.getCompanyid());
        fillCategoryData(dummyNewsletterCategory, name, description, fromname, fromemail, adminemail);

        Set violations = validator.validate(dummyNewsletterCategory);
        if (violations.isEmpty()){
            fillCategoryData(newsletterCategory, name, description, fromname, fromemail, adminemail);
            ServiceActionResult saveResult = update(newsletterCategory);
            if (saveResult.isSuccess()){
                NewsletterCategoryDTO dto = binder.bindFromBusinessObject(NewsletterCategoryDTO.class, saveResult.getPayload());
                return ServiceActionResult.buildSuccess(dto);
            }else{
                return ServiceActionResult.buildFailure(null, saveResult.getValidationKeys());
            }

        }else{
            List<String> violationsKeys = new LinkedList<String>();
            fillViolations(violations, violationsKeys);
            return ServiceActionResult.buildFailure(null, violationsKeys);
        }
    }

    @Override
    public ServiceActionResult deleteCategory(long categoryId) {
        ServiceActionResult<NewsletterCategory> sarCategory = findById(categoryId);
        if (!sarCategory.isSuccess()){
            return sarCategory;
        }
        NewsletterCategory newsletterCategory = sarCategory.getPayload();
        return delete(newsletterCategory);
    }

    @Override
    public ServiceActionResult<NewsletterCategoryDTO> getCategoryDTO(long categoryId) {
        ServiceActionResult<NewsletterCategory> sarCategory = findById(categoryId);
        if (!sarCategory.isSuccess()){
            return ServiceActionResult.buildFailure(null, sarCategory.getValidationKeys());
        }
        return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterCategoryDTO.class, sarCategory.getPayload()));
    }
    
}
