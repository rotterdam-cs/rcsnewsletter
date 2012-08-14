
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.NewsletterArchiveDTO;
import com.rcs.newsletter.core.model.NewsletterArchive;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.Date;
import java.util.List;
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
    DTOBinder binder;
    
@Override
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

    @Override
    public ServiceActionResult<ListResultsDTO<NewsletterArchiveDTO>> findAllArchives(ThemeDisplay themeDisplay, int start, int limit, String ordercrit, String order) {
        // get total records count
        int totalRecords = findAllCount(themeDisplay);
        
        // get records
        ServiceActionResult<List<NewsletterArchive>> listResult = findAll(themeDisplay, start, limit, ordercrit, order);
        if (!listResult.isSuccess()){
            return ServiceActionResult.buildFailure(null);
        }
        
        // create and return ListResultsDTO
        ListResultsDTO<NewsletterArchiveDTO> dto = new ListResultsDTO<NewsletterArchiveDTO>(limit, start, totalRecords, binder.bindFromBusinessObjectList(NewsletterArchiveDTO.class, listResult.getPayload()));
        return ServiceActionResult.buildSuccess(dto);
    }

    @Override
    public ServiceActionResult<NewsletterArchiveDTO> findArchive(Long archiveId) {
        ServiceActionResult<NewsletterArchive> findResult = findById(archiveId);
        if (findResult.isSuccess()){
            return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterArchiveDTO.class, findResult.getPayload()));
        }
        return ServiceActionResult.buildFailure(null);
    }
}
