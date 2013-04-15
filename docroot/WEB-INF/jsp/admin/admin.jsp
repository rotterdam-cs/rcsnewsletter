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
<!-- TABS -->
<portlet:resourceURL id="lists" var="listsURL"/>
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
                        beforeSend: function(){
                            jQuery('#ui-tabs-1, #ui-tabs-2, #ui-tabs-3, #ui-tabs-4, #ui-tabs-5').html('');
                        },
                        success: function(){
                            initTab();
                        },
                        error: function( xhr, status, index, anchor ) {
                            jQuery( anchor.hash ).html("Couldn't load this tab. We'll try to fix this as soon as possible.");
                        }
                    }
                });
            });
        </script>
        <%@include file="../commons/errorsFunctions.jsp" %>
    </head>
    <body>
        <div id="newsletterAdmin<portlet:namespace/>" class="newsletter-admin-wrapper">
            <ul>
                <li><a href="${listsURL}"><fmt:message key="newsletter.admin.lists"/></a></li>
                <li><a href="${subscribersURL}"><fmt:message key="newsletter.admin.subscribers"/></a></li>
                <li><a href="${templatesURL}"><fmt:message key="newsletter.admin.templates"/></a></li>
                <li><a href="${mailingURL}"><fmt:message key="newsletter.admin.mailing"/></a></li>
                <li><a href="${archiveURL}"><fmt:message key="newsletter.admin.archive"/></a></li>                
            </ul>
        </div>
    </body>
</html>
