package uk.co.mayfieldis.dao;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;


public class NHSConsultantEntitiestoHeaderVars implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		NHSConsultantEntities entity = exchange.getIn().getBody(NHSConsultantEntities.class);
		exchange.getIn().setHeader("ParentOrganisationCode",entity.LocationOrganisationCode);
	}

}
