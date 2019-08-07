package com.ajizan.liferay.mailjet.service.impl;

import com.ajizan.liferay.mailjet.configuration.MailjetServiceConfiguration;
import com.ajizan.liferay.mailjet.service.MailjetService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Emailv31;
import com.mailjet.client.resource.Template;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author ecoquelin
 */
@Component(immediate = true, property = {

}, configurationPid = "com.ajizan.liferay.mailjet.configuration.MailjetServiceConfiguration", service = MailjetService.class)
public class MailjetServiceImpl implements MailjetService {

	@Override
	public void sendEmail(String toEmail, String toName, String templateId, Map<String, String> variables)
			throws PortalException {

		if (!_mailjetServiceConfiguration.isEnabled()) {
			_log.warn("receiving message to send through mailjet but the service is disabled");
		}

		JSONObject variablesAsJson = new JSONObject(variables);

		String mailjetApiPublicKey = _mailjetServiceConfiguration.getApiPublicKey();
		String mailjetApiSecretKey = _mailjetServiceConfiguration.getApiSecretKey();
		String fromEmailAddress = _mailjetServiceConfiguration.getFromEmailAddress();
		String fromName = _mailjetServiceConfiguration.getFromName();
		int mailjetTemplateId = this.getMailjetTemplateId(templateId);
		//int mailjetTemplateId = 9999;

		if (_log.isDebugEnabled()) {
			_log.debug("using mailjetTemplateId=" + mailjetTemplateId);
			Iterator<String> iterator = variables.keySet().iterator();
			_log.debug("toEmail=" + toEmail);
			_log.debug("toName=" + toName);
			_log.debug("fromEmailAddress=" + toName);
			_log.debug("fromName=" + toName);
			while (iterator.hasNext()) {
				String variable = iterator.next();
				String value = variables.get(variable);
				_log.debug(variable + "=" + value);
			}
		}


		MailjetClient client;
		MailjetRequest request;
		MailjetResponse response;
		client = new MailjetClient(mailjetApiPublicKey, mailjetApiSecretKey, new ClientOptions("v3.1"));
		request = new MailjetRequest(
				Emailv31.resource)
						.property(Emailv31.MESSAGES,
								new JSONArray()
										.put(new JSONObject()
												.put(Emailv31.Message.FROM,
														new JSONObject().put("Email", fromEmailAddress).put("Name",
																fromName))
												.put(Emailv31.Message.TO,
														new JSONArray().put(new JSONObject().put("Email", toEmail)
																.put("Name", toName)))
												.put(Emailv31.Message.TEMPLATELANGUAGE, true)
												.put(Emailv31.Message.TEMPLATEID, mailjetTemplateId)
												.put(Emailv31.Message.VARIABLES, variablesAsJson)));

		try {
			response = client.post(request);
		} catch (MailjetException | MailjetSocketTimeoutException e) {
			throw new PortalException(e);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("status=" + response.getStatus());
			_log.debug("data=" + response.getData());
		}

		if (response.getStatus() != 200) {
			throw new PortalException("Mailjet has rejected the message. status=" + response.getStatus());
		}

	}

	private int getMailjetTemplateId(String templateId) throws PortalException {

		if (!templateIds.containsKey(templateId)) {

			if (_log.isDebugEnabled()) {
				_log.debug("template name " + templateId + " has not yet been cached");
			}

			String mailjetApiPublicKey = _mailjetServiceConfiguration.getApiPublicKey();
			String mailjetApiSecretKey = _mailjetServiceConfiguration.getApiSecretKey();

			MailjetClient client;
			MailjetRequest request;
			MailjetResponse response;
			client = new MailjetClient(mailjetApiPublicKey, mailjetApiSecretKey);
			try {
				request = new MailjetRequest(Template.resource, "user|" + templateId);
				_log.debug(request.buildUrl() + request.queryString());
				response = client.get(request);
			} catch (MailjetException | MailjetSocketTimeoutException | UnsupportedEncodingException
					| MalformedURLException e) {
				throw new PortalException(e);
			}

			if (_log.isDebugEnabled()) {
				_log.debug("status=" + response.getStatus());
				_log.debug("data=" + response.getData());
			}

			if (response.getStatus() == 200) {
				int mailjetTemplateId = ((JSONObject) response.getData().get(0)).getInt(Template.ID);
				templateIds.put(templateId, mailjetTemplateId);
			} else {
				throw new PortalException("no template exists for name " + templateId);
			}
		}

		return GetterUtil.getInteger(templateIds.get(templateId));

	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_mailjetServiceConfiguration = ConfigurableUtil.createConfigurable(MailjetServiceConfiguration.class,
				properties);
	}

	private volatile MailjetServiceConfiguration _mailjetServiceConfiguration;

	private static Map<String, Integer> templateIds = new HashMap<>();

	private static Log _log = LogFactoryUtil.getLog(MailjetServiceImpl.class);

}