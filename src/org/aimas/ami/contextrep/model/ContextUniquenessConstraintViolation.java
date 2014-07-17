package org.aimas.ami.contextrep.model;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.update.UpdateRequest;

public class ContextUniquenessConstraintViolation extends ContextConstraintViolation {
	private Node triggeringAssertionUUID;
	
	
	public ContextUniquenessConstraintViolation(ContextAssertion constrainedAssertion, 
			Node triggeringAssertionUUID, UpdateRequest triggeringRequest,
			Resource constraintSource, String conflictingAssertionUUID1, String conflictingAssertionUUID2) {
	    super(new ViolationAssertionWrapper [] {
	    		new ViolationAssertionWrapper(constrainedAssertion, conflictingAssertionUUID1),
	    		new ViolationAssertionWrapper(constrainedAssertion, conflictingAssertionUUID2)
	    	}, constraintSource, triggeringRequest, ContextConstraintType.Uniqueness);
	    
	    this.triggeringAssertionUUID = triggeringAssertionUUID;
    }
    
	public Node getTriggeringAssertionUUID() {
		return triggeringAssertionUUID;
	}
}
