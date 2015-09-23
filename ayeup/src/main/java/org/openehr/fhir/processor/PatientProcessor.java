package org.openehr.fhir.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openehr.fhir.ayeup.CamelRoutes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.hl7.fhir.instance.model.Patient;

public class PatientProcessor implements Processor {
	
	private static final Logger log = LoggerFactory.getLogger(CamelRoutes.class);
	
	public void process(Exchange exchange) throws Exception {
		
		//final FhirContext ctx = FhirContext.forDstu2();
			
		org.hl7.fhir.instance.model.Patient patient = new Patient();
        patient.addIdentifier();
        patient.getIdentifier().get(0).setSystem("urn:hapitest:mrns");
        patient.getIdentifier().get(0).setValue("00002");
        patient.addName().addFamily("Test");
        patient.getName().get(0).addGiven("PatientOne");
        patient.setGender(null);
        
		try 
		{
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
			exchange.getIn().setHeader(Exchange.HTTP_QUERY, "_id="+exchange.getIn().getHeader(Exchange.FILE_NAME, String.class)); //+exchange.getIn().
			
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			
			
			exchange.getIn().setBody(ow.writeValueAsString(patient));
		
		}
		catch (Exception e)
		{
			log.error("An Error has been thrown");
		}
		finally 
		{
			log.info("In final sectionReturning from function");
		}
	}



}
