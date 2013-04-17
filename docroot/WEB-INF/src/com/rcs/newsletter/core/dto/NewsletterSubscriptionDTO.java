package com.rcs.newsletter.core.dto;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.rcs.newsletter.core.model.enums.SubscriptionStatus;

import java.io.Serializable;
import org.jdto.annotation.Source;
import org.jdto.annotation.Sources;
import org.jdto.mergers.StringFormatMerger;

/**
 *
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
public class NewsletterSubscriptionDTO implements Serializable{
	private Log log = LogFactoryUtil.getLog(NewsletterSubscriptionDTO.class);
	private long id;

    private SubscriptionStatus status;    

    @Source("subscriptor.id")
    private long subscriptorId;
    
    @Source("subscriptor.firstName")
    private String subscriptorFirstName = "";
    
    @Source("subscriptor.lastName")
    private String subscriptorLastName = "";
    
    @Sources( value = {
                    @Source("subscriptor.firstName"),
                    @Source("subscriptor.lastName")
              },
              merger=StringFormatMerger.class,
              mergerParam="%s %s")
    private String subscriptorFullname = "";
    
    @Source("subscriptor.email")
    private String subscriptorEmail;
    
    private String activationKey;
    private String deactivationKey;
    
    @Source("category.id")
    private String categoryId;
    
    @Source("category.name")
    private String categoryName;

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getDeactivationKey() {
        return deactivationKey;
    }

    public void setDeactivationKey(String deactivationKey) {
        this.deactivationKey = deactivationKey;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public String getSubscriptorEmail() {
        return subscriptorEmail;
    }

    public void setSubscriptorEmail(String subscriptorEmail) {
        this.subscriptorEmail = subscriptorEmail;
    }

    public String getSubscriptorFirstName() {
        return subscriptorFirstName;
    }

    public void setSubscriptorFirstName(String subscriptorFirstName) {
        if (subscriptorFirstName != null && !subscriptorFirstName.equals("null")) {
    		this.subscriptorFirstName = subscriptorFirstName;
        } else {
        	this.subscriptorFirstName = "";
        } 
    }

    public String getSubscriptorLastName() {
        return subscriptorLastName;
    }

    public void setSubscriptorLastName(String subscriptorLastName) {
        if (subscriptorLastName != null && !subscriptorLastName.equals("null")) {
    		this.subscriptorLastName = subscriptorLastName;
        } else {
        	this.subscriptorLastName = "";
        }    	
    }

    public String getSubscriptorFullname() {
        return subscriptorFullname;
    }

    public void setSubscriptorFullname(String subscriptorFullname) {
    	if (subscriptorFullname != null && !subscriptorFullname.equals("null null")) {
    		this.subscriptorFullname = subscriptorFullname;
        } else {
        	this.subscriptorFullname = "";
        }
    	
    }

    public long getSubscriptorId() {
        return subscriptorId;
    }

    public void setSubscriptorId(long subscriptorId) {
        this.subscriptorId = subscriptorId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
