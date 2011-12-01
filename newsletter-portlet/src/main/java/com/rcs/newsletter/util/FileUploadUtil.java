package com.rcs.newsletter.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.lang.ArrayUtils;

public class FileUploadUtil {

    public static boolean isMultipart(final ActionRequest request) {
        return PortletFileUpload.isMultipartContent(request);
    }

    public static List<FileItem> parseRequest(final ActionRequest request) throws FileUploadException {
        FileItemFactory factory = new DiskFileItemFactory();
        PortletFileUpload upload = new PortletFileUpload(factory);
        return upload.parseRequest(request);
    }

    public static Map getParameters(final List<FileItem> items) {
        Map result = new HashMap();
        for (FileItem item : items) {
            if (item.isFormField()) {
                String[] paramValues = (String[]) result.get(item.getFieldName());
                if (paramValues == null) {
                    paramValues = new String[]{};
                }
                try {
                    paramValues = (String[]) ArrayUtils.add(paramValues, item.getString("UTF-8"));
                } catch (UnsupportedEncodingException ex) {
                    paramValues = (String[]) ArrayUtils.add(paramValues, item.getString());
                }
                result.put(item.getFieldName(), paramValues);
            }
        }
        return result;
    }

//    public static Map<String, String> getParameters(final List<FileItem> items) {
//        Map<String, String> result = new HashMap<String, String>();
//        for (FileItem item : items) {
//            if (item.isFormField()) {
//                result.put(item.getFieldName(), item.getString());
//            }
//        }
//        return result;
//    }

    public static Map<String, FileItem> getFiles(final List<FileItem> items) {
        Map<String, FileItem> result = new HashMap<String, FileItem>();
        for (FileItem item : items) {
            if (!item.isFormField()) {
                result.put(item.getName(), item);
            }
        }
        return result;
    }
}