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
<portlet:resourceURL id='editTemplate' var='editTemplateUrl'/>


<%--
    Header and Actions
    ##################
--%>
<input id="btn-addtemplate-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.tab.templates.button.addtemplate" />" />
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
        // click on 'Add Template' button
        jQuery('#btn-addtemplate-<portlet:namespace/>').click(function(){
           jQuery('#templates-panel').load('${editTemplateUrl}');
        });
    }
    
    /**
     * Creates JQuery Grid for Templates
     */
    function createGrid(){
        jQuery("#templates-list-<portlet:namespace/>").jqGrid({
            url:'${getTemplatesUrl}' + "&nocache=" + (new Date()).getTime(),
            datatype: "json",
            autowidth: true,
            colNames:[
                    '<fmt:message key="newsletter.admin.general.id" />',
                    '<fmt:message key="newsletter.admin.general.name" />',
                    ''],
            colModel:[
                    {name:'id', index:'id', width:55},
                    {name:'name',index:'name'},
                    {name:'action', index:'action', width:80},
            ],
            jsonReader : {
                     root: "payload.result",
                     repeatitems : false,
                     id : "id"
            },
            gridComplete: function() {
                var ids = jQuery("#templates-list-<portlet:namespace/>").jqGrid('getDataIDs');
                for(var i = 0; i < ids.length; i++){
                    var editIcon = '<div style="float:left; margin-left:20px;" class="ui-icon ui-icon-pencil" onclick="javascript:editTemplate(' + ids[i] + ');"></div>';
                    var deleteIcon = '<div style="float:left; margin-left:20px;" class="ui-icon ui-icon-trash" onclick="javascript:deleteTemplate(' + ids[i] + ');"></div>';

                    jQuery("#templates-list-<portlet:namespace/>").jqGrid('setRowData',ids[i],{ 'action' : editIcon + deleteIcon } );
                }
            },
            pager: '#templates-list-pager<portlet:namespace/>',
            sortname: 'id',
            viewrecords: true,
            sortorder: "asc",
            caption:""
        });
        jQuery("#templates-list-<portlet:namespace/>").jqGrid('navGrid','#templates-list-pager<portlet:namespace/>',{edit:false,add:false,del:false});
    }
    
    
    
    
</script>