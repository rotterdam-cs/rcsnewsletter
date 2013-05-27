package com.rcs.newsletter.core.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

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

    
    
}