package uk.co.mayfieldis.esb.SDSHAPI;

import java.util.Iterator;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipFileDataFormat;
import org.apache.camel.model.dataformat.BindyType;

import uk.co.mayfieldis.dao.EnrichwithParentOrganisation;
import uk.co.mayfieldis.dao.EnrichwithUpdateType;

public class SDSCamelRoute extends RouteBuilder {

    @Override
    public void configure() 
    {
    	
    	ZipFileDataFormat zipFile = new ZipFileDataFormat();
    	zipFile.setUsingIterator(true);
    	
    	EnrichwithParentOrganisation enrichOrg = new EnrichwithParentOrganisation();
    	EnrichwithUpdateType enrichUpdateType = new EnrichwithUpdateType();
    	
    	/*
    	errorHandler(deadLetterChannel("direct:error")
        		.useOriginalMessage()
        		.maximumRedeliveries(2));
            	    
    	    from("direct:error")
            	.routeId("NHS SDS Fail Handler")
            	.to("log:uk.co.mayfieldis.esb.SDSHAPI.SDSCamelRoute?level=ERROR");
    	  */
    	
    		// Should follow Practice upload otherwise practice won't exist
    	   /* from("scheduler://egcur?delay=24h&initialDelay=20m")
    	    	.routeId("Retrieve NHS GP Practitioner Zip")
    	    	.setHeader(Exchange.HTTP_METHOD, constant("GET"))
    	    	.to("http4://systems.hscic.gov.uk/data/ods/datadownloads/data-files/egpcur.zip")
    	    	.to("file:C:/NHSSDS/zip?fileName=${date:now:yyyyMMdd}-egpcur.zip");
    	    
    	    from("scheduler://epraccur?delay=24h")
    	    	.routeId("Retrieve NHS Surgery Organizations Zip")
	    		.setHeader(Exchange.HTTP_METHOD, constant("GET"))
	    		.to("http4://systems.hscic.gov.uk/data/ods/datadownloads/data-files/epraccur.zip")
	    		.to("file:C:/NHSSDS/zip?fileName=${date:now:yyyyMMdd}-epraccur.zip");
			*/
    	    from("file:C:/NHSSDS/zip?readLock=markerFile&preMove=inprogress&move=.done&include=.*.(zip)&delay=1000")
	    		.routeId("Unzip NHS Reference Files")
	    		.unmarshal(zipFile)
	    		.split(body(Iterator.class))
	    			.streaming()
	    				.to("log:uk.co.mayfieldis.esb.SDSHAPI.SDSCamelRoute.zip?level=INFO")
	    				.to("file:C:/NHSSDS/Extract")
	    			.end()
	    		.end();
    	    
    	    from("file:C:/NHSSDS/Extract?readLock=markerFile&preMove=inprogress&move=.done&include=.*.(csv)&delay=1000")
    	    	.routeId("Split CSV File")
    	    	.unmarshal()
                .bindy(BindyType.Csv, uk.co.mayfieldis.dao.NHSEntities.class)
    	    	.split(body())
    	    		.to("vm:LineProcessing");
    	    
    	    from("vm:LineProcessing")
    	    	.routeId("Process entries")
    	    	.enrich("direct:org",enrichOrg)
    	    	.enrich("direct:lookup",enrichUpdateType)
    	    	.to("log:uk.co.mayfieldis.esb.SDSHAPI.SDSCamelRoute.enriched?level=INFO")
    	    	.log("${header.CamelHttpMethod}")
    	    	.filter(header(Exchange.HTTP_METHOD).isEqualTo("POST")).to("direct:Update")
    	    	.filter(header(Exchange.HTTP_METHOD).isEqualTo("PUT")).to("direct:Update");
    	    	//.filter(header(Exchange.HTTP_METHOD).isEqualTo("GET")).to("direct:Update");
    	    	// Gets are discarded
    	    
    	    from("direct:Update")
    	    	.routeId("Update JPA Server")
    	    	.toD("http://localhost:8080/hapi-fhir-jpaserver/baseDstu2/${header.FHIRResource}")
    	    	.to("log:uk.co.mayfieldis.esb.SDSHAPI.SDSCamelRoute.update?level=INFO");
    	    
    	    from("direct:org")
    	    	.routeId("Lookup FHIR Organisation")
    	    	.process("entitytoHeader")
    	    	.toD("http://localhost:8080/hapi-fhir-jpaserver/baseDstu2/Organization?identifier=urn:fhir.nhs.uk/id/ODSOrganisationCode|${header.ParentOrganisationCode}")
    	    	.to("log:uk.co.mayfieldis.esb.SDSHAPI.SDSCamelRoute.org?level=INFO");
    	    
    	    from("direct:lookup")
	    	.routeId("Lookup FHIR Resources")
	    	.toD("http://localhost:8080/hapi-fhir-jpaserver/baseDstu2/${header.FHIRResource}")
	    	.to("log:uk.co.mayfieldis.esb.SDSHAPI.SDSCamelRoute.lookup?level=INFO");
    }
}
