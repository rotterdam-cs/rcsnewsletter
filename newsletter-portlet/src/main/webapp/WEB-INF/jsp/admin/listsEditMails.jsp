<%-- 
    Document   : listsEditMails
    Created on : 14/08/2012, 12:02:58
    Author     : ggenovese <gustavo.genovese@rotterdam-cs.com>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<fmt:setBundle basename="Language"/>
<fmt:setBundle basename="Newsletter" var="newsletter"/>
<portlet:defineObjects />
<portlet:resourceURL id="saveEmail" var="saveEmailURL"/>

<script type="text/javascript">
    jQuery(document).ready(function(){
        jQuery('.editMailsButtons button').button();
        jQuery('#helpButton<portlet:namespace/>').button();
        
        jQuery('#helpButton<portlet:namespace/>').click(function(){
            jQuery('#helpDialog<portlet:namespace/>').dialog({modal: true, title: '<fmt:message key="newsletter.admin.category.general.help"/>'});
        });

        jQuery('.editMailsButtons .cancelEditMail').click(function(){
            jQuery('#ckEditorContainer<portlet:namespace/>').html('');
            jQuery('#ckEditorContainer<portlet:namespace/>').hide();
            jQuery('#gridContainer<portlet:namespace/>').show();
            clearErrors();
        });
        
        jQuery('.editMailsButtons .saveMail').click(function(){
            var htmlEmailContent = window.<portlet:namespace/>emailEditor.getHTML();
            jQuery.ajax({
                url: '${saveEmailURL}',
                dataType: 'json',
                data: {
                    listId: ${listId},
                    type: '${type}',
                    content: htmlEmailContent
                },
                success: function(data){
                    if (data && data.success){
                        jQuery('.editMailsButtons .cancelEditMail').click();
                    } else {
                        showErrors(data.messages);
                    }
                }
            });
        });
    });
    
    /**
    * Function that initializes the html editor component
    */
    function _NewsletterAdmin_WAR_newsletterportlet_initEditor(e){
        // Load template in html editor field
        return "${currentContent}";
    }    
    
</script>

<h1>${title}</h1>
<button type="button" id="helpButton<portlet:namespace/>"><fmt:message key="newsletter.admin.category.general.help"/></button>

<div id="ckEditor<portlet:namespace/>">
    <liferay-ui:input-editor height="100" name="emailEditor"/>
</div>

<br/>
<div class="editMailsButtons">
    <button type="button" class="saveMail"><fmt:message key="general.save"/></button>
    <button type="button" class="cancelEditMail"><fmt:message key="newsletter.common.cancel"/></button>
</div>

<div style="display: none;" id="helpDialog<portlet:namespace/>">
    ${helpContent}
</div>
