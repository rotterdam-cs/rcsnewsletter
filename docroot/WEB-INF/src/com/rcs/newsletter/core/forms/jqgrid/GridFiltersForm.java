package com.rcs.newsletter.core.forms.jqgrid;

/**
 *
 * @author marcoslacoste
 */
public class GridFiltersForm {

    private String groupOp;
    private GridFilterRule rules[];

   

    /**
     * @return the groupOp
     */
    public String getGroupOp() {
        return groupOp;
    }

    /**
     * @param groupOp the groupOp to set
     */
    public void setGroupOp(String groupOp) {
        this.groupOp = groupOp;
    }

    /**
     * @return the rules
     */
    public GridFilterRule[] getRules() {
        return rules;
    }

    /**
     * @param rules the rules to set
     */
    public void setRules(GridFilterRule[] rules) {
        this.rules = rules;
    }
}
