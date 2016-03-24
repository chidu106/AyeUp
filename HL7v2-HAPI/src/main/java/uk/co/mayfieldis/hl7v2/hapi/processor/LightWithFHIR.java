package uk.co.mayfieldis.hl7v2.hapi.processor;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class LightWithFHIR implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange hl7v2Exchange, Exchange FHIRExchange) {
		// TODO Auto-generated method stub
		return hl7v2Exchange;
	}

}
