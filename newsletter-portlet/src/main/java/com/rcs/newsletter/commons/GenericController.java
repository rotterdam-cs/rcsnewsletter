package com.rcs.newsletter.commons;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import java.util.Map;
import java.util.ResourceBundle;
import javax.portlet.PortletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.portlet.ModelAndView;

/**
 * Generic Controller to be extended from portal controllers
 *
 * @author Prj.M@x <pablo.rendon@rotterdam-cs.com>
 */
public abstract class GenericController {

    protected Log logger = LogFactoryUtil.getLog(getClass());
    public static final long UNDEFINED = -1;
    public static final String ORDER_BY_ASC = "asc";
    public static final String ORDER_BY_DESC = "desc";
    public static final int PAGINATION_DEFAULT_LIMIT = 5;
    public static final String DATE_BACK_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FRONT_FORMAT = "Y-m-d";
    public static final String JQFORM_ACTION_ADD = "add";
    public static final String JQFORM_ACTION_EDIT = "edit";
    public static final String JQFORM_ACTION_DEL = "del";
    
    public static final String MODEL_ERRORS = "errors";
    
    
    @Autowired
    protected Utils utils;

    protected String validationMessages(Map<String, String> failures) {
        StringBuilder sb = new StringBuilder();

        for (String failureMsg : failures.values()) {
            if (sb.length() > 0) {
                sb.append("\",");
            }
            sb.append("\"").append(failureMsg).append("\"");
        }
        if (failures.size() > 0) {
            sb.insert(0, "{\"error\":[");
            sb.append("]}");
        }
        return sb.toString();
    }

    public static String getKeyLocalizedValue(String key, PortletRequest request) {
        ResourceBundle res = ResourceBundle.getBundle("Language", Utils.getCurrentLocale(request));
        if (key == null || !res.containsKey(key)){
            return null;
        }
        else {
            return res.getString(key);
        }
    }

    public ModelAndView jsonResponse(ServiceActionResult result) {
        return new ModelAndView(new JacksonJsonView(), JacksonJsonView.MODEL_NAME, result);
    }

    public ModelAndView stringResponse(String result) {
        return new ModelAndView(new StringView(true), StringView.MODEL_NAME, result);
    }

    public ModelAndView stringResponse(String result, boolean isJSON) {
        return new ModelAndView(new StringView(isJSON), StringView.MODEL_NAME, result);
    }
}
