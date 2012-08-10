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
<portlet:resourceURL id='getMailingList' var='getMailingUrl'/>
<portlet:resourceURL id='editMailing' var='editMailingUrl'/>


<%--
    Header and Actions
    ##################
--%>
<input id="btn-addmailing-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.tab.mailing.button.addmailing" />" />
<hr>


<%--
    Grid and Pager
    ##############
--%>
<table id="mailing-list-<portlet:namespace/>"></table>
<div id="mailing-list-pager<portlet:namespace/>"></div>

<br>
<table>
    <tr>
        <td><input type="text" style="width:200px" placeholder="<fmt:message key="newsletter.tab.mailing.field.placeholder.testemail" />"  />&nbsp;</td>
        <td><input type="button" id="btn-send-test-<portlet:namespace/>" value="<fmt:message key="newsletter.tab.mailing.button.sendtest" />"  /></td>
    </tr>
</table>
<br>
<input type="button" id="btn-send-test-<portlet:namespace/>" value="<fmt:message key="newsletter.tab.mailing.button.sendnewsletter" />"  />





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
        jQuery('#mailing-panel input[type="button"]').button();
    }
    
    
    /**
     * Init UI events
     */
    function initEvents(){
        // click on 'Add Mailing' button
        jQuery('#btn-addmailing-<portlet:namespace/>').click(function(){
            jQuery('#mailing-panel').load('${editMailingUrl}');
        });
        
        
    }
    
    /**
     * Creates JQuery Grid for Mailing
     */
    function createGrid(){
        jQuery("#mailing-list-<portlet:namespace/>").jqGrid({
            url:'${getMailingUrl}' + "&nocache=" + (new Date()).getTime(),
            datatype: "json",
            autowidth: true,
            colNames:[
                '<fmt:message key="newsletter.admin.general.id" />',
                '<fmt:message key="newsletter.admin.general.name" />',
                '<fmt:message key="newsletter.tab.mailing.field.label.list" />',
                ''],
            colModel:[
                {name:'id',      index:'id',        width:40,         sortable: false, search:false},
                {name:'name',    index:'name',      sortable: false,  search:false},
                {name:'listName',    index:'listName',      sortable: false,  search:false},
                {name:'action',  index:'action',    width:40,         sortable: false, search:false}
            ],
            jsonReader : {
                root: "payload.result",
                repeatitems : false,
                id : "id",
                page: "payload.currentPage",
                total: "payload.totalPages"
            },
            gridComplete: function() {
                var ids = jQuery("#mailing-list-<portlet:namespace/>").jqGrid('getDataIDs');
                for(var i = 0; i < ids.length; i++){
                    var editIcon = '<div style="float:left; margin-left:20px; cursor:pointer" class="ui-icon ui-icon-pencil" onclick="javascript:editMailing(' + ids[i] + ');"></div>';
                    var deleteIcon = '<div style="float:left; margin-left:20px; cursor:pointer" class="ui-icon ui-icon-trash" onclick="javascript:deleteMailing(' + ids[i] + ');"></div>';

                    jQuery("#mailing-list-<portlet:namespace/>").jqGrid('setRowData',ids[i],{ 'action' : editIcon + deleteIcon } );
                }
                // Fix jQGrid scroll issue
                jQuery('#mailing-panel .ui-jqgrid-bdiv').css('overflow', 'hidden');
            },
            pager: '#mailing-list-pager<portlet:namespace/>',
            sortname: 'id',
            viewrecords: true,
            sortorder: "asc",
            caption: '<fmt:message key="newsletter.tab.mailing.grid.title" />',
            height: '100%',
            rowNum: 10
            
        });
        jQuery("#mailing-list-<portlet:namespace/>").jqGrid('navGrid','#mailing-list-pager<portlet:namespace/>',{edit:false,add:false,del:false});
    }
    
    
    
    /**
     * Edit mailing
     */
    function editMailing(mailingId){
        jQuery('#mailing-panel').load('${editMailingUrl}', {id: mailingId});
    }
    
    /**
     * Delete mailing
     */
    function deleteMailing(mailingId){
        jQuery('#mailing-panel').load('${editMailingUrl}', {id: mailingId, remove: true});
       
        
        
    }
    
    
    
</script>