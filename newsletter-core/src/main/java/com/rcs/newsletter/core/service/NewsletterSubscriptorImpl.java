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
import edu.emory.mathcs.backport.java.util.Collections;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
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

    private Criteria createCriteriaForStatusAndCategory(ThemeDisplay themeDisplay, SubscriptionStatus status, long categoryId, boolean addRowCountProjection){
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
        
        int count = findAllByStatusAndCategoryCount(themeDisplay, status, categoryId);
        
        
        String hql = "SELECT min(s.id) FROM NewsletterSubscription s INNER JOIN s.subscriptor o INNER JOIN s.category c WHERE s.companyid=? AND s.groupid=? ";
        if (status != null){
            hql += " AND s.status = ? ";
        }
        if (categoryId != 0){
            hql += " AND c.id = ? ";
        }        
        hql += " GROUP BY o.id ";

        if (!ordercrit.isEmpty()) {
            hql += " ORDER BY " + getActualOrderFieldForHQL(ordercrit);
            if (NewsletterConstants.ORDER_BY_DESC.equals(order)) {
                hql += " DESC";
            } else {
                hql += " ASC";
            }
        }
        
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
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
        List<Long> subscriptionIds = query.list();
        
        List<NewsletterSubscription> newsletterSubscription;
        if (subscriptionIds.isEmpty()){
            newsletterSubscription = Collections.emptyList();
        }else{
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NewsletterSubscription.class);
            criteria.add(Restrictions.in(NewsletterSubscription.ID, subscriptionIds));
            if (!ordercrit.isEmpty()) {
                String sortField = getActualOrderFieldForCriteria(ordercrit);
                if (NewsletterConstants.ORDER_BY_DESC.equals(order)) {
                    criteria.addOrder(Order.desc(sortField));
                } else {
                    criteria.addOrder(Order.asc(sortField));
                }
            }
            newsletterSubscription = criteria.list();
        }

        ListResultsDTO<NewsletterSubscriptionDTO> payload = new ListResultsDTO(limit, start, count, 
                binder.bindFromBusinessObjectList(NewsletterSubscriptionDTO.class, newsletterSubscription));

        return ServiceActionResult.buildSuccess(payload);
    }

    private String getActualOrderFieldForHQL(String requestedField){
        if ("subscriptorId".equalsIgnoreCase(requestedField)){
            return "o.id";
        }else{
            return requestedField;
        }
    }
    
    private String getActualOrderFieldForCriteria(String requestedField){
        if ("subscriptorId".equalsIgnoreCase(requestedField)){
            return "subscriptor.id";
        }else{
            return requestedField;
        }
    }
    
    @Override
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
