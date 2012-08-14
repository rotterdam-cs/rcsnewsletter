<%-- 
    Document   : registerForm
    Created on : Aug 14, 2012, 10:45:59 AM
    Author     : marcoslacoste
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib  prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<fmt:setBundle basename="Language"/>
<portlet:defineObjects />


<%--
    URLs
    ##################
--%>
<portlet:resourceURL id='register' var='registerUrl'/>
<portlet:resourceURL id='showUnregisterForm' var='showUnregisterFormUrl'/>


<%--
    Header and Actions
    ##################
--%>


<%--
    Edit Form
    #########
--%>


<form:form id="register-form-${namespace}"  cssClass="newsletter-forms-form" modelAttribute="registerForm" >
    <table>
        <tr>
            <td><label><fmt:message key="newsletter.registration.firstname" /></label></td>
            <td><form:input path="firstName"  cssClass="field-long required" /></td>
        </tr>
        <tr>
            <td><label><fmt:message key="newsletter.registration.lastname" /></label></td>
            <td><form:input path="lastName"  cssClass="field-long required" /></td>
        </tr>
        <tr>
            <td><label><fmt:message key="newsletter.registration.email" />*</label></td>
            <td><form:input path="email"  cssClass="field-long required custom-email" /></td>
        </tr>
        <tr>
            <td><a href="javascript:loadUnregisterView()"><fmt:message key="newsletter.registration.link.unregister" /></a> </td>
            <td>
                <input id="btn-register-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.registration.button.register" />" />
            </td>
        </tr>
    </table>
</form:form>


<%--
    Dialogs
    ##################
--%>
<div style="display:none">

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
        jQuery('#newsletter-registration-panel-<portlet:namespace/> input[type="button"]').button();
        
    }
    
    
    /**
     * Init UI events
     */
    function initEvents(){
        
        
        // click on 'Remove' button
        jQuery('#btn-register-<portlet:namespace/>').click(function(){
            // validate form
             var form = jQuery('#register-form-${namespace}');
             if (form.valid()){ 
                 
                    // send data via ajax
                    jQuery.ajax({
                        url: '${registerUrl}'
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
    
    
    /**
     *  Loads unregister view
     */
    function loadUnregisterView(){
        $('#newsletter-registration-panel-<portlet:namespace/>').load('${showUnregisterFormUrl}');
    }