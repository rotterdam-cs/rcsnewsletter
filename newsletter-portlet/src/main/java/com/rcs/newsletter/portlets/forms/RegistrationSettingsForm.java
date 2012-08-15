/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.portlets.forms;

/**
 *
 * @author marcoslacoste
 */
public class RegistrationSettingsForm {
    private Long listId;
    private boolean disabledNameFields;

    /**
     * @return the listId
     */
    public Long getListId() {
        return listId;
    }

    /**
     * @param listId the listId to set
     */
    public void setListId(Long listId) {
        this.listId = listId;
    }

    /**
     * @return the disabledNameFields
     */
    public boolean isDisabledNameFields() {
        return disabledNameFields;
    }

    /**
     * @param disabledNameFields the disabledNameFields to set
     */
    public void setDisabledNameFields(boolean disabledNameFields) {
        this.disabledNameFields = disabledNameFields;
    }
    
}
