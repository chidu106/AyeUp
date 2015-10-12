package org.ayeup.rest;


import org.ayeup.samples.PatientProcessorDummy;

public class PatientService {
	
		/*
	    // use a tree map so they become sorted
	    private final Map<String, Patient> patients = new TreeMap<String, Patient>();

	    
	    public PatientService() {
	        patients.put("123", new Patient(123, "John Doe"));
	        patients.put("456", new Patient(456, "Donald Duck"));
	        patients.put("789", new Patient(789, "Slow Turtle"));
	    }
		*/
	    public String getPatient(String id) {
	    	PatientProcessorDummy dum = new PatientProcessorDummy();
	    	String json =dum.getPatient(id); 
	    	return json;
	    }
	   
	     /*
	    public Collection<Patient> listPatients() {
	        return patients.values();
	    }
		
	    
	    public void updatePatient(Patient patient) {
	        patients.put("" + patient.getId(), patient);
	    }
		 */
}
