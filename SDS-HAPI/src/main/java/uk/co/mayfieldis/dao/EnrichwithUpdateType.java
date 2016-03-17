package uk.co.mayfieldis.dao;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.hl7.fhir.instance.formats.JsonParser;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.Address;
import org.hl7.fhir.instance.model.Bundle;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.Organization;
import org.hl7.fhir.instance.model.Period;
import org.hl7.fhir.instance.model.Practitioner;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.instance.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.instance.model.Practitioner.PractitionerPractitionerRoleComponent;
import org.hl7.fhir.instance.model.valuesets.PractitionerRole;

public class EnrichwithUpdateType implements AggregationStrategy  {

	@Override
	public Exchange aggregate(Exchange exchange, Exchange enrichment) 
	{
		exchange.getIn().setHeader(Exchange.HTTP_METHOD,"GET");
		if (enrichment.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).equals("200"))
		{
			ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) enrichment.getIn().getBody(byte[].class));
			Bundle bundle = null;
			
			if (enrichment.getIn().getHeader(Exchange.CONTENT_TYPE).equals("application/json"))
			{
				JsonParser composer = new JsonParser();
				try
				{
					bundle = (Bundle) composer.parse(xmlContentBytes);
				}
				catch(Exception ex)
				{
					
				}
			}
			else
			{
				XmlParser composer = new XmlParser();
				try
				{
					bundle = (Bundle) composer.parse(xmlContentBytes);
				}
				catch(Exception ex)
				{
					
				}
			}
		  
			if (bundle!=null && bundle.getEntry().size()==0)
			{
				// No resource found go ahead
				exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");	
				if (exchange.getIn().getHeader("FHIRResource").toString().contains("Organization"))
				{
					exchange.getIn().setHeader("FHIRResource","Organization");
				}
				if (exchange.getIn().getHeader("FHIRResource").toString().contains("Practitioner"))
				{
					exchange.getIn().setHeader("FHIRResource","Practitioner");
				}
			}
			
			if (bundle!=null && bundle.getEntry().size()>0)
			{
				
				// This bit isn't fixed 
				
				ByteArrayInputStream newContent = new ByteArrayInputStream ((byte[]) enrichment.getIn().getBody(byte[].class));
				ByteArrayInputStream oldContent = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
				if (!newContent.equals(oldContent))
				{
					// Record is different so update it
					exchange.getIn().setHeader(Exchange.HTTP_METHOD,"PUT");
					
					if (enrichment.getIn().getHeader(Exchange.CONTENT_TYPE).equals("application/json"))
					{
						JsonParser composer = new JsonParser();
						try
						{
							if (exchange.getIn().getHeader("FHIRResource").toString().contains("Organization"))
							{
								Organization organisation = (Organization) composer.parse(oldContent);
								
								exchange.getIn().setHeader("FHIRResource","Organization/"+organisation.getId());
							}
							if (exchange.getIn().getHeader("FHIRResource").toString().contains("Practitioner"))
							{
								Practitioner practitioner = (Practitioner) composer.parse(oldContent);
								exchange.getIn().setHeader("FHIRResource","Practitioner/"+practitioner.getId());
							}
						}
						catch(Exception ex)
						{
						}
					}
					else
					{
						XmlParser composer = new XmlParser();
						try
						{
							if (exchange.getIn().getHeader("FHIRResource").toString().contains("Organization"))
							{
								Organization organisation = (Organization) composer.parse(oldContent);  
								exchange.getIn().setHeader("FHIRResource","Organization/"+organisation.getId());
							}
							if (exchange.getIn().getHeader("FHIRResource").toString().contains("Practitioner"))
							{
								Practitioner practitioner = (Practitioner) composer.parse(oldContent);
								exchange.getIn().setHeader("FHIRResource","Practitioner/"+practitioner.getId());
							}
						}
						catch(Exception ex)
						{
						}
					}
				}
			}
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json+fhir");
		}
		return exchange;
	}
}


