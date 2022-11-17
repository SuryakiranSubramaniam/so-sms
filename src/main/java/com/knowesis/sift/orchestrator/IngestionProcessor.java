package com.knowesis.sift.orchestrator;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.PropertyInject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowesis.sift.orchestrator.domain.Constants;

public class IngestionProcessor {
	@PropertyInject("{{so.ingestion.successCode}}")
	private int successCode;
	ObjectMapper mapper;
	
	 @PostConstruct
	public void onPostConstruct() {
		 mapper = new ObjectMapper();
	 }
	
	public Map<String, Object> processAPIResponse (@Body Map<String,Object> body,@Headers Map<String,Object> headers,@ExchangeProperties  Map<String,Object> properties) throws JsonParseException, JsonMappingException, IOException   {
		 Map<String, Object> originalMsg = mapper.readValue((String) properties.get(Constants.OriginalMessage), new TypeReference<Map<String, Object>>(){});
		int httpResponseCode=(int) headers.get(Exchange.HTTP_RESPONSE_CODE);
        if(httpResponseCode == successCode) {
        	headers.put("deSuccess",true);
        	return originalMsg;
        }
        headers.put("deSuccess",false);
        originalMsg.put(Constants.status,"FAILURE" );
        return originalMsg;
	}
}
