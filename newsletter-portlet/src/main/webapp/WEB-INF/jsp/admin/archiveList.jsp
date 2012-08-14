<%-- 
    Document   : archiveEdit
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
<portlet:resourceURL id='getArchives' var='getArchivesUrl'/>
<portlet:resourceURL id='viewArchive' var='viewArchiveUrl'/>



<%--
    Grid and Pager
    ##############
--%>
<table id="archives-list-<portlet:namespace/>"></table>
<div id="archives-list-pager<portlet:namespace/>"></div>





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
     * Creates JQuery Grid for Archive entires
     */
    function createGrid(){
        jQuery("#archives-list-<portlet:namespace/>").jqGrid({
            url:'${getArchivesUrl}' + "&nocache=" + (new Date()).getTime(),
            datatype: "json",
            autowidth: true,
            colNames:[
                '<fmt:message key="newsletter.admin.general.id" />',
                '<fmt:message key="newsletter.admin.general.name" />',
                '<fmt:message key="newsletter.tab.archives.field.label.list" />',
                '<fmt:message key="newsletter.tab.archives.field.label.date" />',
                ''],
            colModel:[
                {name:'id',             index:'id',          width:40,      sortable: false,  search:false},
                {name:'name',           index:'name',                       sortable: false,  search:false},
                {name:'categoryName',   index:'categoryName',               sortable: false,  search:false},
                {name:'dateFormatted',  index:'dateFormatted',              sortable: false,  search:false},
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
                var ids = jQuery("#archives-list-<portlet:namespace/>").jqGrid('getDataIDs');
                for(var i = 0; i < ids.length; i++){
                    var viewIcon = '<div style="float:left; margin-left:20px; cursor:pointer" class="ui-icon ui-icon-zoomin" onclick="javascript:viewArchive(' + ids[i] + ');"></div>';
                    jQuery("#archives-list-<portlet:namespace/>").jqGrid('setRowData',ids[i],{ 'action' : viewIcon } );
                }
                // Fix jQGrid scroll issue
                jQuery('#archives-panel .ui-jqgrid-bdiv').css('overflow', 'hidden');
            },
            pager: '#archives-list-pager<portlet:namespace/>',
            sortname: 'id',
            viewrecords: true,
            sortorder: "asc",
            caption: '<fmt:message key="newsletter.tab.archives.grid.title" />',
            height: '100%',
            rowNum: 10
            
        });
        jQuery("#archives-list-<portlet:namespace/>").jqGrid('navGrid','#archives-list-pager<portlet:namespace/>',{edit:false,add:false,del:false});
    }
    
    
    
    /**
     * View archive
     */
    function viewArchive(archiveId){
        jQuery('#archives-panel').load('${viewArchiveUrl}', {id: archiveId});
    }
    
    
    
    
    
    
</script>