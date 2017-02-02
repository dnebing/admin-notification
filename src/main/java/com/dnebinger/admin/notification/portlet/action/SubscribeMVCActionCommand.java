package com.dnebinger.admin.notification.portlet.action;

import com.dnebinger.admin.notification.portlet.AdminNotificationPortlet;
import com.dnebinger.admin.notification.portlet.AdminNotificationPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.SubscriptionLocalService;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

/**
 * class SubscribeMVCActionCommand: The action command to handle subscription and unsubscription.
 *
 * @author dnebinger
 */
@Component(
		immediate = true,
		property = {
				"javax.portlet.name=" + AdminNotificationPortletKeys.ADMIN_NOTIFICATION,
				"mvc.command.name=/update_subscription"
		},
		service = MVCActionCommand.class
)
public class SubscribeMVCActionCommand extends BaseMVCActionCommand {
	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (Validator.isNull(cmd)) {
			// an error
		}

		long userId = PortalUtil.getUserId(actionRequest);

		if (Constants.SUBSCRIBE.equals(cmd)) {
			_subscriptionLocalService.addSubscription(userId, 0, AdminNotificationPortlet.class.getName(), 0);
		} else if (Constants.UNSUBSCRIBE.equals(cmd)) {
			_subscriptionLocalService.deleteSubscription(userId, AdminNotificationPortlet.class.getName(), 0);
		}
	}

	@Reference(unbind = "-")
	protected void setSubscriptionLocalService(final SubscriptionLocalService subscriptionLocalService) {
		_subscriptionLocalService = subscriptionLocalService;
	}

	private SubscriptionLocalService _subscriptionLocalService;
}
