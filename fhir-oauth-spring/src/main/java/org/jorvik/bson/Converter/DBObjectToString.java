package org.jorvik.bson.Converter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.mongodb.DBObject;

public class DBObjectToString implements Processor  {
	
	public void process(Exchange exchange) throws Exception {
		DBObject content = (DBObject) exchange.getIn().getBody(); 
		
		// Remove the extra mongo bit - ideally needs to go into the id bit of FHIR  
		content.removeField("_id");
		
		// now convert to pojo if we need to convert to xml
		
		exchange.getIn().setBody(content.toString());
	
	}
}
