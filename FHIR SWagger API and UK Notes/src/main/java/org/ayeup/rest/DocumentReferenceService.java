package org.ayeup.rest;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class DocumentReferenceService {
	

	    // use a tree map so they become sorted
	    private final Map<String, DocumentReference> documents = new TreeMap<String, DocumentReference>();

	    private Random ran = new Random();

	    public DocumentReferenceService() {
	        documents.put("123", new DocumentReference(123, "John Doe"));
	        documents.put("456", new DocumentReference(456, "Donald Duck"));
	        documents.put("789", new DocumentReference(789, "Slow Turtle"));
	    }

	    /**
	     * Gets a user by the given id
	     *
	     * @param id  the id of the user
	     * @return the user, or <tt>null</tt> if no user exists
	     */
	    public DocumentReference getDocumentReference(String id) {
	        if ("789".equals(id)) {
	            // simulate some cpu processing time when returning the slow turtle
	            int delay = 500 + ran.nextInt(1500);
	            try {
	                Thread.sleep(delay);
	            } catch (Exception e) {
	                // ignore
	            }
	        }
	        return documents.get(id);
	    }

	    /**
	     * List all users
	     *
	     * @return the list of all users
	     */
	    public Collection<DocumentReference> listDocumentReferences() {
	        return documents.values();
	    }

	    /**
	     * Updates or creates the given user
	     *
	     * @param user the user
	     */
	    public void updateDocumentReference(DocumentReference document) {
	        documents.put("" + document.getId(), document);
	    }

}
