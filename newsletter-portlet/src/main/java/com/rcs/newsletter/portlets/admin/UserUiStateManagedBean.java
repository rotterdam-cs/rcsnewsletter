package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.TabChangeEvent;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author juan
 */
@Named
@Scope("session")
public class UserUiStateManagedBean {
    
    public static final int LISTS_TAB_INDEX = 0;
    public static final int SUBSCRIBERS_TAB_INDEX = 1;
    public static final int MAILING_TAB_INDEX = 2;
    public static final int ARCHIVE_TAB_INDEX = 3;
    
    private int adminActiveTabIndex;
    
    @PostConstruct
    public void init() {
        adminActiveTabIndex = 0;
    }

    public int getAdminActiveTabIndex() {
        return adminActiveTabIndex;
    }

    public void setAdminActiveTabIndex(int adminActiveTabIndex) {
        this.adminActiveTabIndex = adminActiveTabIndex;
    }
    
    public void onTabsUpdated(TabChangeEvent event) {
        //DUMMY METHOD TO MAKE THE MAGIC HAPPEN
        System.out.println(adminActiveTabIndex);
    }
    
    public JournalArticle getJournalArticleByArticleId(String articleId) {
        JournalArticle result = null;
        try {
            result = JournalArticleLocalServiceUtil.getArticle(getThemeDisplay().getScopeGroupId(), articleId);
        } catch (Exception e) {
        }

        return result;
    }
    
    public List<JournalArticle> getJournalArticles() {
        List<JournalArticle> result = new ArrayList<JournalArticle>();
        try {
            result = JournalArticleLocalServiceUtil.getArticles(getThemeDisplay().getScopeGroupId());
        } catch (Exception e) {
        }

        return result;
    }
    
    private ThemeDisplay getThemeDisplay() {
        ThemeDisplay result = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();

        Map requestMap = facesContext.getExternalContext().getRequestMap();
        result = (ThemeDisplay) requestMap.get(WebKeys.THEME_DISPLAY);

        return result;
    }    
}
