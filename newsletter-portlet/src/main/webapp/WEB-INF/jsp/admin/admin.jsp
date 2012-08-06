<%-- 
    Document   : admin
    Created on : 06/08/2012, 15:46:52
    Author     : ggenovese <gustavo.genovese@rotterdam-cs.com>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<fmt:setBundle basename="Language"/>
<portlet:defineObjects />
<portlet:resourceURL id="subscribers" var="subscribersURL"/>
<portlet:resourceURL id="mailing" var="mailingURL"/>
<portlet:resourceURL id="archive" var="archiveURL"/>
<portlet:resourceURL id="templates" var="templatesURL"/>

<html>
    <head>
        <script type="text/javascript">
            Liferay.on('portletReady', function(event) {
                if('_' + event.portletId + '_' != '<portlet:namespace/>') {
                    return;
                }
                jQuery( "#newsletterAdmin<portlet:namespace/>" ).tabs({
                    ajaxOptions: {
                        error: function( xhr, status, index, anchor ) {
                            $( anchor.hash ).html("Couldn't load this tab. We'll try to fix this as soon as possible.");
                        }
                    }
                });
            });
        </script>
    </head>
    <body>
        <div id="newsletterAdmin<portlet:namespace/>">
            <ul>
                <li><a href="#lists<portlet:namespace/>"><fmt:message key="newsletter.admin.lists"/></a></li>
                <li><a href="${subscribersURL}"><fmt:message key="newsletter.admin.subscribers"/></a></li>
                <li><a href="${mailingURL}"><fmt:message key="newsletter.admin.mailing"/></a></li>
                <li><a href="${archiveURL}"><fmt:message key="newsletter.admin.archive"/></a></li>
                <li><a href="${templatesURL}"><fmt:message key="newsletter.admin.templates"/></a></li>
            </ul>

            <div id="lists<portlet:namespace/>">
                <div id="listsGrid<portlet:namespace/>">
                <div id="listPager<portlet:namespace/>"/>
            </div>
        </div>
    </body>
</html>
