package org.ayeup.samples;

import java.text.SimpleDateFormat;
import java.util.Date;






import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.ayeup.NHS.NHSEnglandConstants;
import org.ayeup.NHS.NHSAcuteTrustConstants;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.DocumentReference;
import org.hl7.fhir.instance.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.instance.model.Identifier.IdentifierUse;
import org.hl7.fhir.instance.model.Narrative;
import org.hl7.fhir.instance.model.Patient;
import org.hl7.fhir.instance.model.Reference;




public class DocumentReferenceProcessorDummy implements Processor {
	
	//private static final Logger log = LoggerFactory.getLogger(CamelRoutes.class);
	
	public void process(Exchange exchange) throws Exception {
		
		DocumentReference document = new DocumentReference();
        document.addIdentifier();
        document.getIdentifier().get(0).setSystem(NHSAcuteTrustConstants.URI_NHS_ACUTE_EDMS_ID);
        document.getIdentifier().get(0).setValue("12345");
        
        String id = exchange.getIn().getHeader("id", String.class);
		
        document.setId(id);
        
		try 
		{
			String format = exchange.getIn().getHeader("_format", String.class);
			if (format==null)
			{
			  format="application/json";	
			}
			
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
			exchange.getIn().setHeader(Exchange.HTTP_QUERY, "_id="+exchange.getIn().getHeader(Exchange.FILE_NAME, String.class)); //+exchange.getIn().
			
			// the + is removed in processing
			if (format.contains("json"))	
			{
				exchange.getIn().setBody(ResourceSerialiser.serialise(document, ParserType.JSON));
			}
			else
			{
				exchange.getIn().setBody(ResourceSerialiser.serialise(document, ParserType.XML));
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
