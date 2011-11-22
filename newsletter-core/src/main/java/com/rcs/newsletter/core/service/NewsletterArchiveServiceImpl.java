
package com.rcs.newsletter.core.service;

import com.rcs.newsletter.core.model.NewsletterArchive;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@Service
@Transactional
public class NewsletterArchiveServiceImpl extends CRUDServiceImpl<NewsletterArchive> implements NewsletterArchiveService {
        
}
