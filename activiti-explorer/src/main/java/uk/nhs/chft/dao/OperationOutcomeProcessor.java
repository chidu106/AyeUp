package uk.nhs.chft.dao;

import java.io.ByteArrayInputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.XmlParser;

import org.hl7.fhir.instance.model.OperationOutcome;

public class OperationOutcomeProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		OperationOutcome operationOutcome = null;
		
		if (exchange.getIn() !=null)
		{
			ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
			if (exchange.getIn().getHeader(Exchange.CONTENT_TYPE).equals("application/json"))
			{
				JsonParser composer = new JsonParser();
				operationOutcome = (OperationOutcome) composer.parse(xmlContentBytes);
			}
			else
			{
				XmlParser composer = new XmlParser();
				operationOutcome = (OperationOutcome) composer.parse(xmlContentBytes);
			}	
		}
		
		
		if (operationOutcome != null)
		{
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE,operationOutcome.getId());
		}
	}

}
