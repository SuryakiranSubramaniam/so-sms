package com.knowesis.sift.orchestrator;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aggregates the messages for sending SMS request<br>
 * 
 * @param oldExchange Old message present in the camel exchange
 * @param newExchange New message  present in the camel exchange
 * 
 * @return Aggregated exchange body.
 */
public class MessageAggregationStrategy implements AggregationStrategy {
	private Logger log = LoggerFactory.getLogger(MessageAggregationStrategy.class);
	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		log.debug("Aggregatory body-Source: oldExchange-{}, newExchange-{}-",oldExchange,newExchange);
		
		Object newBody = newExchange.getIn().getBody();
	
		ArrayList<HashMap<String, Object>> list = null;
		if (oldExchange == null) {
			list = new ArrayList<HashMap<String, Object>>();
			HashMap<String,Object> newMessageMap = new HashMap<String,Object>();
			list.add((HashMap<String, Object>)newBody);
			newExchange.getIn().setBody(list);
			log.debug("old Exchange is null");
			return newExchange;
		} else {
			list = ((Exchange) oldExchange).getIn().getBody(ArrayList.class);
			HashMap<String,Object> newMessageMap = new HashMap<String,Object>();
			list.add((HashMap<String, Object>)newBody);
			log.debug("old Exchange not null");
			log.info("The number of message processed {}",list.size());
			return oldExchange;
		}
	}
}
