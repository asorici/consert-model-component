package org.aimas.ami.contextrep.model;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.update.UpdateRequest;

public class ContextIntegrityConstraintViolation extends ContextConstraintViolation {
	private Node triggeringAssertionUUID;
	
	public ContextIntegrityConstraintViolation(ViolationAssertionWrapper[] violatingAssertions, 
			Node triggeringAssertionUUID, UpdateRequest triggeringRequest, Resource constraintSource) {
	    
		super(violatingAssertions, constraintSource, triggeringRequest, ContextConstraintType.Uniqueness);
	    
	    this.triggeringAssertionUUID = triggeringAssertionUUID;
    }
	
	public Node getTriggeringAssertionUUID() {
		return triggeringAssertionUUID;
	}
}
