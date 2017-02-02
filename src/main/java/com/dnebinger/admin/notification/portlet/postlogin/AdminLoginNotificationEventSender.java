package com.dnebinger.admin.notification.portlet.postlogin;

import com.dnebinger.admin.notification.portlet.AdminNotificationPortlet;
import com.dnebinger.admin.notification.portlet.AdminNotificationPortletKeys;
import com.dnebinger.admin.notification.portlet.Constants;
import com.dnebinger.admin.notification.portlet.notifications.AdminNotificationType;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.GroupSubscriptionCheckSubscriptionSender;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SubscriptionSender;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

/**
 * class AdminLoginNotificationEventSender: This is the admin login notification
 * sender that implements the post login lifecycle action handler.
 *
 * @author dnebinger
 */
@Component(
		immediate = true, property = {"key=login.events.post"},
		service = LifecycleAction.class
)
public class AdminLoginNotificationEventSender implements LifecycleAction {

	@Override
	public void processLifecycleEvent(LifecycleEvent lifecycleEvent)
			throws ActionException {

		// get the request associated with the event
		HttpServletRequest request = lifecycleEvent.getRequest();

		// get the user associated with the event
		User user = null;

		try {
			user = PortalUtil.getUser(request);
		} catch (PortalException e) {
			// failed to get the user, just ignore this
		}

		if (user == null) {
			// failed to get a valid user, just return.
			return;
		}

		// We have the user, but are they an admin?
		PermissionChecker permissionChecker = null;

		try {
			permissionChecker = PermissionCheckerFactoryUtil.create(user);
		} catch (Exception e) {
			// ignore the exception
		}

		if (permissionChecker == null) {
			// failed to get a permission checker
			return;
		}

		// If the permission checker indicates the user is not omniadmin, nothing to report.
		if (! permissionChecker.isOmniadmin()) {
			return;
		}

		// this user is an administrator, need to issue the event
		ServiceContext serviceContext = null;

		try {
			// create a service context for the call
			serviceContext = ServiceContextFactory.getInstance(request);

			// note that when you're behind an LB, the remote host may be the address
			// for the LB instead of the remote client.  In these cases the LB will often
			// add a request header with a special key that holds the remote client host
			// so you'd want to use that if it is available.
			String fromHost = request.getRemoteHost();

			// notify subscribers
			notifySubscribers(user.getUserId(), fromHost, user.getCompanyId(), serviceContext);
		} catch (PortalException e) {
			// ignored
		}
	}

	protected void notifySubscribers(long userId, String fromHost, long companyId, ServiceContext serviceContext)
			throws PortalException {

		// so all of this stuff should normally come from some kind of configuration.
		// As this is just an example, we're using a lot of hard coded values and portal-ext.properties values.

		String entryTitle = "Admin User Login";

		String fromName = PropsUtil.get(Constants.EMAIL_FROM_NAME);
		String fromAddress = GetterUtil.getString(PropsUtil.get(Constants.EMAIL_FROM_ADDRESS), PropsUtil.get(PropsKeys.ADMIN_EMAIL_FROM_ADDRESS));

		LocalizedValuesMap subjectLocalizedValuesMap = new LocalizedValuesMap();
		LocalizedValuesMap bodyLocalizedValuesMap = new LocalizedValuesMap();

		subjectLocalizedValuesMap.put(Locale.ENGLISH, "Administrator Login");
		bodyLocalizedValuesMap.put(Locale.ENGLISH, "Adminstrator has logged in.");

		AdminLoginSubscriptionSender subscriptionSender =
				new AdminLoginSubscriptionSender();

		subscriptionSender.setFromHost(fromHost);

		subscriptionSender.setClassPK(0);
		subscriptionSender.setClassName(AdminNotificationPortlet.class.getName());
		subscriptionSender.setCompanyId(companyId);

		subscriptionSender.setCurrentUserId(userId);
		subscriptionSender.setEntryTitle(entryTitle);
		subscriptionSender.setFrom(fromAddress, fromName);
		subscriptionSender.setHtmlFormat(true);

		subscriptionSender.setMailId("admin_login", 0);

		int notificationType = AdminNotificationType.NOTIFICATION_TYPE_ADMINISTRATOR_LOGIN;

		subscriptionSender.setNotificationType(notificationType);

		String portletId = AdminNotificationPortletKeys.ADMIN_NOTIFICATION;

		subscriptionSender.setPortletId(portletId);

		subscriptionSender.setReplyToAddress(fromAddress);
		subscriptionSender.setServiceContext(serviceContext);

		subscriptionSender.addPersistedSubscribers(AdminNotificationPortlet.class.getName(), 0);

		subscriptionSender.flushNotificationsAsync();
	}

}