
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.NewsletterArchiveDTO;
import com.rcs.newsletter.core.dto.NewsletterOnlineViewDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.forms.jqgrid.GridRestrictionsUtil;
import com.rcs.newsletter.core.model.NewsletterArchive;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.core.service.util.EmailFormat;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.jdto.DTOBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Service
@Transactional
public class NewsletterArchiveServiceImpl extends CRUDServiceImpl<NewsletterArchive> implements NewsletterArchiveService {

    @Autowired
    private NewsletterSubscriptionService subscriptionService;
    
    @Autowired
    DTOBinder binder;

    public ServiceActionResult<NewsletterArchiveDTO> saveArchive(NewsletterMailing mailing, String emailBody, ThemeDisplay themeDisplay) {
        NewsletterArchive archive = new NewsletterArchive();
        archive.setGroupid(mailing.getGroupid());
        archive.setCompanyid(mailing.getCompanyid());
        archive.setDate(new Date());
        archive.setCategoryName(mailing.getList().getName());
        archive.setArticleTitle(mailing.getName());
        archive.setEmailBody(emailBody);
        archive.setName(mailing.getName());     
        
        ServiceActionResult<NewsletterArchive> saveResult = save(archive);
        if (saveResult.isSuccess()){
            return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterArchiveDTO.class, saveResult.getPayload()));
        }else{
            return ServiceActionResult.buildFailure(null);
        }
        
    }

    
    public ServiceActionResult<ListResultsDTO<NewsletterArchiveDTO>> findAllArchives(ThemeDisplay themeDisplay, GridForm gridForm, String orderField, String orderType) {
        // get total records count
        int totalRecords = findAllCount(themeDisplay);
        
        // get records
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterArchive.class);
        criteria.addOrder(Order.asc(orderField));
        
        // add search filters
        if (gridForm != null){
            Criterion criterion = GridRestrictionsUtil.createCriterion(gridForm.getFiltersForm());
            if (criterion != null){
                criteria.add(criterion);
            }
        }

        @SuppressWarnings("unchecked")
		List<NewsletterArchive> list = criteria.list();
        
        // create and return ListResultsDTO
        ListResultsDTO<NewsletterArchiveDTO> dto = new ListResultsDTO<NewsletterArchiveDTO>(gridForm.getRows(), gridForm.calculateStart(), totalRecords, binder.bindFromBusinessObjectList(NewsletterArchiveDTO.class, list));
        return ServiceActionResult.buildSuccess(dto);
    }

    
    public ServiceActionResult<NewsletterArchiveDTO> findArchive(Long archiveId) {
        ServiceActionResult<NewsletterArchive> findResult = findById(archiveId);
        if (findResult.isSuccess()){
            return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterArchiveDTO.class, findResult.getPayload()));
        }
        return ServiceActionResult.buildFailure(null);
    }

    
    public ServiceActionResult<NewsletterOnlineViewDTO> getNewsletterForViewer(Long archiveId, Long subscriptionId, ThemeDisplay themeDisplay) {
        ResourceBundle bundle = ResourceBundle.getBundle("Language", themeDisplay.getLocale());
        
        // find archive
        ServiceActionResult<NewsletterArchiveDTO> archiveResult = findArchive(archiveId);
        if (!archiveResult.isSuccess()){
            return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.tab.archives.error.notfound"));
        }
        ServiceActionResult<NewsletterSubscription> subscriptionResult = subscriptionService.findById(subscriptionId);
        if (!subscriptionResult.isSuccess()){
            return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.tab.subscription.error.notfound"));
        }
        
        NewsletterArchiveDTO archiveDTO = archiveResult.getPayload();
        NewsletterSubscription subscription = subscriptionResult.getPayload();
        
        String emailBody = archiveDTO.getEmailBody();
        emailBody = EmailFormat.replaceUserInfo(emailBody, subscription, themeDisplay, archiveId);


        // fill dto for viewer
        NewsletterOnlineViewDTO viewDTO = new NewsletterOnlineViewDTO();
        viewDTO.setListName(archiveDTO.getCategoryName());
        viewDTO.setNewsletterBody(emailBody);
        
        return ServiceActionResult.buildSuccess(viewDTO);
        
        
    }
}
