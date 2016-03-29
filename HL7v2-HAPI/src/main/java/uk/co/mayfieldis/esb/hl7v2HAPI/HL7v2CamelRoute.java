package uk.co.mayfieldis.esb.hl7v2HAPI;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hl7.HL7DataFormat;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import uk.co.mayfieldis.hl7v2.hapi.processor.ADTA28A31toPatient;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichMFNM05withLocation;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichPatientwithOrganisation;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichPatientwithPatient;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichPatientwithPractitioner;
import uk.co.mayfieldis.hl7v2.hapi.processor.LightWithFHIR;
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
    	MFNM02PractitionerProcessor mfnm02PractitionerProcessor = new MFNM02PractitionerProcessor();
    	MFNM05LocationProcessor mfnm05LocationProcessor = new MFNM05LocationProcessor();
    	EnrichPatientwithOrganisation enrichPatientwithOrganisation = new EnrichPatientwithOrganisation();
    	EnrichPatientwithPractitioner enrichPatientwithPractitioner = new EnrichPatientwithPractitioner();
    	EnrichPatientwithPatient enrichPatientwithPatient = new EnrichPatientwithPatient();
    	
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
					.wireTap("vm:ADT")
					.end()
				.when(header("CamelHL7MessageType").isEqualTo("MFN"))
					.wireTap("vm:MFN")
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
    	from("vm:ADT?waitForTaskToComplete=Never")
    		.routeId("ADT")
    		.choice() //enrich("vm:ADT",lightWithFHIR)
    			.when(header("CamelHL7TriggerEvent").isEqualTo("A28")).to("activemq:ADT_A28A31")
    			.when(header("CamelHL7TriggerEvent").isEqualTo("A31")).to("activemq:ADT_A28A31")
    			.when(header("CamelHL7TriggerEvent").isEqualTo("A40")).to("activemq:ADT_A40")
    		.end();
    	
    	from("vm:MFN?waitForTaskToComplete=Never")
    		.routeId("MFN")
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
	    
		from("activemq:ADT_A28A31")
			.routeId("ADT_A28A31")
			.process(adta28a31toPatient)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true")
			.enrich("vm:Organisation",enrichPatientwithOrganisation)
			.enrich("vm:Practitioner",enrichPatientwithPractitioner)
			.enrich("vm:Patient",enrichPatientwithPatient)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
			.to("activemq:FileFHIR");
    	    	
    	from("activemq:ADT_A40")
    		.routeId("ADT_A40")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true");
            	
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
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier=urn:fhir.nhs.uk/id/ODSOrganisationCode|${header.FHIROrganisationCode}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:Practitioner")
    		.routeId("Lookup FHIR Practitioner")
	    	.setBody(simple(""))
	    	.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Practitioner",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier=urn:fhir.nhs.uk/id/GeneralPractitionerPPDCode|${header.FHIRGP}",String.class))
	    	.to("vm:HAPIFHIR");
    	
    	from("vm:Patient")
			.routeId("Lookup FHIR Patient")
			.setBody(simple(""))
			.setHeader(Exchange.HTTP_METHOD, simple("GET", String.class))
	    	.setHeader(Exchange.HTTP_PATH, simple("/Patient",String.class))
	    	.setHeader(Exchange.HTTP_QUERY,simple("identifier=http://fhir.chft.nhs.uk/DistrictNumber|${header.FHIRPatient}",String.class))
	    	.to("vm:HAPIFHIR");

    }
}
