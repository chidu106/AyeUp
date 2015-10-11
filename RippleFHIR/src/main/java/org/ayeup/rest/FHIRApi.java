package org.ayeup.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import org.hl7.fhir.instance.model.Patient;



public class FHIRApi extends RouteBuilder {

	 @Override
	    public void configure() throws Exception {
	        // configure we want to use servlet as the component for the rest DSL
	        // and we enable json binding mode
		 restConfiguration().component("servlet").bindingMode(RestBindingMode.json)
         .dataFormatProperty("prettyPrint", "true")
         .contextPath("RippleFHIR/FHIR").port(8080);
	 
	        // this user REST service is json only
	        rest("/Patient").description("Patient rest service")
	            //.consumes("application/json").produces("application/json")
	            .get("/{id}").description("Find user by id").outType(Patient.class)
	                //.param().name("id").type(path).description("The id of the user to get").dataType("int").endParam()
	                .to("direct:Patient")
	            .get("/findAll").description("Find all users").outTypeList(Patient.class)
	                .to("direct:Patient");
	        
	        rest("/echo")
	        	.get("/ping")
	        	.description("Test Service")
	        	.route().transform().constant("Pong");
	        
	        from("direct:Patient").transform().constant("{ \"resourceType\": \"Patient\", \"id\": \"example\" }" );
	    }

}
