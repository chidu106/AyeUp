package org.ayeup.rest;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ayeup.NHS.NHSAcuteTrustConstants;
import org.ayeup.NHS.NHSEnglandConstants;
import org.ayeup.NHS.NHSScotlandConstants;

import org.hl7.fhir.instance.model.Narrative;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.instance.model.Identifier.IdentifierUse;

public class PatientService {
	
		public Patient PatientDummy1(String id)
		{
			Patient patient = new Patient();
			
			patient.setId(id);
			
	        patient.addIdentifier();
	        patient.getIdentifier().get(0).setSystem(NHSEnglandConstants.URI_NHS_NUMBER_ENGLAND);
	        patient.getIdentifier().get(0).setValue("9123456"+id);
	        patient.getIdentifier().get(0).setUse(IdentifierUse.OFFICIAL);
	        patient.addIdentifier();
	        patient.getIdentifier().get(1).setSystem(NHSScotlandConstants.URI_CHI_NUMBER_SCOTLAND);
	        patient.getIdentifier().get(1).setValue("3312316"+id);
	        patient.getIdentifier().get(1).setUse(IdentifierUse.OFFICIAL);
	        patient.addIdentifier();
	        patient.getIdentifier().get(2).setSystem(NHSAcuteTrustConstants.URI_NHS_ACUTE_HOSPITAL_NUMBER);
	        patient.getIdentifier().get(2).setValue(id);
	        patient.getIdentifier().get(2).setUse(IdentifierUse.USUAL);
	        
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
	        
			try {
				Date dob;
				dob = fmt.parse("1916-12-5");
				patient.setBirthDate(dob);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
			
			}
	        
	        
	        patient.setActive(true);
	        
	        Reference surgery = new Reference();
	        surgery.setDisplay("Churchview Surgery");
	        surgery.setReference("Organization/urn:oid:2.16.840.1.113883.2.1.4.3|B86016");
	        patient.setManagingOrganization(surgery);
	        
	        Narrative text = new Narrative();
	        text.getUserData("Horatio Spidimus @ Churchview Surgery");
	        patient.setText(text);
	        
	        return patient;
		}
}
