package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author juan
 */
@Named
@Scope("session")
public class UserUiStateManagedBean implements Serializable {
    
    public static final int LISTS_TAB_INDEX = 0;
    public static final int SUBSCRIBERS_TAB_INDEX = 1;
    public static final int MAILING_TAB_INDEX = 2;
    public static final int ARCHIVE_TAB_INDEX = 3;
    
    private int adminActiveTabIndex;
    
    private static final Logger logger = LoggerFactory.getLogger(UserUiStateManagedBean.class);
    
    //global lists
    List<JournalArticle> journalArticles;
    
    @PostConstruct
    public void init() {
        adminActiveTabIndex = 0;
        refresh();
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
        return journalArticles;
    }
    
    private ThemeDisplay getThemeDisplay() {
        ThemeDisplay result = null;
        FacesContext facesContext = FacesContext.getCurrentInstance();

        Map requestMap = facesContext.getExternalContext().getRequestMap();
        result = (ThemeDisplay) requestMap.get(WebKeys.THEME_DISPLAY);

        return result;
    }

    public void refresh() {
        try {
            journalArticles = JournalArticleLocalServiceUtil.getArticles();
        } catch (Exception ex) {
            logger.error("Error while trying to get the list of journal articles", ex);
        }
    }
}
