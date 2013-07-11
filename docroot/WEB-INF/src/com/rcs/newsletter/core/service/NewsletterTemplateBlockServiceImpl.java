package com.rcs.newsletter.core.service;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;

/**
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
@Service
@Transactional
public class NewsletterTemplateBlockServiceImpl extends CRUDServiceImpl<NewsletterTemplateBlock> implements NewsletterTemplateBlockService {
    
    @Autowired
    private SessionFactory sessionFactory;
        
	public List<NewsletterTemplateBlock> findAllByMailing(NewsletterMailing mailing) {
        Session currentSession = sessionFactory.getCurrentSession();        
        Criteria blocksCriteria = currentSession.createCriteria(NewsletterTemplateBlock.class);
        blocksCriteria.add(Restrictions.eq(NewsletterTemplateBlock.MAILING, mailing));
        
        @SuppressWarnings("unchecked")
		List<NewsletterTemplateBlock> blocks = blocksCriteria.list();
        return blocks;
    }
    
}