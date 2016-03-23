package uk.co.mayfieldis.hl7v2.hapi.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.Practitioner;
import org.springframework.cglib.transform.impl.AddPropertyTransformer;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import uk.co.mayfieldis.FHIRConstants.CHFTFHIRCodeSystems;
import uk.co.mayfieldis.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.dao.ResourceSerialiser;



public class MFNM02PractitionerProcessor implements Processor {

	
	public void process(Exchange exchange) throws HL7Exception 
	{
		Message message = exchange.getIn().getBody(Message.class);
		// Use Terser as code is more readable
		Terser terser = new Terser(message);
		
		Practitioner practitioner = new Practitioner();
		HumanName name = new HumanName();
		name.addFamily(terser.get("/.STF-3-1"))
			.addGiven(terser.get("/.STF-3-2"))
			.addPrefix(terser.get("/.STF-3-5"));
		practitioner.setName(name);
		
		practitioner.addIdentifier()
			.setValue(terser.get("/.STF-1"))
			.setSystem(FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS);
		
		practitioner.addIdentifier()
			.setValue(terser.get("/.STF-2"))
			.setSystem(CHFTFHIRCodeSystems.URI_CHFT_PAS_CONSULTANT_CODE);
		
		practitioner.addPractitionerRole()
			.addSpecialty()
				.addCoding()
					.setCode(terser.get("/.PRA-5"))
					.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES);
		
		String Response = ResourceSerialiser.serialise(practitioner, ParserType.XML);
	
		
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		
		if (terser.get("/.MFI-3").equals("UPD"))
		{
			exchange.getIn().setHeader(Exchange.HTTP_PATH, "/Practitioner/"+practitioner.getId());
			
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"PUT");
		}
		else
		{
			exchange.getIn().setHeader(Exchange.HTTP_PATH, "/Practitioner");
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
		}
		
		exchange.getIn().setBody(Response);
		
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		
	}



}
