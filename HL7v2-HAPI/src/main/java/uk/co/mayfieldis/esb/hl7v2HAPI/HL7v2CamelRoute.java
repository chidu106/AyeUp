package uk.co.mayfieldis.esb.hl7v2HAPI;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hl7.HL7DataFormat;
import org.apache.camel.model.RedeliveryPolicyDefinition;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import uk.co.mayfieldis.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.hl7v2.hapi.processor.ADTA01A04A08toEncounter;
import uk.co.mayfieldis.hl7v2.hapi.processor.ADTA05A38toAppointment;
import uk.co.mayfieldis.hl7v2.hapi.processor.ADTA28A31toPatient;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichAppointmentwithAppointment;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichAppointmentwithLocation;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichAppointmentwithPatient;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichAppointmentwithPractitioner;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichEncounterwithAppointment;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichEncounterwithEncounter;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichEncounterwithLocation;
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

import org.apache.activemq.RedeliveryPolicy;
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
    	ADTA05A38toAppointment adta05a38toAppointment = new ADTA05A38toAppointment();
    	MFNM02PractitionerProcessor mfnm02PractitionerProcessor = new MFNM02PractitionerProcessor();
    	MFNM05LocationProcessor mfnm05LocationProcessor = new MFNM05LocationProcessor();
    	EnrichPatientwithOrganisation enrichPatientwithOrganisation = new EnrichPatientwithOrganisation();
    	EnrichPatientwithPractitioner enrichPatientwithPractitioner = new EnrichPatientwithPractitioner();
    	EnrichPatientwithPatient enrichPatientwithPatient = new EnrichPatientwithPatient();
    	EnrichEncounterwithPatient enrichEncounterwithPatient = new EnrichEncounterwithPatient();
    	EnrichEncounterwithPractitioner enrichEncounterwithPractitioner = new EnrichEncounterwithPractitioner();
    	EnrichEncounterwithOrganisation enrichEncounterwithOrganisation = new EnrichEncounterwithOrganisation();
    	EnrichEncounterwithLocation enrichEncounterwithLocation = new EnrichEncounterwithLocation();
    	EnrichEncounterwithAppointment enrichEncounterwithAppointment = new EnrichEncounterwithAppointment();
    	EnrichEncounterwithEncounter enrichEncounterwithEncounter = new EnrichEncounterwithEncounter(); 
    	
    	EnrichAppointmentwithPatient enrichAppointmentwithPatient = new EnrichAppointmentwithPatient();
    	EnrichAppointmentwithPractitioner enrichAppointmentwithPractitioner = new EnrichAppointmentwithPractitioner();
    	
    	EnrichAppointmentwithLocation enrichAppointmentwithLocation = new EnrichAppointmentwithLocation();
    	EnrichAppointmentwithAppointment enrichAppointmentwithAppointment = new EnrichAppointmentwithAppointment();
    	
    	onException(org.apache.
    			camel.CamelAuthorizationException.class)
    		.routeId("AuthourisationError")
    		.handled(true)
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.processor.OperationOutcome?level=WARN")
    		.to("bean:outcome?method=getOutcome(401,'Unauthorized'+${exception.policyId}, '')");
    	
    	RedeliveryPolicyDefinition retryPolicyDef = new RedeliveryPolicyDefinition();
    	
    	retryPolicyDef
    		.setRedeliveryDelay("100");
    	retryPolicyDef.setMaximumRedeliveries("3");
    	
    	onException(org.apache.camel.http.common.HttpOperationFailedException.class)
    		.routeId("HttpError")
    		.handled(false)
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true&level=WARN");
    	
    	errorHandler(defaultErrorHandler().maximumRedeliveries(3));
    	
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
				.when(header("CamelHL7TriggerEvent").isEqualTo("A02")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A03")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A04")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A05")).to("activemq:ADT_A05A38")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A08")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A11")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A12")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A13")).to("activemq:ADT_A01A04A08")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A38")).to("activemq:ADT_A05A38") 
				.when(header("CamelHL7TriggerEvent").isEqualTo("A28")).to("activemq:ADT_A28A31")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A31")).to("activemq:ADT_A28A31")
			/*	.when(header("CamelHL7TriggerEvent").isEqualTo("A40")).to("activemq:ADT_A40") */
			.end();
    	
    	
    	// Demographics 
		from("activemq:ADT_A28A31")
			.routeId("ADT_A28A31 Demographics")
			.process(adta28a31toPatient)
			.enrich("vm:lookupOrganisation",enrichPatientwithOrganisation)
			.enrich("vm:lookupGP",enrichPatientwithPractitioner)
			.enrich("vm:lookupPatient",enrichPatientwithPatient)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to("activemq:HAPIFHIR");
			//.to("activemq:FileFHIR");
		
    	// Encounters and Episodes
		from("activemq:ADT_A01A04A08")
			.routeId("ADT_A01A04A08 Encounters")
			.process(adta01a04a08toEncounter)
			.enrich("vm:lookupPatient",enrichEncounterwithPatient)
			.enrich("vm:lookupConsultant",enrichEncounterwithPractitioner)
			.enrich("vm:lookupOrganisation",enrichEncounterwithOrganisation)
			.choice()
				.when(header("FHIRLocation").isNotNull())
					.enrich("vm:lookupLocation",enrichEncounterwithLocation)
			.end()
			.choice()
			.when(header("FHIRAppointment").isNotNull())
				.enrich("vm:lookupAppointment",enrichEncounterwithAppointment)
			.end()
			.enrich("vm:lookupEncounter",enrichEncounterwithEncounter)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to("activemq:HAPIFHIR");
		
		// Encounters and Episodes
		from("activemq:ADT_A05A38")
			.routeId("ADT_A05A38 Appointments")
			.process(adta05a38toAppointment)
			.enrich("vm:lookupPatient",enrichAppointmentwithPatient)
			.enrich("vm:lookupConsultant",enrichAppointmentwithPractitioner)
			.choice()
				.when(header("FHIRLocation").isNotNull())
					.enrich("vm:lookupLocation",enrichAppointmentwithLocation)
			.end()
			.enrich("vm:lookupAppointment",enrichAppointmentwithAppointment)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to("activemq:HAPIFHIR");
 
    	from("vm:lookupLocation")
    		.routeId("Loookup FHIR Location")
    		.log("Lookup Location ${header.FHIRLocation}")
    		.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Location",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+NHSTrustFHIRCodeSystems.uriCHFTLocation+"|${header.FHIRLocation}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:lookupOrganisation")
	    	.routeId("Lookup FHIR Organisation")
	    	.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Organization",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE+"|${header.FHIROrganisationCode}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:lookupGP")
    		.routeId("Lookup FHIR Practitioner (GP)")
	    	.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Practitioner",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_NHS_GMP_CODE+"|${header.FHIRGP}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:lookupConsultant")
			.routeId("Lookup FHIR Practitioner (Consultant)")
	    	.setBody(simple(""))
	    	//.log("GET /Practitioner?identifier="+FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS+"|${header.FHIRPractitioner}")
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Practitioner",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS+"|${header.FHIRPractitioner}",String.class))
	    	.to("vm:HAPIFHIR");
	    	
    	from("vm:lookupPatient")
			.routeId("Lookup FHIR Patient")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Patient",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+NHSTrustFHIRCodeSystems.URI_PATIENT_DISTRICT_NUMBER+"|${header.FHIRPatient}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:lookupEncounter")
			.routeId("Lookup FHIR Encounter")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Encounter",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+NHSTrustFHIRCodeSystems.uriCHFTActivityId+"|${header.FHIREncounter}",String.class))
	    	.to("vm:HAPIFHIR");
	    
    	from("vm:lookupAppointment")
			.routeId("Lookup FHIR Appointment")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Appointment",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier="+NHSTrustFHIRCodeSystems.uriCHFTAppointmentId+"|${header.FHIRAppointment}",String.class))
	    	.to("vm:HAPIFHIR");
	    
    	from("activemq:FileFHIR")
			.routeId("FileStore")
			.to("file:C:/NHSSDS/fhir?fileName=${date:now:yyyyMMdd hhmm.ss} ${header.CamelHL7MessageControl}.xml");
	
    	from("vm:HAPIFHIR")
			.routeId("HAPI FHIR")
			.to("http:chft-ddmirth.xthis.nhs.uk:8181/hapi-fhir-jpaserver/baseDstu2?connectionsPerRoute=60");
    	
    	from("activemq:HAPIFHIR")
			.routeId("HAPI FHIR MQ")
			.to("http:chft-ddmirth.xthis.nhs.uk:8181/hapi-fhir-jpaserver/baseDstu2?connectionsPerRoute=60");
    }
}
