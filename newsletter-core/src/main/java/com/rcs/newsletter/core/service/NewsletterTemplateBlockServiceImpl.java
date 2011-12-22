package com.rcs.newsletter.core.service;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Validator;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
@Service
@Transactional
public class NewsletterTemplateBlockServiceImpl extends CRUDServiceImpl<NewsletterTemplateBlock> implements NewsletterTemplateBlockService {
    //private static Log logger = LogFactoryUtil.getLog(NewsletterTemplateBlockServiceImpl.class);
    
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private Validator validator;  
    
    //@Autowired
    //private SessionFactory sessionFactory;
    
    //private final static Logger logger = LoggerFactory.getLogger(NewsletterTemplateBlockServiceImpl.class);
    
//   @Override
//    public List<NewsletterTemplateBlock> findNewsletterTemplateBlocksByTemplate(NewsletterTemplate template) {
//       logger.error("*********************** searching template");
//        List<NewsletterTemplateBlock> result = new ArrayList<NewsletterTemplateBlock>();
//        try {
//            logger.error("*********************** searching template 1 ");
//            Session currentSession = sessionFactory.getCurrentSession();
//            Criteria criteria = currentSession.createCriteria(NewsletterTemplateBlock.class);
//            criteria.add(Restrictions.eq(NewsletterTemplateBlock.TEMPLATE, template));
//            logger.error("*********************** searching template 2 size: " + criteria.list().size() );
//
//            result = criteria.list();
//        } catch (NonUniqueResultException ex) {
//            String error = "Error loading templateBlocks by template " + ex;
//            logger.error(error);
//        }
//        
//        return result;
//    }
   
   @Override
   public ServiceActionResult<List<NewsletterTemplateBlock>> findNewsletterTemplateBlocksByTemplate() {
        //List<NewsletterTemplateBlock> result = sessionFactory.getCurrentSession().createCriteria(NewsletterTemplateBlock.class).list();
        
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterTemplateBlock.class);       
       
        List<NewsletterTemplateBlock> entities = criteria.list();
        
        boolean success = true;
        List<String> validationKeys = new ArrayList<String>();
        
        ServiceActionResult<List<NewsletterTemplateBlock>> result = new ServiceActionResult<List<NewsletterTemplateBlock>>(success, entities, validationKeys);

        return result;        
    }
    
}