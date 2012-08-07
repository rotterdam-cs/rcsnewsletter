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
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<fmt:setBundle basename="Language"/>
<portlet:defineObjects />

<%--
    URLs
    ##################
--%>
<portlet:resourceURL id='templatesList' var='templatesListUrl'/>


<%--
    Header and Actions
    ##################
--%>

<h1><fmt:message key="newsletter.tab.templates.title.addtemplate" /></h1>
<input id="btn-help-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.admin.category.general.help" />" />
<hr>

<%--
    Edit Form
    #########
--%>
<form id="template-form-<portlet:namespace/>" class="newsletter-forms-form">
    <table>
        <tr>
            <td><label><fmt:message key="newsletter.tab.templates.field.label.id" /></label></td>
            <td><c:out value="${template.id}"/></td>
        </tr>
        <tr>
            <td><label><fmt:message key="newsletter.tab.templates.field.label.name" /></label></td>
            <td><input type="text" name="name" class="required" style="width:200px" value="<c:out value="${template.name}" />" /></td>
        </tr>
        <tr>
            <td colspan="2" id="td-ckeditor">
                     <input type="hidden" id="inputEditor" name="description"/>
                    <liferay-ui:input-editor   width="250" height="100"   />
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <br>
                <input id="btn-validate-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.tab.templates.button.validate" />" />
                <input id="btn-cancel-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.common.cancel" />" />
            </td>
        </tr>
    </table>
</form>
            
            
<script type="text/javascript">
    
    jQuery(document).ready(function(){

       initEvents();
       
    });
    
    
    /**
     * Init UI events
     */
    function initEvents(){
        // click on 'Add Template' button
        jQuery('#btn-cancel-<portlet:namespace/>').click(function(){
           jQuery('#templates-panel').load('${templatesListUrl}');
        });
    }
    
    /**
    * Function that initializes the html editor component
    */
    function _NewsletterAdmin_WAR_newsletterportlet_initEditor(e){
        $('#template-form-<portlet:namespace/> .cke_contents').css('height','150px');                      

        // Load description in html editor field
        return '${template.template}';
    }

    
    
    
</script>