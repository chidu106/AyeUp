package org.ayeup.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.ayeup.samples.DocumentReferenceSamples;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.DocumentReference;







public class DocumentReferenceBundleProcessor implements Processor {
	
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
        
		DocumentReferenceSamples docService = new DocumentReferenceSamples();
		
		Bundle bundle = new Bundle();
		
		DocumentReference document = docService.DummyDocRef1("123");
		
		bundle.addEntry().setResource(document);
		
		document = docService.DummyDocRef2("456");
		bundle.addEntry().setResource(document);
        
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
				exchange.getIn().setBody(ResourceSerialiser.serialise(bundle, ParserType.JSON));
			}
			else
			{
				exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
				exchange.getIn().setBody(ResourceSerialiser.serialise(bundle, ParserType.XML));
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
