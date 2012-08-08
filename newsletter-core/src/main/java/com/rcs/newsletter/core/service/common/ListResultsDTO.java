package com.rcs.newsletter.core.service.common;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Miguel Senosiain <miguel.senosiain@rotterdam-cs.com/>
 */
public class ListResultsDTO<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private int limit;
    private int currentPage;
    private int totalRecords;
    private List<T> result;

    public ListResultsDTO(int limit, int startRecord, int totalRecords, List<T> result) {
        this.limit = limit;
        this.currentPage = 1 + (int)Math.ceil((double)startRecord / limit);
        this.totalRecords = totalRecords;
        this.result = result;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public int getTotalPages() {
        //Do the calculations
        if (totalRecords > 0 && limit > 0) {
            double pagesDouble = (double) totalRecords / limit;
            return (int) Math.ceil(pagesDouble);
        } else {
            return 0;
        }
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }
}
