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
    
    /* themeDisplay configuration */
    private Long layoutPlid;
    private String pathFriendlyURLPublic;
    private String pathFriendlyURLPrivateUser;
    private String pathFriendlyURLPrivateGroup;
    private String pathImage;
    private String pathMain;
    private String pathContext;
    private String pathThemeImages;
    private String serverName;
    private String CDNHost;
    
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
	
	/* 
	 * themeDisplay configuration 
	 */
	
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
	
	public Long getLayoutPlid() {
		return layoutPlid;
	}
	public void setLayoutPlid(Long layoutPlid) {
		this.layoutPlid = layoutPlid;
	}
	
	public String getPathFriendlyURLPublic() {
		return pathFriendlyURLPublic;
	}
	public void setPathFriendlyURLPublic(String pathFriendlyURLPublic) {
		this.pathFriendlyURLPublic = pathFriendlyURLPublic;
	}
	
	public String getPathFriendlyURLPrivateUser() {
		return pathFriendlyURLPrivateUser;
	}
	public void setPathFriendlyURLPrivateUser(String pathFriendlyURLPrivateUser) {
		this.pathFriendlyURLPrivateUser = pathFriendlyURLPrivateUser;
	}
	
	public String getPathFriendlyURLPrivateGroup() {
		return pathFriendlyURLPrivateGroup;
	}
	public void setPathFriendlyURLPrivateGroup(String pathFriendlyURLPrivateGroup) {
		this.pathFriendlyURLPrivateGroup = pathFriendlyURLPrivateGroup;
	}
	
	public String getPathImage() {
		return pathImage;
	}
	public void setPathImage(String pathImage) {
		this.pathImage = pathImage;
	}
	
	public String getPathMain() {
		return pathMain;
	}
	public void setPathMain(String pathMain) {
		this.pathMain = pathMain;
	}
	
	public String getPathContext() {
		return pathContext;
	}
	public void setPathContext(String pathContext) {
		this.pathContext = pathContext;
	}
	
	public String getPathThemeImages() {
		return pathThemeImages;
	}
	public void setPathThemeImages(String pathThemeImages) {
		this.pathThemeImages = pathThemeImages;
	}
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public String getCDNHost() {
		return CDNHost;
	}
	public void setCDNHost(String CDNHost) {
		this.CDNHost = CDNHost;
	}
    
    
}