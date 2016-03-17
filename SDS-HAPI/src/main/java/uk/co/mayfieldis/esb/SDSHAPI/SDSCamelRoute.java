package uk.co.mayfieldis.esb.SDSHAPI;

import java.util.Iterator;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.zipfile.ZipFileDataFormat;
import org.apache.camel.model.dataformat.BindyType;

public class SDSCamelRoute extends RouteBuilder {

    @Override
    public void configure() 
    {
    	
    	ZipFileDataFormat zipFile = new ZipFileDataFormat();
    	zipFile.setUsingIterator(true);
    	
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
    	    */
    	    from("scheduler://epraccur?delay=24h")
    	    	.routeId("Retrieve NHS Surgery Organizations Zip")
	    		.setHeader(Exchange.HTTP_METHOD, constant("GET"))
	    		.to("http4://systems.hscic.gov.uk/data/ods/datadownloads/data-files/epraccur.zip")
	    		.to("file:C:/NHSSDS/zip?fileName=${date:now:yyyyMMdd}-epraccur.zip");

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
    	    	//.split(body(String.class).tokenize("\n"))
    	    	.unmarshal()
                .bindy(BindyType.Csv, uk.co.mayfieldis.dao.NHSEntities.class)
    	    	.split(body())
    	    		.process("entitytoFHIR")
    	    		.to("vm:LineProcessing");
    	    
    	    from("vm:LineProcessing")
    	    	.routeId("Process entries")
    	    	.to("log:uk.co.mayfieldis.esb.SDSHAPI.SDSCamelRoute.csv?level=INFO");
    }
}
