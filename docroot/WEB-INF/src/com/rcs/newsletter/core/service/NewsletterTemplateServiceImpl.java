package com.rcs.newsletter.core.service;

import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.dto.NewsletterTemplateDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.forms.jqgrid.GridRestrictionsUtil;
import com.rcs.newsletter.core.model.NewsletterEntity;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javax.validation.Validator;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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

    
    public ServiceActionResult<ListResultsDTO<NewsletterTemplateDTO>> findAllTemplates(ThemeDisplay themeDisplay, GridForm gridForm, String orderField, String orderType) {
        // get total records count
        int totalRecords = findAllCount(themeDisplay);
        
        // get records
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterTemplate.class);
        criteria.addOrder(Order.asc(orderField));
        
        // add search filters
        if (gridForm != null){
            Criterion criterion = GridRestrictionsUtil.createCriterion(gridForm.getFiltersForm());
            if (criterion != null){
                criteria.add(criterion);
            }
        }
        List<NewsletterTemplate> list = criteria.list();
        
        // create and return ListResultsDTO
        ListResultsDTO<NewsletterTemplateDTO> dto = new ListResultsDTO<NewsletterTemplateDTO>(gridForm.getRows(), gridForm.calculateStart(), totalRecords, binder.bindFromBusinessObjectList(NewsletterTemplateDTO.class, list));
        return ServiceActionResult.buildSuccess(dto);
    }

    
    public ServiceActionResult<NewsletterTemplateDTO> findTemplate(Long id) {
        ServiceActionResult<NewsletterTemplate> findResult = findById(id);
        if (findResult.isSuccess()){
            return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterTemplateDTO.class, findResult.getPayload()));
        }
        return ServiceActionResult.buildFailure(null);
    }

    
    
    public ServiceActionResult<NewsletterTemplateDTO> saveTemplate(ThemeDisplay themeDisplay, NewsletterTemplateDTO templateDTO) {
        
        // fix template undesired chars
        String templateHTML = templateDTO.getTemplate();
        templateHTML =  templateHTML.replace("\u200B", "");
        templateHTML = templateHTML.replace("&lt;", "<");
        templateHTML = templateHTML.replace("&gt;", ">");
        
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
            return ServiceActionResult.buildSuccess(binder.bindFromBusinessObject(NewsletterTemplateDTO.class, result.getPayload()));
        }else{
            return ServiceActionResult.buildFailure(null);
        }
    }

    
    private void fillTemplate(NewsletterTemplateDTO templateDTO, NewsletterTemplate template) {
        template.setName(templateDTO.getName());
        template.setTemplate(templateDTO.getTemplate());
    }

    private void fillTemplateDTO(NewsletterTemplate template, NewsletterTemplateDTO templateDTO) {
        templateDTO.setBlocks(countBlocksInTemplate(template.getId()));
    }
    
    public ServiceActionResult deleteTemplate(ThemeDisplay themeDisplay, Long templateId) {
        ResourceBundle bundle = ResourceBundle.getBundle("Language", themeDisplay.getLocale());
        ServiceActionResult<NewsletterTemplate> findResult = findById(templateId);
        if (findResult.isSuccess()){
            List<NewsletterMailing> mailings = findByMailingsUsedByTemplate(templateId);
            if (mailings.size() > 0){
                return ServiceActionResult.buildFailure(null, bundle.getString("newsletter.tab.templates.error.deleting.existingmailing"));
            }
            return delete(findResult.getPayload());
        }
        return ServiceActionResult.buildFailure(null);
    }

    
    public List<NewsletterTemplateDTO> findAllTemplates(ThemeDisplay themeDisplay) {
        Criteria criteria = createCriteriaForTemplates(themeDisplay);
        criteria.addOrder(Order.asc("name"));
        
        List<NewsletterTemplate> result = criteria.list();
        List<NewsletterTemplateDTO> dtos = new ArrayList<NewsletterTemplateDTO>();
        
        for(NewsletterTemplate entity: result){
            NewsletterTemplateDTO dto = binder.bindFromBusinessObject(NewsletterTemplateDTO.class, entity);
            fillTemplateDTO(entity, dto);
            dtos.add(dto);
        }
        return dtos;
    }
    
    
     private Criteria createCriteriaForTemplates(ThemeDisplay themeDisplay){
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterTemplate.class);
        criteria.add(Restrictions.eq(NewsletterEntity.COMPANYID, themeDisplay.getCompanyId()));        
        criteria.add(Restrictions.eq(NewsletterEntity.GROUPID, themeDisplay.getScopeGroupId()));
        return criteria;
    }

    public int countBlocksInTemplate(Long templateId) {
        NewsletterTemplateDTO templateDTO = findTemplate(templateId).getPayload();
        return StringUtils.countMatches(templateDTO.getTemplate(), "[block]");
    }
    
    
     private List<NewsletterMailing> findByMailingsUsedByTemplate(Long templateId) {
       Criteria criteria = sessionFactory.getCurrentSession().createCriteria(NewsletterMailing.class);
       criteria.add(Restrictions.eq("template.id", templateId));
       return criteria.list();
    }
   
    
}
