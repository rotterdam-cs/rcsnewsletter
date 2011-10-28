
package com.rcs.newsletter.util;

import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.util.servlet.ServletResponseUtil;
import java.io.IOException;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class JsonResponseUtil {
    
    private static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";    
    private static final Logger log = Logger.getLogger(JsonResponseUtil.class);
    
    /**
     * Writes the provided string as an already formatted JSON string.
     * @param liferayPortletResponse
     * @param freeJSONString 
     */
    public static void write(PortletResponse liferayPortletResponse, JsonBuilder response) {
        // Get the HttpServletResponse
        HttpServletResponse servletResponse = ((LiferayPortletResponse)liferayPortletResponse).getHttpServletResponse();

        // Sets the JSON Content Type
        servletResponse.setContentType(CONTENT_TYPE_APPLICATION_JSON);
        try {
            ServletResponseUtil.write(servletResponse, response.toString());
        } catch (IOException ex){
            log.error("Error writing JSON response");
        }
    }
    
}
