package com.rcs.newsletter.core.service;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.NewsletterConstants;
import com.rcs.newsletter.core.model.NewsletterEntity;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generic CRUD implementation
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Service
@Transactional
public class CRUDServiceImpl<E extends NewsletterEntity> implements CRUDService<E> {

    @Autowired
    protected SessionFactory sessionFactory;
    
    @Autowired
    protected Validator validator;
    
    protected Log logger = LogFactoryUtil.getLog(getClass());

    private Class getEntityClass() {
        Class result = null;

        Type genericSuperClass = this.getClass().getGenericSuperclass();

        if (genericSuperClass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericSuperClass;
            Type[] fieldArgTypes = pt.getActualTypeArguments();
            result = (Class) fieldArgTypes[0];
        }

        return result;
    }

    
    public ServiceActionResult<E> save(E entity) {
        List<String> validationKeys = new ArrayList<String>();

        Set violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            fillViolations(violations, validationKeys);
            String[] dummy = null;
            return ServiceActionResult.buildFailure(entity, validationKeys.toArray(dummy));
        } else {
            sessionFactory.getCurrentSession().save(entity);
            return ServiceActionResult.buildSuccess(entity);
        }
    }

    public ServiceActionResult<E> update(E entity) {
        List<String> validationKeys = new ArrayList<String>();

        Set violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            fillViolations(violations, validationKeys);
            return ServiceActionResult.buildFailure(entity, validationKeys);
        } else {
            sessionFactory.getCurrentSession().saveOrUpdate(entity);
            return ServiceActionResult.buildSuccess(entity);
        }
    }

    public ServiceActionResult<E> delete(E entity) {
        sessionFactory.getCurrentSession().delete(entity);
        try {
            sessionFactory.getCurrentSession().flush();
            return ServiceActionResult.buildSuccess(null);
        }catch(HibernateException ex){
            return ServiceActionResult.buildFailure(entity);
        }
    }

    public ServiceActionResult<E> findById(long entityId) {
        String error = "";
        try {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(getEntityClass());
            criteria.add(Restrictions.eq(NewsletterEntity.ID, entityId));
            criteria.setMaxResults(1);
            Object entityObject = criteria.uniqueResult();
            if(entityObject != null) {
                E entity = (E) entityObject;
                return ServiceActionResult.buildSuccess(entity);
            }
        } catch (Exception ex) {
            error = "General error";
            logger.error(error, ex);
        }
        return ServiceActionResult.buildFailure(null, error);
    }

    public ServiceActionResult<List<E>> findAll(ThemeDisplay themeDisplay) {
        return findAll(themeDisplay, -1, -1);        
    }
    
    public ServiceActionResult<List<E>> findAll(ThemeDisplay themeDisplay, int start, int limit) {
        return findAll(themeDisplay, -1, -1, "", "");        
    }
    
    /**
     * Fill the validation keys list with information from the validation messages
     * @param violations
     * @param validationKeys 
     */
    protected void fillViolations(Set<ConstraintViolation<?>> violations, List<String> validationKeys) {
        for (ConstraintViolation<?> constraintViolation : violations) {
            validationKeys.add(constraintViolation.getPropertyPath()+" "+constraintViolation.getMessage());
        }
    }
    
    public ServiceActionResult<List<E>> findAll(ThemeDisplay themeDisplay, int start, int limit, String ordercrit, String order) {       
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(getEntityClass());
        
        criteria.add(Restrictions.eq(NewsletterEntity.COMPANYID, themeDisplay.getCompanyId()));        
        criteria.add(Restrictions.eq(NewsletterEntity.GROUPID, themeDisplay.getScopeGroupId()));
        
        if (start != -1) {
            criteria.setFirstResult(start);
        }
        if (limit != -1) {
            criteria.setMaxResults(limit);
        }
        if (!ordercrit.isEmpty()) {                
            if (NewsletterConstants.ORDER_BY_DESC.equals(order)) {
                criteria.addOrder(Order.desc(ordercrit)); 
            } else {
                criteria.addOrder(Order.asc(ordercrit)); 
            }
        }
        List<E> entities = criteria.list();
        
        return ServiceActionResult.buildSuccess(entities);        
    }

    public int findAllCount(ThemeDisplay themeDisplay) {
        int result = 0;
        try {            
            Session currentSession = sessionFactory.getCurrentSession();
            Criteria criteria = currentSession.createCriteria(getEntityClass());
            
            criteria.add(Restrictions.eq(NewsletterEntity.COMPANYID, themeDisplay.getCompanyId()));        
            criteria.add(Restrictions.eq(NewsletterEntity.GROUPID, themeDisplay.getScopeGroupId()));
            
            result = criteria.list().size();                    
                        
        } catch (NonUniqueResultException ex) {
            String error = "Error in findAllCount" + ex;
            logger.error(error);
        }   
        return result;
    }
}
