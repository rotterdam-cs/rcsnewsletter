<%-- 
    Document   : subscription
    Created on : 14/10/2011, 11:11:12
    Author     : ariel@rotterdam-cs.com
--%>
<%@page import="com.rcs.newsletter.core.model.NewsletterCategory"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page contentType="text/html" isELIgnored="false" pageEncoding="UTF-8"%>
<%@taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui"%>
<%@taglib prefix="liferay-portlet" uri="http://java.sun.com/portlet"%>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@taglib prefix="theme" uri="http://liferay.com/tld/theme"%>
<%@taglib prefix="aui" uri="http://liferay.com/tld/aui"%>

<portlet:defineObjects/>
<theme:defineObjects/>

<span><liferay-ui:message key='newsletter.subscription.confirmation.title' arguments='<%=request.getParameter("email")%>'/></span>
<aui:form name="confirmation-form-id">
    <aui:fieldset>
        <aui:input name='confirmationKey' label='<%=LanguageUtil.get(locale, "newsletter.subscription.confirmation.key")%>' type='text' value=""></aui:input>
        <aui:input name='disabledEmail' label='<%=LanguageUtil.get(locale, "newsletter.common.email")%>' type='text' disabled='true' value='${email}'></aui:input>
        <aui:input name='categoryId' type='hidden' value='${categoryId}'></aui:input>
        <input name="<portlet:namespace />email" type="hidden" value="${email}" />
        <aui:button-row>
            <aui:button class='aui-button-input' value='<%=LanguageUtil.get(locale, "newsletter.subscription.confirmation")%>' onClick='confirmRegistration()'/>
        </aui:button-row>
        
    </aui:fieldset>
</aui:form>