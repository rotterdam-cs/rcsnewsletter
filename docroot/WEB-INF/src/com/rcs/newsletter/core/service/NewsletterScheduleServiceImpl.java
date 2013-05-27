
package com.rcs.newsletter.core.service;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.jdto.DTOBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.NewsletterScheduleDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.forms.jqgrid.GridRestrictionsUtil;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterSchedule;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;

/**
 *
 * @author Pablo Rendon <pablo.rendon@rotterdam-cs.com>
 */
@Service
@Transactional
public class NewsletterScheduleServiceImpl extends CRUDServiceImpl<NewsletterSchedule> implements NewsletterScheduleService {

//    @Autowired
//    private NewsletterSubscriptionService subscriptionService;
    
    @Autowired
    DTOBinder binder;
    
    public ServiceActionResult<NewsletterScheduleDTO> saveSchedule(NewsletterMailing mailing, Date sendDate, ThemeDisplay themeDisplay) {
    	NewsletterSchedule newsletterSchedule = new NewsletterSchedule();
    	newsletterSchedule.setGroupid(mailing.getGroupid());
    	newsletterSchedule.setCompanyid(mailing.getCompanyid());
    	newsletterSchedule.setMailing(mailing);
    	newsletterSchedule.setPending(true);
    	newsletterSchedule.setSendDate(sendDate);
        
        ServiceActionResult<NewsletterSchedule> saveResult = save(newsletterSchedule);
        if (saveResult.isSuccess()){
            return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterScheduleDTO.class, saveResult.getPayload()));
        }else{
            return ServiceActionResult.buildFailure(null);
        }
    }

    
    public ServiceActionResult<ListResultsDTO<NewsletterScheduleDTO>> findAllSchedules(ThemeDisplay themeDisplay, GridForm gridForm, String orderField, String orderType) {
        // get total records count
        int totalRecords = findAllCount(themeDisplay);
        
        // get records
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterSchedule.class);
        criteria.addOrder(Order.asc(orderField));
        
        // add search filters
        if (gridForm != null){
            Criterion criterion = GridRestrictionsUtil.createCriterion(gridForm.getFiltersForm());
            if (criterion != null){
                criteria.add(criterion);
            }
        }

        List<NewsletterSchedule> list = criteria.list();
        
        // create and return ListResultsDTO
        ListResultsDTO<NewsletterScheduleDTO> dto = new ListResultsDTO<NewsletterScheduleDTO>(gridForm.getRows(), gridForm.calculateStart(), totalRecords, binder.bindFromBusinessObjectList(NewsletterScheduleDTO.class, list));
        return ServiceActionResult.buildSuccess(dto);
    }

    
    public ServiceActionResult<NewsletterScheduleDTO> findSchedule(Long scheduleId) {
        ServiceActionResult<NewsletterSchedule> findResult = findById(scheduleId);
        if (findResult.isSuccess()){
            return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterScheduleDTO.class, findResult.getPayload()));
        }
        return ServiceActionResult.buildFailure(null);
    }


}
