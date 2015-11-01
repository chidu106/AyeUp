package org.jorvik.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.ayeup.constants.NHS.NHSEnglandConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Patient implements Processor {
private MongoTemplate mongoTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(org.jorvik.processor.Patient.class);
	
	@Autowired
	 public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		 
	 }
	
	public void process(Exchange exchange) throws Exception {
		
		MongoOperations mongo = (MongoOperations)mongoTemplate;
		log.info("In Patient Processor");
		String id = exchange.getIn().getHeader("_id", String.class);
		
		if (id==null)
		{
			id="";
		}
		if (!id.isEmpty())
		{
			
			BasicDBObject mongoObj  = new BasicDBObject();
			
			DBObject content = mongo.getCollection("Patient").findOne(mongoObj);
			
			// Remove the extra mongo bit - ideally needs to go into the id bit of FHIR  
			content.removeField("_id");
			
			// now convert to pojo if we need to convert to xml
			
			exchange.getIn().setBody(content.toString());
		}
		else
		{
			BasicDBObject mongoObj  = new BasicDBObject();
			
			String phone = exchange.getIn().getHeader("phone", String.class);
			if (phone==null)
			{
				phone="";
			}
			if (!phone.isEmpty() ) 
			{
				mongoObj.put("telecom.value",phone);	
			}
			
			String name = exchange.getIn().getHeader("name", String.class);
			if (name==null)
			{
				name="";
			}
			if (!name.isEmpty() ) 
			{
				List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
				obj.add(new BasicDBObject("name.family", name));
				obj.add(new BasicDBObject("name.given", name));
				mongoObj.put("$or", obj);
					
			}
			
			String ident =exchange.getIn().getHeader("identifier", String.class);
			if (ident==null)
			{
				ident="";
			}
			if (!ident.isEmpty() ) 
			{
				ident = ident.replace("|", ",");
				//String[] parts = ident.split("\0x7C");
				// You'd think the above line of code would work.
				String[] parts = ident.split(",");
				String System = null;
				String Value = null;
				if (parts[0].isEmpty())
				{
					// Going on the assumption that in 80% of cases this will be for NHS Number
					System = NHSEnglandConstants.URI_NHS_NUMBER_ENGLAND;
				}
				else
				{
					System = parts[0];
				}
				Value = parts[1];
				
				List<BasicDBObject> obj = new ArrayList<BasicDBObject>();
				obj.add(new BasicDBObject("identifier.system", System));
				obj.add(new BasicDBObject("identifier.value", Value));
				mongoObj.put("$and", obj);
			}
			DBCursor cursor = mongo.getCollection("Patient").find(mongoObj);
			
			String Response = "{ \n"
					 + " \"resourceType\": \"Bundle\", \n";
					
			Response = Response + " \"entry\": [ \n";
			
			DBObject content = null;
			Boolean first = true;
			while (cursor.hasNext()) 
			{
				content = cursor.next();
				content.removeField("_id");
				if (!first)
				{
					Response = Response + " , \n";
				}
				else
				{
					first = false;
				}
				Response = Response + content.toString();
			}
			Response = Response + " ] \n";
			
			Response = Response + "}";
			
			exchange.getIn().setBody(Response);
			
		
		}
		
	}

}
