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

<fmt:setBundle basename="Language"/>
<portlet:defineObjects />

<portlet:resourceURL id='showRegisterForm' var='showRegisterFormUrl'/>

<%--
    Errors and Messages common view
    ###############################
--%>
<%@include file="../commons/errorsView.jsp" %>
<%@include file="../commons/errorsFunctions.jsp" %>


<div id="newsletter-registration-panel-<portlet:namespace/>"></div>

</body>


<script type="text/javascript">
     Liferay.on('portletReady', function(event) {
            if('_' + event.portletId + '_' != '<portlet:namespace/>') {
                return;
            }

            <%--
            Include Register Form View (by default)
            ########################################
            --%>
            $('#newsletter-registration-panel-<portlet:namespace/>').load('${showRegisterFormUrl}');
    });
</script>

