package org.ayeup.camel;

import java.util.Arrays;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;

public class CamelRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		// 2015 Oct 12 KGM Current causing exceptions so using the XML version only..
		
		restConfiguration()
			.component("servlet")
			.contextPath("api-fhir/rest2")
			.bindingMode(RestBindingMode.json)
			.dataFormatProperty("prettyPrint", "true")
			.port(8080);
		
		rest("/Patient")
			.consumes("application/json")
			.produces("application/json")
			.delete("Patient REST service")
			.get("/{id}")
				//.outType(org.hl7.fhir.instance.model.Patient.class)
				.param().name("id").type(RestParamType.path).description("The id of the patient to get (not NHS number)").dataType("integer").endParam()
				//.param().name("format").type(RestParamType.query).description("The id of the patient to get (not NHS number)").dataType("list").allowableValues( Arrays.asList("sup1", "sup2", "sup3")).endParam()
				.responseMessage().message("Patient not found").endResponseMessage()
				.responseMessage().code(404).message("Patient not found").endResponseMessage()
				.responseMessage().code(400).message("Bad Request").endResponseMessage()
				.responseMessage().code(403).message("Not Authorized").endResponseMessage()
				.responseMessage().code(405).message("Not Allowed").endResponseMessage()
				.responseMessage().code(422).message("Unprocessable Entity").responseModel(org.ayeup.samples.OperationOutcome.class).endResponseMessage()
				.route()
					.to("bean:patientService?method=getPatient(${header.id})")
					.filter(simple("${body} == null"))
						.setHeader(Exchange.HTTP_RESPONSE_CODE,constant(404));
				
					
		
	}

}
