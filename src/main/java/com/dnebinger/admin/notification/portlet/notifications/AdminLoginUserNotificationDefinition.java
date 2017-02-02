package com.dnebinger.admin.notification.portlet.notifications;

import com.dnebinger.admin.notification.portlet.AdminNotificationPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;
import org.osgi.service.component.annotations.Component;

/**
 * class AdminLoginUserNotificationDefinition: The user notification definition for the user login.
 *
 * @author dnebinger
 */
@Component(
		immediate = true,
		property = {"javax.portlet.name=" + AdminNotificationPortletKeys.ADMIN_NOTIFICATION},
		service = UserNotificationDefinition.class
)
public class AdminLoginUserNotificationDefinition extends UserNotificationDefinition {
	public AdminLoginUserNotificationDefinition() {
		// pass in our portlet key, 0 for a class name id (don't care about it), the notification type (not really), and
		// finally the resource bundle key for the message the user sees.
		super(AdminNotificationPortletKeys.ADMIN_NOTIFICATION, 0,
				AdminNotificationType.NOTIFICATION_TYPE_ADMINISTRATOR_LOGIN,
				"receive-a-notification-when-an-admin-logs-in");

		// add a notification type for each sort of notification that we want to support.
		addUserNotificationDeliveryType(
				new UserNotificationDeliveryType(
						"email", UserNotificationDeliveryConstants.TYPE_EMAIL, false,
						true));
		addUserNotificationDeliveryType(
				new UserNotificationDeliveryType(
						"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
						true));
	}
}
