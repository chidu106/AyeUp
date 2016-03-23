package uk.co.mayfieldis.hl7v2.hapi.processor;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.HealthcareService;
import org.hl7.fhir.instance.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hl7.fhir.instance.model.HealthcareService.ServiceTypeComponent;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.util.Terser;
import uk.co.mayfieldis.FHIRConstants.CHFTFHIRCodeSystems;
import uk.co.mayfieldis.FHIRConstants.FHIRCodeSystems;
import uk.co.mayfieldis.dao.ResourceSerialiser;

public class EnrichMFNM05withLocation implements AggregationStrategy {

	private static final Logger log = LoggerFactory.getLogger(uk.co.mayfieldis.hl7v2.hapi.processor.EnrichMFNM05withLocation.class);
	
	@Override
	public Exchange aggregate(Exchange exchange, Exchange enrichment) {
		
		Message message = exchange.getIn().getBody(Message.class);
		
		HealthcareService healthcareService = new HealthcareService();
		
		// Use Terser as code is more readable
		Terser terser = new Terser(message);
			
		try
		{
			healthcareService.setServiceName(terser.get("/.LOC-2"));
			
			healthcareService.addIdentifier()
				.setValue(terser.get("/.LOC-1-1"))
				.setSystem(CHFTFHIRCodeSystems.URI_CHFT_CLINIC_CODE);
			
			ServiceTypeComponent serviceType = healthcareService.addServiceType();
			if (!terser.get("/.LDP-4-1").isEmpty())
			{
				CodeableConcept localSpecialty = new CodeableConcept();
				localSpecialty.addCoding()
					.setCode(terser.get("/.LDP-4-1"))
					.setSystem(CHFTFHIRCodeSystems.URI_CHFT_SPECIALTY);
				serviceType.addSpecialty(localSpecialty);
			}
			if (!terser.get("/.LDP-4-2").isEmpty())
			{
				CodeableConcept NHSSpecialty = new CodeableConcept();
				NHSSpecialty.addCoding()
					.setCode(terser.get("/.LDP-4-2"))
					.setSystem(FHIRCodeSystems.URI_NHS_SPECIALTIES);
					
				serviceType.addSpecialty(NHSSpecialty);
			}
			if (!terser.get("/.LOC-1-4").isEmpty())
			{
				Reference locationRef = new Reference();
				locationRef.setReference("Location/"+terser.get("/.LOC-1-4"));
				healthcareService.setLocation(locationRef);
			}	
			
			String Response = ResourceSerialiser.serialise(healthcareService, ParserType.XML);
		
			
			exchange.getIn().setHeader(Exchange.HTTP_QUERY,"");
			
			if (terser.get("/.MFI-3").equals("UPD"))
			{
				exchange.getIn().setHeader(Exchange.HTTP_PATH, "/HealthcareService/"+healthcareService.getId());
				exchange.getIn().setHeader(Exchange.HTTP_METHOD,"PUT");
			}
			else
			{
				exchange.getIn().setHeader(Exchange.HTTP_PATH, "/HealthcareService");
				exchange.getIn().setHeader(Exchange.HTTP_METHOD,"POST");
			}
			
			exchange.getIn().setBody(Response);
			
			exchange.getIn().setHeader(Exchange.CONTENT_TYPE,"application/xml+fhir");
		}
		catch (Exception ex)
		{
			log.error(ex.getMessage());
		}
		
		return exchange;
	}

}
