
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterEntity;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;

/**
 * Generic CRUD Service interface
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface CRUDService<E extends NewsletterEntity> {
    
    public ServiceActionResult<E> save(E entity);
    
    public ServiceActionResult<E> update(E entity);
    
    public ServiceActionResult<Void> delete(E entity);
    
    public ServiceActionResult<E> findById(long entityId);
    
    public ServiceActionResult<List<E>> findAll(ThemeDisplay themeDisplay);
    
    public ServiceActionResult<List<E>> findAll(ThemeDisplay themeDisplay, int start, int limit);
    
    public ServiceActionResult<List<E>> findAll(ThemeDisplay themeDisplay, int start, int limit, String ordercrit, String order );
        
    public int findAllCount(ThemeDisplay themeDisplay);
    
}
