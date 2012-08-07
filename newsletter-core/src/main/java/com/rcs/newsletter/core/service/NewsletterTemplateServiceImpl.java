package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
@Service
@Transactional
public class NewsletterTemplateServiceImpl extends CRUDServiceImpl<NewsletterTemplate> implements NewsletterTemplateService {

    @Autowired
    private SessionFactory sessionFactory;
    
    private final static Logger logger = LoggerFactory.getLogger(NewsletterTemplateServiceImpl.class);

    @Override
    public ServiceActionResult<ListResultsDTO<NewsletterTemplate>> findAllTemplates(ThemeDisplay themeDisplay, int start, int limit, String ordercrit, String order) {
        // get total records count
        int totalRecords = findAllCount(themeDisplay);
        
        // get records
        ServiceActionResult<List<NewsletterTemplate>> listResult = findAll(themeDisplay, start, limit, ordercrit, order);
        if (!listResult.isSuccess()){
            return ServiceActionResult.buildFailure(null);
        }
        
        // create and return ListResultsDTO
        ListResultsDTO<NewsletterTemplate> dto = new ListResultsDTO<NewsletterTemplate>(limit, start, totalRecords, listResult.getPayload());
        return ServiceActionResult.buildSuccess(dto);
    }
    
   
    
}
