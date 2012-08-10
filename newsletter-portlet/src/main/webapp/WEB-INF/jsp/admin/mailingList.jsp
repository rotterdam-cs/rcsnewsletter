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
<portlet:resourceURL id='testEmail' var='testEmailUrl'/>


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
<form id="send-test-form-<portlet:namespace/>">
    <table>
        <tr>
            <td><input type="text" id="input-send-test-<portlet:namespace/>" name="sendTestEmail" class="required email" style="width:200px" placeholder="<fmt:message key="newsletter.tab.mailing.field.placeholder.testemail" />"  />&nbsp;</td>
            <td><input type="button" id="btn-send-test-<portlet:namespace/>" value="<fmt:message key="newsletter.tab.mailing.button.sendtest" />"  /></td>
        </tr>
    </table>
</form>
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
        
        
        // click on 'Send Test' button
        jQuery('#btn-send-test-<portlet:namespace/>').click(function(){
            clearMessages();
            clearErrors();
            
            // validate mailing selection and email address
            var fieldValid = jQuery('#send-test-form-<portlet:namespace/>').valid();
            var rowSelectionValid = jQuery('#mailing-list-<portlet:namespace/> input.radio-field:checked').size() == 1;
            
            if (!rowSelectionValid){
                showErrors(['<fmt:message key="newsletter.tab.mailing.error.nomailingselection"/>']);
            }
            
            if (fieldValid && rowSelectionValid){
                var mailingId = jQuery('#mailing-list-<portlet:namespace/> input.radio-field:checked').val();
                var emailAddress = jQuery('#input-send-test-<portlet:namespace/>').val();
                sendTestEmail(mailingId, emailAddress);
            }
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
                '',
                '<fmt:message key="newsletter.admin.general.id" />',
                '<fmt:message key="newsletter.admin.general.name" />',
                '<fmt:message key="newsletter.tab.mailing.field.label.list" />',
                ''],
            colModel:[
                {name:'radio',     index:'id',        width:30,         sortable: false,  search:false, formatter: radioColumnFormatter},
                {name:'id',        index:'id',        width:40,         sortable: false,  search:false},
                {name:'name',      index:'name',                        sortable: false,  search:false},
                {name:'listName',  index:'listName',                    sortable: false,  search:false},
                {name:'action',    index:'action',    width:40,         sortable: false,  search:false}
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
            onSelectRow: function(id){ 
                jQuery(this).find('tr[id="' + id + '"] input.radio-field').attr('checked', 'checked');
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
    
    /**
     * Formatter for Radio Column
     */
    function radioColumnFormatter ( cellvalue, options, rowObject )
    {
        return '<input type="radio" class="radio-field" name="selectedRow" value="' + rowObject.id + '" style="margin:auto; width:100%" />';
    }
    
    /**
     * 
     */
    function sendTestEmail(mailingId, emailAddress){
         jQuery.ajax({
            url: '${testEmailUrl}'
            ,type: 'POST'
            ,data: {
                mailingId: mailingId, 
                emailAddress: emailAddress
            }
            ,success: function(response){
                if (response.success){
                    showMessages(response.messages);
                }else{
                    showErrors(response.validationKeys);
                }
            }
            ,failure: function(response){
                showErrors(['<fmt:message key="newsletter.tab.mailing.error.sendingtestemail"/>']);
            }
        });
    }
    
</script>