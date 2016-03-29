package org.ayeup.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.ayeup.samples.ConditionSamples;

import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.Condition;


public class ConditionProcessor implements Processor {
		
		//private static final Logger log = LoggerFactory.getLogger(CamelRoutes.class);
		
		
		
		public void process(Exchange exchange) throws Exception {
			
			String id = exchange.getIn().getHeader("id", String.class);
			if (id==null)
			{
				id="";
			}
			if (id.isEmpty() ) 
			{
				id = "612898_A00387543-9051675";
			}
	        
			ConditionSamples conditionService = new ConditionSamples();
			
			Condition condition = null;
			if (id=="2")
			{
				condition = conditionService.DummyCondition1(id);
			}
			else
			{
				condition = conditionService.DummyConditionAnswer(id);
			}
			
				
			
	        
			try 
			{
				String format = exchange.getIn().getHeader("_format", String.class);
				if (format==null)
				{
				  format="application/json";	
				}
				
				
				
				exchange.getIn().setHeader(Exchange.HTTP_QUERY, "_id="+exchange.getIn().getHeader(Exchange.FILE_NAME, String.class)); //+exchange.getIn().
				
				// the + is removed in processing
				if (format.contains("json"))	
				{
					exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json+fhir");
					exchange.getIn().setBody(ResourceSerialiser.serialise(condition, ParserType.JSON));
				}
				else
				{
					exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
					exchange.getIn().setBody(ResourceSerialiser.serialise(condition, ParserType.XML));
				}
			}
			catch (Exception e)
			{
				//log.error("An Error has been thrown");
			}
			finally 
			{
				//log.info("In final sectionReturning from function");
			}
		}
}
