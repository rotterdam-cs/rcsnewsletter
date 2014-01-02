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
<portlet:resourceURL id='sendNewsletter' var='sendNewsletterUrl'/>
<portlet:resourceURL id='sheduleNewsletter' var='sheduleNewsletterUrl'/>



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

<input type="button" id="btn-send-newsletter-<portlet:namespace/>" value="<fmt:message key="newsletter.tab.mailing.button.sendnewsletter" />"  />
        
<%--
@@ToDo        
<input type="text" value="" id="schedule" name="schedule" class="hasDatepicker">
--%>

<%--
    Dialogs
    ##################
--%>
<div style="display:none">

    <div id="send-newsletter-dialog-<portlet:namespace/>" title="<fmt:message key="newsletter.tab.mailing.dialog.sendnewsletter.title"/>">
    </div>
</div>
<div style="display:none">

    <div id="shedule-newsletter-dialog-<portlet:namespace/>" title="Schedule">
        <p><div id="scheduleDatepicker-<portlet:namespace/>" ></div> 

  			<input id="inputScheduleHours-<portlet:namespace/>"/ style="display:none">
  			<div id="scheduleHours-<portlet:namespace/>"></div>
  			
        </p>
    </div>
</div>

<script type="text/javascript">
    
    jQuery(document).ready(function(){
        styleUI();
        initEvents();
        createGrid();
        /*
        @@ToDo
        jQuery('#schedule').datetimepicker();
        */
    });
    
    /**
     * Apply styles to UI
     */
    function styleUI(){
        // styles on buttons
        jQuery('#mailing-panel input[type="button"]').button();
        
        //  confirm send newsletter dialog
        jQuery("#send-newsletter-dialog-<portlet:namespace/>").dialog({
            autoOpen: false,
            modal: true,
            width: 400,
            buttons: {
                "<fmt:message key="newsletter.tab.mailing.button.sendnewsletter" />": function(){
                    jQuery("#send-newsletter-dialog-<portlet:namespace/>").dialog('close');
                    var rowId = jQuery('#mailing-list-<portlet:namespace/>').jqGrid('getGridParam', 'selrow');
                    var rowData = jQuery('#mailing-list-<portlet:namespace/>').jqGrid('getRowData', rowId);
                    sendNewsletter(rowData.id);
                },
                "<fmt:message key="newsletter.common.cancel" />": function(){
                    jQuery("#send-newsletter-dialog-<portlet:namespace/>").dialog('close');
                },
                "<fmt:message key="newsletter.tab.mailing.button.schedulenewsletter" />": function(){
                	jQuery("#shedule-newsletter-dialog-<portlet:namespace/>").dialog({
                		autoOpen: true,
                		modal: true,
                		width: 400,
                		buttons:{
                            "<fmt:message key="newsletter.tab.mailing.button.schedulenewsletter" />": function(){
                            	jQuery("#send-newsletter-dialog-<portlet:namespace/>").dialog('close');
                                jQuery("#shedule-newsletter-dialog-<portlet:namespace/>").dialog('close');
                                var rowId = jQuery('#mailing-list-<portlet:namespace/>').jqGrid('getGridParam', 'selrow');
                                var rowData = jQuery('#mailing-list-<portlet:namespace/>').jqGrid('getRowData', rowId);
                                sheduleNewsletter(rowData.id);
                            },
                            "<fmt:message key="newsletter.common.cancel" />": function(){
                                jQuery("#shedule-newsletter-dialog-<portlet:namespace/>").dialog('close');
                            }
                		}
                		
                	});
                }
            }
        });
        
        
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
        
        // click on 'Send Test' button
        jQuery('#btn-send-newsletter-<portlet:namespace/>').click(function(){
            clearMessages();
            clearErrors();
            
            // validate mailing selection and email address
            var rowSelectionValid = jQuery('#mailing-list-<portlet:namespace/> input.radio-field:checked').size() == 1;
            
            if (!rowSelectionValid){
                showErrors(['<fmt:message key="newsletter.tab.mailing.error.nomailingselection"/>']);
            }
            
            if (rowSelectionValid){
                openSendNewsletterDialog();
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
                '',
                '',
                ''],
            colModel:[
                {name:'radio',              index:'id',                 width:30,   sortable: false,  search:false, formatter: radioColumnFormatter},
                {name:'id',                 index:'id',                 width:40,   sortable: false,  search:false},
                {name:'name',               index:'name',               searchoptions:{sopt:['cn']},  sortable: false,  search:true},
                {name:'listName',           index:'listName',                       sortable: false,  search:false},
                {name:'subscribersNumber',  index:'subscribersNumber',              sortable: false,  search:false},
                {name:'articleNames',       index:'articleNames',                   sortable: false,  search:false},
                {name:'action',             index:'action',             width:40,   sortable: false,  search:false}
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
                    var editIcon = '<div title="<fmt:message key="newsletter.common.actions.edit"/>"  style="float:left; margin-left:20px; cursor:pointer" class="ui-icon ui-icon-pencil" onclick="javascript:editMailing(' + ids[i] + ');"></div>';
                    var deleteIcon = '<div title="<fmt:message key="newsletter.common.actions.delete"/>"  style="float:left; margin-left:20px; cursor:pointer" class="ui-icon ui-icon-trash" onclick="javascript:deleteMailing(' + ids[i] + ');"></div>';

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
        jQuery("#mailing-list-<portlet:namespace/>").jqGrid('navGrid','#mailing-list-pager<portlet:namespace/>',{edit:false,add:false,del:false},{},{},{},{multipleSearch:true});
        jQuery("#mailing-list-<portlet:namespace/>").hideCol('articleNames');
        jQuery("#mailing-list-<portlet:namespace/>").hideCol('subscribersNumber');
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
        return '<input type="radio" class="radio-field" name="selectedRow" value="' + rowObject.id + '" style="margin:auto; width:100%" onclick="selectRow(\'' + options.rowId + '\')"  />';
    }
    
    
    /**
     * Select a particular grid row
     */ 
    function selectRow(rowId){
        jQuery('#mailing-list-<portlet:namespace/>').jqGrid('setSelection', rowId);
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
    

    function openSendNewsletterDialog(){
         var rowId = jQuery('#mailing-list-<portlet:namespace/>').jqGrid('getGridParam', 'selrow');
         var rowData = jQuery('#mailing-list-<portlet:namespace/>').jqGrid('getRowData', rowId);
         
         var message = '<fmt:message key="newsletter.tab.mailing.dialog.sendnewsletter.msg" />';
         message = message.replace('{MAILING_NAME}', '<b>' + rowData.name + '</b>');
         message = message.replace('{ARTICLE_NAMES}', '<b>' + rowData.articleNames + '</b>');
         message = message.replace('{LIST_NAME}', '<b>' + rowData.listName + '</b>');
         message = message.replace('{SUBSCRIBERS_NUMBER}', '<b>' + rowData.subscribersNumber + '</b>');
         
         jQuery('#send-newsletter-dialog-<portlet:namespace/>').html(message);
         jQuery('#send-newsletter-dialog-<portlet:namespace/>').dialog('open');
    }

    function sendNewsletter(mailingId){
    	jQuery("#newsletterAdmin<portlet:namespace/>").mask('<fmt:message key="newsletter.tab.mailing.sendingnewsletter"/>');
        jQuery.ajax({
            url: '${sendNewsletterUrl}'
            ,type: 'POST'
            ,data: {
                mailingId: mailingId
            }
            ,success: function(response) {
            	jQuery("#newsletterAdmin<portlet:namespace/>").unmask();
                if (response.success){
                    showMessages(response.messages);
                }else{
                    showErrors(response.validationKeys);
                }
                jQuery("#mailing-list-<portlet:namespace/>").trigger('reloadGrid')
            }
            ,failure: function(response){
                showErrors(['<fmt:message key="newsletter.tab.mailing.error.sendingnewsletter"/>']);
            }
        });
    }
    
    /* Schedule Mailing*/
    
    scheduleDatepicker = jQuery( "#scheduleDatepicker-<portlet:namespace/>" ).datepicker({dateFormat: "yy-mm-dd" });
    jQuery( "#scheduleHours-<portlet:namespace/>" ).timepicker({'showMinute':false,'showButtonPanel':false, 'altField': "#inputScheduleHours-<portlet:namespace/>"});
    
    function sheduleNewsletter(mailingId){
    	jQuery("#newsletterAdmin<portlet:namespace/>").mask('<fmt:message key="newsletter.tab.mailing.sendingnewsletter"/>');
       
    	jQuery.ajax({
            url: '${sheduleNewsletterUrl}'
            ,type: 'POST'
            ,data: {
                mailingId: mailingId,
                date: jQuery.datepicker.formatDate("yy-mm-dd",jQuery( "#scheduleDatepicker-<portlet:namespace/>" ).datepicker('getDate')),
                hour: jQuery( "#inputScheduleHours-<portlet:namespace/>" ).val()
            }
            ,success: function(response) {
            	
            	jQuery("#newsletterAdmin<portlet:namespace/>").unmask();
                if (response.success){
                    showMessages(response.messages);
                }else{
                    showErrors(response.validationKeys);
                }
                jQuery("#mailing-list-<portlet:namespace/>").trigger('reloadGrid');
            }
            ,failure: function(response){
            	
                showErrors(['<fmt:message key="newsletter.tab.mailing.error.sendingnewsletter"/>']);
            }
        });
    }




</script>