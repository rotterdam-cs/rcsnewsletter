package com.rcs.newsletter.portlets.admin;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PaginationManagedBean implements Serializable{
    
    private static Log log = LogFactoryUtil.getLog(SubscriberAdminManagedBean.class);    
    
    protected int paginationStart = -1;
    protected int paginationLimit = -1;
    protected int paginationTotal;
    protected int paginationPages;
    protected int paginationCurrentPage;
    protected int paginationShowingResults;
    protected List paginationPagesList = new ArrayList();
    
    //Pagination Getter/Setter
    public int getPaginationCurrentPage() {
        return paginationCurrentPage;
    }

    public void setPaginationCurrentPage(int paginationCurrentPage) {
        this.paginationCurrentPage = paginationCurrentPage;
    }

    public int getPaginationLimit() {
        return paginationLimit;
    }

    public void setPaginationLimit(int paginationLimit) {
        this.paginationLimit = paginationLimit;
    }

    public int getPaginationPages() {
        return paginationPages;
    }

    public void setPaginationPages(int paginationPages) {
        this.paginationPages = paginationPages;
    }

    public int getPaginationStart() {
        return paginationStart;
    }

    public void setPaginationStart(int paginationStart) {
        this.paginationStart = paginationStart;
    }

    public int getPaginationTotal() {
        return paginationTotal;
    }

    public List getPaginationPagesList() {
        return paginationPagesList;
    }

    public void setPaginationPagesList(List paginationPagesList) {
        this.paginationPagesList = paginationPagesList;
    }

    public int getPaginationShowingResults() {
        return paginationShowingResults;
    }

    public void setPaginationShowingResults(int paginationShowingResults) {
        this.paginationShowingResults = paginationShowingResults;
    }
            
    public void setPaginationTotal(int paginationTotal) {
        this.paginationTotal = paginationTotal;
        double pagesDouble =  Double.parseDouble(Integer.toString(paginationTotal))/Double.parseDouble(Integer.toString(paginationLimit));
        setPaginationPages((int) Math.ceil( pagesDouble ));
        double currentPageDouble =  Double.parseDouble(Integer.toString(paginationStart))/Double.parseDouble(Integer.toString(paginationLimit));
        setPaginationCurrentPage((int) Math.ceil( currentPageDouble ));        
        setPaginationShowingResults(( paginationCurrentPage * paginationLimit ) + paginationLimit);
        paginationPagesList = new ArrayList();
        for (int i = 0; i < paginationPages; i++) {
            paginationPagesList.add(i);            
        }
    }
    
    public void nextPage() {
        if (getPaginationCurrentPage()+1 < getPaginationPages()) {
            setPaginationStart( paginationStart + paginationLimit );
            double currentPageDouble =  Double.parseDouble(Integer.toString(paginationStart))/Double.parseDouble(Integer.toString(paginationLimit));
            setPaginationCurrentPage((int) Math.ceil(currentPageDouble));
        }
    }
    
    public void prevPage() {
        if (getPaginationCurrentPage() > 0) {
            setPaginationStart(paginationStart - paginationLimit);
            double currentPageDouble =  Double.parseDouble(Integer.toString(paginationStart))/Double.parseDouble(Integer.toString(paginationLimit));
            setPaginationCurrentPage((int) Math.ceil(currentPageDouble));
        }
    }
    
    public void gotoPage() {
        if (getPaginationCurrentPage() >= 0 && getPaginationCurrentPage() <= getPaginationPages()) {
            setPaginationStart(getPaginationCurrentPage() * getPaginationLimit());            
        }
    }
    
    public void gotoFirstPage() {        
        setPaginationStart(0);        
    }
    
    public void gotoLastPage() {        
        setPaginationStart(getPaginationPages()+1);            
    }
}
