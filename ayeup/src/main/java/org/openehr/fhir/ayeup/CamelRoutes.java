package org.openehr.fhir.ayeup;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
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
	            	.get("/Patient/{subjectId}")
	            		.description("Get FHIR Patient").outType(Patient.class)
	            		.to("direct:Patient")
	                .get("/Patient")
	            		.description("Get FHIR Patient-parameters only").outType(Patient.class)
	            		.to("direct:Patient")
	            	.get("/AllergyIntolerance")
	                	.consumes("application/json")
	                	.to("direct:AllergyIntolerance")
	                .get("/Logon")
	                	.to("direct:Logon");
	            
	            from("direct:Logon")
        			.routeId("Logon")
        			.process(new Processor() {
	            	    public void process(Exchange exchange) throws Exception {
	            	    	//.removeHeaders("*")
	            	    	exchange.getIn().setBody("");
				            exchange.getIn().setHeader(Exchange.HTTP_PATH, constant("/rest/v1/session"));
				            exchange.getIn().setHeader(Exchange.HTTP_METHOD, constant("POST"));
            				exchange.getIn().setHeader(Exchange.HTTP_QUERY, constant("username=c4h_train&password=c4h_train99"));     
	            		}
	            	})
        			.to("http4:rest.ehrscape.com?sslContextParametersRef=openehrSSLContextParameters&bridgeEndpoint=true");
	            
	            from("direct:GetEHRPatientId")
	            	.routeId("GetEHRPatientId")
	            	.process(new Processor() {
	            	    public void process(Exchange exchange) throws Exception {
				            
	            	    	exchange.getIn().setBody("");
				            exchange.getIn().setHeader(Exchange.HTTP_PATH, constant("/rest/v1/ehr"));
				            exchange.getIn().setHeader(Exchange.HTTP_METHOD, constant("GET"));
            				exchange.getIn().setHeader(Exchange.HTTP_QUERY, constant("subjectId="+exchange.getIn().getHeader("subjectId",String.class)+"&subjectNamespace=ehrscape"));     
	            		}
	            	})
	            	.to("http4:rest.ehrscape.com?sslContextParametersRef=openehrSSLContextParameters&bridgeEndpoint=true")
	            	.process(new Processor() {
	            	    public void process(Exchange exchange) throws Exception {
	            	        String json = exchange.getIn().getBody(String.class);
	            	        String PatientEHRID = JsonPath.read(json, "$.ehrId");
	            	        
	            			log.info("EHR_ID="+PatientEHRID);
	            			exchange.getIn().setBody("");
	            			exchange.getIn().setHeader(Exchange.HTTP_PATH,"/rest/v1/demographics/ehr/"+PatientEHRID+"/party");
	            			exchange.getIn().setHeader(Exchange.HTTP_QUERY, "");
	            			exchange.getIn().setHeader("PatientEHRID", PatientEHRID);
	            	   }   
	            	})
	            	.recipientList(simple("http4:rest.ehrscape.com?sslContextParametersRef=openehrSSLContextParameters&bridgeEndpoint=true"))
	            	.processRef("convPatient");
	            	
	            from("direct:PatientDummy")
            		.routeId("PatientDummy")
            		
            		.processRef("convPatientDummy");
            
	            from("direct:Patient")
        			.routeId("PatientEHR")
        			.process(new Processor() {
	            	    public void process(Exchange exchange) throws Exception {
				            
				            String id = exchange.getIn().getHeader("_id", String.class);
				            String subjectId = exchange.getIn().getHeader("subjectId", String.class);           
				     
				            if (subjectId != null && !subjectId.isEmpty())
				            {
				            	log.info("_Id="+id);
				            }
				            if (id != null && !id.isEmpty())
				            {
				            	log.info("_Id="+id);
				            	subjectId = id;
				            }
				            exchange.getIn().setHeader("subjectId",subjectId);
				      }
	            	})
        			.choice()
    					.when(header("Ehr-Session").isNull())
    						.to("direct:Logon")
    				.end()
    				.to("direct:GetEHRPatientId");
            
	            from("direct:Condition")
	            	.routeId("Condition")
	            	.choice()
    					.when(header("Ehr-Session").isNull())
    						.to("direct:Logon")
    				.end()
	                .transform().constant("Condition - Hello World")
	                .marshal().json(JsonLibrary.Jackson);
	            
	            from("direct:AllergyIntolerance")
	            	.routeId("AllergyIntolerance")
	                .transform().constant("AllergyIntollerence - Bye World");
	        }
	    
	
}
