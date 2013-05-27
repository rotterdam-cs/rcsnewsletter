/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.core.dto;

import java.util.Date;
import org.jdto.annotation.Source;
import org.jdto.mergers.DateFormatMerger;

/**
 *
 *  @author Pablo Rendon <pablo.rendon@rotterdam-cs.com>
 */
public class NewsletterScheduleDTO  extends DataTransferObject {
    
    private Long id;
    
    private Boolean pending;
    
    @Source("mailing.id")
    private long mailingId;
    
    @Source("mailing.name")
    private long mailing;
    
    private Date sendDate;
    
    @Source(value = "sendDate", merger=DateFormatMerger.class, mergerParam="yyyy-MM-dd hh:mm")
    private String sendDateFormatted;

    
    
	public long getMailing() {
		return mailing;
	}

	public void setMailing(long mailing) {
		this.mailing = mailing;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getPending() {
		return pending;
	}

	public void setPending(Boolean pending) {
		this.pending = pending;
	}

	public long getMailingId() {
		return mailingId;
	}

	public void setMailingId(long mailingId) {
		this.mailingId = mailingId;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public String getSendDateFormatted() {
		return sendDateFormatted;
	}

	public void setSendDateFormatted(String sendDateFormatted) {
		this.sendDateFormatted = sendDateFormatted;
	}
    
    
}
