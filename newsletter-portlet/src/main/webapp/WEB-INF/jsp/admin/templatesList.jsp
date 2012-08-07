<%-- 
    Document   : templateEdit
    Created on : Aug 7, 2012, 10:45:59 AM
    Author     : marcoslacoste
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<fmt:setBundle basename="Language"/>
<portlet:defineObjects />

<%--
    URLs
    ##################
--%>
<portlet:resourceURL id='getTemplatesList' var='getTemplatesUrl'/>


<%--
    Header and Actions
    ##################
--%>
<input id="btn-help-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.tab.templates.button.addtemplate" />" />
<br><br>


<%--
    Grid and Pager
    ##############
--%>
<table id="templates-list-<portlet:namespace/>"></table>
<div id="templates-list-pager<portlet:namespace/>"></div>



<script type="text/javascript">
    
    jQuery(document).ready(function(){

       initEvents();
       createGrid();
       
    });
    
    
    /**
     * Init UI events
     */
    function initEvents(){
        jQuery('#btn-help-<portlet:namespace/>').click(function(){
           alert('load form');
           
        });
    }
    
    /**
     * Creates JQuery Grid for Templates
     */
    function createGrid(){
        jQuery("#templates-list-<portlet:namespace/>").jqGrid({
            url:'${getTemplatesUrl}',
            datatype: "json",
            autowidth: true,
            colNames:[
                    '<fmt:message key="newsletter.admin.general.id" />',
                    '<fmt:message key="newsletter.admin.general.name" />'],
            colModel:[
                    {name:'id',index:'id', width:55},
                    {name:'name',index:'name', width: '100%'},
            ],
            rowNum:10,
            rowList:[10,20,30],
            pager: '#templates-list-pager<portlet:namespace/>',
            sortname: 'id',
            viewrecords: true,
            sortorder: "asc",
            caption:""
        });
        jQuery("#templates-list-<portlet:namespace/>").jqGrid('navGrid','#templates-list-pager<portlet:namespace/>',{edit:true,add:false,del:true});
    }
    
    
    
    
</script>