package org.aimas.ami.contextrep.model;

public class ViolationAssertionWrapper {
	private ContextAssertion assertion;
	private String assertionInstanceUUID;
	
	/**
	 * @param assertion
	 * @param assertionInstanceUUID
	 */
    public ViolationAssertionWrapper(ContextAssertion assertion, String assertionInstanceUUID) {
	    this.assertion = assertion;
	    this.assertionInstanceUUID = assertionInstanceUUID;
    }

	public ContextAssertion getAssertion() {
		return assertion;
	}

	public String getAssertionInstanceUUID() {
		return assertionInstanceUUID;
	}
}
