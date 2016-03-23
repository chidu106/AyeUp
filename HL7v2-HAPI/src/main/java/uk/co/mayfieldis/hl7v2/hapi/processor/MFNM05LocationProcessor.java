package uk.co.mayfieldis.hl7v2.hapi.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.HealthcareService;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.HealthcareService.ServiceTypeComponent;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;

import ca.uhn.hl7v2.util.Terser;
import uk.co.mayfieldis.FHIRConstants.CHFTFHIRCodeSystems;
import uk.co.mayfieldis.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.dao.ResourceSerialiser;



public class MFNM05LocationProcessor implements Processor {

	
	
	
	public void process(Exchange exchange) throws HL7Exception 
	{
		Message message = exchange.getIn().getBody(Message.class);
		
		// Use Terser as code is more readable
		Terser terser = new Terser(message);
		
		if (!terser.get("/.LOC-1-4").isEmpty())
		{
			exchange.getIn().setHeader("FHIRLocation", "Location?idenitifer="+CHFTFHIRCodeSystems.+terser.get("/.LOC-1-4"));
			
		}	
		
	}



}
