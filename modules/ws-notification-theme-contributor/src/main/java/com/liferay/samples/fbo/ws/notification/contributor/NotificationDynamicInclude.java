package com.liferay.samples.fbo.ws.notification.contributor;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.samples.fbo.ws.notification.configuration.NotificationConfiguration;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
		immediate = true,
		configurationPid = "com.liferay.samples.fbo.ws.notification.configuration.NotificationConfiguration",
		service = DynamicInclude.class
	)
public class NotificationDynamicInclude implements DynamicInclude {

	private final static Logger LOG = LoggerFactory.getLogger(NotificationDynamicInclude.class);
	
	private volatile NotificationConfiguration _notificationConfiguration;
	
	@Activate
	@Modified
	private void activate(Map<String, String> properties) {

		this._notificationConfiguration = ConfigurableUtil.createConfigurable(NotificationConfiguration.class, properties);
		
	}
	
	@Override
	public void include(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String key)
			throws IOException {

		String messageURL = _notificationConfiguration.notificationMessageUri();
		
		if(httpServletRequest.getRequestURL().toString().equals(messageURL)) {
			return;
		}
		
		Locale locale = PortalUtil.getLocale(httpServletRequest);
		
		User user;
		
		try {
			user = PortalUtil.getUser(httpServletRequest);
		} catch (PortalException e) {
			LOG.error("Failed to get User", e);
			return;
		}
		
		if(isAdmin(user)) {

			StringBuilder content = new StringBuilder();
			content.append("registerNotificationModal('")
				.append("ws://")
				.append(httpServletRequest.getServerName())
				.append(":")
				.append(httpServletRequest.getServerPort())
				.append("/o/notification');");
			
			ScriptData scriptData = new ScriptData();

			scriptData.append(null, content.toString(), StringPool.BLANK, ScriptData.ModulesType.ES6);

			scriptData.writeTo(httpServletResponse.getWriter());
			
		}
		
	}

	private boolean isAdmin(User user) {
		
		Iterator<Role> rolesIterator = user.getRoles().iterator();

		while(rolesIterator.hasNext()) {
			Role role = rolesIterator.next();
			if(RoleConstants.ADMINISTRATOR.equals(role.getName())) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

}
