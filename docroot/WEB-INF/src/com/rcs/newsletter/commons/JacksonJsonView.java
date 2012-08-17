package com.rcs.newsletter.commons;

import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.servlet.view.AbstractView;

/**
 *
 * @author juan
 */
public class JacksonJsonView extends AbstractView {

    public static final String MODEL_NAME = "model";

    private final static ObjectMapper mapper = new ObjectMapper();

    public static final Logger log = Logger.getLogger(JacksonJsonView.class);
    
    public JacksonJsonView() {
        setContentType("application/json");
    }

    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(getContentType());
        response.setCharacterEncoding("UTF-8");

        //disable caching
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
        response.addDateHeader("Expires", 1L);
/*        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With");*/
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            Object modelObject = model.get(MODEL_NAME);
            String json = mapper.writeValueAsString(modelObject);
            out.print(json);
        } catch (Exception ex) {
            log.error("Error jsoning the object", ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
