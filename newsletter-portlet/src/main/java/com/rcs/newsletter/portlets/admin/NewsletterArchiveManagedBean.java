
package com.rcs.newsletter.portlets.admin;


import com.rcs.newsletter.core.model.NewsletterArchive;
import com.rcs.newsletter.core.service.NewsletterArchiveService;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.context.annotation.Scope;


/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Named
@Scope("request")
public class NewsletterArchiveManagedBean implements Serializable {
 
    private static final long serialVersionUID = 1L;
    
    @Inject    
    private UserUiStateManagedBean uiState;
    @Inject
    private NewsletterArchiveService archiveService;
    
    private List<NewsletterArchive> archives;
    
    @PostConstruct
    public void init() {
        archives = archiveService.findAll().getPayload();
    }

    public List<NewsletterArchive> getArchives() {
        return archives;
    }
}
