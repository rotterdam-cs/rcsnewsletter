package com.rcs.newsletter.commons;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import java.util.Locale;
import javax.portlet.PortletRequest;
import org.springframework.stereotype.Component;

/**
 *
 * @author Gustavo Genovese <gustavo.genovese@rotterdam-cs.com>
 */
@Component
public class Utils {

    /**
     * Gets the current display theme
     *
     * @param request the request
     * @return
     */
    public static ThemeDisplay getThemeDisplay(PortletRequest request) {
        return (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    }

    /**
     * Gets the group id
     *
     * @param request the request
     * @return the group id
     */
    public static long getGroupId(PortletRequest request) {
        ThemeDisplay themeDisplay = getThemeDisplay(request);
        return themeDisplay.getScopeGroupId();
    }

    /**
     * Returns the locale associated to the language selected by the user in the
     * url
     *
     * @param request
     * @return
     */
    public static Locale getCurrentLocale(PortletRequest request) {

        //getting the language specified in the url of liferay
        String languageId = LanguageUtil.getLanguageId(request);

        //getting the locale associated to the language
        Locale locale = LocaleUtil.fromLanguageId(languageId);

        return locale;
    }

    /**
     * Gets the logged in Liferay user id, 0 if not logged
     *
     * @param request
     * @return
     */
    public static long getLoggedInUserId(PortletRequest request) {
        Object userId = request.getAttribute(WebKeys.USER_ID);
        if (userId != null) {
            return (Long) userId;
        }
        return 0;
    }
  
}
