<%-- 
    Document   : mailing
    Created on : 06/08/2012, 17:07:17
    Author     : Marcos
--%>


<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<fmt:setBundle basename="Language"/>
<portlet:defineObjects />



<%--
    Errors and Messages common view
    ###############################
--%>
<%@include file="../commons/errorsView.jsp" %>

<div id="mailing-panel">



    <%--
        Include Malining List View (by default)
        ########################################
    --%>
    <jsp:include page="mailingList.jsp" />

</div>

</body>


<script type="text/javascript">
    function initTab(){
        clearMessages();
    }
</script>

