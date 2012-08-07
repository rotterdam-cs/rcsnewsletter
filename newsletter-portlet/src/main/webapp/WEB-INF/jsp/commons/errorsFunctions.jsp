<%-- 
    Document   : errorsFunctions
    Created on : 07/08/2012, 14:54:25
    Author     : ggenovese <gustavo.genovese@rotterdam-cs.com>
--%>
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
        var htmlContent = '<p><span style="float: left; margin-right: .3em;" class="ui-icon ui-icon-alert"></span><strong>Error:</strong>' + errorMessage +'</p>';
        jQuery('#errorsView<portlet:namespace/>').addClass('ui-state-error ui-corner-all');
        jQuery('#errorsView<portlet:namespace/>').html(htmlContent);
    }
    
    function clearErrors(){
        jQuery('#errorsView<portlet:namespace/>').removeClass('ui-state-error ui-corner-all');
        jQuery('#errorsView<portlet:namespace/>').html('');
    }
</script>
