package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.NewsletterConstants;
import com.rcs.newsletter.core.model.NewsletterEntity;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.dtos.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.hibernate.Criteria;
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
public class NewsletterSubscriptorImpl extends CRUDServiceImpl<NewsletterSubscriptor> implements NewsletterSubscriptorService {

    @Autowired
    private NewsletterSubscriptionService subscriptionService;

    @Autowired
    private DTOBinder binder;

    /*@Override
    public ServiceActionResult<NewsletterSubscriptor> findByEmail(ThemeDisplay themeDisplay, String email) {
        boolean success = true;
        List<String> validationKeys = new ArrayList<String>();
        NewsletterSubscriptor newsletterSubscriptor = null;
        try {
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(NewsletterSubscriptor.class);
            criteria.add(Restrictions.eq(NewsletterSubscriptor.EMAIL, email));            
            
            criteria.add(Restrictions.eq(NewsletterEntity.COMPANYID, themeDisplay.getCompanyId()));        
            criteria.add(Restrictions.eq(NewsletterEntity.GROUPID, themeDisplay.getScopeGroupId()));
            
            Object uniqueObject = criteria.uniqueResult();
            if(uniqueObject != null) {
                newsletterSubscriptor = (NewsletterSubscriptor) uniqueObject;
            } else {
                success = false;
            }
        } catch (NonUniqueResultException ex) {
            String error = "Exists more than unique email";            
            log.error(error + ex);
            success = false;
        }        
        
        ServiceActionResult<NewsletterSubscriptor> result = 
                new ServiceActionResult<NewsletterSubscriptor>(success, newsletterSubscriptor, validationKeys);
        return result;
    }*/

    private Criteria createCriteriaForStatusAndCategory(ThemeDisplay themeDisplay, SubscriptionStatus status, long categoryId){
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NewsletterSubscription.class);
        criteria.add(Restrictions.eq(NewsletterEntity.COMPANYID, themeDisplay.getCompanyId()));
        criteria.add(Restrictions.eq(NewsletterEntity.GROUPID, themeDisplay.getScopeGroupId()));
        criteria.createAlias(NewsletterSubscription.SUBSCRIPTOR, "subscriptor");
        if (status != null) {
            criteria.add(Restrictions.eq(NewsletterSubscription.STATUS, status));
        }
        if (categoryId > 0){
            criteria.createCriteria(NewsletterSubscription.CATEGORY).add(Restrictions.idEq(categoryId));
        }
        return criteria;
    }
    
    @Override
    public ServiceActionResult<ListResultsDTO<NewsletterSubscriptionDTO>> findAllByStatusAndCategory(
            ThemeDisplay themeDisplay, 
            int start, int limit, String ordercrit, String order,
            SubscriptionStatus status, long categoryId) {
        
        List<NewsletterSubscription> newsletterSubscription;
       
        int count = findAllByStatusAndCategoryCount(themeDisplay, status, categoryId);

        Criteria criteria = createCriteriaForStatusAndCategory(themeDisplay, status, categoryId);
        if (start != -1) {
            criteria.setFirstResult(start);
        }
        if (limit != -1) {
            criteria.setMaxResults(limit);
        }
        if (!ordercrit.isEmpty()) {                
            if (NewsletterConstants.ORDER_BY_DESC.equals(order)) {
                criteria.addOrder(Order.desc(getActualOrderField(ordercrit))); 
            } else {
                criteria.addOrder(Order.asc(getActualOrderField(ordercrit))); 
            }
        }
        newsletterSubscription = criteria.list();
        ListResultsDTO<NewsletterSubscriptionDTO> payload = new ListResultsDTO(limit, start, count, 
                binder.bindFromBusinessObjectList(NewsletterSubscriptionDTO.class, newsletterSubscription));

        return ServiceActionResult.buildSuccess(payload);
    }

    private String getActualOrderField(String requestedField){
        if ("subscriptorId".equalsIgnoreCase(requestedField)){
            return "subscriptor.id";
        }else{
            return requestedField;
        }
    }
    
    @Override
    public int findAllByStatusAndCategoryCount(ThemeDisplay themeDisplay, SubscriptionStatus status, long categoryId) {
        Criteria criteria = createCriteriaForStatusAndCategory(themeDisplay, status, categoryId);
        criteria.setProjection(Projections.rowCount());
        criteria.setMaxResults(1);
        int count = ((Long)criteria.uniqueResult()).intValue();
        return count;
    }


    @Override
    public ServiceActionResult updateSubscriptor(long subscriptorId, String firstName, String lastName, String email) {
        ServiceActionResult<NewsletterSubscriptor> sarSubscriptor = findById(subscriptorId);
        if (!sarSubscriptor.isSuccess()){
            return sarSubscriptor;
        }
        NewsletterSubscriptor subscriptor = sarSubscriptor.getPayload();
        
        NewsletterSubscriptor dummySubscriptor = new NewsletterSubscriptor();
        dummySubscriptor.setFirstName(firstName);
        dummySubscriptor.setLastName(lastName);
        dummySubscriptor.setEmail(email);
        
        Set violations = validator.validate(dummySubscriptor);
        if (violations.isEmpty()){
            subscriptor.setFirstName(firstName);
            subscriptor.setLastName(lastName);
            subscriptor.setEmail(email);
            return update(subscriptor);
        }
        
        List<String> violationsKeys = new LinkedList<String>();
        fillViolations(violations, violationsKeys);
        return ServiceActionResult.buildFailure(null, violationsKeys);
    }

    @Override
    public ServiceActionResult deleteSubscriptor(long subscriptorId) {
        ServiceActionResult<NewsletterSubscriptionDTO> sarSubscriptionDTO = subscriptionService.findSubscriptionBySubscriptorId(subscriptorId);
        if (!sarSubscriptionDTO.isSuccess()){
            return sarSubscriptionDTO;
        }
        
        ServiceActionResult<NewsletterSubscription> sarSubscription = subscriptionService.findById(sarSubscriptionDTO.getPayload().getId());
        if (!sarSubscription.isSuccess()){
            return sarSubscription;
        }
        
        ServiceActionResult<NewsletterSubscription> sarDeleteSubscription = subscriptionService.delete(sarSubscription.getPayload());
        if (!sarSubscription.isSuccess()){
            return sarDeleteSubscription;
        }
        
        ServiceActionResult<NewsletterSubscriptor> sarSubscriptor = findById(subscriptorId);
        if (!sarSubscriptor.isSuccess()){
            return sarSubscriptor;
        }
        NewsletterSubscriptor subscriptor = sarSubscriptor.getPayload();
        return delete(subscriptor);
    }
}
