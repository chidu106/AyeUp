package uk.co.mayfieldis.esb.hl7v2HAPI;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hl7.HL7DataFormat;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import uk.co.mayfieldis.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.hl7v2.hapi.processor.ADTA01A04A08toEncounter;
import uk.co.mayfieldis.hl7v2.hapi.processor.ADTA28A31toPatient;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichEncounterwithOrganisation;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichEncounterwithPatient;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichEncounterwithPractitioner;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichMFNM05withLocation;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichPatientwithOrganisation;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichPatientwithPatient;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichPatientwithPractitioner;
import uk.co.mayfieldis.hl7v2.hapi.processor.MFNM02PractitionerProcessor;
import uk.co.mayfieldis.hl7v2.hapi.processor.MFNM05LocationProcessor;

import static org.apache.camel.component.hl7.HL7.ack;

import org.apache.camel.Exchange;


public class HL7v2CamelRoute extends RouteBuilder {

    @Override
    public void configure() 
    {
    	   	
    	HapiContext hapiContext = new DefaultHapiContext();
    	
    	hapiContext.getParserConfiguration().setValidating(false);
    	HL7DataFormat hl7 = new HL7DataFormat();
    	
    	hl7.setHapiContext(hapiContext);
    	
    	//LightWithFHIR lightWithFHIR = new LightWithFHIR(); 
    	EnrichMFNM05withLocation enrichMFNM05withLocation = new EnrichMFNM05withLocation();
    	ADTA28A31toPatient adta28a31toPatient = new ADTA28A31toPatient();  
    	ADTA01A04A08toEncounter adta01a04a08toEncounter = new ADTA01A04A08toEncounter(); 
    	MFNM02PractitionerProcessor mfnm02PractitionerProcessor = new MFNM02PractitionerProcessor();
    	MFNM05LocationProcessor mfnm05LocationProcessor = new MFNM05LocationProcessor();
    	EnrichPatientwithOrganisation enrichPatientwithOrganisation = new EnrichPatientwithOrganisation();
    	EnrichPatientwithPractitioner enrichPatientwithPractitioner = new EnrichPatientwithPractitioner();
    	EnrichPatientwithPatient enrichPatientwithPatient = new EnrichPatientwithPatient();
    	EnrichEncounterwithPatient enrichEncounterwithPatient = new EnrichEncounterwithPatient();
    	EnrichEncounterwithPractitioner enrichEncounterwithPractitioner = new EnrichEncounterwithPractitioner();
    	EnrichEncounterwithOrganisation enrichEncounterwithOrganisation = new EnrichEncounterwithOrganisation();
    	
    	onException(org.apache.
    			camel.CamelAuthorizationException.class)
    		.routeId("Error")
    		.handled(true)
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.processor.OperationOutcome?level=WARN")
    		.to("bean:outcome?method=getOutcome(401,'Unauthorized'+${exception.policyId}, '')");
    	
    	from("hl7MinaListener")
    		.routeId("HL7v2")
    		.unmarshal(hl7)
    		//.process("HL7v2Service")
    		.choice()
				.when(header("CamelHL7MessageType").isEqualTo("ADT"))
					.wireTap("activemq:ADT")
					.end()
				.when(header("CamelHL7MessageType").isEqualTo("MFN"))
					.wireTap("activemq:MFN")
					.end()
			.end()
    		.transform(ack());
    	/*
    	 * 
    	 * //.when(header("CamelHL7MessageType").isEqualTo("ORM")).to("vm:ORM")
				//.when(header("CamelHL7MessageType").isEqualTo("ORU")).to("vm:ORU")
    	 * 
    	from("vm:ORM")
    		.routeId("ORM")
    		.log("ORM");

    	from("vm:ORU")
			.routeId("ORU")
			.log("ORU");
		*/
    	
    	from("activemq:MFN")
    		.routeId("MFN")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true")
    		.choice()
    			.when(header("CamelHL7TriggerEvent").isEqualTo("M02")).to("activemq:MFN_M02")
    			.when(header("CamelHL7TriggerEvent").isEqualTo("M05")).to("activemq:MFN_M05")
    		.end();
    	
    	from("activemq:MFN_M02")
			.routeId("MFN_M02")
			.process(mfnm02PractitionerProcessor)
			.to("activemq:FileFHIR");
	
		from("activemq:MFN_M05")
			.routeId("MFN_M05")
			.process(mfnm05LocationProcessor)
			.enrich("vm:Location",enrichMFNM05withLocation)
			.to("activemq:FileFHIR");
	    	
    	from("activemq:ADT")
    		.routeId("ADT")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true")
    		.choice()
				.when(header("CamelHL7TriggerEvent").isEqualTo("A01")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A04")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A08")).to("activemq:ADT_A01A04A08")
			/* .when(header("CamelHL7TriggerEvent").isEqualTo("A05")).to("activemq:ADT_A05") */
				.when(header("CamelHL7TriggerEvent").isEqualTo("A28")).to("activemq:ADT_A28A31")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A31")).to("activemq:ADT_A28A31")
			/*	.when(header("CamelHL7TriggerEvent").isEqualTo("A40")).to("activemq:ADT_A40") */
			.end();
    	
    	
    	
    	
    	
    	/*
    	
    	from("activemq:ADT_A05")
			.routeId("ADT_A05");
			
		from("activemq:ADT_A40")
    		.routeId("ADT_A40");
	*/
    	// Demographics 
		from("activemq:ADT_A28A31")
			.routeId("ADT_A28A31 Demographics")
			.process(adta28a31toPatient)
			.enrich("vm:Organisation",enrichPatientwithOrganisation)
			.enrich("vm:Practitioner",enrichPatientwithPractitioner)
			.enrich("vm:Patient",enrichPatientwithPatient)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to("vm:HAPIFHIR");
			//.to("activemq:FileFHIR");
		
    	// Encounters and Episodes
		from("activemq:ADT_A01A04A08")
			.routeId("ADT_A01A04A08 Encounters")
			.process(adta01a04a08toEncounter)
			.enrich("vm:Patient",enrichEncounterwithPatient)
			.enrich("vm:Consultant",enrichEncounterwithPractitioner)
			.enrich("vm:Organisation",enrichEncounterwithOrganisation)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to("activemq:FileFHIR");
		
         
    	from("vm:Location")
    		.routeId("Location Lookup")
    		.transform(constant("Move along. Nothing to see here"));
    	
    	from("activemq:FileFHIR")
    		.routeId("FileStore")
    		.to("file:C:/NHSSDS/fhir?fileName=${date:now:yyyyMMdd hhmm.ss} ${header.CamelHL7MessageControl}.xml");
    	
    	from("vm:HAPIFHIR")
    		.routeId("HAPI FHIR")
    		.to("http:chft-ddmirth.xthis.nhs.uk:8181/hapi-fhir-jpaserver/baseDstu2?connectionsPerRoute=60");
		
    	from("vm:Organisation")
	    	.routeId("Lookup FHIR Organisation")
	    	.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Organization",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE+"|${header.FHIROrganisationCode}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:Practitioner")
    		.routeId("Lookup FHIR Practitioner")
	    	.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Practitioner",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_NHS_GMP_CODE+"|${header.FHIRGP}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:Consultant")
			.routeId("Lookup FHIR Consultant")
	    	.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Practitioner",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS+"|${header.FHIRGP}",String.class))
	    	.to("vm:HAPIFHIR");
	    	
    	from("vm:Patient")
			.routeId("Lookup FHIR Patient")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Patient",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+NHSTrustFHIRCodeSystems.URI_PATIENT_DISTRICT_NUMBER+"|${header.FHIRPatient}",String.class))
	    	.to("vm:HAPIFHIR");

    }
}
