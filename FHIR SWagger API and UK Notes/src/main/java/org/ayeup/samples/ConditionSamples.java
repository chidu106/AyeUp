package org.ayeup.samples;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ayeup.NHS.NHSFHIRCodeSystems;
import org.ayeup.constants.openEHR.GOVFictitiousCouncil;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.Condition;
import org.hl7.fhir.instance.model.Condition.ConditionVerificationStatus;
import org.hl7.fhir.instance.model.DateTimeType;
import org.hl7.fhir.instance.model.Encounter;
import org.hl7.fhir.instance.model.Encounter.EncounterClass;
import org.hl7.fhir.instance.model.Period;
import org.hl7.fhir.instance.model.Practitioner;
import org.hl7.fhir.instance.model.Reference;



public class ConditionSamples {
	
	public Condition DummyCondition1(String id)
	{
		Condition condition = new Condition();
		
		condition.setId("a31957ec-9f7c-4c7b-aeb9-555ff4067d95::ripple_osi.ehrscape.c4h::1");
		
		condition.addIdentifier()
			.setSystem(GOVFictitiousCouncil.URI_openEHR_PROBLEM_DIAGNOSIS)
			.setValue("64ed73f2-50e2-44e4-9ea5-a2d383be6b97");
		
		CodeableConcept code = new CodeableConcept();
		code.addCoding()
			.setSystem(NHSFHIRCodeSystems.URI_SNOMED)
			.setCode("22298006")
			.setDisplay("myocardial infarction");
		condition.setCode(code);
		
		DateTimeType onset=new DateTimeType();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
        	Date onsetDate;
        	onsetDate = fmt.parse("2015-10-19 12:32:17"); 
        	onset.setValue(onsetDate);
        	condition.setOnset(onset);
        }
        catch (ParseException e1) {
        	// TODO Auto-generated catch block
	    }
        
        condition.setVerificationStatus(ConditionVerificationStatus.CONFIRMED);
        
        condition.setNotes("A comment");
        
        Reference patRef = new Reference();
        patRef.setReference("Patient/64ed73f2-50e2-44e4-9ea5-a2d383be6b97");
        condition.setPatient(patRef);
        
        Reference drRef = new Reference();
        drRef.setReference("Practitioner/#dr");
        condition.setAsserter(drRef);
        
        Practitioner dr = new Practitioner();
        dr.setId("#dr");
        dr.getName()
        	.addFamily("Shannon")
        	.addGiven("Tony")
        	.addPrefix("Dr")
        	.setText("Dr Tony Shannon");
        condition.getContained().add(dr);
        
        Reference encounterRef = new Reference();
        encounterRef.setReference("Encounter/#enc");
        condition.setEncounter(encounterRef);
        
        Encounter encounter = new Encounter();
        encounter.setId("#enc");
        
        Period period = new Period();
        Date startDate;
		try {
			startDate = fmt.parse("2015-10-19 12:49:10");
			period.setStart(startDate);
			encounter.setPeriod(period);
				
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		encounter.setClass_(EncounterClass.OTHER);
        
        condition.getContained().add(encounter);
        
		return condition;
	}
	
	public Condition DummyConditionAnswer(String id)
	{
		
		/*
		 
		"http://idcr.rippleosi.org/api/patients/9999999000/diagnoses/79e19641-1b06-4ca7-9562-dad23b640833%3A%3Aanswer.hopd.com%3A%3A31"
		 
		{
			  "sourceId": "79e19641-1b06-4ca7-9562-dad23b640833::answer.hopd.com::31",
			  "source": "openehr",
			  "problem": "Asthma",
			  
			  
			  "terminology": "SNOMED-CT",
			  "code": "301485011"
			  
			  "dateOfOnset": 458784000000,
			  
			  "description": "Default",
		 }
		  
		 */
		Condition condition = new Condition();
		
		condition.setId("79e19641-1b06-4ca7-9562-dad23b640833::answer.hopd.com::31");
		
		// System used below would ideally refer to business i.e. Leeds City council openehr instance id 
		// have left with a answer default. The Id above is the database id
		condition.addIdentifier()
			.setSystem("http://fhir.answer.hopd.com/source/openehr")
			.setValue("79e19641-1b06-4ca7-9562-dad23b640833::answer.hopd.com::31");
		
		CodeableConcept code = new CodeableConcept();
		code.addCoding()
			//Ideally need list of System types and mappings. SNOMED-CT mapping is shown below 
			.setSystem("http://snomed.info/sct")
			.setCode("301485011")
			.setDisplay("Asthma");
		condition.setCode(code);
		
		DateTimeType onset=new DateTimeType();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
        	Date onsetDate;
        	// Date in answer.com model  458784000000
        	onsetDate = fmt.parse("2015-10-19 12:32:17"); 
        	onset.setValue(onsetDate);
        	condition.setOnset(onset);
        }
        catch (ParseException e1) {
        	// TODO Auto-generated catch block
	    }
        
        condition.setNotes("Default");
        
        // These are mandatory in the default profile.
        
        Reference patRef = new Reference();
        patRef.setReference("Patient/9999999000");
        condition.setPatient(patRef);
        
        condition.setVerificationStatus(ConditionVerificationStatus.UNKNOWN);
                
		return condition;
	}


}
