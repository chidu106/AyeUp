package org.ayeup.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class BinaryProcessor implements Processor  {
	
		
		public void process(Exchange exchange) throws Exception {
			
						
	        
			try 
			{
				exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"text/html");
				exchange.getIn().setBody("<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head></head><body>In a REST interface Binary is the raw document, no XML or JSON version of the Binary resource</body></html>");
				
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