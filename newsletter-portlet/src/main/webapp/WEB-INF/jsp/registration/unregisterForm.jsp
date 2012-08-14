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


<%--
    Header and Actions
    ##################
--%>


<%--
    Edit Form
    #########
--%>


<form:form id="unregister-form-${namespace}"  cssClass="newsletter-forms-form"  >
    <table>
        <tr>
            <td><a href="javascript:loadRegisterView()"><fmt:message key="newsletter.registration.link.register" /></a> </td>
            <td>
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
        
        
          
    }
    
    
    /**
     *  Loads unregister view
     */
    function loadRegisterView(){
        $('#newsletter-registration-panel-<portlet:namespace/>').load('${showRegisterFormUrl}');
    }