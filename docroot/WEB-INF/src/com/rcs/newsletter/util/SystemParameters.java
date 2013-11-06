package com.rcs.newsletter.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SystemParameters {
	
    @Value("${newsletter.articles.category}")
    private String newsletterArticleCategory;
    
    @Value("${newsletter.articles.type}")
    private String newsletterArticleType;
    
    @Value("${newsletter.articles.tag}")
    private String newsletterArticleTag;


	public String getNewsletterArticleTag() {
		return newsletterArticleTag;
	}


	public String getNewsletterArticleType() {
		return newsletterArticleType;
	}
	
	public String getNewsletterArticleCategory() {
		return newsletterArticleCategory;
	}

}