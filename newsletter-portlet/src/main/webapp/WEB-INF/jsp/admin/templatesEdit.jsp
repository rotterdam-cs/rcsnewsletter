<%-- 
    Document   : templateEdit
    Created on : Aug 7, 2012, 10:45:59 AM
    Author     : marcoslacoste
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<fmt:setBundle basename="Language"/>
<portlet:defineObjects />


<%--
    Header and Actions
    ##################
--%>

<h1><fmt:message key="newsletter.tab.templates.title.addtemplate" /></h1>

<input id="btn-help" type="button" value="<fmt:message key="newsletter.admin.category.general.help" />" />

<%--
    Edit Form
    #########
--%>
<form class="newsletter-forms-form">
    <table>
        <tr>
            <td>
                <label><fmt:message key="newsletter.tab.templates.field.label.name" /></label> 
            </td>
            <td>
                <input type="text" name="name" />
            </td>
        </tr>
        <tr>
            <td colspan="2" id="td-ckeditor">

            </td>
    </table>
</form>