package org.jorvik.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;


import com.mongodb.DBObject;

public class Condition implements Processor  {
	
	private MongoTemplate mongoTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(org.jorvik.processor.Condition.class);
	
	@Autowired
	 public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		
		 
	 }
	
	public void process(Exchange exchange) throws Exception {
		
		MongoOperations mongo = (MongoOperations)mongoTemplate;
		 log.info("In Condition Processor");
		 DBObject content = mongo.getCollection("Condition").findOne();
		
		// Remove the extra mongo bit - ideally needs to go into the id bit of FHIR  
			content.removeField("_id");
			
			// now convert to pojo if we need to convert to xml
			
			exchange.getIn().setBody(content.toString());
	}
}
