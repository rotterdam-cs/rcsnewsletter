<%-- 
    Document   : registration
    Created on : 14/08/2012, 17:07:17
    Author     : Marcos Lacoste
--%>


<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<fmt:setBundle basename="Language"/>
<portlet:defineObjects />


<%--
    URLs
    ##################
--%>
<portlet:resourceURL id='showRegisterForm' var='showRegisterFormUrl'/>
<portlet:resourceURL id='saveSettings' var='saveSettingsUrl'/>


<h2><fmt:message key="newsletter.registration.settings.title" /></h2>
<hr>

<%--
    Errors and Messages common view
    ###############################
--%>
<%@include file="../commons/errorsView.jsp" %>
<%@include file="../commons/errorsFunctions.jsp" %>



<form:form id="settings-form-${namespace}"  cssClass="newsletter-forms-form" modelAttribute="settings" >
    <table>
        <tr>
            <td><label><fmt:message key="newsletter.registration.settings.field.list" /></label></td>
            <td><form:select items="${listOptions}" itemLabel="name" itemValue="id" path="listId" /> </td>
        </tr>
        <tr>
            <td><label><fmt:message key="newsletter.registration.settings.field.disablednamefields" /></label></td>
            <td><form:checkbox path="disabledNameFields" /> </td>
        </tr>
        <tr>
	        <td colspan="2">
	            <input id="btn-save-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.registration.settings.button.savesettings" />" />
	        </td>
    	</tr>
</table>
</form:form>


<script type="text/javascript">
    
    
    jQuery(document).ready(function(){
        styleUI();
        initEvents();
        clearMessages();
        clearErrors();
        
    });
    
    /**
     * Apply styles to UI
     */
    function styleUI(){
        // styles on buttons
        jQuery('#settings-form-${namespace} input[type="button"]').button();
       
    }    
      
   
    
    /**
     * Init UI events
     */
    function initEvents(){

        // click on 'Save' button
        jQuery('#btn-save-<portlet:namespace/>').click(function(){
            clearMessages();
            clearErrors();

            // validate form
            var form = jQuery('#settings-form-${namespace}');
            if (form.valid()){

                // save via ajax
                jQuery.ajax({
                    url: '${saveSettingsUrl}'
                    ,type: 'POST'
                    ,data: form.serialize()
                    ,success: function(response){
                        if (response.success){
                            showMessages(response.messages);
                        }else{
                            showErrors(response.validationKeys);
                        }
                    }
                    ,failure: function(response){
                        // TODO: display errors here
                    }
                });
            }
        });


    }
    
   
    
</script>