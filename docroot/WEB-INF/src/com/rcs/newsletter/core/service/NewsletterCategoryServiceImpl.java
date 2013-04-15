
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterEntity;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
import com.rcs.newsletter.core.dto.NewsletterCategoryDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.forms.jqgrid.GridRestrictionsUtil;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
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
    
    private Log logger = LogFactoryUtil.getLog(NewsletterCategoryServiceImpl.class);
          
    @Autowired
    private DTOBinder binder;
    
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

    private Criteria createCriteriaForCategories(ThemeDisplay themeDisplay, GridForm gridForm){
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterCategory.class);
        criteria.add(Restrictions.eq(NewsletterEntity.COMPANYID, themeDisplay.getCompanyId()));        
        criteria.add(Restrictions.eq(NewsletterEntity.GROUPID, themeDisplay.getScopeGroupId()));
        
        // add search filters
        if (gridForm != null){
            Criterion criterion = GridRestrictionsUtil.createCriterion(gridForm.getFiltersForm());
            if (criterion != null){
                criteria.add(criterion);
            }
        }
        
        return criteria;
    }
    
    public ServiceActionResult<ListResultsDTO<NewsletterCategoryDTO>> findAllNewsletterCategories(ThemeDisplay themeDisplay, GridForm gridForm) {

        Criteria criteriaForCount = createCriteriaForCategories(themeDisplay, gridForm);
        criteriaForCount.setProjection(Projections.rowCount());
        criteriaForCount.setMaxResults(1);
        
        int count = ((Long)criteriaForCount.uniqueResult()).intValue();
        
        Criteria criteria = createCriteriaForCategories(themeDisplay, gridForm);
        
        List<NewsletterCategory> result = criteria.list();

        ListResultsDTO<NewsletterCategoryDTO> payload = new ListResultsDTO(gridForm.getRows(), gridForm.calculateStart(), count, binder.bindFromBusinessObjectList(NewsletterCategoryDTO.class, result));
        return ServiceActionResult.buildSuccess(payload);
    }
    
    public List<NewsletterCategoryDTO> findAllNewsletterCategories(ThemeDisplay themeDisplay) {
        Criteria criteria = createCriteriaForCategories(themeDisplay, null);
        criteria.addOrder(Order.asc("name"));
        List<NewsletterCategory> result = criteria.list();
        List<NewsletterCategoryDTO> listDTO = binder.bindFromBusinessObjectList(NewsletterCategoryDTO.class, result);
        return listDTO;
    }
    
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
        if (newsletterCategory.getSubscriptionEmail() == null){
        	newsletterCategory.setSubscriptionEmail("");
        }
        if (newsletterCategory.getUnsubscriptionEmail() == null){
        	newsletterCategory.setUnsubscriptionEmail("");
        }
        if (newsletterCategory.getGreetingEmail() == null){
        	newsletterCategory.setGreetingEmail("");
        }
    }
    
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

    public ServiceActionResult deleteCategory(long categoryId) {
        ServiceActionResult<NewsletterCategory> sarCategory = findById(categoryId);
        if (!sarCategory.isSuccess()){
            return sarCategory;
        }
        
        
        NewsletterCategory newsletterCategory = sarCategory.getPayload();
        
        Session currentSession = sessionFactory.getCurrentSession();
        
        // delete mailings
        for(NewsletterMailing m: newsletterCategory.getMailings()){
        	if (m != null){
        		currentSession.delete(m);
        	}
        	
        	// delete template blocks
        	for(NewsletterTemplateBlock b: m.getBlocks()){
        		if (b != null){
        			currentSession.delete(b);
        		}
        	}
        	
        }
        
        // delete subscriptions
        for(NewsletterSubscription s: newsletterCategory.getSubscriptions()){
        	if (s != null){
        		currentSession.delete(s);
        	}
        }
        
        return delete(newsletterCategory);
    }

    public ServiceActionResult<NewsletterCategoryDTO> getCategoryDTO(long categoryId) {
        ServiceActionResult<NewsletterCategory> sarCategory = findById(categoryId);
        if (!sarCategory.isSuccess()){
            return ServiceActionResult.buildFailure(null, sarCategory.getValidationKeys());
        }
        return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterCategoryDTO.class, sarCategory.getPayload()));
    }

    public ServiceActionResult setCategoryGreetingEmailContent(long categoryId, String content) {
        ServiceActionResult<NewsletterCategory> sarCategory = findById(categoryId);
        if (!sarCategory.isSuccess()){
            return sarCategory;
        }
        sarCategory.getPayload().setGreetingEmail(content);
        return ServiceActionResult.buildSuccess(null);
    }

    public ServiceActionResult setCategorySubscribeEmailContent(long categoryId, String content) {
        ServiceActionResult<NewsletterCategory> sarCategory = findById(categoryId);
        if (!sarCategory.isSuccess()){
            return sarCategory;
        }
        sarCategory.getPayload().setSubscriptionEmail(content);
        return ServiceActionResult.buildSuccess(null);
    }

    public ServiceActionResult setCategoryUnsubscribeEmailContent(long categoryId, String content) {
        ServiceActionResult<NewsletterCategory> sarCategory = findById(categoryId);
        if (!sarCategory.isSuccess()){
            return sarCategory;
        }
        sarCategory.getPayload().setUnsubscriptionEmail(content);
        return ServiceActionResult.buildSuccess(null);
    }
    
}
