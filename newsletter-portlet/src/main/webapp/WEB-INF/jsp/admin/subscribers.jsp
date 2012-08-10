<%-- 
    Document   : subscribers
    Created on : 06/08/2012, 17:07:17
    Author     : ggenovese <gustavo.genovese@rotterdam-cs.com>
--%>


<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<fmt:setBundle basename="Language"/>
<fmt:setBundle basename="Newsletter" var="newsletter"/>
<portlet:defineObjects />
<portlet:resourceURL id="getSubscribers" var="getSubscribersURL"/>
<portlet:resourceURL id="getSubscriptorData" var="getSubscriptorDataURL"/>
<portlet:resourceURL id="editDeleteSubscriptor" var="editDeleteSubscriptorURL"/>
<portlet:actionURL var="importSubscribersURL">
    <portlet:param name="action" value="importSubscribers"/>
</portlet:actionURL>

<html>
    <head>
        <script type="text/javascript">
            jQuery(document).ready(function(){
                fillListsCombo();
                createGrid();
                jQuery('#saveSubscriber<portlet:namespace/>').button();
                jQuery('#cancelSubscriber<portlet:namespace/>').button();
                jQuery('#importButton<portlet:namespace/>').button();
                jQuery('#exportButton<portlet:namespace/>').button();
                jQuery('#importSubscribersButton<portlet:namespace/>').button();
                jQuery('#cancelImportSubscribers<portlet:namespace/>').button();
                
                function fillListsCombo(){
                    var elements = '<option value=""><fmt:message key="newsletter.admin.general.all.lists"/></option>';
                    <c:forEach items="${lists}" var="list">
                    elements += '<option value="${list.id}">${list.name}</option>';
                    </c:forEach>
                    jQuery('#listsCombo<portlet:namespace/>').append(elements);
                }
                
                function createGrid() {
                    jQuery("#subscribersGrid<portlet:namespace/>").jqGrid({
                        url:'${getSubscribersURL}',
                        datatype: "json",
                        autowidth: true,
                        postData: {
                            nocache: (new Date()).getTime(),
                            selectedList: jQuery('#listsCombo<portlet:namespace/>').val(),
                            onlyInactive: jQuery('#activeStatusCheck<portlet:namespace/>').is(':checked')
                        },
                        colNames:[
                            '<fmt:message key="newsletter.admin.general.id"/>',
                            '<fmt:message key="newsletter.admin.general.name"/>',
                            '<fmt:message key="newsletter.admin.general.email"/>',
                            '<fmt:message key="newsletter.admin.general.action"/>'
                        ],
                        colModel:[
                            { name : 'subscriptorId',  width : 50,  searchoptions: {}, sortable : false, search : false },
                            { name : 'subscriptorFullname' ,  width : 150, searchoptions: {}, sortable : false, search : false },
                            { name : 'subscriptorEmail',  width : 100, searchoptions: {},  sortable : false, search : false },
                            { name : 'action', width : 100, searchoptions: {},  sortable : false, search : false }
                        ],
                        jsonReader : {
                            root: "payload.result",
                            page: "payload.currentPage",
                            total: "payload.totalPages",
                            records: "payload.totalRecords",
                            repeatitems : false,
                            id : "subscriptorId"
                        },
                        gridComplete: function() {
                            var ids = jQuery('#subscribersGrid<portlet:namespace/>').jqGrid('getDataIDs');
                            for(var i = 0; i < ids.length; i++){
                                var editIcon = '<div style="float:left; margin-left:20px;" class="ui-icon ui-icon-pencil editSubscriptorActionIcon" subscriptorId="' + ids[i] + '"></div>';
                                var deleteIcon = '<div style="float:left; margin-left:20px;" class="ui-icon ui-icon-trash deleteSubscriptorActionIcon" subscriptorId="' + ids[i] + '"></div>';
                                jQuery("#subscribersGrid<portlet:namespace/>").jqGrid('setRowData',ids[i],{ 'action' : editIcon + deleteIcon } );                                
                            }
                            
                            jQuery('.editSubscriptorActionIcon').click(function(){
                                var id=jQuery(this).attr("subscriptorId");
                                getRowSubscriptorShowForm(id, 'UPDATE', false);
                            });

                            jQuery('.deleteSubscriptorActionIcon').click(function(){
                                var id=jQuery(this).attr("subscriptorId");
                                getRowSubscriptorShowForm(id, 'DELETE', true);
                            });                            
                        },
                        pager : '#subscribersPager<portlet:namespace/>',
                        sortname : 'subscriptorId',
                        viewrecords: true,
                        sortorder: 'asc',
                        caption : "<fmt:message key="newsletter.admin.subscribers"/>",
                        height: '100%',
                        rowNum : 15
                    });
                    jQuery("#subscribersGrid<portlet:namespace/>")
                        .jqGrid('navGrid','#subscribersPager<portlet:namespace/>',
                            {edit:false,add:false,del:false});
                }

                jQuery('#listsCombo<portlet:namespace/>, #activeStatusCheck<portlet:namespace/>').change(function(){
                    jQuery("#subscribersGrid<portlet:namespace/>").setGridParam({
                        postData: {
                            nocache: (new Date()).getTime(),
                            selectedList: jQuery('#listsCombo<portlet:namespace/>').val(),
                            onlyActive: jQuery('#activeStatusCheck<portlet:namespace/>').is(':checked')
                        }
                    }).trigger("reloadGrid");
                });
                

                
                function getRowSubscriptorShowForm(id, action, disabledFields){
                    jQuery.ajax({
                       url: '${getSubscriptorDataURL}',
                       dataType: 'json',
                       data:{
                           subscriptorId: id
                       },
                       success: function(data){
                           if (data && data.success){
                               jQuery('.fieldsToDisable').attr("disabled", disabledFields);
                               jQuery('#subscribersForm<portlet:namespace/> input[name="action"]').val(action);
                               jQuery('#subscribersForm<portlet:namespace/> input[name="subscriptorId"]').val(id);
                               jQuery('#subscribersForm<portlet:namespace/> input[name="firstName"]').val(data.payload[0].subscriptorFirstName);
                               jQuery('#subscribersForm<portlet:namespace/> input[name="lastName"]').val(data.payload[0].subscriptorLastName);
                               jQuery('#subscribersForm<portlet:namespace/> input[name="email"]').val(data.payload[0].subscriptorEmail);
                               
                               var subscribedLists = '';
                               for (var i=0; i<data.payload.length; i++){
                                   subscribedLists += data.payload[i].categoryName;
                                   if (i+1 < data.payload.length)
                                       subscribedLists += ', ';
                               }
                               jQuery('#subscribedLists<portlet:namespace/>').html(subscribedLists);
                               
                               jQuery('#gridSubscribersContainer<portlet:namespace/>').hide();
                               jQuery('#subscribersCrudContainer<portlet:namespace/>').show();
                           }
                       }
                    });
                }

                jQuery('#cancelSubscriber<portlet:namespace/>').click(function(){
                    clearErrors();
                    validator.resetForm();
                    backToGrid();
                });
                
                function backToGrid(){
                    jQuery('#subscribersCrudContainer<portlet:namespace/>').hide();                
                    jQuery('#gridSubscribersContainer<portlet:namespace/>').show();
                }
                
                var validator = jQuery('#subscribersForm<portlet:namespace/>').validate({
                    submitHandler: function(form) {
                        clearErrors();
                        jQuery(form).ajaxSubmit({
                            dataType: 'json',
                            url: '${editDeleteSubscriptorURL}',
                            success: function(data){
                                if (data && data.success){
                                    jQuery("#subscribersGrid<portlet:namespace/>").trigger('reloadGrid');
                                    backToGrid();
                                }else{
                                    showErrors(data.validationKeys);
                                }
                            }
                        });
                    }
                });
                
                jQuery('#importButton<portlet:namespace/>').click(function(){
                    clearErrors();
                    validatorImport.resetForm();
                    jQuery('#gridSubscribersContainer<portlet:namespace/>').hide();
                    jQuery('#importSubscribers<portlet:namespace/>').show();
                });

                jQuery('#cancelImportSubscribers<portlet:namespace/>').click(function(){
                    jQuery('#importSubscribers<portlet:namespace/>').hide();                
                    jQuery('#gridSubscribersContainer<portlet:namespace/>').show();
                    clearErrors();
                    clearMessages();                    
                });
                
                var validatorImport = jQuery('#importSubscribersForm<portlet:namespace/>').validate({
                    rules: {
                        file: {
                            required: true,
                            accept: "xls|xlsx" 
                        }
                    },
                    submitHandler: function(form) {
                        clearErrors();
                        clearMessages();
                        jQuery(form).ajaxSubmit({
                            url: '${importSubscribersURL}',
                            dataType: 'json',
                            data: {
                                list: jQuery('#listsComboImport<portlet:namespace/>').val()
                            },
                            type: 'POST',
                            success:function(data){
                                if (data && data.success){
                                    showMessages(data.errors);
                                }else{
                                    showErrors(data.errors);
                                }
                            }
                        });
                        
                    }
                });
                
            });
        </script>
    </head>
    <body>
        <%@include file="../commons/errorsView.jsp" %>
        
        <%-- GRID --%>
        <div id="gridSubscribersContainer<portlet:namespace/>">
            <label><fmt:message key="newsletter.admin.subscribers.only.show.subscribers.off"/></label>
            <select id="listsCombo<portlet:namespace/>"></select><br/>

            <input type="checkbox" id="activeStatusCheck<portlet:namespace/>"/>
                <fmt:message key="newsletter.admin.subscribers.only.show.inactive.subscribers" bundle="${newsletter}"/><br/>

            <table id="subscribersGrid<portlet:namespace/>"></table>
            <div id="subscribersPager<portlet:namespace/>"></div>
            
            <button id="importButton<portlet:namespace/>" type="button"><fmt:message key="newsletter.admin.subscribers.import" bundle="${newsletter}"/></button>
            <button id="exportButton<portlet:namespace/>" type="button"><fmt:message key="newsletter.admin.subscribers.export" bundle="${newsletter}"/></button>                         
        </div>
        
        <%-- EDIT/DELETE FORM --%>
        <div style="display: none;" id="subscribersCrudContainer<portlet:namespace/>">
            <form id="subscribersForm<portlet:namespace/>" class="newsletter-forms-form">
                <input type="hidden" name="action"/>
                <input type="hidden" name="subscriptorId"/>
                <table>
                    <tr>
                        <td><label><fmt:message key="general.firstname"/></label></td>
                        <td><input class="newsletter-forms-input-text fieldsToDisable required" type="text" name="firstName"/></td>
                    </tr>
                    <tr>
                        <td><label><fmt:message key="general.lastname"/></label></td>
                        <td><input class="newsletter-forms-input-text fieldsToDisable required" type="text" name="lastName"/></td>
                    </tr>
                    <tr>
                        <td><label><fmt:message key="general.email"/></label></td>
                        <td><input class="newsletter-forms-input-text fieldsToDisable required" type="text" name="email"/></td>
                    </tr>
                    <tr>
                        <td><label><fmt:message key="newsletter.admin.lists"/></label></td>
                        <td><span id="subscribedLists<portlet:namespace/>"></span></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <button type="submit" id="saveSubscriber<portlet:namespace/>"><fmt:message key="general.save"/></button>
                            <button type="button" id="cancelSubscriber<portlet:namespace/>"><fmt:message key="newsletter.admin.general.cancel"/></button>
                        </td>
                    </tr>                    
                </table>
            </form>
        </div>

        <%-- IMPORT SUBSCRIBERS FORM --%>
        <div style="display: none;" id="importSubscribers<portlet:namespace/>">
            <form id="importSubscribersForm<portlet:namespace/>" class="newsletter-forms-form" enctype="multipart/form-data" method="post">
                <table>
                    <tr>
                        <td>
                            <label><fmt:message key="newsletter.admin.subscribers.import.category.select" bundle="${newsletter}"/></label><br/>
                            <select id="listsComboImport<portlet:namespace/>" class="required">
                                <option value=""><fmt:message key="newsletter.admin.category.select" bundle="${newsletter}"/></option>
                                <c:forEach items="${lists}" var="list">
                                    <option value="${list.id}">${list.name}</option>
                                </c:forEach>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <input type="file" name="file" class="required newsletter-forms-input-text"/><br/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <fmt:message key="newsletter.admin.subscriber.import.excel.format.instructions1" bundle="${newsletter}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <fmt:message key="newsletter.admin.subscriber.import.excel.format.instructions2" bundle="${newsletter}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <button type="submit" id="importSubscribersButton<portlet:namespace/>">Import</button>
                            <button type="button" id="cancelImportSubscribers<portlet:namespace/>"><fmt:message key="newsletter.admin.general.back"/></button>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </body>
</html>
