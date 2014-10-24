
package com.rcs.newsletter.core.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jdto.DTOBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.ThemeLocalService;
import com.liferay.portal.service.ThemeLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.NewsletterScheduleDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.forms.jqgrid.GridRestrictionsUtil;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterSchedule;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
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
    
    @Autowired
    private NewsletterMailingService mailingService;

    private Log logger = LogFactoryUtil.getLog(NewsletterScheduleServiceImpl.class);
    
    
    public ServiceActionResult<NewsletterScheduleDTO> saveSchedule(NewsletterMailing mailing2, Date sendDate, ThemeDisplay themeDisplay) {
    	
    	NewsletterSchedule newsletterSchedule = new NewsletterSchedule();
    	newsletterSchedule.setGroupid(mailing2.getGroupid());
    	newsletterSchedule.setCompanyid(mailing2.getCompanyid());
    	newsletterSchedule.setMailing(mailing2);
    	newsletterSchedule.setPending(true);
    	newsletterSchedule.setSendDate(sendDate);
    	
    	/* save themeDisplay configuration */
    	newsletterSchedule.setLocale(themeDisplay.getLocale());
    	newsletterSchedule.setLayoutPlid(themeDisplay.getLayout().getPlid());
    	newsletterSchedule.setPortalURL(themeDisplay.getURLPortal());
    	newsletterSchedule.setUrlHome(themeDisplay.getURLHome());
    	newsletterSchedule.setPathFriendlyURLPublic(themeDisplay.getPathFriendlyURLPublic());
    	newsletterSchedule.setPathFriendlyURLPrivateUser(themeDisplay.getPathFriendlyURLPrivateUser());
    	newsletterSchedule.setPathFriendlyURLPrivateGroup(themeDisplay.getPathFriendlyURLPrivateGroup());
    	newsletterSchedule.setPathImage(themeDisplay.getPathImage());
    	newsletterSchedule.setPathMain(themeDisplay.getPathMain());
    	newsletterSchedule.setPathContext(themeDisplay.getPathContext());
    	newsletterSchedule.setPathThemeImages(themeDisplay.getPathThemeImages());
    	newsletterSchedule.setServerName(themeDisplay.getServerName());
    	newsletterSchedule.setCDNHost(themeDisplay.getCDNHost());
        
        ServiceActionResult<NewsletterSchedule> saveResult = save(newsletterSchedule);
        if (saveResult.isSuccess()){
        	logger.info("shedule saved");
        	return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterScheduleDTO.class, saveResult.getPayload()));
            
        }else{
        	logger.info("error saving schedule");
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

        @SuppressWarnings("unchecked")
		List<NewsletterSchedule> list = criteria.list();
        
        // create and return ListResultsDTO
        ListResultsDTO<NewsletterScheduleDTO> dto = new ListResultsDTO<NewsletterScheduleDTO>(gridForm.getRows(), gridForm.calculateStart(), totalRecords, binder.bindFromBusinessObjectList(NewsletterScheduleDTO.class, list));
        return ServiceActionResult.buildSuccess(dto);
    }

    
    public List<NewsletterSchedule> findAllSchedulesUntilNow() {
    	
        // get records
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterSchedule.class);
        logger.info(new Date().toString());
        criteria.add(Restrictions.le("sendDate", new Date()));
        
        @SuppressWarnings("unchecked")
		List<NewsletterSchedule> list = criteria.list();
        
         return list;
    }

    
    public ServiceActionResult<NewsletterScheduleDTO> findSchedule(Long scheduleId) {
        ServiceActionResult<NewsletterSchedule> findResult = findById(scheduleId);
        if (findResult.isSuccess()){
            return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterScheduleDTO.class, findResult.getPayload()));
        }
        return ServiceActionResult.buildFailure(null);
    }
    
    @Scheduled(cron="0 1 * * * *") //runs every hour at 1
    public void sendScheduleMailing(){
    	logger.info("Inside sendScheduleMailing Job");  	
    	List<NewsletterSchedule> scheduleMailings = findAllSchedulesUntilNow();
    	
    	try {

	    	for(NewsletterSchedule schedule : scheduleMailings){
	    		
	    		ThemeDisplay themeDisplay = new ThemeDisplay();
	    		themeDisplay.setLocale(schedule.getLocale());
	    		themeDisplay.setDoAsGroupId(schedule.getGroupid());
	    		themeDisplay.setCompany(CompanyLocalServiceUtil.getCompany(schedule.getCompanyid()));
				themeDisplay.setLayout(LayoutLocalServiceUtil.getLayout(schedule.getLayoutPlid()));
	    		themeDisplay.setPortalURL(schedule.getPortalURL());
	    		themeDisplay.setURLHome(schedule.getUrlHome());
	    		themeDisplay.setPathFriendlyURLPublic(schedule.getPathFriendlyURLPublic());
	    		themeDisplay.setPathFriendlyURLPrivateUser(schedule.getPathFriendlyURLPrivateUser());
	    		themeDisplay.setPathFriendlyURLPrivateGroup(schedule.getPathFriendlyURLPrivateGroup());
	    		themeDisplay.setPathImage(schedule.getPathImage());
	        	themeDisplay.setPathMain(schedule.getPathMain());
	        	themeDisplay.setPathContext(schedule.getPathContext());
	        	themeDisplay.setPathThemeImages(schedule.getPathThemeImages());
	        	themeDisplay.setServerName(schedule.getServerName());
	        	themeDisplay.setCDNHost(schedule.getCDNHost());
	
	    		NewsletterMailing mailing = schedule.getMailing();
	    		
	    		Date date = schedule.getSendDate();
	    		
	    		deleteSchedule(schedule.getId());
	    		
	    		ServiceActionResult<Void> result = mailingService.sendNewsletter(mailing.getId(), themeDisplay);
	    		
	    		if (!result.isSuccess()){
	    			saveSchedule(mailing, date, themeDisplay);
	    		}
	    		
	    	}
    	} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    

    public ServiceActionResult<Void> deleteSchedule(Long scheduleId) {
        ServiceActionResult<NewsletterSchedule> findResult = findById(scheduleId);
        if (findResult.isSuccess()) {
        	// delete schedule
            return delete(findResult.getPayload());
        }
        return ServiceActionResult.buildFailure(null);
    }

}
