// console.log('Hello ws-notification-theme-contributor');

function openNotificationModal(title, uri) {

	Liferay.Util.openWindow(
			{
				dialog: {
					width: 640,
					height: 480,
					draggable: false,
					resizable: false,
					visible: true
				},
				id: "notification-modal",
				title: title,
				uri: uri,
			}
		);
	
}

function registerNotificationModal(url) {

	modalNotificationSocket = new WebSocket(url); 
	modalNotificationSocket.onopen = function (event) { console.log("/!\\ Connecting websocket"); } 
	modalNotificationSocket.onerror = function (event) { console.log(event); } 
	modalNotificationSocket.onmessage = function (event) { 
		console.log(event.data);
		eventData = JSON.parse(event.data);
		openNotificationModal(eventData.title, eventData.uri);
	} 
	modalNotificationSocket.onclose = function (event) { console.log("/!\\ Closing websocket"); }

}
