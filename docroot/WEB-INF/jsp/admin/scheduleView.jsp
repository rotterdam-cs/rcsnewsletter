<%-- 
    Document   : archiveView
    Created on : Aug 13, 2012, 10:45:59 AM
    Author     : marcoslacoste
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<fmt:setBundle basename="Language"/>
<portlet:defineObjects />

<%--
    URLs
    ##################
--%>
<portlet:resourceURL id='archivesList' var='archivesListUrl'/>

<%--
    Header
    #########
--%>
<input id="backButton" type="button" value="<fmt:message key="newsletter.common.back" />" />

<hr>

<%--
    Edit Form
    #########
--%>


<form id="archive-form-<portlet:namespace/>" class="newsletter-forms-form">
    
    <table>
        <tr>
            <td><label><fmt:message key="newsletter.tab.archives.field.label.id" /></label></td>
            <td><c:out value="${archive.id}"/> <input type="hidden" name="id" value="${archive.id}" /> </td>
        </tr>
        <tr>
            <td><label><fmt:message key="newsletter.tab.archives.field.label.name" /></label></td>
            <td><input type="text" name="name" class="required field-long" style="width:200px" value="<c:out value="${archive.mailing}" />" /></td>
        </tr>
        <tr>
            <td colspan="2">
                <b><fmt:message key="newsletter.tab.archives.label.archivepreview" /></b>
                <br>
                <div style="padding:5px; border: solid 1px">
                    ${archive.emailBody}
                </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <br>
                <input id="btn-cancel-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.common.cancel" />" />
            </td>
        </tr>
    </table>
</form>
            
            
  
            
<script type="text/javascript">
    
    jQuery(document).ready(function(){
       styleUI();
       initEvents();
       clearMessages();
    });
    
    
    /**
     * Apply styles to UI
     */
    function styleUI(){
       // styles on buttons
       jQuery('#archives-panel input[type="button"]').button();
       
       
      
        disableComponents();
    }
    
    
    /**
     * Init UI events
     */
    function initEvents(){
        
     
        // click on 'Cancel' button
        jQuery('#btn-cancel-<portlet:namespace/>').click(function(){
           jQuery('#archives-panel').load('${archivesListUrl}');
        });
        
       
    }
   
        
    /**
     * Disable components (readonly)
     */
    function disableComponents(){
        jQuery('#archive-form-<portlet:namespace/> input[name="name"]').attr('disabled', 'disabled');
    }
    
    
</script>