<%-- 
    Document   : subscription
    Created on : 10/10/2011, 11:11:12
    Author     : ariel@rotterdam-cs.com
--%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.rcs.newsletter.core.model.NewsletterSubscription"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page contentType="text/html" isELIgnored="false" pageEncoding="UTF-8"%>
<%@taglib prefix="liferay-ui" uri="http://liferay.com/tld/ui"%>
<%@taglib prefix="liferay-portlet" uri="http://java.sun.com/portlet"%>
<%@taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@taglib prefix="theme" uri="http://liferay.com/tld/theme"%>
<%@taglib prefix="aui" uri="http://liferay.com/tld/aui"%>

<portlet:defineObjects/>

<portlet:actionURL var="registerAction">
    <portlet:param name="action" value="registerNewsletterSubscription" />
</portlet:actionURL>
<portlet:actionURL var="confirmSubscriptionAction">
    <portlet:param name="action" value="confirmNewsletterSubscription" />
</portlet:actionURL>
<portlet:actionURL var="unregisterNewsletterSubscription">
    <portlet:param name="action" value="unregisterNewsletterSubscription" />
</portlet:actionURL>
<portlet:actionURL var="confirmUnsubscriptionAction">
    <portlet:param name="action" value="confirmNewsletterUnsubscription" />
</portlet:actionURL>

<portlet:resourceURL id="confirmationPageAction" var="confirmationPageAction" />
<portlet:resourceURL id="unregisterConfirmationPageAction" var="unregisterConfirmationPageAction" />
<portlet:resourceURL id="subscriptionSuccessAction" var="subscriptionSuccessAction" />
<portlet:resourceURL id="subscriptionFailureAction" var="subscriptionFailureAction" />
<portlet:resourceURL id="unsubscriptionSuccessAction" var="unsubscriptionSuccessAction" />
<portlet:resourceURL id="unsubscriptionFailureAction" var="unsubscriptionFailureAction" />

<theme:defineObjects/>

<script type="text/javascript">
    
    function unsubscribe() {
        AUI().use('aui-autocomplete', function(A) {
            var divNode = A.one('#subscription-content-div');
            A.io.request('${unregisterNewsletterSubscription}', {
                method : 'GET'
                ,dataType : 'json'
                ,form: {
                    id: '<portlet:namespace/>subscription-form-id'
                }
                ,on : { 
                    success : function() {    
                        var response = this.get('responseData');                    
                        if(response.success) {                                
                            A.io.request('${unregisterConfirmationPageAction}', {
                                method : 'GET'                                    
                                ,data: {
                                    email: response.data.email
                                    ,categoryId: response.data.categoryId
                                }
                                ,on : { 
                                    success : function() {
                                        divNode.html(this.get('responseData'));
                                    } 
                                } 
                            });

                        } else {
                            alert(response.message)
                        }
                    } 
                } 
            });
        });
    }
    
    function subscribe() {
        AUI().use('aui-autocomplete', function(A) {
            var divNode = A.one('#subscription-content-div');
            A.io.request('${registerAction}', {
                method : 'GET'
                ,dataType : 'json'
                ,form: {
                    id: '<portlet:namespace/>subscription-form-id'
                }
                ,on : { 
                    success : function() {    
                        var response = this.get('responseData');                    
                        if(response.success) {                                
                            A.io.request('${confirmationPageAction}', {
                                method : 'GET'                                    
                                ,data: {
                                    email: response.data.email
                                    ,categoryId: response.data.categoryId
                                }
                                ,on : { 
                                    success : function() {
                                        divNode.html(this.get('responseData'));
                                    } 
                                } 
                            });

                        } else {
                            alert(response.message)
                        }
                    } 
                } 
            });
        });
    }
    
    function confirmRegistration() {
        AUI().use('aui-autocomplete', function(A) {
            var divNode = A.one('#subscription-content-div');
            A.io.request('${confirmSubscriptionAction}', {
                method : 'GET'
                ,dataType : 'json'
                ,form: {
                    id: '<portlet:namespace/>confirmation-form-id'
                }
                ,on : { 
                    success : function() {    
                        var response = this.get('responseData');                    
                        if(response.success) {                                
                            A.io.request('${subscriptionSuccessAction}', {
                                method : 'GET'                                    
                                ,data: {
                                    email: response.data.email
                                    ,categoryId: response.data.categoryId
                                    ,categoryName: response.data.categoryName
                                }
                                ,on : { 
                                    success : function() {                                        
                                        divNode.html(this.get('responseData'));
                                    } 
                                } 
                            });

                        } else {
                            A.io.request('${subscriptionFailureAction}', {
                                method : 'GET'                                    
                                ,data: {
                                    email: response.data.email
                                    ,categoryId: response.data.categoryId
                                    ,categoryName: response.data.categoryName
                                }
                                ,on : { 
                                    success : function() {
                                        divNode.html(this.get('responseData'));
                                    } 
                                } 
                            });
                        }
                    } 
                } 
            });
        });
    }
    
    function confirmUnregistration() {
        AUI().use('aui-autocomplete', function(A) {
            var divNode = A.one('#subscription-content-div');
            A.io.request('${confirmUnsubscriptionAction}', {
                method : 'GET'
                ,dataType : 'json'
                ,form: {
                    id: '<portlet:namespace/>cancellation-form-id'
                }
                ,on : { 
                    success : function() {    
                        var response = this.get('responseData');                    
                        if(response.success) {                                
                            A.io.request('${unsubscriptionSuccessAction}', {
                                method : 'GET'                                    
                                ,data: {
                                    email: response.data.email
                                    ,categoryId: response.data.categoryId
                                    ,categoryName: response.data.categoryName
                                }
                                ,on : { 
                                    success : function() {                                        
                                        divNode.html(this.get('responseData'));
                                    } 
                                } 
                            });

                        } else {
                            A.io.request('${unsubscriptionFailureAction}', {
                                method : 'GET'                                    
                                ,data: {
                                    email: response.data.email
                                    ,categoryId: response.data.categoryId
                                    ,categoryName: response.data.categoryName
                                    ,message: response.message
                                }
                                ,on : { 
                                    success : function() {
                                        divNode.html(this.get('responseData'));
                                    } 
                                } 
                            });
                        }
                    } 
                } 
            });
        });
    }
</script>

<div id="subscription-content-div">
    <h3><liferay-ui:message key="newsletter.subscribe.title"/></h3>
    <aui:form name="subscription-form-id">
        <aui:fieldset>
            <c:forEach items="${categories}" var="category">        
                <aui:input name='categoryId' label='${category.name}' type='radio' value='${category.id}'></aui:input>            
            </c:forEach>
            <aui:input name='firstName' label='<%=LanguageUtil.get(locale, "newsletter.common.firstname")%>' type='text' value=''></aui:input>
            <aui:input name='lastName' label='<%=LanguageUtil.get(locale, "newsletter.common.lastname")%>' type='text' value=''></aui:input>
            <aui:input name='email' label='<%=LanguageUtil.get(locale, "newsletter.common.email")%>' type='text' value=''></aui:input>
            <aui:button-row>
                <aui:button class='aui-button-input' value='<%=LanguageUtil.get(locale, "newsletter.unsubscribe")%>' onClick='unsubscribe()'/>
                <aui:button class="aui-button-input" value='<%=LanguageUtil.get(locale, "newsletter.subscribe")%>' onClick='subscribe()'/>
            </aui:button-row>
        </aui:fieldset>
    </aui:form>
</div>