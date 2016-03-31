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
import org.hl7.fhir.instance.model.Coding;
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
import org.slf4j.LoggerFactory;

import uk.co.mayfieldis.FHIRConstants.FHIRCodeSystems;

public class ConsultantEnrichwithOrganisation implements AggregationStrategy  {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.dao.ConsultantEnrichwithOrganisation.class);
	
	@Override
	public Exchange aggregate(Exchange exchange, Exchange enrichment) 
	{
		
		NHSConsultantEntities entity = exchange.getIn().getBody(NHSConsultantEntities.class);
		
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
		
		String Id = entity.PractitionerCode; 
		
		if ( Id.startsWith("C") && Id.length()>6)
		{
			Practitioner gp = new Practitioner();
			//gp.setId(entity.OrganisationCode);
			
			gp.addIdentifier()
				.setValue(entity.PractitionerCode)
				.setSystem(FHIRCodeSystems.URI_OID_NHS_PERSONNEL_IDENTIFIERS);
			
			
			HumanName name = new HumanName();
			
			if (!entity.Surname.isEmpty()) 
			{
				name.addFamily(entity.Surname);
			}
			if (!entity.Initials.isEmpty()) 
			{
				name.addGiven(entity.Initials);
			}
			gp.setName(name);
			
			PractitionerPractitionerRoleComponent practitionerRole = new PractitionerPractitionerRoleComponent(); 
									
			CodeableConcept pracspecialty= new CodeableConcept();
			pracspecialty.addCoding()
				.setCode(entity.SpecialityFunctionCode)
				.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES);
			practitionerRole
				.addSpecialty(pracspecialty);
			
			CodeableConcept role= new CodeableConcept();
			role.addCoding()
					.setCode(PractitionerRole.DOCTOR.toString())
					.setSystem("http://hl7.org/fhir/practitioner-role");
			
			if (parentOrganisation !=null)
			{
				
				Reference organisation = new Reference();
				organisation.setReference("Organization/"+parentOrganisation.getId());
				practitionerRole.setManagingOrganization(organisation);
				Extension parentOrg= new Extension();
				parentOrg.setUrl(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE+"/ParentCode");
				CodeableConcept parentCode = new CodeableConcept();
				parentCode.addCoding()
					.setCode(exchange.getIn().getHeader("ParentOrganisationCode").toString())
					.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
				
				parentOrg.setValue(parentCode);
				practitionerRole.addExtension(parentOrg);
			}			
			
			practitionerRole.setRole(role);
			
			gp.addPractitionerRole(practitionerRole);
			// XML as Ensemble doesn't like JSON
			String Response = ResourceSerialiser.serialise(gp, ParserType.XML);
			exchange.getIn().setHeader("FHIRResource","/Practitioner");
			exchange.getIn().setHeader("FHIRQuery","identifier="+gp.getIdentifier().get(0).getSystem()+"|"+gp.getIdentifier().get(0).getValue());
			exchange.getIn().setBody(Response);
		}
		
		exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		exchange.getIn().setHeader(Exchange.HTTP_METHOD,"GET");
		return exchange;
	}
}


