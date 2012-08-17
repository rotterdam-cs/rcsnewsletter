
package com.rcs.newsletter.commons;

import javax.portlet.ActionRequest;
import javax.portlet.filter.ActionRequestWrapper;

/**
 *
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
public class NewsletterActionRequest extends ActionRequestWrapper {

    public NewsletterActionRequest(ActionRequest request) {
        super(request);
    }    
}
