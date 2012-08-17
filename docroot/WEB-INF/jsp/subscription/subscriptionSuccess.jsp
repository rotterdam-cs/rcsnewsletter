<%-- 
    Document   : subscriptionSuccess
    Created on : Oct 18, 2011, 12:37:42 PM
    Author     : Ariel Parra <ariel@rotterdam-cs.com>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui"%>

<h1><liferay-ui:message key='newsletter.subscription.succesfully.title' arguments='<%=request.getParameter("categoryName")%>'/></h1>
