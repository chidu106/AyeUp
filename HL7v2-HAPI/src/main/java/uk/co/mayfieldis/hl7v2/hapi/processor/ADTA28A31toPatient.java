package uk.co.mayfieldis.hl7v2.hapi.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import uk.co.mayfieldis.FHIRConstants.CHFTFHIRCodeSystems;
import uk.co.mayfieldis.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.dao.ResourceSerialiser;

public class ADTA28A31toPatient implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.hl7v2.hapi.processor.ADTA28A31toPatient.class);
	@Override
	public void process(Exchange exchange) throws Exception {
		
		Message message = exchange.getIn().getBody(Message.class);
		
		Patient patient = new Patient();
		
		// Use Terser as code is more readable
		Terser terser = new Terser(message);
		Integer maxRepitions = 5;
		
		try
		{
			for (int f=1;f<maxRepitions;f++)
			{
				//log.info("Item="+f);
				try
				{
					if (!terser.get("/.PID-3("+f+")-1").isEmpty())
					{
						log.info("Item "+f+" is not empty "+terser.get("/.PID-3("+f+")-1"));
						switch (terser.get("/.PID-3("+f+")-4").toString())
						{
							case "PAS":
								patient.addIdentifier()
									.setSystem(CHFTFHIRCodeSystems.URI_PATIENT_DISTRICT_NUMBER)
									.setValue(terser.get("/.PID-3("+f+")-1").toString());
								break;
							case "RWY":
								patient.addIdentifier()
									.setSystem(CHFTFHIRCodeSystems.URI_PATIENT_HOSPITAL_NUMBER)
									.setValue(terser.get("/.PID-3("+f+")-1").toString());
								break;
							case "NHS":
								patient.addIdentifier()
									.setSystem(FHIRCodeSystems.URI_NHS_NUMBER_ENGLAND)
									.setValue(terser.get("/.PID-3("+f+")-1").toString());
								break;
						}
					}
				}
				catch(Exception ex)
				{
					// Exception thrown on no data
				}
			}
		}
		catch(Exception ex)
		{
		 	log.error(ex.getMessage());
		}	
			
		String Response = ResourceSerialiser.serialise(patient, ParserType.XML);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		//exchange.getIn().setHeader(Exchange.HTTP_PATH, "/Practitioner/"+patient.getId());
		exchange.getIn().setBody(Response);
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		
	}

}
