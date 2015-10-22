package org.ayeup.samples;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class BinaryService {
	

	    // use a tree map so they become sorted
	    private final Map<String, Binary> binaries = new TreeMap<String, Binary>();

	    private Random ran = new Random();

	    public BinaryService() {
	    	binaries.put("123", new Binary(123, "John Doe"));
	    	binaries.put("456", new Binary(456, "Donald Duck"));
	    	binaries.put("789", new Binary(789, "Slow Turtle"));
	    }

	    /**
	     * Gets a user by the given id
	     *
	     * @param id  the id of the user
	     * @return the user, or <tt>null</tt> if no user exists
	     */
	    public Binary getBinary(String id) {
	        if ("789".equals(id)) {
	            // simulate some cpu processing time when returning the slow turtle
	            int delay = 500 + ran.nextInt(1500);
	            try {
	                Thread.sleep(delay);
	            } catch (Exception e) {
	                // ignore
	            }
	        }
	        return binaries.get(id);
	    }

	    /**
	     * List all users
	     *
	     * @return the list of all users
	     */
	    public Collection<Binary> listBinary() {
	        return binaries.values();
	    }

	    /**
	     * Updates or creates the given user
	     *
	     * @param user the user
	     */
	    public void updateBinary(Binary document) {
	        binaries.put("" + document.getId(), document);
	    }

}
