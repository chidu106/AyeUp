package org.ayeup.samples;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.ayeup.NHS.NHSFicitiousTrustConstants;
import org.hl7.fhir.instance.model.Attachment;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.DocumentReference;
import org.hl7.fhir.instance.model.Encounter;
import org.hl7.fhir.instance.model.Identifier;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Period;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.Encounter.EncounterClass;
import org.hl7.fhir.instance.model.Encounter.EncounterState;

public class DocumentReferenceSamples {

	    
	    public DocumentReference DummyDocRef1(String id)
		{
			DocumentReference document = new DocumentReference();
			
			
			document.setId(id);
			
			Identifier ident = new Identifier();
			ident.setSystem(NHSFicitiousTrustConstants.URI_NHS_ACUTE_DOCUMENT_REGISTRY);
			ident.setValue("12345");
			document.setMasterIdentifier(ident);
					
	        document.addIdentifier()
	        	.setSystem(NHSFicitiousTrustConstants.URI_NHS_ACUTE_EDMS_ID)
	        	.setValue("67890");
	        
	        
	        
	        CodeableConcept typeCode = new CodeableConcept();
	        typeCode.addCoding()
	        	.setSystem("http://snomed.info/sct")
	        	.setCode("823681000000100")
	        	.setDisplay("Outpatient letter");
	        document.setType(typeCode);
	        
	        CodeableConcept classCode = new CodeableConcept();
	        classCode.addCoding()
	        	.setSystem("urn:oid:2.16.840.1.113883.2.1.6.8")
	        	.setCode("110")
	        	.setDisplay("ORTHOPAEDICS");
	        document.setClass_(classCode);
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	        Date createDate;
			try {
				createDate = sdf.parse("21/12/2012");
				document.setCreated(createDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        
	        document.addContent();
	        Attachment docAttachment = new Attachment();
	        docAttachment.setContentType("application/msword");
	        docAttachment.setUrl(NHSFicitiousTrustConstants.URI_BINARY_ACUTE_LOCATION +"612898_A00387543-9051675");
	        document.getContent().get(0).setAttachment(docAttachment);
	        
	        
	        Encounter encounter = new Encounter();
	        encounter.setId("#enc");
	        encounter.addIdentifier()
	        	.setSystem(NHSFicitiousTrustConstants.URI_NHS_ACUTE_EPISODE_ID)
	        	.setValue("9066683-1");
	        encounter.setStatus(EncounterState.FINISHED);
	        encounter.setClass_(EncounterClass.OUTPATIENT);
	        Period period = new Period();
	        Date startDate;
			try {
				startDate = sdf.parse("19/12/2012");
				period.setStart(startDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        encounter.setPeriod(period);
	        document.getContained().add(encounter);
	        
	        Reference encRef = new Reference();
	        encRef.setReference("#enc");
	        document.getContext().setEncounter(encRef);
	        document.getContext().setEncounterTarget(encounter);
	        
	        PatientSamples patService = new PatientSamples();
			
			Patient patient = patService.PatientDummy1("#pat",false);
			Reference patRef = new Reference();
	        patRef.setReference("#pat");
	        document.setSubject(patRef);
	        document.getContained().add(patient);
	        
	        return document;
		}
	    
	    public DocumentReference DummyDocRef2(String id)
		{
			DocumentReference document = new DocumentReference();
			
			
			document.setId(id);
			
			Identifier ident = new Identifier();
			ident.setSystem(NHSFicitiousTrustConstants.URI_NHS_ACUTE_DOCUMENT_REGISTRY);
			ident.setValue("34512");
			document.setMasterIdentifier(ident);
					
	        document.addIdentifier()
	        	.setSystem(NHSFicitiousTrustConstants.URI_NHS_COMMUNITY_EDMS_ID)
	        	.setValue("678");
	        
	        
	        
	        CodeableConcept typeCode = new CodeableConcept();
	        typeCode.addCoding()
	        	.setSystem("http://snomed.info/sct")
	        	.setCode("820011000000105")
	        	.setDisplay("Common assessment framework assessment record (record artifact)");
	        document.setType(typeCode);
	        
	        CodeableConcept classCode = new CodeableConcept();
	        classCode.addCoding()
	        	.setSystem("urn:oid:2.16.840.1.113883.2.1.6.8")
	        	.setCode("900")
	        	.setDisplay("COMMUNITY MEDICINE");
	        document.setClass_(classCode);
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	        Date createDate;
			try {
				createDate = sdf.parse("11/09/2005");
				document.setCreated(createDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        
	        document.addContent();
	        Attachment docAttachment = new Attachment();
	        docAttachment.setContentType("application/pdf");
	        docAttachment.setUrl(NHSFicitiousTrustConstants.URI_BINARY_COMMUNITY_LOCATION + "Care Plan Assessment.pdf");
	        document.getContent().get(0).setAttachment(docAttachment);
	        
	        PatientSamples patService = new PatientSamples();
			
			Patient patient = patService.PatientDummy1("#pat",false);
			Reference patRef = new Reference();
	        patRef.setReference("#pat");
	        document.setSubject(patRef);
	        document.getContained().add(patient);
	        
	        return document;
		}


}
