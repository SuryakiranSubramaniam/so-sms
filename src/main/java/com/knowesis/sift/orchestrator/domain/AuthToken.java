package com.knowesis.sift.orchestrator.domain;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used for process the response from AUTH API
 *
 */
public class AuthToken {

	private String encryptAlgorithm;
	private String authTokenRequest;
	private String encryptPassword;
	
	@PostConstruct
	public void onPostConstruct() {
		authTokenRequest = System.getenv("AUTH_TOKEN_REQUEST");
		encryptPassword = System.getenv("CAMEL_ENCRYPTION_PASSWORD");
		encryptAlgorithm = System.getenv("SO_ENCRYPT_ALGORITHM");
	}
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	/**
	 * authToken 
	 */
	private String authToken;
	/**
	 *  rest url
	 */
	private String restURL;
	
	/**
	 * This variable stores the timestamp at which the token expires
	 */
	private long tokenExpiryEpoch;
	
	/**
	 * Set authentication token in a string variable authToken
	 * Set rest URL  in a string variable restURL
	 * @param body Map. It is the auth token recieved from AUTH API
	 */
	/*
	 * Sample respsonse from auth Api of SFMC
		{
		    "access_token": "eyJhbGciOiJIUzI1NiIsImtpZCI6IjEiLCJ2ZXIiOiIxIiwidHlwIjoiSldUIn0.eyJhY2Nlc3NfdG9rZW4iOiJaR2JIRWJISUR1S2FSSks3MjVPTFAyNTMiLCJjbGllbnRfaWQiOiIxODQwaHh0NXlxY2FiZ3B1NWluMGwzd2oiLCJlaWQiOjEwMDAxNDU0OSwic3RhY2tfa2V5IjoiUzEwIiwicGxhdGZvcm1fdmVyc2lvbiI6MiwiY2xpZW50X3R5cGUiOiJTZXJ2ZXJUb1NlcnZlciJrDKNl4MBFTreJez28nhxBBpbFDl8MYzSSba2XQDti9DmodzYY4XhjNgb2FbVqfuopZnhiqX1p3q1u13cWsg54",
		    "token_type": "Bearer",
		    "expires_in": 1079,
		    "scope": "offline automations_execute automations_read automations_write journeys_execute journeys_read journeys_write email_read email_send email_write push_read push_send push_write sms_read sms_send sms_write audiences_read audiences_write list_and_subscribers_read list_and_subscribers_write data_extensions_read data_extensions_write file_locations_read file_locations_write tracking_events_read tracking_events_write",
		    "soap_instance_url": "https://mc63kdf7d4l9r0c3-0njhd1ss851.soap.marketingcloudapis.com/",
		    "rest_instance_url": "https://mc63kdf7d4l9r0c3-0njhd1ss851.rest.marketingcloudapis.com/"
		}
	 */
	public void setVariables(@Body Map<String,Object> body) {
		this.authToken = "Bearer " + (String) body.get("access_token");
		this.restURL = StringUtils.substringAfter((String) body.get("rest_instance_url"), "https://") ;
		//expires_in is in seconds, so convert to milliseconds and add current system time to get the time at which the token will expire
		this.tokenExpiryEpoch = Long.valueOf(body.get("expires_in").toString()) * 1000 + System.currentTimeMillis() ; 
		log.trace("Authorization Token,{}","Bearer " + (String) body.get("access_token"));
		log.trace("Rest URL,{}", StringUtils.substringAfter((String) body.get("rest_instance_url"), "https://"));
	}

	/**
	 *  set Auth token in a header <code> Authorization </code>
	 *  set rest url in a header <code> RestURL </code>
	 * @param headers Map. Is has all the exchange headers
	 */
	public void setAPIHeaders(@Headers Map<String,Object> headers) {
		headers.put("Authorization", authToken);
		headers.put("RestURL", restURL);
		log.debug("API headers set succesfully");
	}	
	
	/**
	 * 
	 * @param headers
	 * this method checks if the authentication token has expired and a header 'isTokenExpired' is appropriately set
	 */
	public void checkTokenExpiry(@Headers Map<String,Object> headers) {
		if (System.currentTimeMillis() > tokenExpiryEpoch)
			headers.put("isTokenExpired", true);
		else
			headers.put("isTokenExpired", false);
	}

	/**
	 * Function to decrypt the AuthTokenRequest.
	 * 
	 * @return decrypted Request body.
	 */
	
	public String getAuthTokenRequest () {
		return decrypt(authTokenRequest);
	}
	
	/**
     *  Function to decrypt RequestBody
     */
    private String decrypt(String encodedReq) {
    	StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(encryptPassword);
        encryptor.setAlgorithm(encryptAlgorithm);
        String decryptedReq = encryptor.decrypt(encodedReq);
   	 	return decryptedReq;
       
    }
    
	
}
