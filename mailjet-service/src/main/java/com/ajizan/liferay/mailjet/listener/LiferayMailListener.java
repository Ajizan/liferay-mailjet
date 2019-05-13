package com.ajizan.liferay.mailjet.listener;

import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, property = {
		"destination.name=" + DestinationNames.MAIL }, service = MessageListener.class)
public class LiferayMailListener implements MessageListener {

	@Override
	public void receive(Message message) throws MessageListenerException {
		// TODO Auto-generated method stub
		
	}

}
