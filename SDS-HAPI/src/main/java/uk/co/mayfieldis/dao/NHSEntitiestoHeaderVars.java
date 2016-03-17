package uk.co.mayfieldis.dao;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;


public class NHSEntitiestoHeaderVars implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		NHSEntities entity = exchange.getIn().getBody(NHSEntities.class);
		
		exchange.getIn().setBody(null);
		exchange.getIn().setHeader(Exchange.HTTP_METHOD,"GET");
		exchange.getIn().setHeader("OrganisationCode",entity.OrganisationCode); 
		exchange.getIn().setHeader("ParentOrganisationCode",entity.ParentOrganisationCode);
		
	}

}
