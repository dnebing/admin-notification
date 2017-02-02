<%@ include file="/init.jsp" %>

<%
	Subscription subscription = subscriptionLocalService.fetchSubscription(user.getCompanyId(), user.getUserId(), AdminNotificationPortlet.class.getName(), 0);

	boolean subscribed = false;
	String command = Constants.SUBSCRIBE;

	if (subscription != null) {
		subscribed = true;
		command = Constants.UNSUBSCRIBE;
	}
%>
<portlet:actionURL name="/update_subscription" var="updateSubscriptionURL">
	<portlet:param name="mvcActionCommand" value="/update_subscription" />
</portlet:actionURL>

<h2>Admin Login Notification Subscription</h2>
<p>Toggle your subscription status.</p>

<aui:form action="<%= updateSubscriptionURL %>" method="post" name="fm" >
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= command %>" />

	<aui:fieldset-group markupView="lexicon">
		<aui:fieldset>
			<aui:button-row>
				<aui:button type="submit" value="<%= command %>" />
			</aui:button-row>
		</aui:fieldset>
	</aui:fieldset-group>
</aui:form>

