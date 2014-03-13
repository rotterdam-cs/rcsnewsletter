package com.rcs.newsletter.core.service;

import com.liferay.portal.kernel.log.Log;
import com.rcs.newsletter.core.model.NewsletterEntity;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.NewsletterConstants;
import javax.validation.ConstraintViolation;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

/**
 * Generic CRUD implementation
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Service
@Transactional
public class CRUDServiceImpl<E extends NewsletterEntity> implements CRUDService<E> {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private Validator validator;
    
    private static Log logger = LogFactoryUtil.getLog(CRUDServiceImpl.class);

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

    @SuppressWarnings("unchecked")
	@Override
    public ServiceActionResult<E> save(NewsletterEntity entity) {
        boolean success = true;
        List<String> validationKeys = new ArrayList<String>();

        Set<ConstraintViolation<NewsletterEntity>> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            success = false;
            fillViolations(violations, validationKeys);
        } else {
            sessionFactory.getCurrentSession().save(entity);
        }

        ServiceActionResult<NewsletterEntity> result = new ServiceActionResult<NewsletterEntity>(success, entity, validationKeys);

        return (ServiceActionResult<E>) result;
    }

    @SuppressWarnings("unchecked")
	@Override
    public ServiceActionResult<E> update(NewsletterEntity entity) {
        boolean success = true;
        List<String> validationKeys = new ArrayList<String>();

        Set violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            success = false;
            fillViolations(violations, validationKeys);
        } else {
            sessionFactory.getCurrentSession().update(entity);
        }

        ServiceActionResult<NewsletterEntity> result = new ServiceActionResult<NewsletterEntity>(success, entity, validationKeys);

        return (ServiceActionResult<E>) result;
    }

    @SuppressWarnings("unchecked")
	@Override
    public ServiceActionResult<E> delete(NewsletterEntity entity) {
        boolean success = true;
        List<String> validationKeys = new ArrayList<String>();

        Set violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            success = false;
            fillViolations(violations, validationKeys);
        } else {
            sessionFactory.getCurrentSession().delete(entity);
        }

        ServiceActionResult<NewsletterEntity> result = new ServiceActionResult<NewsletterEntity>(success, entity, validationKeys);

        return (ServiceActionResult<E>) result;
    }

    @Override
    public ServiceActionResult<E> findById(long entityId) {
        boolean success = false;
        List<String> validationKeys = new ArrayList<String>();
        E entity = null;

        try {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(getEntityClass());
            criteria.add(Restrictions.eq(NewsletterEntity.ID, entityId));
            criteria.setMaxResults(1);
            Object entityObject = criteria.uniqueResult();
            if(entityObject != null) {
                entity = (E) entityObject;
                success = true;
            }
        } catch (NonUniqueResultException ex) {
            String error = "Exists more than unique id";
            logger.error(error, ex);
        } catch (Exception ex) {
            String error = "General error";
            logger.error(error, ex);
        }
        ServiceActionResult<E> result = new ServiceActionResult<E>(success, entity, validationKeys);

        return result;
    }

    @Override
    public ServiceActionResult<List<E>> findAll(ThemeDisplay themeDisplay) {
        return findAll(themeDisplay, -1, -1);        
    }
    
    @Override
    public ServiceActionResult<List<E>> findAll(ThemeDisplay themeDisplay, int start, int limit) {
        return findAll(themeDisplay, -1, -1, "", "");        
    }
    
    /**
     * Fill the validation keys list with information from the validation messages
     * @param violations
     * @param validationKeys 
     */
    protected void fillViolations(Set<ConstraintViolation<NewsletterEntity>> violations, List<String> validationKeys) {
        for (ConstraintViolation<NewsletterEntity> constraintViolation : violations) {
            validationKeys.add(constraintViolation.getPropertyPath()+" "+constraintViolation.getMessage());
        }
    }
    
    @Override
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
        
        boolean success = true;
        List<String> validationKeys = new ArrayList<String>();
        
        ServiceActionResult<List<E>> result = new ServiceActionResult<List<E>>(success, entities, validationKeys);

        return result;
        
    }

    @Override
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
