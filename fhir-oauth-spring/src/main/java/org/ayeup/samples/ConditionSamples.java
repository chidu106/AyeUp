package org.ayeup.samples;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ayeup.constants.NHS.NHSUKConstants;
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
			.setSystem(NHSUKConstants.URI_SNOMED)
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

}
