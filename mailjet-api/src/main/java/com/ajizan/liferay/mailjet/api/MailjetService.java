package com.ajizan.liferay.mailjet.api;

import java.util.Map;

/**
 * @author ecoquelin
 */
public interface MailjetService {
	
	public void sendEmail(String toEmail, String toName, String templateId, Map<String, String> variables );
	
}