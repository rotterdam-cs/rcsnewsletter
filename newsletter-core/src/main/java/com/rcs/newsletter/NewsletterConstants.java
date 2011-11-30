
package com.rcs.newsletter;

/**
 * Constants for Newsletter
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public interface NewsletterConstants {    
 
    public static final String BUNDLE_BASENAME = "Language";
    public static final String ALGORITHM_SHA1 = "SHA1";    
    public static final String NEWSLETTER_ADMIN = "admin@liferay.com";
    
    public static final String CONFIRMATION_LINK_TOKEN = "{LINK}";
    public static final String LIST_NAME_TOKEN = "{LIST}";
    public static final String FIRST_NAME_TOKEN = "{FIRSTNAME}";
    public static final String LAST_NAME_TOKEN = "{LASTNAME}";
    
    public static final String NEWSLETTER_BUNDLE = "Newsletter";
    public static final String LANGUAGE_BUNDLE = "Language";
    public static final String SERVER_MESSAGE_BUNDLE = "ServerMessages";
    
    public static final String ORDER_BY_ASC = "asc";
    public static final String ORDER_BY_DESC = "desc";
    
    public static final int PAGINATION_DEFAULT_START = 0;
    public static final int PAGINATION_DEFAULT_LIMIT = 5;
    
}
