package org.openehr.fhir.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openehr.fhir.NHS.NHSEnglandConstants;
import org.openehr.fhir.NHS.NHSScotlandConstants;
import org.openehr.fhir.ayeup.CamelRoutes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.instance.model.Identifier.IdentifierUse;
import org.hl7.fhir.instance.model.Narrative;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Reference;



public class PatientProcessorDummy implements Processor {
	
	private static final Logger log = LoggerFactory.getLogger(CamelRoutes.class);
	
	public void process(Exchange exchange) throws Exception {
		
		Patient patient = new Patient();
        patient.addIdentifier();
        patient.getIdentifier().get(0).setSystem(NHSEnglandConstants.URI_NHS_NUMBER_ENGLAND);
        patient.getIdentifier().get(0).setValue("9123456780");
        patient.getIdentifier().get(0).setUse(IdentifierUse.OFFICIAL);
        patient.addIdentifier();
        patient.getIdentifier().get(1).setSystem(NHSScotlandConstants.URI_NHS_NUMBER_SCOTLAND);
        patient.getIdentifier().get(1).setValue("9876543210");
        patient.addIdentifier();
        patient.getIdentifier().get(2).setSystem(NHSScotlandConstants.URI_CHI_NUMBER_SCOTLAND);
        patient.getIdentifier().get(2).setValue("3312316780");
        patient.getIdentifier().get(2).setUse(IdentifierUse.OFFICIAL);
        patient.addIdentifier();
        patient.getIdentifier().get(3).setSystem(NHSScotlandConstants.URI_CHI_NUMBER_SCOTLAND);
        patient.getIdentifier().get(3).setValue("3312316781");
        patient.getIdentifier().get(3).setUse(IdentifierUse.SECONDARY);
        
        patient.addName().addFamily("Spidimus");
        patient.getName().get(0).addGiven("Horatio");
        patient.getName().get(0).addPrefix("Mr");
          
        patient.setGender(AdministrativeGender.MALE);
        
        patient.addAddress();
        patient.getAddress().get(0).addLine("6 Gertrude Reed Avenue");
        patient.getAddress().get(0).addLine("Barnbow");
        patient.getAddress().get(0).setCity("Leeds");
        patient.getAddress().get(0).setPostalCode("LS15 0RF");
        
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date dob = fmt.parse("1916-12-5");
        patient.setBirthDate(dob);
        
        patient.setActive(true);
        
        Reference surgery = new Reference();
        surgery.setDisplay("Churchview Surgery");
        surgery.setReference("Organization/urn:oid:2.16.840.1.113883.2.1.4.3|B86016");
        patient.setManagingOrganization(surgery);
        
        Narrative text = new Narrative();
        text.getUserData("Horatio Spidimus @ Churchview Surgery");
        patient.setText(text);
        
		try 
		{
			String format = exchange.getIn().getHeader("_format", String.class);
			if (format==null)
			{
			  format="application/xml+fhir";	
			}
			
			log.debug("format = "+format);
			
			String id = exchange.getIn().getHeader("id", String.class);
			log.debug("id = "+id);
			
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
			exchange.getIn().setHeader(Exchange.HTTP_QUERY, "_id="+exchange.getIn().getHeader(Exchange.FILE_NAME, String.class)); //+exchange.getIn().
			
			// the + is removed in processing
			if (format.contains("json"))	
			{
				exchange.getIn().setBody(ResourceSerialiser.serialise(patient, ParserType.JSON));
			}
			else
			{
				exchange.getIn().setBody(ResourceSerialiser.serialise(patient, ParserType.XML));
			}
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
