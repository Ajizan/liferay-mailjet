package com.ajizan.liferay.mailjet.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(category = "mailjet")
@Meta.OCD(
		id =  "com.ajizan.liferay.mailjet.configuration.MailjetServiceConfiguration",
		localization = "content/Language",
		name = "mailjet-configuration-name")
public interface MailjetServiceConfiguration {

	@Meta.AD(deflt = "false", name = "mailjet-enabled", description="mailjet-enabled-description", required = false)
	public boolean isEnabled();
	
	@Meta.AD(deflt = "XXX", name = "mailjet-api-public-key", description="mailjet-api-public-key-description", required = false)
	public String getApiPublicKey();

	@Meta.AD(deflt = "XXX", name = "mailjet-api-secret-key", description="mailjet-api-secret-key-description", required = false)
	public String getApiSecretKey();
	
	@Meta.AD(deflt = "oneemailaddress@example.com", name = "mailjet-from-email-address", description="mailjet-from-email-address-description", required = false)
	public String getFromEmailAddress();
	
	@Meta.AD(deflt = "Mailjet Admin", name = "mailjet-from-name", description="mailjet-from-name-description", required = false)
	public String getFromName();
	
}
