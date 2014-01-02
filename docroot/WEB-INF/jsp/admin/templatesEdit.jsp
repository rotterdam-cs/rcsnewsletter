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
<portlet:resourceURL id='deleteTemplate' var='deleteTemplateUrl'/>


<%--
	HEADER
	######
 --%>






<input id="btn-help-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.admin.category.general.help" />" />
<input id="backButton" type="button" value="<fmt:message key="newsletter.common.back" />" />

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
            <td><input type="text" name="name" class="required field-long" value="<c:out value="${template.name}" />" /></td>
        </tr>
        <tr>
            <td colspan="2" id="td-ckeditor">
                    <c:if test="${remove}">
                        <br>
                        ${template.template}
                    </c:if>
                    <c:if test="${!remove}">
                        <input type="hidden" id="inputEditor" name="template"/>
                        <liferay-ui:input-editor   width="250" height="100" name="template_editor"     />
                     </c:if>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <br>
                <c:if test="${remove}">
                   <input id="btn-remove-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.admin.general.remove" />" />
                </c:if>
                <c:if test="${!remove}">
                   <input id="btn-save-<portlet:namespace/>" type="button" value="<fmt:message key="general.save" />" />
                </c:if>
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
   
    <div id="delete-template-dialog-<portlet:namespace/>" title="<fmt:message key="newsletter.confim.delete.title"/>">
        <fmt:message key="newsletter.confim.delete.body"/>
    </div>
</div>
            
            
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
       jQuery('#templates-panel input[type="button"]').button();
       
       
       // help dialog
       jQuery('#templates-help-<portlet:namespace/>').dialog({
            autoOpen: false
           ,modal: true
           ,resizable: false
           ,width: 450
           ,title: '<fmt:message key="newsletter.tab.templates.help.addtemplate.title" />'
       });
       
       //  confirm delete dialog
       jQuery("#delete-template-dialog-<portlet:namespace/>").dialog({
            autoOpen: false,
            modal: true
       });
       
       
        <c:if test="${remove}">
            disableComponents();
        </c:if>
    }
    
    
    /**
     * Init UI events
     */
    function initEvents(){
        
        // click on 'Help' button
        jQuery('#btn-help-<portlet:namespace/>').click(function(){
           jQuery('#templates-help-<portlet:namespace/>').dialog('open');
        });
        
        // click on 'Remove' button
        jQuery('#btn-remove-<portlet:namespace/>').click(function(){
           deleteTemplate();
        });
        
        // click on 'Cancel' button
        jQuery('#btn-cancel-<portlet:namespace/>').click(function(){
           jQuery('#templates-panel').load('${templatesListUrl}');
        });
        
        // click on 'Validate' button
        jQuery('#btn-save-<portlet:namespace/>').click(function(){
           var form = jQuery('#template-form-<portlet:namespace/>');
           
           // extract text from ckeditor
           
           var html = window.<portlet:namespace/>template_editor.getHTML();
           jQuery("#inputEditor").val(html);
           
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
                       showMessages(response.messages);
                       jQuery('#btn-cancel-<portlet:namespace/>').trigger('click');
                   }
                   ,failure: function(response){
                       // TODO: display errors here
                   }
               });
           }
        });
    }
    
    <c:if test="${!remove}">
    /**
    * Function that initializes the html editor component
    */
    
    function <portlet:namespace/>initEditor(e){
        // Load template in html editor field
        return "${template.template}";
    }
    </c:if>


    /**
     * Validate template structure
     */
    function isTemplateValid() {
            var content = window.<portlet:namespace/>template_editor.getHTML();  
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
        
        /**
         * Delete the template
         */
        function deleteTemplate(){
             var templateId = '${template.id}';
             jQuery("#delete-template-dialog-<portlet:namespace/>").dialog({
                autoOpen: false,
                modal: true,
                buttons : {
                    "<fmt:message key="newsletter.confirm.button.yes"/>" : function() {
                        jQuery.ajax({
                             url: '${deleteTemplateUrl}'
                            ,type: 'POST'
                            ,data: {id: templateId}
                            ,success: function(response){
                                if (response.success){
                                    showMessages(response.messages);
                                }else{
                                    showErrors(response.validationKeys);
                                }
                                $(this).dialog("close");
                                jQuery('#btn-cancel-<portlet:namespace/>').trigger('click');
                            }
                            ,failure: function(response){
                                $(this).dialog("close");
                                // TODO: display errors here
                            }
                        });
                        $(this).dialog("close");
                    },
                    "<fmt:message key="newsletter.common.cancel" />" : function() {
                        $(this).dialog("close");
                    }
                }
            });
            jQuery("#delete-template-dialog-<portlet:namespace/>").dialog("open");
        }
        
        
        /**
         * Disable components (readonly)
         */
        function disableComponents(){
            jQuery('#template-form-<portlet:namespace/> input[name="name"]').attr('disabled', 'disabled');
        }
    
    
</script>