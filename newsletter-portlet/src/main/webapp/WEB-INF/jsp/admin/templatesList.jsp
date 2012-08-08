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
<hr>



<%--
    Grid and Pager
    ##############
--%>
<table id="templates-list-<portlet:namespace/>"></table>
<div id="templates-list-pager<portlet:namespace/>"></div>





<script type="text/javascript">
    
    jQuery(document).ready(function(){
        styleUI();
        initEvents();
        createGrid();
    });
    
    /**
     * Apply styles to UI
     */
    function styleUI(){
        // styles on buttons
        jQuery('#templates-panel input[type="button"]').button();
    }
    
    
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
                {name:'id',     index:'id',     width:40,         sortable: false, search:false},
                {name:'name',   index:'name',   sortable: false,  search:false},
                {name:'action', index:'action', width:40,         sortable: false, search:false}
            ],
            jsonReader : {
                root: "payload.result",
                repeatitems : false,
                id : "id",
                page: "payload.currentPage",
                total: "payload.totalPages"
            },
            gridComplete: function() {
                var ids = jQuery("#templates-list-<portlet:namespace/>").jqGrid('getDataIDs');
                for(var i = 0; i < ids.length; i++){
                    var editIcon = '<div style="float:left; margin-left:20px; cursor:pointer" class="ui-icon ui-icon-pencil" onclick="javascript:editTemplate(' + ids[i] + ');"></div>';
                    var deleteIcon = '<div style="float:left; margin-left:20px; cursor:pointer" class="ui-icon ui-icon-trash" onclick="javascript:deleteTemplate(' + ids[i] + ');"></div>';

                    jQuery("#templates-list-<portlet:namespace/>").jqGrid('setRowData',ids[i],{ 'action' : editIcon + deleteIcon } );
                }
                // Fix jQGrid scroll issue
                jQuery('#templates-panel .ui-jqgrid-bdiv').css('overflow', 'hidden');
            },
            pager: '#templates-list-pager<portlet:namespace/>',
            sortname: 'id',
            viewrecords: true,
            sortorder: "asc",
            caption:"",
            height: '100%',
            rowNum: 10
            
        });
        jQuery("#templates-list-<portlet:namespace/>").jqGrid('navGrid','#templates-list-pager<portlet:namespace/>',{edit:false,add:false,del:false});
    }
    
    
    
    /**
     * Edit template
     */
    function editTemplate(templateId){
        jQuery('#templates-panel').load('${editTemplateUrl}', {id: templateId});
    }
    
    /**
     * Delete template
     */
    function deleteTemplate(templateId){
        jQuery('#templates-panel').load('${editTemplateUrl}', {id: templateId, remove: true});
       
        
        
    }
    
    
    
</script>