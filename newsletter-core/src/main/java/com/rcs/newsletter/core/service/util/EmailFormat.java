package com.rcs.newsletter.core.service.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.theme.ThemeDisplay;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class EmailFormat {

    private static Log log = LogFactoryUtil.getLog(EmailFormat.class);

    /**
     * Fix the relative Paths to Absolute Paths on images
     */
    public static String fixImagesPath(String emailBody, ThemeDisplay themeDisplay) {
        String siteURL = getUrl(themeDisplay);
        String result = emailBody.replaceAll("src=\"/", "src=\" " + siteURL);
        result = result.replaceAll("&amp;", "&");
        return result;
    }

    /**
     * Returns the base server URL
     */
    public static String getUrl(ThemeDisplay themeDisplay) {
        StringBuilder result = new StringBuilder();
        String[] toReplaceTmp = themeDisplay.getURLHome().split("/");
        for (int i = 0; i < toReplaceTmp.length; i++) {
            if (i < 3) {
                result.append(toReplaceTmp[i]);
                result.append("/");
            }
        }
        return result.toString();
    }

    public static File getFile(URL u) throws Exception {
        URLConnection uc = u.openConnection();
        String contentType = uc.getContentType();
        int contentLength = uc.getContentLength();
        if (contentType.startsWith("text/") || contentLength == -1) {
            throw new IOException("This is not a binary file.");
        }
        InputStream raw = uc.getInputStream();
        InputStream is = new BufferedInputStream(raw);

        File tmp = null;
        OutputStream output = null;
        try {
            log.error("ContenType: " + contentType);
            String fileExt = "";
            if (contentType.endsWith("png")){
                fileExt = ".png";
            } else if (contentType.endsWith("jpg")){
                fileExt = ".jpg";
            } else if (contentType.endsWith("jpeg")){
                fileExt = ".jpeg";
            } else if (contentType.endsWith("jpe")){
                fileExt = ".jpe";
            } else if (contentType.endsWith("gif")){
                fileExt = ".gif";
            }
            tmp = File.createTempFile("image", fileExt);
            output = new FileOutputStream(tmp);
            int val;  
            while ((val = is.read()) != -1) {
                output.write(val);
            }            
        } catch (IOException e) {
            log.error(e);
        } finally {
            try {
                is.close();
                output.flush();
                output.close();
            } catch (Exception e) {
                log.error(e);
            }
        }        
        return tmp;
    }

    /**
     * Method imported from COPS (com.rcs.community.common.MimeMail)
     * Returns an ArrayList with all the different images paths. Duplicated paths are deleted.
     *
     * NB! the returned images urls may have html encoding included.
     * 
     * @param htmltext a String HTML with content
     * @return an ArrayList with the images paths
     */
    public static ArrayList getImagesPathFromHTML(String htmltext) {

        ArrayList imagesList = new ArrayList();
        try {
            // get everything that is inside the <img /> tag
            String[] imagesTag = StringUtils.substringsBetween(htmltext, "<img ", ">");

            if (imagesTag != null) { // if there are images

                for (int i = 0; i < imagesTag.length; i++) {
                    // get what is in the src attribute
                    String imagePath = StringUtils.substringBetween(imagesTag[i], "src=\"", "\"");
                    if (imagePath == null) {
                        imagePath = StringUtils.substringBetween(imagesTag[i], "src='", "'");
                    }

                    if (!imagesList.contains(imagePath)) { // don't save the duplicated images
                        imagesList.add(imagePath);
                    }
                }
            }


            /// and now for the background images only one style of typing is allowed for now!!!
            imagesTag = StringUtils.substringsBetween(htmltext, "background=\"", "\"");


            if (imagesTag != null) {
                for (int i = 0; i < imagesTag.length; i++) {
                    // get what is in the src attribute
                    String imagePath = imagesTag[i].trim();
                    log.error("processing: " + imagePath);
                    if (!imagesList.contains(imagePath)) { // don't save the duplicated images
                        imagesList.add(imagePath);
                    }
                }
            }
            imagesTag = StringUtils.substringsBetween(htmltext, "background=\'", "\'");
            if (imagesTag != null) {
                for (int i = 0; i < imagesTag.length; i++) {
                    // get what is in the src attribute
                    String imagePath = imagesTag[i].trim();
                    log.error("processing: " + imagePath);
                    if (!imagesList.contains(imagePath)) { // don't save the duplicated images
                        imagesList.add(imagePath);
                    }
                }
            }



        } catch (Exception ex) {
            log.error("error in getImagesPathFromHTML: ", ex);
        }
        return imagesList;
    }
}
