package com.rcs.newsletter.commons;

import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.AbstractView;

/**
 *
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
public class StringView extends AbstractView{
    public static final String MODEL_NAME = "model";
    
    public StringView(boolean isJSON) {
        if (isJSON){
            setContentType("application/json");
        }else {
            setContentType("text/plain");
        }
    }
    
    @Override
    protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(getContentType());
        response.setCharacterEncoding("UTF-8");
        
        //disable caching
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
        response.addDateHeader("Expires", 1L);
    }
    
    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String modelObject = model.get(MODEL_NAME).toString();
            out.print(modelObject);
        } catch (Exception ex) {
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
