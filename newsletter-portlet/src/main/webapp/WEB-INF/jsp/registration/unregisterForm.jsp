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
<portlet:resourceURL id='showRegisterForm' var='showRegisterFormUrl'/>
<portlet:resourceURL id='unregister' var='unregisterUrl'/>


<%--
    Header and Actions
    ##################
--%>


<%--
    Edit Form
    #########
--%>


<form:form id="unregister-form-${namespace}"  cssClass="newsletter-forms-form" modelAttribute="registerForm">
    <form:hidden path="categoryId"  />
    <table>
        <tr>
            <td><label><fmt:message key="newsletter.registration.email" />*</label></td>
            <td><form:input path="email"  cssClass="field-long required custom-email" /></td>
        </tr>
        <tr>
            <c:if test="${registerForm.categoryId ne 0}">
            <tr>
                <td colspan="2">
                    <hr>
                </td>
            </tr>
            <td>
                <td><a href="javascript:loadRegisterView()"><fmt:message key="newsletter.registration.link.register" /></a> </td>
                <input id="btn-unregister-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.registration.link.unregister" />" />
            </td>
        </c:if>
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
        
        <c:if test="${registerForm.categoryId eq 0}">
                showErrors(['<fmt:message key="newsletter.registration.settings.error.nolistfound" />']);
    </c:if>
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
        
                // click on 'Unregister' button
                jQuery('#btn-unregister-<portlet:namespace/>').click(function(){
                    clearMessages();
                    clearErrors();
                    // validate form
                    var form = jQuery('#unregister-form-${namespace}');
                    if (form.valid()){ 
                 
                        // send data via ajax
                        jQuery.ajax({
                            url: '${unregisterUrl}'
                            ,type: 'POST'
                            ,data: form.serialize()
                            ,success: function(response){
                                if (response.success){
                                    showMessages(response.messages);
                                    form.resetForm();
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
            function loadRegisterView(){
                $('#newsletter-registration-panel-<portlet:namespace/>').load('${showRegisterFormUrl}');
            }