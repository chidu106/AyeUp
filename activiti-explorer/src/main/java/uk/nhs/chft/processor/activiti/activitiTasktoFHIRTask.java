package uk.nhs.chft.processor.activiti;

import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Order;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.nhs.chft.dao.FHIRCodeSystems;
import uk.nhs.chft.dao.ResourceSerialiser;
import uk.nhs.chft.esb.activiti.ActivitiCamelRoute;

public class activitiTasktoFHIRTask implements Processor {

	private static final Logger log = LoggerFactory.getLogger(ActivitiCamelRoute.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		
		log.info("Adding http headers");
		
		String messageId = exchange.getProperty("messageId").toString();
		
		log.info("Activiti Message Id="+messageId);
		Order order = new Order();
		
		order.setId(messageId);
		
		order.addIdentifier()
			.setSystem("http://fhir.chft.nh.uk/Message/TIEMessageId")
			.setValue(messageId);
		
		Date date = new Date();
		order.setDate(date);
		CodeableConcept type = new CodeableConcept();
		type.addCoding()
			.setSystem(FHIRCodeSystems.URI_SNOMED)
			.setDisplay("Laboratory test")
			.setCode("15220000");
		
		order.setReason(type);
		
		Patient patient = new Patient();
		patient.setId("#pat");
		patient.addIdentifier()
			.setSystem(FHIRCodeSystems.URI_NHS_NUMBER_ENGLAND)
			.setValue(exchange.getProperty("NHSNumber").toString());
		
		
		patient.addName()
			.addFamily(exchange.getProperty("familyName").toString())
			.addGiven(exchange.getProperty("givenName").toString());
		
		order.getContained()
			.add(patient);
		Reference ref = new Reference();
		ref.setReference("#pat");
		order.setSubject(ref);

		// Format and build response
		String Response  = null;
		
		Response = ResourceSerialiser.serialise(order, ParserType.XML);
		
		exchange.getIn().setBody(Response);
		
		exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		//exchange.getIn().setHeader(Exchange.HTTP_QUERY, "/Task"); 
		
	}

}
