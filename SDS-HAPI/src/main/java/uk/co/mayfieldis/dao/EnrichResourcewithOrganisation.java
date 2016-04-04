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
import org.hl7.fhir.instance.model.Extension;
import org.hl7.fhir.instance.model.Practitioner.PractitionerPractitionerRoleComponent;
import org.hl7.fhir.instance.model.valuesets.PractitionerRole;
import org.slf4j.Logger;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;

import uk.co.mayfieldis.FHIRConstants.FHIRCodeSystems;

public class EnrichResourcewithOrganisation implements AggregationStrategy  {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.dao.EnrichResourcewithOrganisation.class);
	
	@Override
	public Exchange aggregate(Exchange exchange, Exchange enrichment) 
	{
		
		Organization parentOrganisation = null;
		//
		if (enrichment.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString().equals("200"))
		{
			ByteArrayInputStream xmlContentBytes = new ByteArrayInputStream ((byte[]) enrichment.getIn().getBody(byte[].class));
			
			
			if (enrichment.getIn().getHeader(Exchange.CONTENT_TYPE).toString().contains("json"))
			{
				JsonParser composer = new JsonParser();
				try
				{
					Bundle bundle = (Bundle) composer.parse(xmlContentBytes);
					if (bundle.getEntry().size()>0)
					{
						parentOrganisation = (Organization) bundle.getEntry().get(0).getResource();
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
					Bundle bundle = (Bundle) composer.parse(xmlContentBytes);
					if (bundle.getEntry().size()>0)
					{
						parentOrganisation = (Organization) bundle.getEntry().get(0).getResource();
					}
				}
				catch(Exception ex)
				{
					
				}
			}
			  
		}
		
		/*
		 * 
		 *  PASTE
		 * 
		 */
		String Id = exchange.getIn().getHeader("NHSEntityId").toString();
		
		if ( (Id.startsWith("G") || Id.startsWith("C")) && Id.length()>6)
		{
			
			if (parentOrganisation !=null)
			{
				
				ByteArrayInputStream xmlNewContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
				
				XmlParser composer = new XmlParser();
				try
				{
					// Add in the parent organisation code
					Practitioner gp = (Practitioner) composer.parse(xmlNewContentBytes);
					
					PractitionerPractitionerRoleComponent practitionerRole = gp.getPractitionerRole().get(0);
				

					Reference organisation = new Reference();
					organisation.setReference("Organization/"+parentOrganisation.getId());
					practitionerRole.setManagingOrganization(organisation);
					Extension parentOrg= new Extension();
					parentOrg.setUrl(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE+"/ParentCode");
					CodeableConcept parentCode = new CodeableConcept();
					parentCode.addCoding()
						.setCode(exchange.getIn().getHeader("FHIROrganisationCode").toString())
						.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
					
					parentOrg.setValue(parentCode);
					practitionerRole.addExtension(parentOrg);
				}
				catch(Exception ex)
				{
					log.error("#12 XML Parse failed 2"+ exchange.getExchangeId() + " "  + ex.getMessage() 
						+" Properties: " + exchange.getProperties().toString()
						+" Headers: " + exchange.getIn().getHeaders().toString() 
						+ " Message:" + exchange.getIn().getBody().toString());
				}
				
			}
		}
		else
		{
			if (parentOrganisation !=null)
			{
				ByteArrayInputStream xmlNewContentBytes = new ByteArrayInputStream ((byte[]) exchange.getIn().getBody(byte[].class));
				
				XmlParser composer = new XmlParser();
				try
				{
					// Add in the parent organisation code
					
					Organization organisation = (Organization) composer.parse(xmlNewContentBytes);
					
					//PractitionerPractitionerRoleComponent practitionerRole = gp.getPractitionerRole().get(0);
					
					Reference ccg = new Reference();
					ccg.setReference("/Organization/"+parentOrganisation.getId());
					organisation.setPartOf(ccg);
				
					Extension parentOrg= new Extension();
					parentOrg.setUrl(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE+"/ParentCode");
					CodeableConcept parentCode = new CodeableConcept();
					parentCode.addCoding()
						.setCode(exchange.getIn().getHeader("FHIRtOrganisationCode").toString())
						.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
				
					parentOrg.setValue(parentCode);
					organisation.addExtension(parentOrg);
				}
				catch(Exception ex)
				{
					log.error("#12 XML Parse failed 2"+ exchange.getExchangeId() + " "  + ex.getMessage() 
						+" Properties: " + exchange.getProperties().toString()
						+" Headers: " + exchange.getIn().getHeaders().toString() 
						+ " Message:" + exchange.getIn().getBody().toString());
				}
			}
		}
		/*
		 *  ENDP
		 */
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		exchange.getIn().setHeader(Exchange.HTTP_METHOD,"GET");
		return exchange;
	}
}


