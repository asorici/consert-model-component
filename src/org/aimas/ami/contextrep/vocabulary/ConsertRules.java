package org.aimas.ami.contextrep.vocabulary;

import org.topbraid.spin.vocabulary.SPIN;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class ConsertRules {
	/** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    public final static String BASE_URI = "http://pervasive.semanticweb.org/ont/2014/05/consert/rules";
	public final static String NS = BASE_URI + "#";
	
	
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    // Vocabulary classes
    /////////////////////
    public final static Resource DomainDetection = m_model.createResource( NS + "DomainDetection" );
    public final static Resource EnteringDomainDetection = m_model.createResource( NS + "EnteringDomainDetection" );
    public final static Resource LeavingDomainDetection = m_model.createResource( NS + "LeavingDomainDetection" );
    
    public final static Resource ContextDomainEnteredRule = m_model.createResource( NS + "ContextDomainEnteredRule" );
    public final static Resource ContextDomainLeftRule = m_model.createResource( NS + "ContextDomainLeftRule" );
    
    // Vocabulary properties
 	////////////////////////
    public static final Property hasDetectedDimension = ResourceFactory.createProperty(SPIN.NS + "hasDetectedDimension"); 
    public static final Property hasDetectedDomainUser = ResourceFactory.createProperty(SPIN.NS + "hasDetectedDomainUser"); 
    public static final Property hasDetectedDomainValue = ResourceFactory.createProperty(SPIN.NS + "hasDetectedDomainValue");
    
    public static final Property DERIVE_ASSERTION = ResourceFactory.createProperty(SPIN.NS + "deriveassertion"); 
}
