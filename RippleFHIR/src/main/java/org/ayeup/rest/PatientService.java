package org.ayeup.rest;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class PatientService {
	

	    // use a tree map so they become sorted
	    private final Map<String, Patient> patients = new TreeMap<String, Patient>();

	    private Random ran = new Random();

	    public PatientService() {
	        patients.put("123", new Patient(123, "John Doe"));
	        patients.put("456", new Patient(456, "Donald Duck"));
	        patients.put("789", new Patient(789, "Slow Turtle"));
	    }

	    /**
	     * Gets a user by the given id
	     *
	     * @param id  the id of the user
	     * @return the user, or <tt>null</tt> if no user exists
	     */
	    public Patient getPatient(String id) {
	        if ("789".equals(id)) {
	            // simulate some cpu processing time when returning the slow turtle
	            int delay = 500 + ran.nextInt(1500);
	            try {
	                Thread.sleep(delay);
	            } catch (Exception e) {
	                // ignore
	            }
	        }
	        return patients.get(id);
	    }

	    /**
	     * List all users
	     *
	     * @return the list of all users
	     */
	    public Collection<Patient> listPatients() {
	        return patients.values();
	    }

	    /**
	     * Updates or creates the given user
	     *
	     * @param user the user
	     */
	    public void updatePatient(Patient patient) {
	        patients.put("" + patient.getId(), patient);
	    }

}
