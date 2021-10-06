package com.liferay.samples.fbo.ws.notification.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(
		category = "third-party", scope = ExtendedObjectClassDefinition.Scope.SYSTEM
	)
@Meta.OCD(
	    id = "com.liferay.samples.fbo.ws.notification.configuration.NotificationConfiguration",
	    localization = "content/Language", name = "notification-configuration-name"
	)
public interface NotificationConfiguration {

	@Meta.AD(deflt = "false", name = "notification-enabled", required = false)
	public boolean enabled();

	@Meta.AD(deflt = "", name = "notification-message-uri", required = false)
	public String notificationMessageUri();

	@Meta.AD(deflt = "", name = "notification-title", required = false)
	public String notificationTitle();

}
