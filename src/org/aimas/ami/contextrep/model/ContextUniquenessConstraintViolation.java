package org.aimas.ami.contextrep.model;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.update.UpdateRequest;

public class ContextUniquenessConstraintViolation extends ContextConstraintViolation {
	private Node triggeringAssertionUUID;
	private UpdateRequest triggeringRequest;
	private String[] conflictingAssertionUUIDs;
	
	
	public ContextUniquenessConstraintViolation(ContextAssertion constrainedAssertion, 
			Node triggeringAssertionUUID, UpdateRequest triggeringRequest,
			Resource constraintSource, String conflictingAssertionUUID1, String conflictingAssertionUUID2) {
	    super(constrainedAssertion, constraintSource, ContextConstraintType.Uniqueness);
	    
	    this.triggeringAssertionUUID = triggeringAssertionUUID;
	    this.triggeringRequest = triggeringRequest;
	    
	    conflictingAssertionUUIDs = new String[2];
	    conflictingAssertionUUIDs[0] = conflictingAssertionUUID1;
	    conflictingAssertionUUIDs[1] = conflictingAssertionUUID2;
    }
    
	public String[] getConflictingAssertionUUIDs() {
		return conflictingAssertionUUIDs;
	}
	
	public Node getTriggeringAssertionUUID() {
		return triggeringAssertionUUID;
	}

	public UpdateRequest getTriggeringRequest() {
		return triggeringRequest;
	}
}
