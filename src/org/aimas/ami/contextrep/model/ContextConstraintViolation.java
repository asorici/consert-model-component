package org.aimas.ami.contextrep.model;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.update.UpdateRequest;

public class ContextConstraintViolation {
	public enum ContextConstraintType { Uniqueness, Value, Integrity }
	
	/*
	 * The list of ContextAssertions (there will be at most two) which compose the violation
	 */
	protected ViolationAssertionWrapper[] violatingAssertions;
	
	/*
	 * The resource identifying the direct query or template call that expresses the constraint
	 */
	protected Resource constraintSource;
	
	/*
	 * The UpdateRequest that triggered the detection of this constraint violation
	 */
	protected UpdateRequest triggeringRequest;
	
	protected ContextConstraintType constraintType;
	
	protected ContextConstraintViolation(ViolationAssertionWrapper[] violatingAssertions, 
			Resource constraintSource, UpdateRequest triggeringRequest, ContextConstraintType constraintType) {
	    
		this.violatingAssertions = violatingAssertions;
	    this.constraintSource = constraintSource;
	    this.triggeringRequest = triggeringRequest;
	    this.constraintType = constraintType;
    }


	public ViolationAssertionWrapper[] getViolatingAssertions() {
		return violatingAssertions;
	}
	
	public Resource getConstraintSource() {
		return constraintSource;
	}
	
	public UpdateRequest getTriggeringRequest() {
		return triggeringRequest;
	}
	
	public ContextConstraintType getType() {
		return constraintType;
	}
	
	public boolean isUniquenessConstraint() {
		return constraintType == ContextConstraintType.Uniqueness;
	}
	
	public boolean isValueConstraint() {
		return constraintType == ContextConstraintType.Value;
	}
	
	public boolean isIntegrityConstraint() {
		return constraintType == ContextConstraintType.Integrity;
	}
	
}
