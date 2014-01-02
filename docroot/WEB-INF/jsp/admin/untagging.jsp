<%-- 
    Document   : untaggingList
    Created on : Nov 31, 2013, 09:45:59 AM
    Author     : Gustavo Del Negro
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

    <div id="untagging-panel">


        <%--
            Include Archive List View (by default)
            ########################################
        --%>
        <jsp:include page="untaggingList.jsp" />

    </div>

</body>


<script type="text/javascript">
    function initTab(){
        clearMessages();
    }
</script>
