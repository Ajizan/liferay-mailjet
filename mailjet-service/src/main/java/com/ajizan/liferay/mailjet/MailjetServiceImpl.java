package com.ajizan.liferay.mailjet;

import com.ajizan.liferay.mailjet.api.MailjetService;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;

/**
 * @author ecoquelin
 */
@Component(immediate = true, property = {
		// TODO enter required service properties
}, service = MailjetService.class)
public class MailjetServiceImpl implements MailjetService {

	@Override
	public void sendEmail(String toEmail, String toName, String templateId, Map<String, String> variables) {
		
		MailjetClient client;
		MailjetRequest request;
		MailjetResponse response;
		client = new MailjetClient(System.getenv("MJ_APIKEY_PUBLIC"), System.getenv("MJ_APIKEY_PRIVATE"),
				new ClientOptions("v3.1"));
		request = new MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES,
				new JSONArray().put(new JSONObject()
						.put(Emailv31.Message.FROM,
								new JSONObject().put("Email", "pilot@mailjet.com").put("Name", "Mailjet Pilot"))
						.put(Emailv31.Message.TO,
								new JSONArray().put(new JSONObject().put("Email", "passenger1@mailjet.com").put("Name",
										"passenger 1")))
						.put(Emailv31.Message.TEXTPART,
								"Dear passenger, welcome to Mailjet! On this {{var:day:\"monday\"}}, may the delivery force be with you! {{var:personalmessage:\"\"}}")
						.put(Emailv31.Message.HTMLPART,
								"<h3>Dear passenger, welcome to <a href=\"https://www.mailjet.com/\">Mailjet</a>!<br /> On this {{var:day:\"monday\"}}, may the delivery force be with you! {{var:personalmessage:\"\"}}")
						.put(Emailv31.Message.TEMPLATELANGUAGE, true)
						.put(Emailv31.Message.SUBJECT, "Your email flight plan!").put(Emailv31.Message.VARIABLES,
								new JSONObject().put("day", "Tuesday").put("personalmessage", "Happy birthday!"))));
		response = client.post(request);
		System.out.println(response.getStatus());
		System.out.println(response.getData());

	}

}