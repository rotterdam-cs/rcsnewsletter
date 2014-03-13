<%-- 
    Document   : admin
    Created on : Oct 18, 2011, 2:55:46 PM
    Author     : Ariel Parra <ariel@rotterdam-cs.com>
--%>

<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.rcs.newsletter.core.model.NewsletterCategory"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page contentType="text/html" isELIgnored="false" pageEncoding="UTF-8"%>
<%@taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui"%>
<%@taglib prefix="liferay-portlet" uri="http://java.sun.com/portlet"%>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@taglib prefix="theme" uri="http://liferay.com/tld/theme"%>
<%@taglib prefix="aui" uri="http://liferay.com/tld/aui"%>

<portlet:defineObjects/>
<portlet:actionURL var="saveNewsletterAction">
    <portlet:param name="action" value="saveNewsletterAction" />
</portlet:actionURL>
<portlet:actionURL var="updateNewsletterAction">
    <portlet:param name="action" value="updateNewsletterInformationAction" />
</portlet:actionURL>
<portlet:actionURL var="deleteNewsletterAction">
    <portlet:param name="action" value="deleteNewsletterAction" />
</portlet:actionURL>
<portlet:actionURL var="getNewsletterCategoryByIdAction">
    <portlet:param name="action" value="getNewsletterCategoryByIdAction"/>
</portlet:actionURL>
<portlet:actionURL var="sendNewsletterAction">
    <portlet:param name="action" value="sendNewsletterAction" />
</portlet:actionURL>

<theme:defineObjects/>

<script type="text/javascript">
    
    function showDiv(divId) {
        div = document.getElementById(divId);
        div.style.display = '';
    }
    
    function hideDiv(divId) {
        div = document.getElementById(divId);
        div.style.display = 'none';
    }
    
    function editCategory(categoryId) {        
        if(categoryId != -1) {
            AUI().use('aui-autocomplete-deprecated', function(A) {            
                A.io.request('${getNewsletterCategoryByIdAction}', {
                    method : 'GET'
                    ,dataType : 'json'
                    ,data: {            
                        id: categoryId
                    }
                    ,on : { 
                        success : function() { 
                            var response = this.get('responseData');

                            if(response.success) {
                                var entity = response.newsletterEntity;
                                document.getElementById('<portlet:namespace/>id').value = entity.id;
                                document.getElementById('<portlet:namespace/>name').value = entity.name;
                                document.getElementById('<portlet:namespace/>description').value = entity.description;
                                document.getElementById('<portlet:namespace/>articleId').value = entity.articleId;

                                showDiv('update-content-div');
                                hideDiv('save-content-div');
                            } else {
                                alert(response.message);                            
                            }
                        } 
                    } 
                });
            });
        } else {
            document.getElementById('<portlet:namespace/>id').value = -1;
            document.getElementById('<portlet:namespace/>name').value = '';
            document.getElementById('<portlet:namespace/>description').value = '';
            document.getElementById('<portlet:namespace/>articleId').value = '0';
                            
            hideDiv('update-content-div');
            showDiv('save-content-div');
        }        
    }
    
    function saveNewsletter() {
        AUI().use('aui-autocomplete-deprecated', function(A) {
            A.io.request('${saveNewsletterAction}', {
                method : 'GET'
                ,dataType : 'json'
                ,form: {
                    id: '<portlet:namespace/>category-form-id'
                }
                ,on : { 
                    success : function() {
                        var response = this.get('responseData');
                    
                        if(response.success) {
                            alert(response.message);
                            Liferay.Portlet.refresh('#p_p_id<portlet:namespace />');
                        } else {
                            alert(response.message);
                        }
                    } 
                } 
            });
        });
    }
  
    function updateNewsletter() {
        AUI().use('aui-autocomplete-deprecated', function(A) {
            A.io.request('${updateNewsletterAction}', {
                method : 'GET'
                ,dataType : 'json'
                ,form: {
                    id: '<portlet:namespace/>category-form-id'
                }
                ,on : { 
                    success : function() {
                        var response = this.get('responseData');
                    
                        if(response.success) {                        
                            alert(response.message);
                            Liferay.Portlet.refresh('#p_p_id<portlet:namespace />');
                        } else {
                            alert(response.message);
                        }
                    } 
                } 
            });
        });
    }
    
    function deleteNewsletter() {
        AUI().use('aui-autocomplete-deprecated', function(A) {
            A.io.request('${deleteNewsletterAction}', {
                method : 'GET'
                ,dataType : 'json'
                ,form: {
                    id: '<portlet:namespace/>category-form-id'
                }
                ,on : { 
                    success : function() {
                        var response = this.get('responseData');
                    
                        if(response.success) {
                            alert(response.message);
                            Liferay.Portlet.refresh('#p_p_id<portlet:namespace />');
                        } else {
                            alert(response.message);
                        }
                    } 
                } 
            });
        });
    }
  
    function sendNewsletter() {
        var categoryId = document.getElementById('<portlet:namespace/>id').value;
        if(categoryId != -1) {
            AUI().use('aui-autocomplete-deprecated', function(A) {
                A.io.request('${sendNewsletterAction}', {
                    method : 'GET'
                    ,dataType : 'json'
                    ,data: {            
                        categoryId: categoryId
                    }
                    ,on : { 
                        success : function() {
                            var response = this.get('responseData');
                    
                            if(response.success) {
                                alert(response.message);
                            } else {
                                alert(response.message);
                            }
                        } 
                    } 
                });
            });
        }
    }
    
</script>

<aui:fieldset>
    <aui:column>
        <aui:select name='location' id='location' label='<%=LanguageUtil.get(locale, "newsletter.admin.category.title")%>' onChange='editCategory(this.value)' inputCssClass='category-combo'>            
            <aui:option label='<%=LanguageUtil.get(locale, "newsletter.admin.category.select")%>' value='-1'></aui:option>
            <c:forEach items="${categories}" var="category">        
                <aui:option label="${category.name}" value="${category.id}"></aui:option>
            </c:forEach>            
        </aui:select>
    </aui:column>
</aui:fieldset>

<aui:form name="category-form-id">
    <aui:fieldset>
        <aui:input id='id' name='id' type='hidden' value='-1'></aui:input>
        <aui:input id='name' name='name' label='<%=LanguageUtil.get(locale, "newsletter.admin.category.name")%>' type='text' value=''></aui:input>
        <aui:input id='description' name='description' label='<%=LanguageUtil.get(locale, "newsletter.common.description")%>' type='textarea' value=''></aui:input>        
        <aui:input id='articleId' name='articleId' label='<%=LanguageUtil.get(locale, "newsletter.admin.article")%>' type='text' value='0'></aui:input>        
        <aui:button-row>
            <div id="save-content-div">
                <aui:button class='button-input' value='<%=LanguageUtil.get(locale, "newsletter.common.save")%>' onClick='saveNewsletter()'/>                
            </div>
            <div id="update-content-div" style="display: none;">
                <aui:button name="update-btn" class='button-input' value='<%=LanguageUtil.get(locale, "newsletter.common.update")%>' onClick='updateNewsletter()' />
                <aui:button name="delete-btn" class='button-input' value='<%=LanguageUtil.get(locale, "newsletter.common.delete")%>' onClick='deleteNewsletter()' />
                <aui:button name="send-btn" class='button-input' value='<%=LanguageUtil.get(locale, "newsletter.admin.send")%>' onClick='sendNewsletter()' />            
            </div>
        </aui:button-row>
    </aui:fieldset>
</aui:form>