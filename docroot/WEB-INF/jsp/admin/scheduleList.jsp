<%-- 
    Document   : scheduleEdit
    Created on : Aug 13, 2012, 10:45:59 AM
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
<portlet:resourceURL id='getSchedules' var='getSchedulesUrl'/>
<portlet:resourceURL id='viewSchedule' var='viewScheduleUrl'/>



<%--
    Grid and Pager
    ##############
--%>
<table id="schedules-list-<portlet:namespace/>"></table>
<div id="schedules-list-pager<portlet:namespace/>"></div>





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
    }
    
    
    /**
     * Init UI events
     */
    function initEvents(){
    }
    
    /**
     * Creates JQuery Grid for Schedule entires
     */
    function createGrid(){
        jQuery("#schedules-list-<portlet:namespace/>").jqGrid({
            url:'${getSchedulesUrl}' + "&nocache=" + (new Date()).getTime(),
            datatype: "json",
            autowidth: true,
            colNames:[
                '<fmt:message key="newsletter.admin.general.id" />',
                '<fmt:message key="newsletter.tab.schedules.field.label.mailing" />',
                '<fmt:message key="newsletter.tab.schedules.field.label.date" />',
                ''],
            colModel:[
                {name:'id',             index:'id',          width:40,      sortable: false,  search:false},
                {name:'mailing',        index:'mailing',        searchoptions:{sopt:['cn']},  sortable: false,  search:true},
                {name:'sendDateFormatted',   	index:'sendDateFormatted',               sortable: false,  search:false},              
                {name:'action',         index:'action',      width:40,      sortable: false,  search:false}
            ],
            jsonReader : {
                root: "payload.result",
                repeatitems : false,
                id : "id",
                page: "payload.currentPage",
                total: "payload.totalPages"
            },
            gridComplete: function() {
                var ids = jQuery("#schedules-list-<portlet:namespace/>").jqGrid('getDataIDs');
                for(var i = 0; i < ids.length; i++){
                    var viewIcon = '<div title="<fmt:message key="newsletter.common.actions.view" />"  style="float:left; margin-left:20px; cursor:pointer" class="ui-icon ui-icon-zoomin" onclick="javascript:viewSchedule(' + ids[i] + ');"></div>';
                    jQuery("#schedules-list-<portlet:namespace/>").jqGrid('setRowData',ids[i],{ 'action' : viewIcon } );
                }
                // Fix jQGrid scroll issue
                jQuery('#schedules-panel .ui-jqgrid-bdiv').css('overflow', 'hidden');
            },
            pager: '#schedules-list-pager<portlet:namespace/>',
            sortname: 'id',
            viewrecords: true,
            sortorder: "asc",
            caption: '<fmt:message key="newsletter.admin.schedule" />',
            height: '100%',
            rowNum: 10
            
        });
        jQuery("#schedules-list-<portlet:namespace/>").jqGrid('navGrid','#schedules-list-pager<portlet:namespace/>',{edit:false,add:false,del:false},{},{},{},{multipleSearch:true});
    }
    
    
    
    /**
     * View schedule
     */
    function viewSchedule(scheduleId){
        jQuery('#schedules-panel').load('${viewScheduleUrl}', {id: scheduleId});
    }
    
    
    
    
    
    
</script>