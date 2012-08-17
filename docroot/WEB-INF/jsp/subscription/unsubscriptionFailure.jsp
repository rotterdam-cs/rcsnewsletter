<%-- 
    Document   : unsubscriptionFailure
    Created on : Oct 25, 2011, 4:00:24 PM
    Author     : Ariel Parra <ariel@rotterdam-cs.com>
--%>

<%@taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui"%>

<h1><liferay-ui:message key='newsletter.unsubscription.failure.title' arguments='<%=request.getParameter("categoryName")%>'/></h1>
<h2>${message}</h2>