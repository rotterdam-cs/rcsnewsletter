
package com.rcs.newsletter.core.model;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Default things for all entities.
 * @author Ariel Parra <ariel@rotterdam-cs.com>
 */
@MappedSuperclass
public abstract class NewsletterEntity implements Serializable{
    
    public static final String ID = "id";
    public static final String GROUPID = "groupid";
    public static final String COMPANYID = "companyid";
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Long id;

    private Long groupid;
    
    private Long companyid;
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyid() {
        return companyid;
    }

    public void setCompanyid(Long companyid) {
        this.companyid = companyid;
    }

    public Long getGroupid() {
        return groupid;
    }

    public void setGroupid(Long groupid) {
        this.groupid = groupid;
    }    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NewsletterEntity other = (NewsletterEntity) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
}
