<%-- 
    Document   : subscriptionFailure
    Created on : Oct 25, 2011, 10:25:40 AM
    Author     : Ariel Parra <ariel@rotterdam-cs.com>
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui"%>

<h1><liferay-ui:message key='newsletter.subscription.failure.title' arguments='<%=request.getParameter("categoryName")%>'/></h1>