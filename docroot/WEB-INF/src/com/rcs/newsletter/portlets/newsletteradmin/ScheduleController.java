package com.rcs.newsletter.portlets.newsletteradmin;

import com.rcs.newsletter.commons.GenericController;
import com.rcs.newsletter.commons.Utils;
import com.rcs.newsletter.core.dto.NewsletterScheduleDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.service.NewsletterScheduleService;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
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
 * @author pablo rendon <pablo.rendon@rotterdam-cs.com>
 */
@Controller
@RequestMapping("VIEW")
public class ScheduleController extends GenericController {
    
    @Autowired
    private NewsletterScheduleService scheduleService;
    
    
    @ResourceMapping("schedule")
    public ModelAndView archiveTab(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/schedule", model);
    }
    
    /**
     * Show view with archive listing
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping("scheduleList")
    public ModelAndView archivesList(ResourceRequest request, ResourceResponse response){
        Map<String,Object> model = new HashMap<String,Object>();
        return new ModelAndView("admin/scheduleList", model);
    }
    
        
    
    
    /**
     * Returns JSON data for archive data grid
     * @param request
     * @param response
     * @return 
     */
    @ResourceMapping(value="getSchedules")
    public ModelAndView getArchives(ResourceRequest request, ResourceResponse response, @ModelAttribute GridForm form){
        // get records using paging
        ServiceActionResult<ListResultsDTO<NewsletterScheduleDTO>> result = scheduleService.findAllSchedules(
            Utils.getThemeDisplay(request), 
            form, 
            "sendDate", 
            ORDER_BY_DESC
        );

        // if an error occurrs then return no record
        if (result.isSuccess()){
            result.getPayload().setCurrentPage(form.getPage());
        }else{
            result.getPayload().setCurrentPage(0);
            result.getPayload().setResult(new ArrayList<NewsletterScheduleDTO>());
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
    @ResourceMapping(value="viewSchedule")
    public ModelAndView viewArchive(ResourceRequest request, ResourceResponse response, Long id){
        ModelAndView mav = new ModelAndView("admin/scheduleView");
        
        NewsletterScheduleDTO archive = new NewsletterScheduleDTO();
        
        if (id != null){
            ServiceActionResult<NewsletterScheduleDTO> findTemplate = scheduleService.findSchedule(id);
            archive = findTemplate.getPayload();
        }
        
        mav.addObject("archive", archive);
        
        return mav;
    }
}
