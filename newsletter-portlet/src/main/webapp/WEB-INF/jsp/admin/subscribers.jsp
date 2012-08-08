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

<html>
    <head>
        <script type="text/javascript">
            jQuery(document).ready(function(){
                fillListsCombo();
                createGrid();
                
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
                        postData: {
                            nocache: (new Date()).getTime(),
                            selectedList: jQuery('#listsCombo<portlet:namespace/>').val(),
                            onlyInactive: jQuery('#activeStatusCheck<portlet:namespace/>').is(':checked')
                        },
                        autowidth: true,
                        colNames:[
                            '<fmt:message key="newsletter.admin.general.id"/>',
                            '<fmt:message key="newsletter.admin.general.name"/>',
                            '<fmt:message key="newsletter.admin.general.email"/>',
                            '<fmt:message key="newsletter.admin.general.action"/>'
                        ],
                        colModel:[
                            { name : 'id',   index: 'id',  width : 50,  searchoptions: {}, sortable : false, search : false },
                            { name : 'subscriptorFullname' ,  width : 150, searchoptions: {}, sortable : false, search : false },
                            { name : 'subscriptorEmail',  width : 100, searchoptions: {},  sortable : false, search : false },
                            { name : 'action', width : 100, searchoptions: {},  sortable : false, search : false }
                        ],
                        rowNum : 10,
                        rowList : [10,20,30],
                        pager : '#subscribersPager<portlet:namespace/>',
                        sortname : 'id',
                        viewRecords: true,
                        jsonReader : {
                            root: "payload.result",
                            page: "payload.currentPage",
                            total: "payload.totalPages",
                            repeatitems : false,
                            id : "id"
                        },
                        caption : "<fmt:message key="newsletter.admin.subscribers"/>",
                        height: '100%',
                        gridComplete: function() {
                            var ids = jQuery('#subscribersGrid<portlet:namespace/>').jqGrid('getDataIDs');
                            for(var i = 0; i < ids.length; i++){
                            }
                        }
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
                
            });
        </script>
    </head>
    <body>
        <%@include file="../commons/errorsView.jsp" %>
        <label><fmt:message key="newsletter.admin.subscribers.only.show.subscribers.off"/></label>
        <select id="listsCombo<portlet:namespace/>"></select><br/>
        
        <input type="checkbox" id="activeStatusCheck<portlet:namespace/>"/>
            <fmt:message key="newsletter.admin.subscribers.only.show.inactive.subscribers" bundle="${newsletter}"/><br/>
        
        <table id="subscribersGrid<portlet:namespace/>"></table>
        <div id="subscribersPager<portlet:namespace/>"></div>
    </body>
</html>
