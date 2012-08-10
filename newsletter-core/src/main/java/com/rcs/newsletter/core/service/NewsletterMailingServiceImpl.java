package com.rcs.newsletter.core.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.theme.ThemeDisplay;
import com.rcs.newsletter.core.model.NewsletterMailing;
import com.rcs.newsletter.core.model.NewsletterSubscription;
import com.rcs.newsletter.core.model.NewsletterSubscriptor;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;
import com.rcs.newsletter.core.service.util.LiferayMailingUtil;
import com.rcs.newsletter.core.service.util.EmailFormat;
import javax.mail.internet.InternetAddress;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.rcs.newsletter.NewsletterConstants.*;
import com.rcs.newsletter.core.dto.ArticleDTO;
import com.rcs.newsletter.core.dto.MailingDTO;
import com.rcs.newsletter.core.model.NewsletterCategory;
import com.rcs.newsletter.core.model.NewsletterTemplate;
import com.rcs.newsletter.core.model.NewsletterTemplateBlock;
import com.rcs.newsletter.core.service.common.ListResultsDTO;
import com.rcs.newsletter.core.service.common.ServiceActionResult;
import com.rcs.newsletter.core.service.util.ArticleUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.validation.Validator;
import org.apache.log4j.Logger;
import org.jdto.DTOBinder;

/**
 *
 * @author juan
 */
@Service
@Transactional
class NewsletterMailingServiceImpl extends CRUDServiceImpl<NewsletterMailing> implements NewsletterMailingService {

    private static Logger logger = Logger.getLogger(NewsletterMailingServiceImpl.class);
    
    @Autowired
    private DTOBinder binder;
    
    @Autowired
    private Validator validator;
    
    @Autowired
    private NewsletterCategoryService categoryService;
    
    @Autowired
    private NewsletterTemplateService templateService;

    @Autowired
    private NewsletterTemplateBlockService templateBlockService;
    
    @Autowired
    private LiferayMailingUtil mailingUtil;
    
    @Value("${newsletter.mail.from}")
    private String fromEmailAddress;
    
    @Value("${newsletter.admin.name}")
    private String fromName;
    
    @Value("${newsletter.articles.type}")
    private String newsletterArticleType;
    
    @Value("${newsletter.articles.category}")
    private String newsletterArticleCategory;
    
    @Value("${newsletter.articles.tag}")
    private String newsletterArticleTag;
    
    @Async
    @Override
    public void sendTestMailing(Long mailingId, String testEmail, ThemeDisplay themeDisplay) {
        try {

            NewsletterMailing mailing = findById(mailingId).getPayload();
            
            String content = EmailFormat.getEmailFromTemplate(mailing, themeDisplay);
            content = content.replace(LIST_NAME_TOKEN, mailing.getName());
            //Add full path to images
            content = EmailFormat.fixImagesPath(content, themeDisplay);

            //Replace User Info
            content = EmailFormat.replaceUserInfo(content, null, themeDisplay);

            String title = mailing.getName();

            InternetAddress fromIA = new InternetAddress(mailing.getList().getFromEmail());
            InternetAddress toIA = new InternetAddress(testEmail);
            MailMessage message = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIA, title, content);
            message.setHTMLFormat(true);
            mailingUtil.sendEmail(message);

            //mailingUtil.sendArticleByEmail(mailing.getArticleId(), themeDisplay, testEmail, fromEmailAddress);

        } catch (PortalException ex) {
            logger.error("Error while trying to read article", ex);
        } catch (SystemException ex) {
            logger.error("Error while trying to read article", ex);
        } catch (Exception ex) {
            logger.error("Error while trying to read article", ex);
        }
    }

    @Async
    @Override
    public void sendMailing(Long mailingId, ThemeDisplay themeDisplay, Long archiveId) {
        try {
            NewsletterMailing mailing = findById(mailingId).getPayload();
            String content = EmailFormat.getEmailFromTemplate(mailing, themeDisplay);

            //Add full path to images
            content = EmailFormat.fixImagesPath(content, themeDisplay);

            String title = mailing.getName();

            InternetAddress fromIA = new InternetAddress(mailing.getList().getFromEmail(), mailing.getList().getFromName());
            InternetAddress toIA = new InternetAddress();
            MailMessage message = EmailFormat.getMailMessageWithAttachedImages(fromIA, toIA, title, content);
            String bodyContent = message.getBody();

            for (NewsletterSubscription newsletterSubscription : mailing.getList().getSubscriptions()) {
                if (newsletterSubscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
                    NewsletterSubscriptor subscriptor = newsletterSubscription.getSubscriptor();
                    String name = subscriptor.getFirstName() + " " + subscriptor.getLastName();

                    MailMessage personalMessage = message;

                    toIA = new InternetAddress(subscriptor.getEmail(), name);
                    logger.info("Sending to " + name + "<" + subscriptor.getEmail() + ">");
                    personalMessage.setTo(toIA);

                    //Replace User Info
                    String tmpContent = EmailFormat.replaceUserInfo(bodyContent, newsletterSubscription, themeDisplay, archiveId);
                    personalMessage.setBody(tmpContent);

                    mailingUtil.sendEmail(personalMessage);
                }
            }
            logger.info("End Sending personalizable conent");
        } catch (Exception ex) {
            logger.error("Error while trying to read article", ex);
        }
    }

    /**
     *
     * @param mailingId
     * @param themeDisplay
     * @return
     */
    @Override
    public String getEmailFromTemplate(Long mailingId, ThemeDisplay themeDisplay) {
        String content = "";
        try {
            NewsletterMailing mailing = findById(mailingId).getPayload();
            content = EmailFormat.getEmailFromTemplate(mailing, themeDisplay);
        } catch (Exception ex) {
            logger.error("Error trying to get email from template. Exception: " + ex.getMessage(), ex);
        }
        return content;
    }

    /**
     * Validates the template format
     *
     * @param mailingId
     * @return
     */
    @Override
    public boolean validateTemplateFormat(Long mailingId) {
        boolean result = true;
        NewsletterMailing mailing = findById(mailingId).getPayload();
        String templateContent = mailing.getTemplate().getTemplate();
        String content = EmailFormat.validateTemplateFormat(templateContent);
        if (content == null || content.isEmpty()) {
            result = false;
        }
        return result;
    }

    @Override
    public ServiceActionResult<ListResultsDTO<MailingDTO>> findAllMailings(ThemeDisplay themeDisplay, int start, int limit, String orderField, String orderType) {
        // get total records count
        int totalRecords = findAllCount(themeDisplay);

        // get records
        ServiceActionResult<List<NewsletterMailing>> listResult = findAll(themeDisplay, start, limit, orderField, orderType);
        if (!listResult.isSuccess()) {
            return ServiceActionResult.buildFailure(null);
        }
        
        // fill dtos
        List<MailingDTO> dtos = new ArrayList<MailingDTO>();
        for(NewsletterMailing entity: listResult.getPayload()){
            MailingDTO dto = binder.bindFromBusinessObject(MailingDTO.class, entity);
            dto = fillMailingDTO(entity, dto, themeDisplay);
            dtos.add(dto);
        }

        // create and return ListResultsDTO
        ListResultsDTO<MailingDTO> dto = new ListResultsDTO<MailingDTO>(limit, start, totalRecords, dtos);
        return ServiceActionResult.buildSuccess(dto);
    }

    @Override
    public ServiceActionResult<MailingDTO> findMailing(Long id, ThemeDisplay themeDisplay) {
        ServiceActionResult<NewsletterMailing> findResult = findById(id);
        if (findResult.isSuccess()) {
            MailingDTO dto = binder.bindFromBusinessObject(MailingDTO.class, findResult.getPayload());
            dto = fillMailingDTO(findResult.getPayload(), dto, themeDisplay);
            return ServiceActionResult.buildSuccess(dto);
        }
        return ServiceActionResult.buildFailure(null);
    }

    @Override
    public ServiceActionResult<MailingDTO> saveMailing(ThemeDisplay themeDisplay, MailingDTO mailingDTO) {

        // find existing mailing or creat a new one
        NewsletterMailing mailing = null;
        if (mailingDTO.getId() != null) {
            mailing = findById(mailingDTO.getId()).getPayload();
            // delete previous blocks
            List<NewsletterTemplateBlock> blocks = templateBlockService.findAllByMailing(mailing);
            for(NewsletterTemplateBlock block: blocks){
                templateBlockService.delete(block);
            }
        } else {
            mailing = binder.extractFromDto(NewsletterMailing.class, mailingDTO);
        }
        fillMailing(mailingDTO, mailing);

        // add group and company information
        mailing.setCompanyid(themeDisplay.getCompanyId());
        mailing.setGroupid(themeDisplay.getScopeGroupId());

        
        // validate required fields
        Set errors = validator.validate(mailing);
        if (!errors.isEmpty()) {
            List<String> errorsList = new ArrayList<String>();
            fillViolations(errors, errorsList);
            return ServiceActionResult.buildFailure(null, errorsList);
        }

        // save mailing information
        ServiceActionResult<NewsletterMailing> result = save(mailing);
        if (result.isSuccess()) {
                    mailing = result.getPayload();
                    
                    // add new blocks
                    if (mailingDTO.getArticleIds() != null){
                        int blockOrder = 1;
                        for(Long articleId: mailingDTO.getArticleIds()){
                            NewsletterTemplateBlock block = new NewsletterTemplateBlock();
                            block.setArticleId(articleId);
                            block.setMailing(mailing);
                            block.setCompanyid(themeDisplay.getCompanyId());
                            block.setGroupid(themeDisplay.getScopeGroupId());
                            block.setBlockOrder(blockOrder);
                            blockOrder++;
                            ServiceActionResult<NewsletterTemplateBlock> addBlockResult = templateBlockService.save(block);
                            if (!addBlockResult.isSuccess()){
                                return ServiceActionResult.buildFailure(null);
                            }
                        }
                    }

            
            
            MailingDTO savedDTO = binder.bindFromBusinessObject(MailingDTO.class, result.getPayload());
            savedDTO = fillMailingDTO(result.getPayload(), savedDTO, themeDisplay);
            return ServiceActionResult.buildSuccess(savedDTO);
        } else {
            return ServiceActionResult.buildFailure(null);
        }
    }
    
    private MailingDTO fillMailingDTO(NewsletterMailing mailing, MailingDTO mailingDTO, ThemeDisplay themeDisplay) {
        List<NewsletterTemplateBlock> blocks = templateBlockService.findAllByMailing(mailing);
        mailingDTO.setArticles(new ArrayList<ArticleDTO>());
        if (blocks != null){
            for(NewsletterTemplateBlock block: blocks){
                ArticleDTO articleDTO = new ArticleDTO();
                try{
                    JournalArticle article =  JournalArticleLocalServiceUtil.getLatestArticle(themeDisplay.getScopeGroupId(), String.valueOf(block.getArticleId()));
                    articleDTO.setId(block.getArticleId());
                    articleDTO.setName(article.getTitle());
                    mailingDTO.getArticles().add(articleDTO);
                }catch(Exception e){
                    logger.error("Error trying to obtain article. Exception: " + e.getMessage(), e);
                }
            }
        }
        return mailingDTO;

    }

    private void fillMailing(MailingDTO mailingDTO, NewsletterMailing mailing) {
        mailing.setName(mailingDTO.getName());

        NewsletterCategory category = categoryService.findById(mailingDTO.getListId()).getPayload();
        mailing.setList(category);

        NewsletterTemplate template = templateService.findById(mailingDTO.getTemplateId()).getPayload();
        mailing.setTemplate(template);

    }

    @Override
    public ServiceActionResult deleteMailing(ThemeDisplay themeDisplay, Long mailingId) {
        ServiceActionResult<NewsletterMailing> findResult = findById(mailingId);
        if (findResult.isSuccess()) {
            
            // delete blocks
            List<NewsletterTemplateBlock> blocks = templateBlockService.findAllByMailing(findResult.getPayload());
            for(NewsletterTemplateBlock block: blocks){
                ServiceActionResult<NewsletterTemplateBlock> deleteBlockResult = templateBlockService.delete(block);
                if (!deleteBlockResult.isSuccess()){
                    return ServiceActionResult.buildFailure(null);
                }
            }
            
            // delete mailing
            return delete(findResult.getPayload());
        }
        return ServiceActionResult.buildFailure(null);
    }

    @Override
    public List<ArticleDTO> findAllArticlesForMailing(ThemeDisplay themeDisplay) {
        List<ArticleDTO> articlesDTO = new ArrayList<ArticleDTO>();
        
        //Get all newsletter articles to create the selectors
        try{
            HashMap<String, JournalArticle> resultArticleNewsletter = new HashMap<String, JournalArticle>();
            try {
                //Search Articles by Type
                List<JournalArticle> articlesByType = ArticleUtils.findArticlesByType(newsletterArticleType);
                
                for (JournalArticle article : articlesByType) {
                    if (!resultArticleNewsletter.containsKey(article.getArticleId())) {
                        resultArticleNewsletter.put(article.getArticleId(), article);
                    }
                }
                //Search Articles by Category
                List<JournalArticle> articlesByCategory = ArticleUtils.findArticlesByCategory(newsletterArticleCategory);
                for (JournalArticle article : articlesByCategory) {
                    if (!resultArticleNewsletter.containsKey(article.getArticleId())) {
                        resultArticleNewsletter.put(article.getArticleId(), article);
                    }
                }
                //Search Articles by Tag
                List<JournalArticle> articlesByTag = ArticleUtils.findArticlesByTag(themeDisplay, newsletterArticleTag);
                for (JournalArticle article : articlesByTag) {
                    if (!resultArticleNewsletter.containsKey(article.getArticleId())) {
                        resultArticleNewsletter.put(article.getArticleId(), article);
                    }
                }
                articlesDTO = fillArticlesDTO(new ArrayList(resultArticleNewsletter.values()), themeDisplay);
                
            } catch (SystemException ex) {
                logger.error("Could not filter the articles by this category, type, or tag", ex);
            } catch (PortalException ex) {
                logger.error("Could not filter the articles by this category, type, or tag", ex);
            }
            List<JournalArticle> newsletterArticles = new ArrayList<JournalArticle>(resultArticleNewsletter.values());
        }catch(Exception e){
            logger.error("Error trying to get articles for Mailing. Exception: " + e.getMessage(), e);
        }
        
        return articlesDTO;
        
    }

    private List<ArticleDTO> fillArticlesDTO(List<JournalArticle> articles, ThemeDisplay themeDisplay) {
        List<ArticleDTO> dtos = new ArrayList<ArticleDTO>();
        for(JournalArticle a: articles){
            ArticleDTO dto = new ArticleDTO();
            dto.setId(Long.valueOf(a.getArticleId()));
            dto.setName(a.getTitle(themeDisplay.getLocale()));
            dtos.add(dto);
        }
        return dtos;
    }

   
   
}
