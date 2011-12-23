package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
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
   
    @Autowired
    private SessionFactory sessionFactory;
        
    @Override
    public List<NewsletterTemplateBlock> findAllByMailing(NewsletterMailing mailing) {
        Criteria blocksCriteria = sessionFactory.getCurrentSession().createCriteria(NewsletterTemplateBlock.class);
        blocksCriteria.add(Restrictions.eq(NewsletterTemplateBlock.MAILING, mailing));
        blocksCriteria.addOrder(Order.asc(NewsletterTemplateBlock.BLOCK_ORDER_COLUMN));
        List<NewsletterTemplateBlock> blocks = blocksCriteria.list();
        return blocks;
    }
    
}