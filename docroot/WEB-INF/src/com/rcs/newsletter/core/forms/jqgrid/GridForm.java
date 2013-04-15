/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.core.forms.jqgrid;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author marcoslacoste
 */
public class GridForm {
    
    private static Log logger = LogFactoryUtil.getLog(GridForm.class);
    
    private String sord;
    private String sidx;
    private int page;
    private int rows = 15;
    private String filters;
    private GridFiltersForm filtersForm;
    
     

    
    
    /**
     * @return the sord
     */
    public String getSord() {
        return sord;
    }

    /**
     * @param sord the sord to set
     */
    public void setSord(String sord) {
        this.sord = sord;
    }

    /**
     * @return the sidx
     */
    public String getSidx() {
        return sidx;
    }

    /**
     * @param sidx the sidx to set
     */
    public void setSidx(String sidx) {
        this.sidx = sidx;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }

    
    public int calculateStart(){
        return (getPage() -1) * getRows();
    }

    /**
     * @return the rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * @return the filters
     */
    public String getFilters() {
        return filters;
    }

    /**
     * @param filters the filters to set
     */
    public void setFilters(String filters) {
        this.filters = filters;
    }

    /**
     * @return the filtersForm
     */
    public GridFiltersForm getFiltersForm() {
        if (filtersForm == null){
            ObjectMapper mapper = new ObjectMapper();
            if (filters != null && filters.trim().length() > 0){
                try {
                    filtersForm =  mapper.readValue(filters.getBytes(), GridFiltersForm.class);
                } catch (Exception ex) {
                    //logger.error("Error trying to convert GridFiltersForm. Exception: " + ex.getMessage(), ex);
                }
            }
            if (filtersForm == null){
                filtersForm = new GridFiltersForm();
            }
        }
        return filtersForm;
    }
    
    
    public void addFieldAlias(String field, String alias){
        if (getFiltersForm().getRules() != null){
           for(GridFilterRule rule: getFiltersForm().getRules()){
               if (rule.getField().equals(field)){
                   rule.setField(alias);
               }
           }
        }
    } 
   
    
    
    
}
