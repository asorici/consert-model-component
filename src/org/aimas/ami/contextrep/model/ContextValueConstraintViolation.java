package org.aimas.ami.contextrep.model;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.update.UpdateRequest;

public class ContextValueConstraintViolation extends ContextConstraintViolation {
	private Resource assertionViolationValue;
	private Resource annotationViolationValue;
	
	public ContextValueConstraintViolation(
            ContextAssertion constrainedAssertion, UpdateRequest triggeringRequest, Resource constraintSource,
            String assertionUUID, Resource assertionViolationValue, Resource annotationViolationValue) {
	    super(new ViolationAssertionWrapper[] {
	    		new ViolationAssertionWrapper(constrainedAssertion, assertionUUID)
	    	}, constraintSource, triggeringRequest, ContextConstraintType.Value);
	    
	    this.assertionViolationValue = assertionViolationValue;
	    this.annotationViolationValue = annotationViolationValue;
    }

	public ContextAssertion getViolatingAssertion() {
		return violatingAssertions[0].getAssertion();
	}
	
	public String getViolatingAssertionUUID() {
		return violatingAssertions[0].getAssertionInstanceUUID();
	}
	
	public boolean hasAssertionViolationValue() {
		return assertionViolationValue != null;
	}
	
	public Resource getAssertionViolationValue() {
		return assertionViolationValue;
	}
	
	public boolean hasAnnotationViolationValue() {
		return annotationViolationValue != null;
	}
	
	public Resource getAnnotationViolationValue() {
		return annotationViolationValue;
	}
	
	
}
