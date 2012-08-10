
package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.NewsletterArchiveDTO;
import com.rcs.newsletter.core.model.NewsletterArchive;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.Date;
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
}
