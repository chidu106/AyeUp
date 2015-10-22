package org.ayeup.samples;

import org.hl7.fhir.instance.model.Binary;


import io.swagger.annotations.ApiModel;


@ApiModel(description = "Represents an Binary resource")
public class BinarySamples {

	 public Binary DummyBinary1(String id)
	 {
		 Binary binary = new Binary();
		 
		 binary.setContentType("text/html");
		 
		 binary.setContentAsBase64("<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head></head><body>In a REST interface Binary is the raw document, no XML or JSON version of the Binary resource</body></html>");
		 
		 return binary;
	 }
}

