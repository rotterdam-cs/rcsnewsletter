package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.TemplateDTO;
import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.Validator;
import org.hibernate.SessionFactory;
import org.jdto.DTOBinder;
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
    
    @Autowired
    private DTOBinder binder;
    
    @Autowired
    private Validator validator;
    
    private final static Logger logger = LoggerFactory.getLogger(NewsletterTemplateServiceImpl.class);

    
    @Override
    public ServiceActionResult<ListResultsDTO<TemplateDTO>> findAllTemplates(ThemeDisplay themeDisplay, int start, int limit, String ordercrit, String order) {
        // get total records count
        int totalRecords = findAllCount(themeDisplay);
        
        // get records
        ServiceActionResult<List<NewsletterTemplate>> listResult = findAll(themeDisplay, start, limit, ordercrit, order);
        if (!listResult.isSuccess()){
            return ServiceActionResult.buildFailure(null);
        }
        
        // create and return ListResultsDTO
        ListResultsDTO<TemplateDTO> dto = new ListResultsDTO<TemplateDTO>(limit, start, totalRecords, binder.bindFromBusinessObjectList(TemplateDTO.class, listResult.getPayload()));
        return ServiceActionResult.buildSuccess(dto);
    }

    
    @Override
    public ServiceActionResult<TemplateDTO> findTemplate(Long id) {
        ServiceActionResult<NewsletterTemplate> findResult = findById(id);
        if (findResult.isSuccess()){
            return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(TemplateDTO.class, findResult.getPayload()));
        }
        return ServiceActionResult.buildFailure(null);
    }

    
    
    @Override
    public ServiceActionResult<TemplateDTO> saveTemplate(ThemeDisplay themeDisplay, TemplateDTO templateDTO) {
        
        // fix template undesired chars
        String templateHTML = templateDTO.getTemplate();
        templateHTML =  templateHTML.replace("\u200B", "");
        templateDTO.setTemplate(templateHTML);
        
        // find existing template or creat a new one
        NewsletterTemplate template = null;
        if (templateDTO.getId() != null){
            template = findById(templateDTO.getId()).getPayload();
            fillTemplate(templateDTO, template);
        }else{
            template = binder.extractFromDto(NewsletterTemplate.class, templateDTO);
        }

        // add group and company information
        template.setCompanyid(themeDisplay.getCompanyId());
        template.setGroupid(themeDisplay.getScopeGroupId());
        
        // validate required fields
        Set errors = validator.validate(template);
        if (!errors.isEmpty()){
            List<String> errorsList = new ArrayList<String>();
            fillViolations(errors, errorsList);
            return ServiceActionResult.buildFailure(null, errorsList);
        }
        
        
        
        ServiceActionResult<NewsletterTemplate> result = save(template);
        if (result.isSuccess()){
            return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(TemplateDTO.class, result.getPayload()));
        }else{
            return ServiceActionResult.buildFailure(null);
        }
    }

    
    private void fillTemplate(TemplateDTO templateDTO, NewsletterTemplate template) {
        template.setName(templateDTO.getName());
        template.setTemplate(templateDTO.getTemplate());
    }

    
    @Override
    public ServiceActionResult deleteTemplate(ThemeDisplay themeDisplay, Long templateId) {
        ServiceActionResult<NewsletterTemplate> findResult = findById(templateId);
        if (findResult.isSuccess()){
            return delete(findResult.getPayload());
        }
        return ServiceActionResult.buildFailure(null);
    }
    
   
    
}
