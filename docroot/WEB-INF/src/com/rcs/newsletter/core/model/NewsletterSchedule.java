package com.rcs.newsletter.core.model;

import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Pablo Rendon <pablo.rendon@rotterdam-cs.com>
 */
@Entity
@Table(name = "newsletter_schedule")
public class NewsletterSchedule extends NewsletterEntity {
    private static final long serialVersionUID = 1L;    
    
    private Boolean pending = true;
    
    @NotNull
    @ManyToOne
    private NewsletterMailing mailing;    
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="send_date")
    private Date sendDate;
    
    private String urlHome;
    private String portalURL;
    private Locale locale;
    
    
    
    public Boolean getPending() {
		return pending;
	}
	public void setPending(Boolean pending) {
		this.pending = pending;
	}
	
	public NewsletterMailing getMailing() {
		return mailing;
	}
	public void setMailing(NewsletterMailing mailing) {
		this.mailing = mailing;
	}
	
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	
	public String getUrlHome() {
		return urlHome;
	}
	public void setUrlHome(String urlHome) {
		this.urlHome = urlHome;
	}
	public String getPortalURL() {
		return portalURL;
	}
	public void setPortalURL(String portalURL) {
		this.portalURL = portalURL;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

    
    
}