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
import com.rcs.newsletter.core.dto.JournalArticleDTO;
import com.rcs.newsletter.core.dto.NewsletterArchiveDTO;
import com.rcs.newsletter.core.dto.NewsletterMailingDTO;
import com.rcs.newsletter.core.forms.jqgrid.GridForm;
import com.rcs.newsletter.core.forms.jqgrid.GridRestrictionsUtil;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
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
    private NewsletterArchiveService archiveService;

    @Autowired
    private NewsletterSubscriptorService subscriptorService;

    
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

    public ServiceActionResult<ListResultsDTO<NewsletterMailingDTO>> findAllMailings(ThemeDisplay themeDisplay, GridForm gridForm, String orderField, String orderType) {
        // get total records count
        int totalRecords = findAllCount(themeDisplay);

        // get records
        Session currentSession = sessionFactory.getCurrentSession();
        Criteria criteria = currentSession.createCriteria(NewsletterMailing.class);
        criteria.addOrder(Order.asc(orderField));
        
        // add search filters
        if (gridForm != null){
            Criterion criterion = GridRestrictionsUtil.createCriterion(gridForm.getFiltersForm());
            if (criterion != null){
                criteria.add(criterion);
            }
        }
        List<NewsletterMailing> list = criteria.list();
        
        
        // fill dtos
        List<NewsletterMailingDTO> dtos = new ArrayList<NewsletterMailingDTO>();
        for(NewsletterMailing entity: list){
            NewsletterMailingDTO dto = binder.bindFromBusinessObject(NewsletterMailingDTO.class, entity);
            dto = fillMailingDTO(entity, dto, themeDisplay);
            dtos.add(dto);
        }

        // create and return ListResultsDTO
        ListResultsDTO<NewsletterMailingDTO> dto = new ListResultsDTO<NewsletterMailingDTO>(gridForm.getRows(), gridForm.calculateStart(), totalRecords, dtos);
        return ServiceActionResult.buildSuccess(dto);
    }
    
    

    public ServiceActionResult<NewsletterMailingDTO> findMailing(Long id, ThemeDisplay themeDisplay) {
        ServiceActionResult<NewsletterMailing> findResult = findById(id);
        if (findResult.isSuccess()) {
            NewsletterMailingDTO dto = binder.bindFromBusinessObject(NewsletterMailingDTO.class, findResult.getPayload());
            dto = fillMailingDTO(findResult.getPayload(), dto, themeDisplay);
            return ServiceActionResult.buildSuccess(dto);
        }
        return ServiceActionResult.buildFailure(null);
    }

    public ServiceActionResult<NewsletterMailingDTO> saveMailing(ThemeDisplay themeDisplay, NewsletterMailingDTO mailingDTO) {

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

            
            
            NewsletterMailingDTO savedDTO = binder.bindFromBusinessObject(NewsletterMailingDTO.class, result.getPayload());
            savedDTO = fillMailingDTO(result.getPayload(), savedDTO, themeDisplay);
            return ServiceActionResult.buildSuccess(savedDTO);
        } else {
            return ServiceActionResult.buildFailure(null);
        }
    }
    
    private NewsletterMailingDTO fillMailingDTO(NewsletterMailing mailing, NewsletterMailingDTO mailingDTO, ThemeDisplay themeDisplay) {
        List<NewsletterTemplateBlock> blocks = templateBlockService.findAllByMailing(mailing);
        
        // blocks information
        mailingDTO.setArticles(new ArrayList<JournalArticleDTO>());
        if (blocks != null){
            for(NewsletterTemplateBlock block: blocks){
                JournalArticleDTO articleDTO = new JournalArticleDTO();
                try{
                    JournalArticle article =  JournalArticleLocalServiceUtil.getLatestArticle(themeDisplay.getScopeGroupId(), String.valueOf(block.getArticleId()));
                    articleDTO.setId(block.getArticleId());
                    articleDTO.setName(article.getTitle(themeDisplay.getLocale()));
                    mailingDTO.getArticles().add(articleDTO);
                }catch(Exception e){
                    logger.error("Error trying to obtain article. Exception: " + e.getMessage(), e);
                }
            }
        }
        mailingDTO.setSubscribersNumber(subscriptorService.findAllByStatusAndCategoryCount(themeDisplay, SubscriptionStatus.ACTIVE, mailing.getList().getId()));
        
        // article names
        String articleNames = "";
        for(int i = 0; i < mailingDTO.getArticles().size() ; i++){
            articleNames+=mailingDTO.getArticles().get(i).getName();
            if (i < (mailingDTO.getArticles().size() - 1)){
                articleNames+=", ";
            }
        }
        mailingDTO.setArticleNames(articleNames);
        
        
        return mailingDTO;

    }

    private void fillMailing(NewsletterMailingDTO mailingDTO, NewsletterMailing mailing) {
        mailing.setName(mailingDTO.getName());

        NewsletterCategory category = categoryService.findById(mailingDTO.getListId()).getPayload();
        mailing.setList(category);

        NewsletterTemplate template = templateService.findById(mailingDTO.getTemplateId()).getPayload();
        mailing.setTemplate(template);

    }

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

    public List<JournalArticleDTO> findAllArticlesForMailing(ThemeDisplay themeDisplay) {
        List<JournalArticleDTO> articlesDTO = new ArrayList<JournalArticleDTO>();
        
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

    private List<JournalArticleDTO> fillArticlesDTO(List<JournalArticle> articles, ThemeDisplay themeDisplay) {
        List<JournalArticleDTO> dtos = new ArrayList<JournalArticleDTO>();
        for(JournalArticle a: articles){
            JournalArticleDTO dto = new JournalArticleDTO();
            dto.setId(Long.valueOf(a.getArticleId()));
            dto.setName(a.getTitle(themeDisplay.getLocale()));
            dtos.add(dto);
        }
        return dtos;
    }

    public ServiceActionResult sendNewsletter(Long mailingId, ThemeDisplay themeDisplay) {
        // obtain mailing info
        ServiceActionResult<NewsletterMailing> findMailingResult= findById(mailingId);
        if (!findMailingResult.isSuccess()){
            return ServiceActionResult.buildFailure(null);
        }
        
        // get email body
        String emailBody = getEmailFromTemplate(mailingId, themeDisplay);
        
        // create archive entry
        logger.info("Creating archive instance");
        ServiceActionResult<NewsletterArchiveDTO> saveArchiveResult = archiveService.saveArchive(findMailingResult.getPayload(), emailBody, themeDisplay);
        if (!saveArchiveResult.isSuccess()){
            return ServiceActionResult.buildFailure(null);
        }
        NewsletterArchiveDTO archiveDTO = saveArchiveResult.getPayload();
        
        
        // send newsletter
        logger.info("Sending newsletter...");
        sendMailing(mailingId, themeDisplay, archiveDTO.getId());
        
        
        // delete mailing after it's sent
        logger.info("Deleting mailing...");
        ServiceActionResult deleteMailing = deleteMailing(themeDisplay, mailingId);
        if (!deleteMailing.isSuccess()){
            return ServiceActionResult.buildFailure(null);
        }
        return ServiceActionResult.buildSuccess(null);
        
        
    }
    
    
   
   
}
