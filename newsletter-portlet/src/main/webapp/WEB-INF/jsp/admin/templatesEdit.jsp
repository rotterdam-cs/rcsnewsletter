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
<portlet:resourceURL id='saveTemplate' var='saveTemplateUrl'/>


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
            <td><c:out value="${template.id}"/> <input type="hidden" name="id" value="${template.id}" /> </td>
        </tr>
        <tr>
            <td><label><fmt:message key="newsletter.tab.templates.field.label.name" /></label></td>
            <td><input type="text" name="name" class="required" style="width:200px" value="<c:out value="${template.name}" />" /></td>
        </tr>
        <tr>
            <td colspan="2" id="td-ckeditor">
                     <input type="hidden" id="inputEditor" name="template"/>
                    <liferay-ui:input-editor   width="250" height="100"   />
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <br>
                <input id="btn-save-<portlet:namespace/>" type="button" value="<fmt:message key="general.save" />" />
                <input id="btn-cancel-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.common.cancel" />" />
            </td>
        </tr>
    </table>
</form>
            
            
<%--
    Dialogs
    ##################
--%>
<div style="display:none">
    
    <div id="templates-help-<portlet:namespace/>">
        <fmt:message key="newsletter.tab.templates.help.addtemplate.body" />
    </div>
</div>
            
            
<script type="text/javascript">
    
    jQuery(document).ready(function(){
       styleUI();
       initEvents();
       
    });
    
    
    /**
     * Apply styles to UI
     */
    function styleUI(){
       // styles on buttons
       jQuery('#templates-panel input[type="button"]').button();
       
       
       // help dialog
       jQuery('#templates-help-<portlet:namespace/>').dialog({
            autoOpen: false
           ,modal: true
           ,resizable: false
           ,width: 450
           ,title: '<fmt:message key="newsletter.tab.templates.help.addtemplate.title" />'
       });
    }
    
    
    /**
     * Init UI events
     */
    function initEvents(){
        
        // click on 'Cancel' button
        jQuery('#btn-help-<portlet:namespace/>').click(function(){
           jQuery('#templates-help-<portlet:namespace/>').dialog('open');
        });
        
        
        // click on 'Cancel' button
        jQuery('#btn-cancel-<portlet:namespace/>').click(function(){
           jQuery('#templates-panel').load('${templatesListUrl}');
        });
        
        // click on 'Validate' button
        jQuery('#btn-save-<portlet:namespace/>').click(function(){
           var form = jQuery('#template-form-<portlet:namespace/>');
           
           // extract text from ckeditor
           jQuery("#inputEditor").val(jQuery("#cke_contents_<portlet:namespace/>editor iframe")[0].contentWindow.document.body.innerHTML);
           
           // validate form
           var formValid = form.valid();
           var templateValid = isTemplateValid();
           if (formValid && templateValid){
               
               // save via ajax
               jQuery.ajax({
                    url: '${saveTemplateUrl}'
                   ,type: 'POST'
                   ,data: form.serialize()
                   ,success: function(response){
                       jQuery('#btn-cancel-<portlet:namespace/>').trigger('click');
                   }
                   ,failure: function(response){
                       alert('error');
                       console.log(response);
                   }
               });
           }
        });
    }
    
    /**
    * Function that initializes the html editor component
    */
    function _NewsletterAdmin_WAR_newsletterportlet_initEditor(e){
        jQuery('#template-form-<portlet:namespace/> .cke_contents').css('height','150px');                      

        // Load description in html editor field
        return "${template.template}";
    }


    /**
     * Validate template structure
     */
    function isTemplateValid() {
                                
            var content = jQuery('#cke_contents_<portlet:namespace/>editor iframe')[0].contentWindow.document.body.innerHTML;                
            var result = true;
            if (content.length == 0){
                result = false;
            }
            var contentTemplate = content.split("[block]");
            if (contentTemplate.length < 2) {//No Blocks
                result = false;
            } else {
                for (i = 1; i < contentTemplate.length; i++) {
                    var contentTemplateInside = contentTemplate[i].split("[block]");
                    var contentTemplateInside2 = contentTemplate[i].split("[/block]");
                    if (contentTemplateInside.length > 1 || contentTemplateInside2.length != 2) {//nested blocks 
                        result = false;
                    }                   
                }
            }


            jQuery('#td-ckeditor label.error').remove();
            if (!result){
                jQuery('#td-ckeditor').append('<label class="error">Invalid value or structure</label>');
            }

            return result;            
        }      
    
    
    
</script>