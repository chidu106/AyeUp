package uk.co.mayfieldis.dao;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;


public class NHSEntitiestoHeaderVars implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		NHSEntities entity = exchange.getIn().getBody(NHSEntities.class);
		
		exchange.getIn().setHeader("OrganisationCode",entity.OrganisationCode);
		if (entity.ParentOrganisationCode != null && !entity.ParentOrganisationCode.isEmpty())
		{
			exchange.getIn().setHeader("ParentOrganisationCode",entity.ParentOrganisationCode);
		}
		else
		{
			exchange.getIn().setHeader("ParentOrganisationCode",entity.HighLevelHealthGeography);
		}
		
		
	}

}
