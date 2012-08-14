<%-- 
    Document   : archive
    Created on : 06/08/2012, 17:07:17
    Author     : ggenovese <gustavo.genovese@rotterdam-cs.com>
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

    <div id="archives-panel">



        <%--
            Include Archive List View (by default)
            ########################################
        --%>
        <jsp:include page="archiveList.jsp" />

    </div>

</body>


<script type="text/javascript">
    function initTab(){
        clearMessages();
    }
</script>
