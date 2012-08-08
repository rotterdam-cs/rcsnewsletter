<%-- 
    Document   : errorsFunctions
    Created on : 07/08/2012, 14:54:25
    Author     : ggenovese <gustavo.genovese@rotterdam-cs.com>
--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<portlet:defineObjects />

<script type="text/javascript">
    function showErrors(errors){
        if (!errors){
            errors = ['Generic error'];
        } else if (typeof(errors) == 'string'){
            errors = [errors];
        }
        var errorMessage = '';
        for (var i=0; errors && i<errors.length; i++) {
            errorMessage += errors[i];
            errorMessage += '<br/>';
        }
        var htmlContent = '<p><span style="float: left; margin-right: .3em;" class="ui-icon ui-icon-alert"></span><strong><fmt:message key="newsletter.general.error" />:</strong>' + errorMessage +'</p>';
        jQuery('#errorsView<portlet:namespace/>').addClass('ui-state-error ui-corner-all');
        jQuery('#errorsView<portlet:namespace/>').html(htmlContent);
    }
    
    function clearErrors(){
        jQuery('#errorsView<portlet:namespace/>').removeClass('ui-state-error ui-corner-all');
        jQuery('#errorsView<portlet:namespace/>').html('');
    }
    
    
    function showMessages(messages){
        if (!messages){
            messages = ['Generic message'];
        } else if (typeof(messages) == 'string'){
            messages = [messages];
        }
        var infoMessage = '';
        for (var i=0; messages && i< messages.length; i++) {
            infoMessage += messages[i];
            infoMessage += '<br/>';
        }
        
        var htmlContent = '<p><span style="float: left; margin-right: .3em;" class="ui-icon ui-icon-info"></span><strong><fmt:message key="newsletter.general.info" />:</strong>' + infoMessage +'</p>';
        jQuery('#messagesView<portlet:namespace/>').addClass('ui-state-highlight ui-corner-all');
        jQuery('#messagesView<portlet:namespace/>').html(htmlContent);
    }
    
    function clearMessages(){
        jQuery('#messagesView<portlet:namespace/>').removeClass('ui-state-highlight ui-corner-all');
        jQuery('#messagesView<portlet:namespace/>').html('');
    }
</script>
