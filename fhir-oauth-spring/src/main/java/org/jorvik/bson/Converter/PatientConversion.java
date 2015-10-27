package org.jorvik.bson.Converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.ayeup.constants.NHS.NHSEnglandConstants;

import com.mongodb.BasicDBObject;


public class PatientConversion implements Processor  {
	
	public void process(Exchange exchange) throws Exception {
		
		
		
		BasicDBObject mongoObj  = new BasicDBObject();
		
		
		String id = exchange.getIn().getHeader("_id", String.class);
		if (id==null)
		{
			id="";
		}
		if (!id.isEmpty() ) 
		{
			mongoObj.put("id",id);	
		}
		
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
			String[] parts = ident.split("|");
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
			mongoObj.put("$or", obj);
		}
		
		exchange.getIn().setBody(mongoObj,com.mongodb.DBObject.class);
		
		
	}
}
