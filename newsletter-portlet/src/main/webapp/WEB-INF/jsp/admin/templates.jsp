<%-- 
    Document   : templates
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

<html>
    <head>
        <script type="text/javascript">
            Liferay.on('portletReady', function(event) {
                if('_' + event.portletId + '_' != '<portlet:namespace/>') {
                    return;
                }
            });
        </script>
    </head>
    <body>
        
        <div id="templates-panel">
            
            <%--
                Include Templates List View (by default)
                ########################################
            --%>
            <jsp:include page="templatesList.jsp" />
        
        </div>
                    
    </body>
</html>
