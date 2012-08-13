package com.rcs.newsletter.portlets.newsletteradmin;

import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.NewsletterArchiveDTO;
import com.rcs.newsletter.core.service.NewsletterArchiveService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.portlets.forms.GridForm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
@Controller
@RequestMapping("VIEW")
public class ArchiveController extends GenericController {
    
    @Autowired
    private NewsletterArchiveService archiveService;
    
    
    @ResourceMapping("archive")
    public ModelAndView archiveTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/archive", model);
    }
    
    /**
     * Show view with archive listing
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping("archivesList")
    public ModelAndView archivesList(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/archiveList", model);
    }
    
        
    
    
    /**
     * Returns JSON data for archive data grid
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping(value="getArchives")
    public ModelAndView getArchives(ResourceRequest request, ResourceResponse response, @ModelAttribute GridForm form){
        // get records using paging
        ServiceActionResult<ListResultsDTO<NewsletterArchiveDTO>> result = archiveService.findAllArchives(
                                    Utils.getThemeDisplay(request), 
                                    form.calculateStart(), 
                                    form.getRows(), 
                                    "date", 
                                    ORDER_BY_DESC);

        // if an error occurrs then return no record
        if (result.isSuccess()){
            result.getPayload().setCurrentPage(form.getPage());
        }else{
            result.getPayload().setCurrentPage(0);
            result.getPayload().setResult(new ArrayList());
            result.getPayload().setTotalRecords(0);
        }
        
        
        return jsonResponse(result);
    }
    
    
    /**
     * Returns view that shows archive entry's detail
     * @param request
     * @param response
     * @param id
     * @return 
     */
    @ResourceMapping(value="viewArchive")
    public ModelAndView viewArchive(ResourceRequest request, ResourceResponse response, Long id){
        ModelAndView mav = new ModelAndView("admin/archiveView");
        
        NewsletterArchiveDTO archive = new NewsletterArchiveDTO();
        
        if (id != null){
            ServiceActionResult<NewsletterArchiveDTO> findTemplate = archiveService.findArchive(id);
            archive = findTemplate.getPayload();
        }
        
        mav.addObject("archive", archive);
        
        return mav;
    }
}
