package uk.co.mayfieldis.hl7v2.hapi.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.hl7.HL7DataFormat;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import uk.co.mayfieldis.hl7v2.hapi.processor.EnrichMFNM05withLocation;

import static org.apache.camel.component.hl7.HL7.ack;


public class CamelRoute extends RouteBuilder {

    @Override
    public void configure() 
    {
    	   	
    	HapiContext hapiContext = new DefaultHapiContext();
    	
    	hapiContext.getParserConfiguration().setValidating(false);
    	HL7DataFormat hl7 = new HL7DataFormat();
    	
    	hl7.setHapiContext(hapiContext);
    	
    	EnrichMFNM05withLocation enrichMFNM05withLocation = new EnrichMFNM05withLocation();
    	
    	onException(org.apache.camel.CamelAuthorizationException.class)
    		.handled(true)
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route.processor.OperationOutcome?level=WARN")
    		.to("bean:outcome?method=getOutcome(401,'Unauthorized'+${exception.policyId}, '')");
    	
    	from("hl7MinaListener")
    		.routeId("HL7v2")
    		.unmarshal(hl7)
    		//.process("HL7v2Service")
    		.choice()
				.when(header("CamelHL7MessageType").isEqualTo("ADT")).to("vm:ADT")
				//.when(header("CamelHL7MessageType").isEqualTo("ORM")).to("vm:ORM")
				//.when(header("CamelHL7MessageType").isEqualTo("ORU")).to("vm:ORU")
				.when(header("CamelHL7MessageType").isEqualTo("MFN")).to("vm:MFN")
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
			.process("MFNM02PractitionerProcessor")
			.to("vm:HAPIFHIR");
	
		from("vm:MFN_M05")
			.routeId("MFN_M05")
			.process("MFNM05LocationProcessor")
			.enrich("vm:Location",enrichMFNM05withLocation)
			.to("vm:HAPIFHIR");
	    	
    	from("vm:ADT")
    		.routeId("ADT")
    		.choice()
				.when(header("CamelHL7TriggerEvent").isEqualTo("A01")).to("vm:ADT_A01")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A04")).to("vm:ADT_A04")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A05")).to("vm:ADT_A05")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A28")).to("vm:ADT_A28")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A31")).to("vm:ADT_A31")
				.when(header("CamelHL7TriggerEvent").isEqualTo("A40")).to("vm:ADT_A40")
			.end();
    	
    	from("vm:ADT_A01")
			.routeId("ADT_A01")
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true");
    	
    	from("vm:ADT_A04")
			.routeId("ADT_A04")
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true");
    
    	from("vm:ADT_A05")
			.routeId("ADT_A05")
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true");

    	from("vm:ADT_A28")
			.routeId("ADT_A28")
			.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true");
    	
    	from("vm:ADT_A31")
    		.routeId("ADT_A31")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true");
    	    	
    	from("vm:ADT_A40")
    		.routeId("ADT_A40")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true");
         
    	from("vm:HAPIFHIR")
    		.routeId("HAPI FHIR")
    		.to("log:uk.co.mayfieldis.hl7v2.hapi.route?showAll=true&multiline=true")
    		//&amp;bridgeEndpoint=true
    		.to("http4:chft-ddmirth.xthis.nhs.uk:8181/hapi-fhir-jpaserver/baseDstu2?connectionsPerRoute=60");
		
    		
		
    }
}
