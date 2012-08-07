<%-- 
    Document   : admin
    Created on : 06/08/2012, 15:46:52
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
<!-- TABS -->
<portlet:resourceURL id="subscribers" var="subscribersURL"/>
<portlet:resourceURL id="mailing" var="mailingURL"/>
<portlet:resourceURL id="archive" var="archiveURL"/>
<portlet:resourceURL id="templates" var="templatesURL"/>

<!-- AJAX requests -->
<portlet:resourceURL id="getLists" var="getListsURL"/>
<portlet:resourceURL id="addEditDeleteList" var="addEditDeleteListURL"/>
<portlet:resourceURL id="getListData" var="getListDataURL"/>

<html>
    <head>
        <script type="text/javascript">
            Liferay.on('portletReady', function(event) {
                if('_' + event.portletId + '_' != '<portlet:namespace/>') {
                    return;
                }
                jQuery( "#newsletterAdmin<portlet:namespace/>" ).tabs({
                    ajaxOptions: {
                        error: function( xhr, status, index, anchor ) {
                            $( anchor.hash ).html("Couldn't load this tab. We'll try to fix this as soon as possible.");
                        }
                    }
                });
                jQuery('#formContainer<portlet:namespace/>').hide();
                createGrid();
                
                jQuery('#addList<portlet:namespace/>').button();
                jQuery('#save<portlet:namespace/>').button();
                jQuery('#cancel<portlet:namespace/>').button();

                function createGrid() {
                    jQuery("#listsGrid<portlet:namespace/>").jqGrid({
                        url:'${getListsURL}' + "&nocache=" + (new Date()).getTime(),
                        datatype: "json",
                        autowidth: true,
                        colNames:[
                            '<fmt:message key="newsletter.admin.general.id"/>',
                            '<fmt:message key="newsletter.admin.general.name"/>',
                            '<fmt:message key="newsletter.admin.general.description"/>',
                            '<fmt:message key="newsletter.admin.general.action"/>'
                        ],
                        colModel:[
                            { name : 'id',          width : 50,  searchoptions: {}, sortable : false, search : false },
                            { name : 'name' ,       width : 150, searchoptions: {}, sortable : false, search : false },
                            { name : 'description', width : 100, searchoptions: {},  sortable : false, search : false },
                            { name : 'action',      width : 100, searchoptions: {},  sortable : false, search : false }
                        ],
                        rowNum : 10,
                        rowList : [10,20,30],
                        pager : '#listPager<portlet:namespace/>',
                        sortname : 'id',
                        viewRecords: true,
                        jsonReader : {
                            root: "payload.result",
                            repeatitems : false,
                            id : "id"
                        },
                        caption : "<fmt:message key="newsletter.admin.lists"/>",
                        height: '100%',
                        gridComplete: function() {
                            var ids = jQuery('#listsGrid<portlet:namespace/>').jqGrid('getDataIDs');
                            for(var i = 0; i < ids.length; i++){
                                var editIcon = '<div style="float:left; margin-left:20px;" class="ui-icon ui-icon-pencil editActionIcon" listId="' + ids[i] + '"></div>';
                                var deleteIcon = '<div style="float:left; margin-left:20px;" class="ui-icon ui-icon-trash deleteActionIcon" listId="' + ids[i] + '"></div>';
                                jQuery("#listsGrid<portlet:namespace/>").jqGrid('setRowData',ids[i],{ 'action' : editIcon + deleteIcon } );
                            }
                        }
                    });
                    jQuery("#listsGrid<portlet:namespace/>")
                        .jqGrid('navGrid','#listPager<portlet:namespace/>',
                            {edit:false,add:false,del:false});
                }
                
                function backToGrid(){
                    jQuery('#formContainer<portlet:namespace/>').hide();                    
                    jQuery('#gridContainer<portlet:namespace/>').show();
                }
                
                var validator = jQuery('#addEditDeleteCategory<portlet:namespace/>').validate({
                    submitHandler: function(form) {
                        clearErrors();
                        jQuery(form).ajaxSubmit({
                            dataType: 'json',
                            url: '${addEditDeleteListURL}',
                            success: function(data){
                                if (data && data.success){
                                    jQuery("#listsGrid<portlet:namespace/>").trigger('reloadGrid');
                                    backToGrid();
                                }else{
                                    showErrors(data.validationKeys);
                                }
                            }
                        });
                    }
                });
                
                jQuery('#addList<portlet:namespace/>').click(function(){
                    showAddEditDeleteForm('CREATE');
                });
                
                jQuery('#cancel<portlet:namespace/>').click(function(){
                    clearErrors();
                    validator.resetForm();
                    backToGrid();
                });
                
                jQuery(document).on('click', '.editActionIcon', function(){
                    var id = jQuery(this).attr('listId');
                    showAddEditDeleteForm('UPDATE');
                    alert(id);
                });
                
                jQuery(document).on('click', '.deleteActionIcon', function(){
                    var id = jQuery(this).attr('listId');
                    showAddEditDeleteForm('DELETE');
                    alert(id);
                });

                function showAddEditDeleteForm(action){
                    jQuery('#addEditDeleteCategory<portlet:namespace/>').resetForm();
                    validator.resetForm();
                    jQuery('#action<portlet:namespace/>').val(action);
                    jQuery('#gridContainer<portlet:namespace/>').hide();
                    jQuery('#formContainer<portlet:namespace/>').show();                    
                }
            });
        </script>
        <%@include file="../commons/errorsFunctions.jsp" %>
    </head>
    <body>
        <div id="newsletterAdmin<portlet:namespace/>">
            <ul>
                <li><a href="#lists<portlet:namespace/>"><fmt:message key="newsletter.admin.lists"/></a></li>
                <li><a href="${subscribersURL}"><fmt:message key="newsletter.admin.subscribers"/></a></li>
                <li><a href="${mailingURL}"><fmt:message key="newsletter.admin.mailing"/></a></li>
                <li><a href="${archiveURL}"><fmt:message key="newsletter.admin.archive"/></a></li>
                <li><a href="${templatesURL}"><fmt:message key="newsletter.admin.templates"/></a></li>
            </ul>

            <!-- LISTS TAB -->
            <div id="lists<portlet:namespace/>">
                <!-- Errors viewer -->
                <%@include file="../commons/errorsView.jsp" %>
                
                <!-- GRID -->
                <div id="gridContainer<portlet:namespace/>">
                    <button type="button" id="addList<portlet:namespace/>"><fmt:message key="newsletter.admin.lists.add" bundle="${newsletter}"/></button>
                    <table id="listsGrid<portlet:namespace/>"></table>
                    <div id="listPager<portlet:namespace/>"></div>
                </div>
                
                <!-- CRUD form -->
                <div id="formContainer<portlet:namespace/>">
                    <form id="addEditDeleteCategory<portlet:namespace/>" class="newsletter-forms-form">
                        <input type="hidden" id="action<portlet:namespace/>" name="action"/>
                        <table>
                            <tr>
                                <td><label><fmt:message key="newsletter.admin.general.name"/></label></td>
                                <td><input type="text" class="required" name="name"/></td>
                            </tr>
                            <tr>
                                <td><label><fmt:message key="newsletter.admin.general.description"/></label></td>
                                <td><textarea name="description" class="required"></textarea></td>
                            </tr>
                            <tr>
                                <td><label><fmt:message key="newsletter.admin.category.fromname" bundle="${newsletter}"/></label></td>
                                <td><input type="text" name="fromname" class="required"/></td>
                            </tr>
                            <tr>
                                <td><label><fmt:message key="newsletter.admin.category.fromemail" bundle="${newsletter}"/></label></td>
                                <td><input type="text" name="fromemail" class="required"/></td>
                            </tr>
                            <tr>
                                <td><label><fmt:message key="newsletter.admin.category.adminemail" bundle="${newsletter}"/></label></td>
                                <td><input type="text" name="adminemail" class=""/></td>
                            </tr>
                            <tr>
                                <td colspan="2"><label>* <fmt:message key="newsletter.admin.category.adminemail.details" bundle="${newsletter}"/></label></td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <button type="submit" id="save<portlet:namespace/>"><fmt:message key="general.save"/></button>
                                    <button type="button" id="cancel<portlet:namespace/>"><fmt:message key="newsletter.admin.general.cancel"/></button>
                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
