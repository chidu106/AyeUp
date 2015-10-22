package org.ayeup.samples;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ayeup.NHS.NHSFicitiousTrustConstants;
import org.ayeup.NHS.NHSEnglandConstants;
import org.ayeup.NHS.NHSScotlandConstants;
import org.hl7.fhir.instance.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.instance.model.Narrative;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.instance.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.instance.model.Identifier.IdentifierUse;
import org.hl7.fhir.instance.model.Patient.PatientCommunicationComponent;

public class PatientSamples {
	
		public Patient PatientDummy1(String id, Boolean largeResponse)
		{
			Patient patient = new Patient();
			
			patient.setId(id);
			
			// This is different to GPSoc requirement. 
	        patient.addIdentifier()
	        	.setSystem(NHSEnglandConstants.URI_NHS_NUMBER_ENGLAND)
	        	.setValue("9123456"+id)
	        	.setUse(IdentifierUse.OFFICIAL);
	        
	        patient.addIdentifier()
	        	.setSystem(NHSScotlandConstants.URI_CHI_NUMBER_SCOTLAND)
	        	.setValue("3312316"+id)
	        	.setUse(IdentifierUse.OFFICIAL);
	        
	        patient.addIdentifier()
	        	.setSystem(NHSFicitiousTrustConstants.URI_NHS_ACUTE_HOSPITAL_NUMBER)
	        	.setValue(id)
	        	.setUse(IdentifierUse.USUAL);
	        
	        patient.addName()
	        	.addFamily("Spidimus")
	        	.addGiven("Horatio")
	        	.addPrefix("Mr");
	        
	        patient.setGender(AdministrativeGender.MALE);
	        
	        patient.addAddress()
        		.addLine("6 Gertrude Reed Avenue")
        		.addLine("Barnbow")
        		.setCity("Leeds")
        		.setPostalCode("LS15 0RF");
        
	        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
	        try {
	        	Date dob;
	        	dob = fmt.parse("1916-12-5");
	        	patient.setBirthDate(dob);
	        } catch (ParseException e1) {
	        	// TODO Auto-generated catch block
		    }
        
	        patient.addTelecom()
	        	.setSystem(ContactPointSystem.PHONE)
	        	.setValue("01634631628")
	        	.setUse(ContactPointUse.MOBILE);
        
	        patient.addCareProvider()
	        	.setDisplay("Churchview Surgery")
	        	.setReference("Organization/urn:oid:2.16.840.1.113883.2.1.4.3|B86016");
        
	        Narrative text = new Narrative();
	        text.getUserData("Horatio Spidimus @ Churchview Surgery");
	        patient.setText(text);

	        
	        if (largeResponse)
	        {
		        patient.getMaritalStatus().addCoding()
		        	.setCode("S")
		        	.setSystem("urn:fhir.nhs.uk:vs/MaritalStatus")
		        	.setDisplay("Single");
		        
		        PatientCommunicationComponent lang = new PatientCommunicationComponent();
		        lang.getLanguage()
		        	.addCoding()
		        		.setCode("004")
		        		.setSystem("urn:fhir.nhs.uk:vs/Language")
		        		.setDisplay("Arabic");
		        lang.setPreferred(false);
		        
		        patient.addCommunication(lang);
		        
		        lang = new PatientCommunicationComponent();
		        lang.getLanguage().addCoding()
	        		.setCode("012")
	        		.setSystem("urn:fhir.nhs.uk:vs/Language")
	        		.setDisplay("English");
		        lang.setPreferred(true);
		        patient.addCommunication(lang);
	        }
	        
	        	        
	        return patient;
		}
}
