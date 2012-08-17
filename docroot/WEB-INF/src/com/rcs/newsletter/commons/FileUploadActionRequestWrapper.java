
package com.rcs.newsletter.commons;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.filter.ActionRequestWrapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

public class FileUploadActionRequestWrapper extends ActionRequestWrapper {
	
    private static final Log logger = LogFactoryUtil.getLog(FileUploadActionRequestWrapper.class);
	
    private Map<String, String[]> parameters;
	
    public FileUploadActionRequestWrapper(final ActionRequest request, FileItem fileItem) throws FileUploadException {
        super(request);
        parameters = new HashMap<String, String[]>();
        String[] st = {fileItem.getString()};
        
        parameters.put(fileItem.getFieldName(), st);
    }
	
    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }
	
    @Override
    public String getParameter(final String name) {
        if (parameters.get(name) != null) {
            return parameters.get(name)[0];
        }
        return null;
    }

    @Override
    public String[] getParameterValues(final String name) {
        return parameters.get(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }
}