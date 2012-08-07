
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
import javax.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jdto.DTOBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private SessionFactory sessionFactory;
    
    @Autowired
    private DTOBinder binder;
    
    @Autowired
    private Validator validator;
    
    private final static Logger logger = LoggerFactory.getLogger(NewsletterSubscriptionImpl.class);
    
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

    @Override
    public ServiceActionResult<ListResultsDTO<NewsletterCategoryDTO>> findAllNewsletterCategories(ThemeDisplay themeDisplay, int start, int limit) {
        Session currentSession = sessionFactory.getCurrentSession();

        Criteria criteriaForCount = currentSession.createCriteria(NewsletterCategory.class);
        criteriaForCount.add(Restrictions.eq(NewsletterEntity.COMPANYID, themeDisplay.getCompanyId()));        
        criteriaForCount.add(Restrictions.eq(NewsletterEntity.GROUPID, themeDisplay.getScopeGroupId()));
        criteriaForCount.setProjection(Projections.rowCount());
        criteriaForCount.setMaxResults(1);
        int count = ((Long)criteriaForCount.uniqueResult()).intValue();
        
        Criteria criteria = currentSession.createCriteria(NewsletterCategory.class);
        criteria.add(Restrictions.eq(NewsletterEntity.COMPANYID, themeDisplay.getCompanyId()));        
        criteria.add(Restrictions.eq(NewsletterEntity.GROUPID, themeDisplay.getScopeGroupId()));
        
        List<NewsletterCategory> result = criteria.list();
        
        /*if(fetchSubscriptors) {
            for(NewsletterCategory newsletterCategory : result) {
                newsletterCategory.setSubscriptions(getNewsletterSubscriptionsByCategoryId(newsletterCategory));
            }
        }*/

        ListResultsDTO<NewsletterCategoryDTO> payload = 
                new ListResultsDTO(limit, start, count, 
                    binder.bindFromBusinessObjectList(NewsletterCategoryDTO.class, result));
        return ServiceActionResult.buildSuccess(payload);
    }
    
    /**
     * Obtain all the subscriptors for the specified category id
     * @param categoryId
     * @return 
     */
    /*private List<NewsletterSubscription> getNewsletterSubscriptionsByCategoryId(NewsletterCategory newsletterCategory) {
        Criteria subscriptionCriteria = sessionFactory.getCurrentSession().createCriteria(NewsletterSubscription.class);
        subscriptionCriteria.add(Restrictions.eq(NewsletterSubscription.CATEGORY, newsletterCategory));

        List<NewsletterSubscription> subscriptions = subscriptionCriteria.list();

        return subscriptions;
    }*/
    
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
        fillCategoryData(newsletterCategory, name, description, fromname, fromemail, adminemail);
        
        Set violations = validator.validate(newsletterCategory);
        if (violations.isEmpty()){
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
