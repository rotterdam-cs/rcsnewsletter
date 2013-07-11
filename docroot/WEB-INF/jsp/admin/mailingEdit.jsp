<%-- 
    Document   : mailingEdit
    Created on : Aug 8, 2012, 10:45:59 AM
    Author     : marcoslacoste
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<fmt:setBundle basename="Language"/>
<portlet:defineObjects />

<%--
    URLs
    ##################
--%>
<portlet:resourceURL id='mailingList' var='mailingListUrl'/>
<portlet:resourceURL id='saveMailing' var='saveMailingUrl'/>
<portlet:resourceURL id='deleteMailing' var='deleteMailingUrl'/>
<portlet:resourceURL id='getPreview' var='getPreviewUrl'/>

<%--
    Header and Actions
    ##################
--%>


<%--
    Edit Form
    #########
--%>


<form:form id="mailing-form-${namespace}"  cssClass="newsletter-forms-form" modelAttribute="mailing" >
    <table>
        <tr>
            <td><label><fmt:message key="newsletter.tab.mailing.field.label.id" /></label></td>
            <td><form:hidden path="id" /></td>
        </tr>
        <tr>
            <td><label><fmt:message key="newsletter.tab.mailing.field.label.name" /></label></td>
            <td><form:input path="name"  cssClass="field-long required" /></td>
        </tr>
        <tr>
            <td><label><fmt:message key="newsletter.tab.mailing.field.label.list" /></label></td>
            <td><form:select items="${listOptions}" itemLabel="name" itemValue="id" path="listId" cssClass="field-long required" /></td>
        </tr>
        <tr>
            <td><label><fmt:message key="newsletter.tab.mailing.field.label.template" /></label></td>
            <td>
                <select id="select-template-<portlet:namespace/>" name="templateId" class="field-long required" onchange="preview();" >
                    <c:forEach items="${templateOptions}" var="t">
                        <option value="${t.id}" 
                                blocksQuantity="${t.blocks}"
                            <c:if test="${t.id eq mailing.templateId}"> selected="selected" </c:if>
                        >${t.name}</option>
                    </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
        
            <td colspan="2"><br><fmt:message key="newsletter.tab.mailing.infotext" /></td>
        </tr>
        
        <tr>
            <td id="mailing-articles-td" colspan="2">
                
                <%-- 
                    Article combo boxes used when editing an existing Mailing
                --%>
                <div id="article-combos-<portlet:namespace/>" style="display: none;">
                    <c:if test="${! empty mailing.id}">
                        <c:forEach items="${mailing.articles}" var="article" varStatus="loop">
                             <fmt:message key="newsletter.tab.mailing.label.articleforblock" /> ${loop.index} &nbsp;
                             <select class="required blockSelectorSelectHide" style="margin-top:5px" id="blockSelectorSelectHide${loop.index}">
                                 <c:forEach items="${articlesOptions}" var="a">
                                     <option value="${a.id}"
                                             <c:if test="${a.id eq article.id}"> selected="selected" </c:if>
                                     >
                                     ${a.name}
                                     </option>
                                 </c:forEach>
                             </select>
                             <br>
                        </c:forEach>
                    </c:if>
                </div>
                
                   
                <%-- 
                    Sample Article combo box used to dynamically generated combo boxes based
                    on the quantity of blocks a template has
                --%>
                <div style="display:none" id="article-sample-combo">
                   <fmt:message key="newsletter.tab.mailing.label.articleforblock" /> &nbsp;
                   <select class="required" style="margin-top:5px" id="blockSelectorSelectHide{idtoreplace}">
                        <c:forEach items="${articlesOptions}" var="a">
                            <option value="${a.id}">${a.name}</option>
                        </c:forEach>
                    </select>
                   <br>
                </div>
                              
                 <div id="preview"> </div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <br>
                <c:if test="${remove}">
                    <input id="btn-remove-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.admin.general.remove" />" />
                </c:if>
                <c:if test="${!remove}">
                    <input id="btn-save-<portlet:namespace/>" type="button" value="<fmt:message key="general.save" />" />
                </c:if>
                <input id="btn-cancel-<portlet:namespace/>" type="button" value="<fmt:message key="newsletter.common.cancel" />" />
            </td>
        </tr>
    </table>
</form:form>


<%--
    Dialogs
    ##################
--%>
<div style="display:none">

    <div id="delete-mailing-dialog-<portlet:namespace/>" title="<fmt:message key="newsletter.confim.delete.title"/>">
        <fmt:message key="newsletter.confim.delete.body"/>
    </div>
</div>


<script type="text/javascript">
    
    
    jQuery(document).ready(function(){
        styleUI();
        initEvents();
        clearMessages();
    });
    
    /**
     * Apply styles to UI
     */
    function styleUI(){
        // styles on buttons
        jQuery('#mailing-panel input[type="button"]').button();
       
       
        //  confirm delete dialog
        jQuery("#delete-mailing-dialog-<portlet:namespace/>").dialog({
            autoOpen: false,
            modal: true
        });
       
       
    <c:if test="${remove}">
            disableComponents();
    </c:if>
        }
    
    
        /**
         * Init UI events
         */
        function initEvents(){
        
        
            // click on 'Remove' button
            jQuery('#btn-remove-<portlet:namespace/>').click(function(){
                deleteMailing();
            });
        
            // click on 'Cancel' button
            jQuery('#btn-cancel-<portlet:namespace/>').click(function(){
                jQuery('#mailing-panel').load('${mailingListUrl}');
            });
            
            // Select template -> generate article combos
            jQuery('#select-template-<portlet:namespace/>').change(function(){
                var blocksQuantity = jQuery(this).find('option:selected').attr('blocksQuantity');
                generateArticleCombos(blocksQuantity);
            });
        
            // click on 'Save' button
            jQuery('#btn-save-<portlet:namespace/>').click(function(){

                // add 'article' name to all article combos
                jQuery('#article-combos-<portlet:namespace/> select').attr('name','articleIds');
                
                // validate form
                var form = jQuery('#mailing-form-<portlet:namespace/>');
                if (form.valid()){
               
                    // save via ajax
                    jQuery.ajax({
                        url: '${saveMailingUrl}'
                        ,type: 'POST'
                        ,data: form.serialize()
                        ,success: function(response){
                            showMessages(response.messages);
                            jQuery('#btn-cancel-<portlet:namespace/>').trigger('click');
                        }
                        ,failure: function(response){
                            // TODO: display errors here
                        }
                    });
                }
            });
            
            
            
            
        }
    
   

        
        /**
         * Delete the mailing
         */
        function deleteMailing(){
            var mailingId = '${mailing.id}';
            jQuery("#delete-mailing-dialog-<portlet:namespace/>").dialog({
                autoOpen: false,
                modal: true,
                buttons : {
                    "<fmt:message key="newsletter.confirm.button.yes"/>" : function() {
                        jQuery.ajax({
                            url: '${deleteMailingUrl}'
                            ,type: 'POST'
                            ,data: {id: mailingId}
                            ,success: function(response){
                                if (response.success){
                                    showMessages(response.messages);
                                }else{
                                    showErrors(response.validationKeys);
                                }
                                $(this).dialog("close");
                                jQuery('#btn-cancel-<portlet:namespace/>').trigger('click');
                            }
                            ,failure: function(response){
                                $(this).dialog("close");
                                // TODO: display errors here
                            }
                        });
                        $(this).dialog("close");
                    },
                    "<fmt:message key="newsletter.common.cancel" />" : function() {
                        $(this).dialog("close");
                    }
                }
            });
            jQuery("#delete-mailing-dialog-<portlet:namespace/>").dialog("open");
        }
        
        
        /**
         * Disable components (readonly)
         */
        function disableComponents(){
            jQuery('#mailing-form-<portlet:namespace/> input[name="name"]').attr('disabled', 'disabled');
            jQuery('#mailing-form-<portlet:namespace/> select').attr('disabled', 'disabled');
        }
        
        /**
         * Generate article combos according to
         * the quantity of blocks in the selected template
         */
        function generateArticleCombos(quantity){
            jQuery('#article-combos-<portlet:namespace/>').html('');
            for(var i = 0 ; i < quantity; i++){
                var htmlCombo = jQuery('#article-sample-combo').html();
                htmlCombo = htmlCombo.replace("{idtoreplace}", i);
                jQuery('#article-combos-<portlet:namespace/>').append(htmlCombo);
            }
        }  
        
        function selectArticlesToTemplate() {
            jQuery.each(jQuery(".blockSelectorSelect"),function(i,e) {
                var check = e.value;
                var idCombo = jQuery(this).attr("id");
                idCombo = idCombo.replace("blockSelectorSelect", "");
                jQuery("#blockSelectorSelectHide" + idCombo).val(check);
            });
        }
        
        function preview() {
        	jQuery("#preview").mask('<fmt:message key="newsletter.loading.preview"/>');
        	var templateId = jQuery("#select-template-<portlet:namespace/>").val();    		
            jQuery.ajax({
				url: '${getPreviewUrl}',
                type: 'POST',
                data: {"templateId" : templateId },
                success: function(response) {
                	jQuery("#preview").unmask();
                	jQuery("#preview").html(response);

					<c:if test="${!empty mailing.id}">                     
                    jQuery.each(jQuery(".blockSelectorSelectHide"), function(i,e) {
                    	var check = e.value;
                        var idCombo = jQuery(this).attr("id");
                        idCombo = idCombo.replace("blockSelectorSelectHide", "");                         
                        jQuery("#blockSelectorSelect" + idCombo).val(check);
                    });
                   	</c:if>
                },
                failure: function(response) {
                	//console.log(response);
                }
            });        	
        }
        
        <c:if test="${!empty mailing.id}">        
        	preview();            
        </c:if>
</script>