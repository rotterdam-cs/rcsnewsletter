package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.NewsletterConstants;
import com.rcs.newsletter.core.dto.NewsletterSubscriptionDTO;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.hibernate.SQLQuery;
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
public class NewsletterSubscriptorServiceImpl extends CRUDServiceImpl<NewsletterSubscriptor> implements NewsletterSubscriptorService {

	private Log logger = LogFactoryUtil.getLog(NewsletterSubscriptorServiceImpl.class);
	
    @Autowired
    private NewsletterSubscriptionService subscriptionService;

    @Autowired
    private DTOBinder binder;
    
    public ServiceActionResult<ListResultsDTO<NewsletterSubscriptionDTO>> findAllByStatusAndCategory(
            ThemeDisplay themeDisplay, 
            int start, int limit, String ordercrit, String order,
            SubscriptionStatus status, long categoryId) {
        
        int count = findAllByStatusAndCategoryCount(themeDisplay, status, categoryId);
        
        String sql = "SELECT subscription.* FROM newsletter_subscription subscription "
                   + "WHERE subscription.id IN ( "
                   + "  SELECT min(s.id) FROM newsletter_subscription s "
                   + "      INNER JOIN newsletter_subscriptor o ON (s.subscriptor_id = o.id) "
                   + "      WHERE s.companyid = ? AND s.groupid = ? ";
        if (status != null){
            sql += " AND s.status = ? ";
        }
        if (categoryId != 0){
            sql += " AND category_id = ? ";
        }
        sql += " GROUP BY o.id) ";

        if (!ordercrit.isEmpty()) {
            sql += " ORDER BY " + getActualOrderFieldForSQL(ordercrit);
            if (NewsletterConstants.ORDER_BY_DESC.equals(order)) {
                sql += " DESC";
            } else {
                sql += " ASC";
            }
        }

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        query.addEntity("subscription", NewsletterSubscription.class);
        
        int paramIndex = 0;
        query.setLong(paramIndex++, themeDisplay.getCompanyId());
        query.setLong(paramIndex++, themeDisplay.getScopeGroupId());
        if (status != null){
            query.setString(paramIndex++, status.name());
        }
        if (categoryId != 0){
            query.setLong(paramIndex++, categoryId);
        }
        if (start != -1) {
            query.setFirstResult(start);
        }
        if (limit != -1) {
            query.setMaxResults(limit);
        }
        List<NewsletterSubscription> newsletterSubscription = query.list();

        ListResultsDTO<NewsletterSubscriptionDTO> payload = new ListResultsDTO(limit, start, count, 
                binder.bindFromBusinessObjectList(NewsletterSubscriptionDTO.class, newsletterSubscription));

        return ServiceActionResult.buildSuccess(payload);
    }

    
    public ServiceActionResult<ListResultsDTO<NewsletterSubscriptionDTO>> findAllByStatusAndCategoryAndCriteria(
    		ThemeDisplay themeDisplay 
            ,int start
            ,int limit
            ,String ordercrit
            ,String order 
            ,SubscriptionStatus status
            ,long categoryId
            ,String searchField
            ,String searchString         
    ) {
        
        int count = findAllByStatusAndCategoryCountAndCriteria(themeDisplay, status, categoryId, searchField, searchString);
        
        String sql = "SELECT subscription.* FROM newsletter_subscription subscription "
                   + "WHERE subscription.id IN ( "
                   + "  SELECT min(s.id) FROM newsletter_subscription s "
                   + "      INNER JOIN newsletter_subscriptor o ON (s.subscriptor_id = o.id) "
                   + "      WHERE s.companyid = ? AND s.groupid = ? ";
        if (status != null){
            sql += " AND s.status = ? ";
        }
        if (categoryId != 0){
            sql += " AND category_id = ? ";
        }
        
        if (searchField != null && searchString != null) {
        	if (searchField.equalsIgnoreCase("subscriptoremail")){
        		searchField = "email";
        	}
        	sql += " AND " + searchField + " ILIKE ? ";
        }
        
        sql += " GROUP BY o.id) ";

        if (!ordercrit.isEmpty()) {
            sql += " ORDER BY " + getActualOrderFieldForSQL(ordercrit);
            if (NewsletterConstants.ORDER_BY_DESC.equals(order)) {
                sql += " DESC";
            } else {
                sql += " ASC";
            }
        }

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        query.addEntity("subscription", NewsletterSubscription.class);
        
        int paramIndex = 0;
        query.setLong(paramIndex++, themeDisplay.getCompanyId());
        query.setLong(paramIndex++, themeDisplay.getScopeGroupId());
        if (status != null){
            query.setString(paramIndex++, status.name());
        }
        if (categoryId != 0){
            query.setLong(paramIndex++, categoryId);
        }
        if (searchField != null && searchString != null) {
            query.setString(paramIndex++, "%" + searchString + "%");
        }
        
        if (start != -1) {
            query.setFirstResult(start);
        }
        if (limit != -1) {
            query.setMaxResults(limit);
        }
                
        List<NewsletterSubscription> newsletterSubscription = query.list();

        ListResultsDTO<NewsletterSubscriptionDTO> payload = new ListResultsDTO(limit, start, count, 
                binder.bindFromBusinessObjectList(NewsletterSubscriptionDTO.class, newsletterSubscription));

        return ServiceActionResult.buildSuccess(payload);
    }
    
    
    
    
    
    private String getActualOrderFieldForSQL(String requestedField){
        if ("subscriptorId".equalsIgnoreCase(requestedField)){
            return "subscriptor_id";
        }else{
            return requestedField;
        }
    }
    
    public int findAllByStatusAndCategoryCount(ThemeDisplay themeDisplay, SubscriptionStatus status, long categoryId) {
        String sql = "select count(*) from ( "
                        + "SELECT count(*) FROM newsletter_subscription s INNER JOIN newsletter_subscriptor o ON s.subscriptor_id=o.id "
                        + "WHERE s.companyid = ? AND s.groupid = ? ";
        
        if (status != null){
            sql += " AND s.status = ? ";
        }
        
        if (categoryId != 0){
            sql += " AND s.category_id = ? ";
        }
        
        sql += " GROUP BY o.id) as count_inner_query";
        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        
        int paramIndex = 0;
        query.setLong(paramIndex++, themeDisplay.getCompanyId());
        query.setLong(paramIndex++, themeDisplay.getScopeGroupId());
        
        if (status != null){
            query.setString(paramIndex++, status.name());
        }
        if (categoryId != 0){
            query.setLong(paramIndex++, categoryId);
        }
        return ((BigInteger)query.uniqueResult()).intValue();
    }
    
    
    
    
    
    public int findAllByStatusAndCategoryCountAndCriteria(ThemeDisplay themeDisplay, SubscriptionStatus status, long categoryId, String searchField, String searchString) {
	        String sql = "select count(*) from ( "
	                + "SELECT count(*) FROM newsletter_subscription s INNER JOIN newsletter_subscriptor o ON s.subscriptor_id=o.id "
	                + "WHERE s.companyid = ? AND s.groupid = ? ";
	
			if (status != null){
			    sql += " AND s.status = ? ";
			}
			
			if (categoryId != 0){
			    sql += " AND s.category_id = ? ";
			}
			
			if (searchField != null  && searchString != null) {
				if (searchField.equalsIgnoreCase("subscriptoremail")){
	        		searchField = "email";
	        	}
				sql += " AND " + searchField + " ILIKE ? ";
	        }
			
			sql += " GROUP BY o.id) as count_inner_query";
			SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql);
			
			int paramIndex = 0;
			query.setLong(paramIndex++, themeDisplay.getCompanyId());
			query.setLong(paramIndex++, themeDisplay.getScopeGroupId());
			
			if (status != null){
			    query.setString(paramIndex++, status.name());
			}
			if (categoryId != 0){
			    query.setLong(paramIndex++, categoryId);
			}
			if (searchField != null && searchString != null){
	            query.setString(paramIndex++, "%" + searchString + "%");
	        }
			return ((BigInteger)query.uniqueResult()).intValue();
	}

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

    public ServiceActionResult deleteSubscriptor(long subscriptorId) {
        ServiceActionResult<List<NewsletterSubscriptionDTO>> sarSubscriptionDTO = subscriptionService.findSubscriptionsBySubscriptorId(subscriptorId);
        if (!sarSubscriptionDTO.isSuccess()){
            return sarSubscriptionDTO;
        }
        for (NewsletterSubscriptionDTO subscriptionDTO: sarSubscriptionDTO.getPayload()){
            ServiceActionResult<NewsletterSubscription> sarSubscription = subscriptionService.findById(subscriptionDTO.getId());
            if (!sarSubscription.isSuccess()){
                return sarSubscription;
            }
            
            ServiceActionResult<NewsletterSubscription> sarDeleteSubscription = subscriptionService.delete(sarSubscription.getPayload());
            if (!sarSubscription.isSuccess()){
                return sarDeleteSubscription;
            }
        }
        
        ServiceActionResult<NewsletterSubscriptor> sarSubscriptor = findById(subscriptorId);
        if (!sarSubscriptor.isSuccess()){
            return sarSubscriptor;
        }
        NewsletterSubscriptor subscriptor = sarSubscriptor.getPayload();
        return delete(subscriptor);
    }
}
