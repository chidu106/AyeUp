package org.openehr.fhir.ayeup;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.hl7.fhir.instance.model.Patient;

import com.jayway.jsonpath.JsonPath;

public class CamelRoutes extends RouteBuilder {
	
	@Override
	        public void configure() throws Exception 
	        {
				String PatientEHRID = "";
				
				restConfiguration().component("jetty")
					.bindingMode(RestBindingMode.off)
					.dataFormatProperty("prettyPrint", "true")
					.port(8080);
	        	
	            rest("/OpenEHR")
	            	.get("/Condition").to("direct:Condition")
	            	.get("/PatientDummy")
	            		.description("Get FHIR Patient").outType(Patient.class)
	            		// camel 2.16 .param().name("id").description("The id of the user to get").dataType("int").endParam()
	            		.to("direct:PatientDummy")
	            	.get("/Patient")
	            		.description("Get FHIR Patient").outType(Patient.class)
	            		.to("direct:Patient")
	                .get("/AllergyIntolerance")
	                	.consumes("application/json")
	                	.to("direct:AllergyIntolerance")
	                .get("/Logon")
	                	.to("direct:Logon");
	            
	            from("direct:Logon")
        			.routeId("Logon")
        			.removeHeaders("*")
        			.setHeader(Exchange.HTTP_METHOD, constant("POST"))
        			.setHeader(Exchange.HTTP_URI, constant("/rest/v1/session"))
                	.setHeader(Exchange.HTTP_QUERY, constant("username=c4h_train&password=c4h_train99"))
        			.to("http4:rest.ehrscape.com/rest/v1/session?sslContextParametersRef=openehrSSLContextParameters&bridgeEndpoint=true");
	            
	            from("direct:GetEHRPatientId")
	            	.routeId("GetEHRPatientId")
	            	.setHeader(Exchange.HTTP_METHOD, constant("GET"))
	            	.setHeader(Exchange.HTTP_URI, constant("/rest/v1/ehr"))
	            	.setHeader(Exchange.HTTP_QUERY, constant("subjectId=63436&subjectNamespace=ehrscape"))
	            	.to("http4:rest.ehrscape.com/rest/v1/ehr?sslContextParametersRef=openehrSSLContextParameters&bridgeEndpoint=true")
	            	.process(new Processor() {
	            	    public void process(Exchange exchange) throws Exception {
	            	        String json = exchange.getIn().getBody(String.class);
	            	        String PatientEHRID = JsonPath.read(json, "$.ehrId");
	            	        
	            			log.info("EHR_ID="+PatientEHRID);
	            			exchange.getIn().setBody("");
	            			exchange.getIn().setHeader(Exchange.HTTP_URI,"/rest/v1/demographics/ehr/"+PatientEHRID+"/party");
	            			exchange.getIn().setHeader(Exchange.HTTP_QUERY, "");
	            			exchange.getIn().setHeader("PatientEHRID", PatientEHRID);
	            	   }   
	            	})
	            	.recipientList(simple("http4:rest.ehrscape.com/rest/v1/demographics/ehr/${in.header.PatientEHRID}/party?sslContextParametersRef=openehrSSLContextParameters&bridgeEndpoint=true"))
	            	.processRef("convPatient");
	            	
	            from("direct:PatientDummy")
            		.routeId("PatientDummy")
            		
            		.processRef("convPatientDummy");
            
	            from("direct:Patient")
        			.routeId("PatientEHR")
        			.choice()
    					.when(header("Ehr-Session").isNull())
    						.to("direct:Logon")
    				.end()
    				.to("direct:GetEHRPatientId");
            
	            from("direct:Condition")
	            	.routeId("Condition")
	                .transform().constant("Condition - Hello World")
	                .marshal().json(JsonLibrary.Jackson);
	            
	            from("direct:AllergyIntolerance")
	            	.routeId("AllergyIntolerance")
	                .transform().constant("AllergyIntollerence - Bye World");
	        }
	    
	
}
