package com.knowesis.sift.orchestrator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.camel.Body;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.PropertyInject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowesis.sift.orchestrator.domain.Constants;
import com.knowesis.sift.orchestrator.exception.MessageResolutionException;

public class SFMCMessagingProcessor {
  
	@PropertyInject("{{so.sms.successCodes}}")
	private String successCode;
	
   ObjectMapper mapper;
   List<String> successCodeList;
   
   private String countryCode;
   
   private String messageDefinitionKey;
   
   @PostConstruct
	public void onPostConstruct() {
		
		mapper = new ObjectMapper();
		String[] errorCodes = StringUtils.split(successCode, ",");
		successCodeList = Arrays.asList(errorCodes);
		countryCode=System.getenv(Constants.COUNTRY_CODE);
		messageDefinitionKey=System.getenv(Constants.MESSAGE_DEFINITION_KEY);
		
	}
   private final Logger log = LoggerFactory.getLogger(getClass());
   
   /**
    * Check http response codes in the success list
    * Sets status, statusCode, action and response fields in the message <br>
    * Following attributes are set in the body for database logging:<br>
    * <ol>
    * <li>status</li>
    * <li>statusCode</li>
    * <li>action</li>
    * <li>response</li>
    * </ol>
    * @param body Map representation of JSON body
    * @param headers MAP representation of headers
    * @param properties MAP representation of properties
 * @return MAP OriginalMessage
 * @throws IOException 
 * @throws JsonMappingException 
 * @throws JsonParseException 
    */
   public List<Map<String, Object>> processMessagingAPIResponse (@Body Map<String,Object> body,@Headers Map<String,Object> headers,@ExchangeProperties  Map<String,Object> properties) throws JsonParseException, JsonMappingException, IOException  {
		List<Map<String, Object>> originalMsg = new ArrayList<Map<String, Object>>();
		originalMsg.addAll((Collection<? extends Map<String, Object>>) properties.get("AggregatedMessage"));
		
		List<Map<String, Object>> responseList = (List<Map<String, Object>>) body.get("responses");
		List<Map<String, Object>> responseMsg = new ArrayList<Map<String, Object>>();
		Map<String, Object> response = new HashMap<String, Object>();

		for (Map<String, Object> responseMap : responseList) {
			for (Map<String, Object> originalMap : originalMsg) {
				String flowId = (String) originalMap.get("flowId");
				String messageKey = (String) responseMap.get("messageKey");
				if(flowId != null && messageKey != null && flowId.equals(messageKey)) {
					response = populateResponseParameters(originalMap, responseMap);
					responseMsg.add(response);
				}
			}
		}
		log.debug("The originalMsg after API response{}", responseMsg);
		return responseMsg;
   }

	private Map<String, Object> populateResponseParameters(Map<String, Object> originalMap, Map<String, Object> responseMap) {

		String action = (String) originalMap.getOrDefault(Constants.action, "OFFERNOTIFY");
		if ( action.equals("CORENOTIFICATION") ) {
			originalMap.put(Constants.action, "OFFERNOTIFY");
		}else if ( action.equals("COREFULFILMENT") ) {
			originalMap.put(Constants.action, "FULFILLNOTIFY");
		}
		
		log.debug("the response in map {}", responseMap);
		if (responseMap.containsKey("errorcode")) {
			Integer errorCode = (Integer) responseMap.get("errorcode");
			String errorMessage = (String) responseMap.get("message");
			originalMap.put(Constants.status, "FAILURE");
			originalMap.put(Constants.actionStatus, "FAILURE");
			originalMap.put(Constants.actionResponse,errorCode+":"+errorMessage);
			return originalMap;
		} else {
			originalMap.put(Constants.status, "SUCCESS");
			originalMap.put(Constants.actionStatus, "SUCCESS");
			originalMap.put(Constants.actionResponse, 0+":Success");
			return originalMap;
		}
	}

	public Map<String, Object> updateTrigger (@Body Map<String,Object> body) throws JsonParseException, JsonMappingException, IOException, MessageResolutionException  {
	   	
	   //adding CountryCode
	   String msisdn=(String) body.get(Constants.msisdn);
	   if( !(msisdn.startsWith( countryCode ) && msisdn.length() >= 10) ){
		   if( msisdn.startsWith("04") ) {
			   body.replace( Constants.msisdn, countryCode+msisdn.substring(1, msisdn.length()) );
		   }
		   else {
			   body.replace(Constants.msisdn, countryCode+msisdn );
		   }
	   }
	   //personlisation of notificationMsg
	   String notificationMsg = (String) body.get(Constants.notificationMsg);
	   if ( notificationMsg != null ) {
		   Map<String, Object> deData = (Map<String, Object>) body.get("deData");
		   
		   String[] tokens = notificationMsg.split("%%",-1);
			for ( String eachWord: tokens ) {
				if ( deData.containsKey(eachWord) ) {
					notificationMsg = notificationMsg.replace("%%" + eachWord + "%%", (CharSequence) deData.get(eachWord));
				}
			}
			if ( notificationMsg.contains("%%") ) {
				log.error("NotificationMsg unable to resolve, with body - {}", mapper.writeValueAsString(body));
				throw new MessageResolutionException("NotificationMsg unable to resolve ..!");
			}else body.put(Constants.notificationMsg, notificationMsg);
	   }
	   
       return body;
   }
	
	public Map<String, Object> checkMessageDefKey (@Body Map<String,Object> body){
		
		String msgDefKey = (String) body.getOrDefault( Constants.messageDefinitionKey, "" );
		if (  msgDefKey == null || msgDefKey.isEmpty() ) {
			body.put( Constants.messageDefinitionKey, messageDefinitionKey );
		}
		return body;
	}
}
