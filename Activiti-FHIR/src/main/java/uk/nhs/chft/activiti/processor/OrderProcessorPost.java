package uk.nhs.chft.activiti.processor;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.DiagnosticOrder;
import org.hl7.fhir.instance.model.Order;
import org.hl7.fhir.instance.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;







import uk.nhs.chft.FHIRConstants.Identifiers;
import uk.nhs.chft.dao.FHIRCodeSystems;

public class OrderProcessorPost implements Processor {

	
	@Autowired
	RuntimeService runtimeService;
	
	private static final Logger log = LoggerFactory.getLogger(uk.nhs.chft.activiti.processor.OrderProcessorPost.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		
		ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
		Order order = null;
		if (exchange.getIn().getHeader(Exchange.CONTENT_TYPE).equals("application/json"))
		{
			JsonParser composer = new JsonParser();
			order = (Order) composer.parse(xmlContentBytes);
		}
		else
		{
			XmlParser composer = new XmlParser();
			order = (Order) composer.parse(xmlContentBytes);
		}
		
		log.info("Order Reason Code "+ order.getReasonCodeableConcept().getCoding().get(0).getCode() );
	
		
		if (order.getReasonCodeableConcept().getCoding().get(0).getCode().equals("15220000"))
		{
			
			Map<String, Object> variables = new HashMap<String, Object>();
			
			log.info("Started Reject Order Process");
			DiagnosticOrder test = (DiagnosticOrder) order.getContained().get(0);
			Patient patient = (Patient) test.getContained().get(0);
			String hospitalNumber = "";
			String NHSNumber = "";
			for (int f=0;f<patient.getIdentifier().size();f++)
			{
				if (patient.getIdentifier().get(f).getSystem().equals(FHIRCodeSystems.URI_NHS_NUMBER_ENGLAND ))
				{
					NHSNumber = patient.getIdentifier().get(f).getValue();
					//
				}
				if (patient.getIdentifier().get(f).getSystem().equals(Identifiers.URI_PATIENT_HOSPITAL_NUMBER ))
				{
					hospitalNumber = patient.getIdentifier().get(f).getValue();
					//
				}
			}
			variables.put("NHSNumber", NHSNumber);
			variables.put("hospitalNumber", hospitalNumber);
			Date dob = patient.getBirthDate();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			variables.put( "patientDOB", dateFormat.format(dob));
			variables.put("givenName", patient.getName().get(0).getGiven().get(0).getValue());
			variables.put("familyName", patient.getName().get(0).getFamily().get(0).getValue());
			if (test.getReason().get(0).getCoding().get(0).getDisplay() != null && !test.getReason().get(0).getCoding().get(0).getDisplay().isEmpty())
			{
				variables.put("testName", test.getReason().get(0).getCoding().get(0).getDisplay());
			}
			else
			{
				variables.put("testName", test.getReason().get(0).getCoding().get(0).getCode());
			}
			
			hospitalNumber = "";
			NHSNumber = "";
			String sdob="";
			String familyName = "";
			String givenName="";
			
			if (order.getContained().size()>1)
			{
				 
				patient = (Patient) order.getContained().get(1);
				hospitalNumber = "";
				NHSNumber = "";
				for (int f=0;f<patient.getIdentifier().size();f++)
				{
					if (patient.getIdentifier().get(f).getSystem().equals(FHIRCodeSystems.URI_NHS_NUMBER_ENGLAND ))
					{
						NHSNumber = patient.getIdentifier().get(f).getValue();
						//
					}
					if (patient.getIdentifier().get(f).getSystem().equals(Identifiers.URI_PATIENT_HOSPITAL_NUMBER ))
					{
						hospitalNumber = patient.getIdentifier().get(f).getValue();
						//
					}
				}
				
				dob = patient.getBirthDate();
				dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				sdob = dateFormat.format(dob);
				givenName = patient.getName().get(0).getGiven().get(0).getValue();
				familyName = patient.getName().get(0).getFamily().get(0).getValue();
			}
			String messageId = "";
			for (int f=0;f<order.getIdentifier().size();f++)
			{
				if (order.getIdentifier().get(f).getSystem().equals("http://fhir.chft.nh.uk/Message/TIEMessageId"))
				{
					messageId = order.getIdentifier().get(f).getValue();
				}
			}
			variables.put("apexpatientDOB", sdob);
			variables.put("apexgivenName", givenName);
			variables.put("apexfamilyName", familyName);
			variables.put("apexNHSNumber", NHSNumber);
			variables.put("apexhospitalNumber", hospitalNumber);
			variables.put("messageId", messageId);
			
			ProcessInstance processInstance = 
					runtimeService.startProcessInstanceByKey("RejectOrderProcess", variables);
			
		//
		}
	}

}
