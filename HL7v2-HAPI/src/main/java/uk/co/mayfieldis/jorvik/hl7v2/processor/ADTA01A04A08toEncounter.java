package uk.co.mayfieldis.jorvik.hl7v2.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.Period;

import org.hl7.fhir.instance.model.Encounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import uk.co.mayfieldis.jorvik.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.jorvik.FHIRConstants.NHSTrustFHIRCodeSystems;
import uk.co.mayfieldis.jorvik.core.ResourceSerialiser;

public class ADTA01A04A08toEncounter implements Processor {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.jorvik.hl7v2.processor.ADTA01A04A08toEncounter.class);
	
	Terser terser = null;
	
	private String terserGet(String query)
	{
		String result = "";
		try
		{
			result = terser.get(query);
			//log.info(query+" = "+result);
		}
		catch(HL7Exception hl7ex)
		{
			// Could add some extra code here
			
			log.debug("#1 "+hl7ex.getMessage());
		}
		catch(Exception ex)
		{
			// Exception thrown on no data
			log.debug("#2 "+ex.getMessage());
		}
		
		return result;
	}
	@Override
	public void process(Exchange exchange) throws Exception {
		
		Message message = exchange.getIn().getBody(Message.class);
		
		Encounter encounter = new Encounter();
		
		// Use Terser as code is more readable
		terser = new Terser(message);
		
		Integer maxRepitions = 5;
		
		try
		{
			// Identifiers PID.PatientIdentifierList()
			for (int f=0;f<maxRepitions;f++)
			{
				String code =null;
				String value =null;
				if (f==0)
				{
					code =terserGet("/.PID-3-4");
					value =terserGet("/.PID-3-1");		
				}
				else
				{
					code =terserGet("/.PID-3("+f+")-4");
					value =terserGet("/.PID-3("+f+")-1");
				}
				if (code != null && !code.isEmpty())
				{
					switch (code)
					{
						case "PAS":
							exchange.getIn().setHeader("FHIRPatient", value);
							break;
					}
				}
			}
			// Names PID.PatientName
			//log.info("Patient Name");
			if (terserGet("/.PV1-19-1") != null && !terserGet("/.PV1-19-1").isEmpty())
			{
				encounter.addIdentifier()
					.setSystem(NHSTrustFHIRCodeSystems.uriCHFTActivityId)
					.setValue(terserGet("/.PV1-19-1"));
			}
			// StartDate
			encounter.setStatus(Encounter.EncounterState.ARRIVED);
			Period period = new Period();
			if (terserGet("/.PV1-44-1") != null && !terserGet("/.PV1-44-1").isEmpty())
			{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
				
	        	try {
	        		Date date;
	        		date = fmt.parse(terserGet("/.PV1-44-1"));
	        		period.setStart(date);
	        		encounter.setStatus(Encounter.EncounterState.INPROGRESS);
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			if (terserGet("/.PV1-45-1") != null && !terserGet("/.PV1-45-1").isEmpty())
			{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
				
	        	try {
	        		Date date;
	        		date = fmt.parse(terserGet("/.PV1-45-1"));
	        		period.setEnd(date);
	        		encounter.setStatus(Encounter.EncounterState.FINISHED);
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			encounter.setPeriod(period);
			
			if (terserGet("/.PV1-10-1") != null && !terserGet("/.PV1-10-1").isEmpty())
			{
				encounter.addType()
					.addCoding()
						.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES)
						.setCode(terserGet("/.PV1-10-1"));
			}
			
			switch (terserGet("/.PV1-2"))
			{
				case "O" : 
					encounter.setClass_(Encounter.EncounterClass.OUTPATIENT);
					if (terserGet("/.PV1-3-2") != null && !terserGet("/.PV1-3-2").isEmpty())
					{
						encounter.addType()
							.addCoding()
								.setSystem(NHSTrustFHIRCodeSystems.URI_CHFT_CLINIC_CODE)
								.setCode(terserGet("/.PV1-3-2"))
								.setDisplay(terserGet("/.PV1-3-9"));
					}
					break;
				case "I" : encounter.setClass_(Encounter.EncounterClass.INPATIENT);
					break;
				case "E" : encounter.setClass_(Encounter.EncounterClass.EMERGENCY);
					break;
			}
			
			
			if (terserGet("/.PV1-3-2") != null && !terserGet("/.PV1-3-2").isEmpty())
			{
				exchange.getIn().setHeader("FHIRLocation", terserGet("/.PV1-3-2"));
			}
			
			if (terserGet("/.PV1-19-1") != null && !terserGet("/.PV1-19-1").isEmpty())
			{
				exchange.getIn().setHeader("FHIREncounter", terserGet("/.PV1-19-1"));
			}
			
			if (terserGet("/.PV1-19-1") != null && !terserGet("/.PV1-19-1").isEmpty())
			{
				exchange.getIn().setHeader("FHIRAppointment", terserGet("/.PV1-19-1"));
			}
			
			if (terserGet("/.PV1-9-1") != null && !terserGet("/.PV1-9-1").isEmpty())
			{
				exchange.getIn().setHeader("FHIRPractitioner", terserGet("/.PV1-9-1"));
			}
			if (terserGet("/.PV1-3-1") != null && !terserGet("/.PV1-3-1").isEmpty())
			{
				exchange.getIn().setHeader("FHIROrganisationCode", terserGet("/.PV1-3-1"));
			}
			
			
			switch (terserGet("/.MSH-9-2"))
			{
				case "A01":
				case "A08":
					exchange.getIn().setHeader(Exchange.HTTP_PATH,"POST");
					break;
				default:
					exchange.getIn().setHeader(Exchange.HTTP_PATH,"PUT");	
			}
		}
		catch (Exception ex)
		{
			log.error("#3 "+ exchange.getExchangeId() + " "  + ex.getMessage() 
					+" Properties: " + exchange.getProperties().toString()
					+" Headers: " + exchange.getIn().getHeaders().toString() 
					+ " Message:" + exchange.getIn().getBody().toString());
		}
		
		String Response = ResourceSerialiser.serialise(encounter, ParserType.XML);
		exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
		//exchange.getIn().setHeader(Exchange.HTTP_PATH, "/Practitioner/"+patient.getId());
		exchange.getIn().setBody(Response);
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		
	}

}
