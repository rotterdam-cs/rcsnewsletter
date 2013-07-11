package com.rcs.newsletter.core.dto;

import java.io.Serializable;

/**
 *
 * @author ggenovese <gustavo.genovese@rotterdam-cs.com>
 */
public class CreateMultipleSubscriptionsResult implements Serializable{
	private static final long serialVersionUID = 1L;
	
	boolean success;
    private long rowsProcessed;
    private long rowsOmitted;
    private long subscriptionsCreated;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getRowsOmitted() {
        return rowsOmitted;
    }

    public void setRowsOmitted(long rowsOmitted) {
        this.rowsOmitted = rowsOmitted;
    }

    public long getRowsProcessed() {
        return rowsProcessed;
    }

    public void setRowsProcessed(long rowsProcessed) {
        this.rowsProcessed = rowsProcessed;
    }

    public long getSubscriptionsCreated() {
        return subscriptionsCreated;
    }

    public void setSubscriptionsCreated(long subscriptionsCreated) {
        this.subscriptionsCreated = subscriptionsCreated;
    }

    
}
