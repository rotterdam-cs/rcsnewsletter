<%-- 
    Document   : onlineviewer
    Created on : 16/08/2012, 10:07:17
    Author     : Marcos Lacoste
--%>


<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setBundle basename="Language"/>
<portlet:defineObjects />


<div id="newsletter-onlineviewer-panel-<portlet:namespace/>">

    
    <c:if test="${not empty result}">
        <%--
            Show messages
        --%>
        <c:if test="${not empty result.messages}">
            <div class="ui-state-highlight ui-corner-all" style="padding: 0 .7em;">
                   <p><span style="float: left; margin-right: .3em;" class="ui-icon ui-icon-info"></span>
                       <c:forEach items="${result.messages}" var="message">
                            <strong><fmt:message key="newsletter.general.info" />:</strong> ${message};
                       </c:forEach>
                   </p>
            </div>
        </c:if>
        
        
        <%--
            Show errors
        --%>
        <c:if test="${not empty result.validationKeys}">
            <div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
                   <p><span style="float: left; margin-right: .3em;" class="ui-icon ui-icon-alert"></span>
                       <c:forEach items="${result.validationKeys}" var="error">
                            <strong><fmt:message key="newsletter.general.error" />:</strong> ${error};
                       </c:forEach>
                   </p>
            </div>
        </c:if>
        
    </c:if>
    
    
    ${newsletter.newsletterBody}

</div>

</body>


<script type="text/javascript">
     Liferay.on('portletReady', function(event) {
            if('_' + event.portletId + '_' != '<portlet:namespace/>') {
                return;
            }
    });
</script>

