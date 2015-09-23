package org.openehr.fhir.ayeup;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class CamelRoutes extends RouteBuilder {
	
	@Override
	        public void configure() throws Exception 
	        {
			 
				restConfiguration().component("jetty")
					.bindingMode(RestBindingMode.json)
					.dataFormatProperty("prettyPrint", "true")
					.port(8080);
	        	
	            rest("/OpenEHR")
	            	.get("/Condition").to("direct:Condition")
	            	.get("/Patient").to("direct:Patient")
	                .get("/AllergyIntolerance").consumes("application/json").to("direct:AllergyIntolerance")
	                .post("/bye").to("mock:update");
	            
	            from("direct:Patient")
            		.routeId("Patient")
            		.processRef("toPatient") ;
	            from("direct:Condition")
	            	.routeId("Condition")
	                .transform().constant("Condition - Hello World");
	            from("direct:AllergyIntolerance")
	            	.routeId("AllergyIntolerance")
	                .transform().constant("AllergyIntollerence - Bye World");
	        }
	    
	
}
