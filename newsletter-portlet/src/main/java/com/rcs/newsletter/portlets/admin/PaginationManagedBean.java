package com.rcs.newsletter.portlets.admin;

import java.util.ArrayList;
import java.util.List;

public class PaginationManagedBean {
    protected int paginationStart = 0;
    protected int paginationLimit = 3;
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
        setPaginationPages((int) Math.ceil( paginationTotal/paginationLimit ));
        setPaginationCurrentPage((int) Math.ceil( paginationStart/paginationLimit ));        
        setPaginationShowingResults(( paginationCurrentPage * paginationLimit ) + paginationLimit);
        paginationPagesList = new ArrayList();
        for (int i = 0; i < paginationPages; i++) {
            paginationPagesList.add(i);            
        }
    }
    
    public void nextPage() {
        if (getPaginationCurrentPage() < getPaginationPages()) {
            setPaginationStart( paginationStart + paginationLimit );
            setPaginationCurrentPage((int) Math.ceil(paginationStart/paginationLimit));
        }
    }
    
    public void prevPage() {
        if (getPaginationCurrentPage() > 1) {
            setPaginationStart(paginationStart - paginationLimit);
            setPaginationCurrentPage((int) Math.ceil(paginationStart/paginationLimit));
        }
    }
    
    public void gotoPage() {
        if (getPaginationCurrentPage() >= 0 && getPaginationCurrentPage() <= getPaginationPages()) {
            setPaginationStart(getPaginationCurrentPage() * getPaginationLimit());            
        }
    }
}
