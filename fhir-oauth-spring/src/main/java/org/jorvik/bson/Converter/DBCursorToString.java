package org.jorvik.bson.Converter;

import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class DBCursorToString implements Processor  {
	
	public void process(Exchange exchange) throws Exception {
		
		String Response = "{ \n"
						 + " \"resourceType\": \"Bundle\", \n";
						
		Response = Response + " \"entry\": [ \n";
		ArrayList<DBObject> cursor = (ArrayList<DBObject>) exchange.getIn().getBody(); 
		DBObject content = null;
		for (int i=0; i<cursor.size(); i++) 
		{
			content = cursor.get(i);
			content.removeField("_id");
			if (i>0)
			{
				Response = Response + " , \n";
			}
			Response = Response + content.toString();
			
		}
		Response = Response + " ] \n";
		
		Response = Response + "}";
		// 
	
		exchange.getIn().setBody(Response);
	
	}
}
