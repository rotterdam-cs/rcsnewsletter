package com.rcs.newsletter.core.dto;

import java.io.Serializable;
import java.util.Date;

import org.jdto.annotation.Source;
import org.jdto.mergers.DateFormatMerger;

/**
 *
 *  @author Pablo Rendon <pablo.rendon@rotterdam-cs.com>
 */
public class NewsletterScheduleDTO  implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
    private Long id;
    
    private Boolean pending;
    
    @Source("mailing.id")
    private long mailingId;
    
    @Source("mailing.name")
    private String mailing;
    
    private Date sendDate;
    
    @Source(value = "sendDate", merger=DateFormatMerger.class, mergerParam="yyyy-MM-dd HH:mm")
    private String sendDateFormatted;
       
	public String getMailing() {
		return mailing;
	}

	public void setMailing(String mailing) {
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
