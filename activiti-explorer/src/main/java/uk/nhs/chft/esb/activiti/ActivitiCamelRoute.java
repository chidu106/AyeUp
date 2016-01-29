package uk.nhs.chft.esb.activiti;


import org.apache.camel.builder.RouteBuilder;

public class ActivitiCamelRoute extends RouteBuilder {

    @SuppressWarnings("deprecation")
	@Override
    public void configure() 
    {
    	
    	from("activiti:RejectOrderProcess:sendHL7task")
    	    .routeId("Activiti ESB REST Op")
    	    .processRef("taskProcessor")
    	    //.to("http:chft-tiedev.xthis.nhs.uk:90/ITK/Activiti/Task?connectionsPerRoute=60")
    	    .to("http:chft-tiedlive.this.nhs.uk/ITK/Activiti/Task?connectionsPerRoute=60")
    	    // Need to remove fhir and camel elements from message returned to activiti
    	  	.setBody()
    	  		.simple("");
    	
    }
}
