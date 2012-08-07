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
                jQuery('#addList<portlet:namespace/>').click(function(){
                    jQuery('#formContainer<portlet:namespace/>').resetForm();
                    jQuery('#gridContainer<portlet:namespace/>').hide();
                    jQuery('#formContainer<portlet:namespace/>').show();
                });

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
                            { name : 'id',          width : 50,  searchoptions: { }, sortable : false, search : false },
                            { name : 'name' ,       width : 150, searchoptions: { }, sortable : false, search : false },
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

                            }
                        }
                    });
                    jQuery("#listsGrid<portlet:namespace/>")
                        .jqGrid('navGrid','#listPager<portlet:namespace/>',
                            {edit:false,add:false,del:false});
                }                
            });
        </script>
    </head>
    <body>
        <div id="newsletterAdmin<portlet:namespace/>" class="newsletter-admin-wrapper">
            <ul>
                <li><a href="#lists<portlet:namespace/>"><fmt:message key="newsletter.admin.lists"/></a></li>
                <li><a href="${subscribersURL}"><fmt:message key="newsletter.admin.subscribers"/></a></li>
                <li><a href="${mailingURL}"><fmt:message key="newsletter.admin.mailing"/></a></li>
                <li><a href="${archiveURL}"><fmt:message key="newsletter.admin.archive"/></a></li>
                <li><a href="${templatesURL}"><fmt:message key="newsletter.admin.templates"/></a></li>
            </ul>

            <div id="lists<portlet:namespace/>">
                <div id="gridContainer<portlet:namespace/>">
                    <button type="button" id="addList<portlet:namespace/>"><fmt:message key="newsletter.admin.lists.add" bundle="${newsletter}"/></button>
                    <table id="listsGrid<portlet:namespace/>"></table>
                    <div id="listPager<portlet:namespace/>"></div>
                </div>
                <div id="formContainer<portlet:namespace/>">
                    <form id="addEditDeleteCategory<portlet:namespace/>">
                        <table>
                            <tr>
                                <td><label><fmt:message key="newsletter.admin.general.name"/></label></td>
                                <td><input type="text" class="required" name="name"/></td>
                            </tr>
                            <tr>
                                <td><label><fmt:message key="newsletter.admin.general.description"/></label></td>
                                <td><input type="text" name="description" class="required"/></td>
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
                                    <button type="button" id="save<portlet:namespace/>"></button>
                                    <button type="button" id="cancel<portlet:namespace/>"></button>
                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
