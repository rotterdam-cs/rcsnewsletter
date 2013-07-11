package com.rcs.newsletter.util;

import java.util.List;

import javax.portlet.ActionRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;

public class FileUploadUtil {

    public static boolean isMultipart(final ActionRequest request) {
        return PortletFileUpload.isMultipartContent(request);
    }

    @SuppressWarnings("unchecked")
	public static List<FileItem> parseRequest(final ActionRequest request) throws FileUploadException {
        FileItemFactory factory = new DiskFileItemFactory();
        PortletFileUpload upload = new PortletFileUpload(factory);
        return upload.parseRequest(request);
    }
}