package uk.co.mayfieldis.activiti.processor;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Order;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Reference;


import uk.co.mayfieldis.dao.FHIRCodeSystems;
import uk.co.mayfieldis.dao.ResourceSerialiser;

public class OrderProcessorGet implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		//Processing preamble
		Boolean xmlformat= false;
		
		String format = exchange.getIn().getHeader("_format", String.class);
		
		if (format !=null && !format.isEmpty())
		{
			if (format.contains("xml"))
			{
				xmlformat = true;
			}
		}
		
		// Build the order
		Order order = new Order();
		order.setId("123");
		
		Date date = new Date();
		order.setDate(date);
		CodeableConcept type = new CodeableConcept();
		type.addCoding()
			.setSystem(FHIRCodeSystems.URI_SNOMED)
			.setDisplay("Child protection procedure")
			.setCode("134187008");
		
		order.setReason(type);
		
		Patient patient = new Patient();
		patient.setId("#pat");
		patient.addIdentifier()
			.setSystem(FHIRCodeSystems.URI_NHS_NUMBER_ENGLAND)
			.setValue("9876543210");
		
		patient.addName()
			.addFamily("Smith")
			.addGiven("Mohammed");
		
		order.getContained()
			.add(patient);
		Reference ref = new Reference();
		ref.setReference("#pat");
		order.setSubject(ref);

		// Format and build response
		String Response  = null;
		if (!xmlformat)
		{
			Response = ResourceSerialiser.serialise(order, ParserType.JSON);
		}
		else
		{
			Response = ResourceSerialiser.serialise(order, ParserType.XML);
		}
		exchange.getIn().setBody(Response);
		if (xmlformat)
		{
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		}
		else
		{
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json+fhir");
		}
		
	}

}
