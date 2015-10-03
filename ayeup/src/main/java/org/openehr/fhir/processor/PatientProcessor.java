package org.openehr.fhir.processor;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
//import org.openehr.fhir.NHS.NHSEnglandConstants;
//import org.openehr.fhir.NHS.NHSScotlandConstants;
import org.openehr.fhir.ayeup.CamelRoutes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.JsonPath;

import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.instance.model.Identifier.IdentifierUse;
//import org.hl7.fhir.instance.model.Narrative;
import org.hl7.fhir.instance.model.Patient;
//import org.hl7.fhir.instance.model.Reference;



public class PatientProcessor implements Processor {
	
	private static final Logger log = LoggerFactory.getLogger(CamelRoutes.class);
	
	public void process(Exchange exchange) throws Exception {
		
		String json = exchange.getIn().getBody(String.class);
		
		log.info(json);
		/*
		 {"meta":{"href":"https://rest.ehrscape.com/rest/v1/demographics/party/63436"},"action":"RETRIEVE","part
			 y":{"id":"63436","version":0,"firstNames":"Steve","lastNames":"Walford","gender":"MALE","dateOfBirth":"1965-07-12T00:00:
			 00.000Z","address":{"id":"63436","version":0,"address":"60 Florida Gardens, Cardiff, LS23 4RT"},"partyAdditionalInfo":[{
			 "id":"63438","version":0,"key":"title","value":"Mr"},{"id":"63437","version":0,"key":"uk.nhs.nhsnumber","value":"7430555
			 "}]}}
		*/
		Patient patient = new Patient();
        patient.addIdentifier();
        patient.getIdentifier().get(0).setSystem("http://openehr.org/FHIR/Patient/EHRID");
        patient.getIdentifier().get(0).setValue(exchange.getIn().getHeader("PatientEHRID",String.class));
        patient.getIdentifier().get(0).setUse(IdentifierUse.USUAL);
    
        patient.addName().addFamily((String) JsonPath.read(json, "$.party.lastNames"));
        patient.getName().get(0).addGiven((String) JsonPath.read(json, "$.party.firstNames"));
        //patient.getName().get(0).addPrefix((String) JsonPath.read(json, "$.party.lastNames"));
          
        if ((String) JsonPath.read(json, "$.party.lastNames")=="MALE")
        {
        	patient.setGender(AdministrativeGender.MALE);
        }
        else
        {
        	patient.setGender(AdministrativeGender.FEMALE);
        	
        }
        
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date dob = fmt.parse((String) JsonPath.read(json, "$.party.dateOfBirth"));
        patient.setBirthDate(dob);
        
        String Address = (String) JsonPath.read(json, "$.party.address.address");
        String[] AddrArray = Address.split(","); 
        
        patient.addAddress();
        for(int i=0; i<AddrArray.length; i++)
        {
        	switch (i)
        	{
	        	case 2: 
	        		patient.getAddress().get(0).setCity(AddrArray[i].toString());
	        		break;
	        	case 3:
	        		patient.getAddress().get(0).setPostalCode(AddrArray[i].toString());
	        		break;
	        	default:
	        		patient.getAddress().get(0).addLine(AddrArray[i].toString());
        	}
        }
        
        patient.setActive(true);
        /*
        Reference surgery = new Reference();
        surgery.setDisplay("Churchview Surgery");
        surgery.setReference("Organization/urn:oid:2.16.840.1.113883.2.1.4.3|B86016");
        patient.setManagingOrganization(surgery);
        
        Narrative text = new Narrative();
        text.getUserData("Horatio Spidimus @ Churchview Surgery");
        patient.setText(text);
        */
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
