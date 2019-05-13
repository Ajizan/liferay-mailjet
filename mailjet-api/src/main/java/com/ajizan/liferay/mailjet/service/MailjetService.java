package com.ajizan.liferay.mailjet.service;

import com.liferay.portal.kernel.exception.PortalException;

import java.util.Map;

/**
 * @author ecoquelin
 */
public interface MailjetService {

	public void sendEmail(String toEmail, String toName, String templateId, Map<String, String> variables)
			throws PortalException;

}