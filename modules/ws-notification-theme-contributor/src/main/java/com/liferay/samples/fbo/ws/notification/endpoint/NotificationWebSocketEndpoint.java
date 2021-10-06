package com.liferay.samples.fbo.ws.notification.endpoint;


import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.samples.fbo.ws.notification.configuration.NotificationConfiguration;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
	    immediate = true,
		configurationPid = "com.liferay.samples.fbo.ws.notification.configuration.NotificationConfiguration",
	    property = {"org.osgi.http.websocket.endpoint.path=/o/notification"},
	    service = Endpoint.class
	)
public class NotificationWebSocketEndpoint extends Endpoint {

	private final static Logger LOG = LoggerFactory.getLogger(NotificationWebSocketEndpoint.class);

	private volatile NotificationConfiguration _notificationConfiguration;
	
	@Activate
	@Modified
	private void activate(Map<String, String> properties) {

		this._notificationConfiguration = ConfigurableUtil.createConfigurable(NotificationConfiguration.class, properties);
		
	}
	
	private Map<String, Timer> timers = new ConcurrentHashMap<String, Timer>();
	
	@Override
	public void onOpen(Session session, EndpointConfig config) {

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                	
                	if(_notificationConfiguration.enabled()) {
	                	StringBuilder eventData = new StringBuilder();
	                	eventData.append("{\"title\": \"");
	                	eventData.append(_notificationConfiguration.notificationTitle());
	                	eventData.append("\", \"uri\": \"");
	                	eventData.append(_notificationConfiguration.notificationMessageUri());
	                	eventData.append("\"}");
	                    session.getBasicRemote().sendText(eventData.toString());
                	}
                	
                } catch (IOException e) {
                	LOG.error("Web socket IO exception", e);
                }
            }
        };
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 20 * 1000);
        
        timers.put(session.getId(), timer);
		
	}

	@Override
	public void onClose(Session session, CloseReason closeReason) {

		Timer timer = timers.get(session.getId());
		timer.cancel();
		
		timers.remove(session.getId());
		
		super.onClose(session, closeReason);
	}
	
}
