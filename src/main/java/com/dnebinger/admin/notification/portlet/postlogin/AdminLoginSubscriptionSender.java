package com.dnebinger.admin.notification.portlet.postlogin;

import com.dnebinger.admin.notification.portlet.Constants;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Subscription;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.SubscriptionSender;

/**
 * class AdminLoginSubscriptionSender: An overriding class to support our own
 * subscription sender details.
 *
 * @author dnebinger
 */
public class AdminLoginSubscriptionSender extends SubscriptionSender {

	private static final long serialVersionUID = -7152698157653361441L;

	protected void populateNotificationEventJSONObject(
			JSONObject notificationEventJSONObject) {

		super.populateNotificationEventJSONObject(notificationEventJSONObject);

		notificationEventJSONObject.put(Constants.FROM_HOST, _fromHost);
	}

	@Override
	protected boolean hasPermission(Subscription subscription, String className, long classPK, User user) throws Exception {
		return true;
	}

	@Override
	protected boolean hasPermission(Subscription subscription, User user) throws Exception {
		return true;
	}

	@Override
	protected void sendNotification(User user) throws Exception {
		// remove the super classes filtering of not notifying user who is self.
		// makes sense in most cases, but we want a notification of admin login so
		// we know when never any admin logs in from anywhere at any time.

		// will be a pain if we get notified because of our own login, but we want to
		// know if some hacker gets our admin credentials and logs in and it's not really us.

		sendEmailNotification(user);
		sendUserNotification(user);
	}

	public void setFromHost(String fromHost) {
		this._fromHost = fromHost;
	}

	private String _fromHost;
}
