package com.rcs.newsletter.core.service.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.theme.ThemeDisplay;

public class EmailFormat {    
    private static Log log = LogFactoryUtil.getLog(EmailFormat.class); 

    public static String fixImagesPath(String emailBody, ThemeDisplay themeDisplay) {
        String siteURL = getUrl(themeDisplay);
        String result = emailBody.replaceAll("src=\"/", "src=\" " + siteURL);
        result = result.replaceAll("&amp;", "&");
        return result;
    }
    
    private static String getUrl(ThemeDisplay themeDisplay) {
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
    
}
