package com.rcs.newsletter.portlets.newsletteradmin;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleResource;
import com.liferay.portlet.journal.service.JournalArticleResourceLocalServiceUtil;
import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.JournalArticleDTO;
import com.rcs.newsletter.core.dto.NewsletterArchiveDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.service.NewsletterArchiveService;
import com.rcs.newsletter.core.service.NewsletterMailingService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.core.service.util.ArticleUtils;
import com.rcs.newsletter.util.SystemParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.jdto.DTOBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author Gustavo Del Negro
 */
@Controller
@RequestMapping("VIEW")
public class UntaggingController extends GenericController {

    @Autowired
    private NewsletterMailingService mailingService;
    
    @Autowired
    private DTOBinder binder;
    
    @Autowired
    private SystemParameters systemParameters;
    
    @ResourceMapping("untagging")
    public ModelAndView archiveTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/untagging", model);
    }
    
    /**
     * Show view with archive listing
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping("untaggingList")
    public ModelAndView archivesList(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/untaggingList", model);
    }
    
        
    
    
    /**
     * Returns JSON data for archive data grid
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping(value="getUntagging")
    public ModelAndView getUntagging(ResourceRequest request, ResourceResponse response, @ModelAttribute GridForm form){
    	
    	logger.debug("inside getUntagging");
        // get records using paging
    	ServiceActionResult<ListResultsDTO<JournalArticleDTO>> result;
    	
		List<JournalArticleDTO> journalArticles =  mailingService.findAllArticlesForMailing(Utils.getThemeDisplay(request));
		
        // get total records count
		int totalRecords = journalArticles.size();
		
        int max = ((form.calculateStart()+form.getRows()) > journalArticles.size()) ? journalArticles.size() : form.calculateStart()+form.getRows(); 
        List<JournalArticleDTO> sublist = journalArticles.subList(form.calculateStart(), max);
        
        // create and return ListResultsDTO
        ListResultsDTO<JournalArticleDTO> dto = new ListResultsDTO<JournalArticleDTO>(form.getRows(), form.calculateStart(), totalRecords, binder.bindFromBusinessObjectList(JournalArticleDTO.class, sublist));
        result = ServiceActionResult.buildSuccess(dto);
        
        // if an error occurrs then return no record
        if (result.isSuccess()){
            result.getPayload().setCurrentPage(form.getPage());
        }else{
            result.getPayload().setCurrentPage(0);
            result.getPayload().setResult(new ArrayList<JournalArticleDTO>());
            result.getPayload().setTotalRecords(0);
        }
        
        
        return jsonResponse(result);
    }
    

    @ResourceMapping(value="deleteTagTypeCategory")
    public ModelAndView deleteTagTypeCategory(ResourceRequest request, ResourceResponse response, @RequestParam Long id) throws PortalException, SystemException, ClassNotFoundException, InstantiationException, IllegalAccessException{
               
    	logger.debug("inside deleteTagTypeCategory");
    	
         ArticleUtils.untagUncategoryUntypeById(id,systemParameters.getNewsletterArticleTag(),systemParameters.getNewsletterArticleType(),systemParameters.getNewsletterArticleCategory());
         
         Map<String,Object> model = new HashMap<String,Object>();
         return new ModelAndView("admin/untaggingList", model);
    }
    
}
