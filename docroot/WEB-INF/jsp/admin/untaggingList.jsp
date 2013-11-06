<%-- 
    Document   : untaggingList
    Created on : Nov 31, 2013, 09:45:59 AM
    Author     : Gustavo Del Negro
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
<portlet:resourceURL id='getUntagging' var='getUntaggingUrl'/>
<portlet:resourceURL id='deleteTagTypeCategory' var='deleteTagUrl'/>



<%--
    Grid and Pager
    ##############
--%>
<table id="untagging-list-<portlet:namespace/>"></table>
<div id="untagging-list-pager<portlet:namespace/>"></div>


<%--
    Delete Tag
    ##############
--%>
<div style="display:none">

    <div id="delete-untagging-dialog-<portlet:namespace/>" title="<fmt:message key="newsletter.confim.delete.title"/>">
        <fmt:message key="newsletter.tab.untagging.confim.delete.body"/>
    </div>
</div>



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
        jQuery("#untagging-list-<portlet:namespace/>").jqGrid({
            url:'${getUntaggingUrl}' + "&nocache=" + (new Date()).getTime(),
            datatype: "json",
            autowidth: true,
            colNames:[
                '<fmt:message key="newsletter.admin.general.id" />',
                '<fmt:message key="newsletter.tab.untagging.field.label.title" />',
                ''],
            colModel:[
                {name:'id',         index:'id',          	width:40,   sortable: false,  search:false},
                {name:'name',   	index:'name',          		    sortable: false,  search:false},
                {name:'action',     index:'action',      	width:40,   sortable: false,  search:false}
            ],
            jsonReader : {
                root: "payload.result",
                repeatitems : false,
                id : "id",
                page: "payload.currentPage",
                total: "payload.totalPages"
            },
            gridComplete: function() {
                var ids = jQuery("#untagging-list-<portlet:namespace/>").jqGrid('getDataIDs');
                for(var i = 0; i < ids.length; i++){
                    var viewIcon = '<div title="<fmt:message key="newsletter.common.actions.delete" />"  style="float:left; margin-left:20px; cursor:pointer" class="ui-icon ui-icon-trash" onclick="javascript:deleteTag(' + ids[i] + ');"></div>';
                    jQuery("#untagging-list-<portlet:namespace/>").jqGrid('setRowData',ids[i],{ 'action' : viewIcon } );
                }
                // Fix jQGrid scroll issue
                jQuery('#untagging-panel .ui-jqgrid-bdiv').css('overflow', 'hidden');
            },
            pager: '#untagging-list-pager<portlet:namespace/>',
            sortname: 'id',
            viewrecords: true,
            sortorder: "asc",
            caption: '<fmt:message key="newsletter.tab.untagging.grid.name" />',
            height: '100%',
            rowNum: 10
            
        });
        jQuery("#untagging-list-<portlet:namespace/>").jqGrid('navGrid','#untagging-list-pager<portlet:namespace/>',{edit:false,add:false,del:false},{},{},{},{multipleSearch:true});
    }
    
    
    
    /**
     * Delete Tag
     */
     
        //  confirm delete dialog
        jQuery("#delete-untagging-dialog-<portlet:namespace/>").dialog({
            autoOpen: false,
            modal: true
        });
        
             
      function deleteTag(id){
            jQuery("#delete-untagging-dialog-<portlet:namespace/>").dialog({
                autoOpen: false,
                modal: true,
                buttons : {
                    "<fmt:message key="newsletter.confirm.button.yes"/>" : function() {
                        jQuery.ajax({
                            url: '${deleteTagUrl}'
                            ,type: 'POST'
                            ,data: {id: id}
                            ,success: function(){
                            	jQuery("#untagging-list-<portlet:namespace/>").trigger( 'reloadGrid' );
                            }
                            ,failure: function(response){
                                alert("Error");
                            }
                        });
                        $(this).dialog("close");
                    },
                    "<fmt:message key="newsletter.common.cancel" />" : function() {
                        $(this).dialog("close");
                    }
                }
            });
            jQuery("#delete-untagging-dialog-<portlet:namespace/>").dialog("open");
        }
    
    
    
    
</script>