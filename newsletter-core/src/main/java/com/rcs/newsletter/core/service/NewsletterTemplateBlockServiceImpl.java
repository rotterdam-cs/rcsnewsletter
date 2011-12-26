package com.rcs.newsletter.core.service;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
import java.util.List;
import org.hibernate.Criteria;
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
    private static Log log = LogFactoryUtil.getLog(NewsletterTemplateBlockServiceImpl.class);  
    
    @Autowired
    private SessionFactory sessionFactory;
        
    @Override
    public List<NewsletterTemplateBlock> findAllByMailing(NewsletterMailing mailing) {
        Session currentSession = sessionFactory.getCurrentSession();        
        Criteria blocksCriteria = currentSession.createCriteria(NewsletterTemplateBlock.class);
        blocksCriteria.add(Restrictions.eq(NewsletterTemplateBlock.MAILING, mailing));
        return blocksCriteria.list();
    }
    
    
    
    
}