/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rcs.newsletter.core.forms.jqgrid;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author marcoslacoste
 */
public class GridRestrictionsUtil {
 
    
    /**
     * Creates a Criterion based on search filters in GridFiltersForm
     * @param filtersForm
     * @return 
     */
    public static Criterion createCriterion(GridFiltersForm filtersForm){
        if (filtersForm.getGroupOp() == null || filtersForm.getRules() == null || filtersForm.getRules().length == 0) {
            return null;
        }

        Junction junction = null;

        // AND / OR junction
        if (filtersForm.getGroupOp().equalsIgnoreCase("and")) {
            junction = Restrictions.conjunction();
        } else {
            junction = Restrictions.disjunction();
        }


        // rules
        for (GridFilterRule rule : filtersForm.getRules()) {

            Criterion c = null;

            // operator == "cn" => contains
            if (rule.getOp().equals("cn")) {
                junction.add(Restrictions.ilike(rule.getField(), rule.getData(), MatchMode.ANYWHERE));
            }

        }
        return junction;
    }
}
