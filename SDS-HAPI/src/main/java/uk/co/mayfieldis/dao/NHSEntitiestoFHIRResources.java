package uk.co.mayfieldis.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.Address;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.instance.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.instance.model.HumanName;
import org.hl7.fhir.instance.model.Organization;
import org.hl7.fhir.instance.model.Period;
import org.hl7.fhir.instance.model.Practitioner;
import org.hl7.fhir.instance.model.Practitioner.PractitionerPractitionerRoleComponent;
import org.hl7.fhir.instance.model.Reference;
import org.hl7.fhir.instance.model.valuesets.PractitionerRole;

public class NHSEntitiestoFHIRResources implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		NHSEntities entity = exchange.getIn().getBody(NHSEntities.class);
		
		String Id = entity.OrganistionCode; 
		
		if (Id.length()==8 && Id.startsWith("G"))
		{
			Practitioner gp = new Practitioner();
			gp.setId(entity.OrganistionCode);
			
			gp.addIdentifier()
				.setValue(entity.OrganistionCode)
				.setSystem(FHIRCodeSystems.URI_NHS_GMP_CODE);
			
			String[] names = entity.Name.split(" ");
			HumanName name = new HumanName();
			
			if (names.length>0) 
			{
				name.addFamily(names[0]);
			}
			for (int f=1;f<names.length;f++)
			{
				if (names[f] !=null && !names[f].isEmpty())
				{
					name.addGiven(names[f]);
				}
			}
			gp.setName(name);
			
			Address address = gp.addAddress();
			
			if (entity.AddressLine1 != null && !entity.AddressLine1.isEmpty())
			{
				address.addLine(entity.AddressLine1);
			}
			if (entity.AddressLine2 != null && !entity.AddressLine2.isEmpty())
			{
				address.addLine(entity.AddressLine2);
			}
			if (entity.AddressLine3 != null && !entity.AddressLine3.isEmpty())
			{
				address.addLine(entity.AddressLine3);
			}
			if (entity.AddressLine4 != null && !entity.AddressLine4.isEmpty())
			{
				address.addLine(entity.AddressLine4);
			}
			if (entity.AddressLine5 != null && !entity.AddressLine5.isEmpty())
			{
				address.addLine(entity.AddressLine5);
			}
			if (entity.Postcode != null && !entity.Postcode.isEmpty())
			{
				address.setPostalCode(entity.Postcode);
			}
			
			if (entity.StatusCode.equals("A"))
			{
				// This setting looks to be garbage. Believe active means they are still on the register but may not be practising medicine 
				gp.setActive(true);
			}
			else
			{
				gp.setActive(false);
			}
			
			if (entity.ContactTelephoneNumber != null && !entity.ContactTelephoneNumber.isEmpty())
			{
				gp.addTelecom()
					.setValue(entity.ContactTelephoneNumber)
					.setSystem(ContactPointSystem.PHONE)
					.setUse(ContactPointUse.WORK);
			}
			
			PractitionerPractitionerRoleComponent role = gp.addPractitionerRole();
			
			if (entity.ParentOrganisationCode != null && !entity.ParentOrganisationCode.isEmpty())
			{
				Reference practice = new Reference();
				practice.setReference("Organiszation/"+entity.ParentOrganisationCode);
				role.setManagingOrganization(practice);
			}			
			
			CodeableConcept pracspecialty= new CodeableConcept();
			pracspecialty.addCoding()
				.setCode("600")
				.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES);
			role
				.addSpecialty(pracspecialty);
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			Period period = new Period();
			
			if (entity.LeftParentDate!=null && !entity.LeftParentDate.isEmpty())
			{
				try {
					period.setEnd(fmt.parse(entity.LeftParentDate));
					gp.setActive(false);
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			if (entity.JoinParentDate!=null && !entity.JoinParentDate.isEmpty())
			{
				try {
					period.setStart(fmt.parse(entity.JoinParentDate));
	        	} catch (ParseException e1) {
	        	// TODO Auto-generated catch block
	        	}
			}
			role.setPeriod(period);
			
			CodeableConcept pracrole= new CodeableConcept();
			pracrole.addCoding()
					.setCode(PractitionerRole.DOCTOR.toString())
					.setSystem("http://hl7.org/fhir/practitioner-role");
			role.setRole(pracrole);
			
			
			String Response = ResourceSerialiser.serialise(gp, ParserType.JSON);
		
			exchange.getIn().setBody(Response);
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json+fhir");
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
		}
		if (Id.length()==6)
		{
			Organization organisation = new Organization();
			
			organisation.setId(Id);
			organisation.addIdentifier()
				.setValue(Id)
				.setSystem(FHIRCodeSystems.URI_NHS_OCS_ORGANISATION_CODE);
			
			organisation.setName(entity.Name);
			
			Address address = organisation.addAddress();
			
			if (entity.AddressLine1 != null && !entity.AddressLine1.isEmpty())
			{
				address.addLine(entity.AddressLine1);
			}
			if (entity.AddressLine2 != null && !entity.AddressLine2.isEmpty())
			{
				address.addLine(entity.AddressLine2);
			}
			if (entity.AddressLine3 != null && !entity.AddressLine3.isEmpty())
			{
				address.addLine(entity.AddressLine3);
			}
			if (entity.AddressLine4 != null && !entity.AddressLine4.isEmpty())
			{
				address.addLine(entity.AddressLine4);
			}
			if (entity.AddressLine5 != null && !entity.AddressLine5.isEmpty())
			{
				address.addLine(entity.AddressLine5);
			}
			if (entity.Postcode != null && !entity.Postcode.isEmpty())
			{
				address.setPostalCode(entity.Postcode);
			}
			if (entity.ContactTelephoneNumber != null && !entity.ContactTelephoneNumber.isEmpty())
			{
				organisation.addTelecom()
					.setValue(entity.ContactTelephoneNumber)
					.setSystem(ContactPointSystem.PHONE)
					.setUse(ContactPointUse.WORK);
			}
			
			if (entity.StatusCode.equals("A"))
			{
				// This setting looks to be garbage. Believe active means they are still on the register but may not be practising medicine 
				organisation.setActive(true);
			}
			else
			{
				organisation.setActive(false);
			}
			
			if (entity.ParentOrganisationCode !=null && !entity.ParentOrganisationCode.isEmpty())
			{
				Reference ccg = new Reference();
				ccg.setReference("Organization/"+entity.ParentOrganisationCode);
				organisation.setPartOf(ccg);
			}
			
			String Response = ResourceSerialiser.serialise(organisation, ParserType.JSON);
			exchange.getIn().setBody(Response);
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/json+fhir");
			exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
		}
		
	}

}
