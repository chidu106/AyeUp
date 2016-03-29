package uk.co.mayfieldis.dao;


import org.hl7.fhir.instance.formats.ParserType;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.OperationOutcome;
import org.hl7.fhir.instance.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.instance.model.OperationOutcome.IssueType;

public class operationOutcomeService {
	
	public operationOutcomeService() {
        
    }
	
	public String getOutcome(Integer errorCode, String errorDescription, String contentType) {
        
		OperationOutcome outcome = new OperationOutcome();
		
		if (errorDescription.contains("statusCode: "))
		{
			String newErrorCode = errorDescription.substring(errorDescription.indexOf("statusCode: ")+12);
			try 
			{
				errorCode = Integer.parseInt(newErrorCode);
			}
			finally
			{
				if (errorCode==0) 
				{
					errorCode =522;
				}
			}
		}
		
		outcome.setId(errorCode.toString());
		if  ((errorCode < 499) && (errorCode >= 400))
		{
			outcome.addIssue()
				.setSeverity(IssueSeverity.WARNING);
		}
		else if (errorCode < 400)
		{
			outcome.addIssue()
				.setSeverity(IssueSeverity.INFORMATION);
		}
		else
		{
			outcome.addIssue()
				.setSeverity(IssueSeverity.ERROR);
		}
		
		
		CodeableConcept details = new CodeableConcept();
		details.setText(errorDescription)
			.addCoding()
			.setCode(errorCode.toString());
			
		switch (errorCode)
		{
			case 401:
				outcome.getIssue().get(0)
					.setCode(IssueType.SECURITY)
					.setDetails(details);
				break;
			case 409:
				outcome.getIssue().get(0)
					.setCode(IssueType.CONFLICT)
					.setDetails(details);
				break;	
			default:
				outcome.getIssue().get(0)
				.setCode(IssueType.EXCEPTION)
				.setDetails(details);
		}
		
		
		String output=null;
		
		if (contentType==null)
		{
		  contentType="application/xml";	
		}
		
		if (contentType.contains("json"))	
		{
			output = ResourceSerialiser.serialise(outcome, ParserType.JSON);
		}
		else
		{
			output = ResourceSerialiser.serialise(outcome, ParserType.XML);
		}
		
        return output;
    }
}
