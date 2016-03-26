package uk.co.mayfieldis.esb.hl7v2HAPI;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hl7.HL7DataFormat;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import uk.co.mayfieldis.hl7v2.hapi.processor.ADTA28A31toPatient;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichMFNM05withLocation;
import uk.co.mayfieldis.hl7v2.hapi.processor.LightWithFHIR;
import uk.co.mayfieldis.hl7v2.hapi.processor.MFNM02PractitionerProcessor;
import uk.co.mayfieldis.hl7v2.hapi.processor.MFNM05LocationProcessor;

import static org.apache.camel.component.hl7.HL7.ack;


public class HL7v2CamelRoute extends RouteBuilder {

    @Override
    public void configure() 
    {
    	   	
    	HapiContext hapiContext = new DefaultHapiContext();
    	
    	hapiContext.getParserConfiguration().setValidating(false);
    	HL7DataFormat hl7 = new HL7DataFormat();
    	
    	hl7.setHapiContext(hapiContext);
    	
    	LightWithFHIR lightWithFHIR = new LightWithFHIR(); 
    	EnrichMFNM05withLocation enrichMFNM05withLocation = new EnrichMFNM05withLocation();
    	ADTA28A31toPatient adta28a31toPatient = new ADTA28A31toPatient();  
    	MFNM02PractitionerProcessor mfnm02PractitionerProcessor = new MFNM02PractitionerProcessor();
    	MFNM05LocationProcessor mfnm05LocationProcessor = new MFNM05LocationProcessor();
    	
    	onException(org.apache.camel.CamelAuthorizationException.class)
    		.handled(true)
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.processor.OperationOutcome?level=WARN")
    		.to("bean:outcome?method=getOutcome(401,'Unauthorized'+${exception.policyId}, '')");
    	
    	from("hl7MinaListener")
    		.routeId("HL7v2")
    		.unmarshal(hl7)
    		//.process("HL7v2Service")
    		.choice()
				.when(header("CamelHL7MessageType").isEqualTo("ADT")).enrich("vm:ADT",lightWithFHIR)
				//.when(header("CamelHL7MessageType").isEqualTo("ORM")).to("vm:ORM")
				//.when(header("CamelHL7MessageType").isEqualTo("ORU")).to("vm:ORU")
				.when(header("CamelHL7MessageType").isEqualTo("MFN")).enrich("vm:MFN",lightWithFHIR)
			.end()
    		.transform(ack());
    	
    	from("vm:ORM")
    		.routeId("ORM")
    		.log("ORM");

    	from("vm:ORU")
			.routeId("ORU")
			.log("ORU");

    	from("vm:MFN")
			.routeId("MFN")
			.choice()
				.when(header("CamelHL7TriggerEvent").isEqualTo("M02")).to("vm:MFN_M02")
				.when(header("CamelHL7TriggerEvent").isEqualTo("M05")).to("vm:MFN_M05")
			.end();
    	
    	from("vm:MFN_M02")
			.routeId("MFN_M02")
			.process(mfnm02PractitionerProcessor)
			.to("vm:FileFHIR");
	
		from("vm:MFN_M05")
			.routeId("MFN_M05")
			.process(mfnm05LocationProcessor)
			.enrich("vm:Location",enrichMFNM05withLocation)
			.to("vm:FileFHIR");
	    	
    	from("vm:ADT")
    		.routeId("ADT")
    		.choice()
				.when(header("CamelHL7TriggerEvent").isEqualTo("A01")).to("activemq:ADT_A01")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A04")).to("activemq:ADT_A04")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A05")).to("activemq:ADT_A05")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A28")).to("activemq:ADT_A28A31")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A31")).to("activemq:ADT_A28A31")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A40")).to("activemq:ADT_A40")
			.end();
    	
    	from("activemq:ADT_A01")
			.routeId("ADT_A01")
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true");
    	
    	from("activemq:ADT_A04")
			.routeId("ADT_A04")
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true");
    
    	from("activemq:ADT_A05")
			.routeId("ADT_A05")
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true");

    	
    	from("activemq:ADT_A28A31")
			.routeId("ADT_A28A31")
			.process(adta28a31toPatient)
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true")
			.to("vm:FileFHIR");
    	    	
    	from("activemq:ADT_A40")
    		.routeId("ADT_A40")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true");
         
    	from("vm:Location")
    		.routeId("Location Lookup")
    		.transform(constant("Move along. Nothing to see here"));
    	
    	from("vm:FileFHIR")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.HL7v2CamelRoute?showAll=true&multiline=true")
    		.to("file:C:/NHSSDS/fhir?fileName=${date:now:yyyyMMdd hhmm.ss} ${header.CamelHL7MessageControl}.xml");
    	
    	from("vm:HAPIFHIR")
    		.routeId("HAPI FHIR")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
    		//&amp;bridgeEndpoint=true
    		.to("http:chft-ddmirth.xthis.nhs.uk:8181/hapi-fhir-jpaserver/baseDstu2?connectionsPerRoute=60");
		
    		
		
    }
}
