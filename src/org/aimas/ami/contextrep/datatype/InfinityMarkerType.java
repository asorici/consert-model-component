package org.aimas.ami.contextrep.datatype;

import org.aimas.ami.contextrep.vocabulary.ConsertCore;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.impl.LiteralLabel;

public class InfinityMarkerType extends BaseDatatype {
	public static final String infinityMarkerTypeURI = ConsertCore.NS + "infinityMarkerType";
    public static final RDFDatatype infinityMarkerType = new InfinityMarkerType();
	
    public static final String NEGATIVE_INFTY = "-inf";
	public static final String POSITIVE_INFTY = "+inf";
	
	/** private constructor - single global instance */
    private InfinityMarkerType() {
        super(infinityMarkerTypeURI);
    }
	
    @Override
    public Class<?> getJavaClass() { 
    	return String.class; 
    }
    
	/**
     * Convert a value of a calendar interval list out to lexical form.
     */
    @Override
    public String unparse(Object value) {
    	String markerValue = (String) value;
    	return markerValue;
    }

    /**
     * Parse a lexical form of a calendar interval list to a value
     * @throws DatatypeFormatException if the lexical form is not legal
     */
    @Override
    public Object parse(String lexicalForm) throws DatatypeFormatException {
        if (lexicalForm.equals(POSITIVE_INFTY) || lexicalForm.equals(NEGATIVE_INFTY)) {
        	return lexicalForm;
        }
        else {
            throw new DatatypeFormatException(lexicalForm, infinityMarkerType, "Marker lexical form neither +inf, nor -inf.");
        }
    }

    /**
     * Compares two instances of values of the given datatype.
     * This does not allow CalendarIntervalLists to be compared to other number
     * formats, Lang tag is not significant.
     */
    @Override
    public boolean isEqual(LiteralLabel value1, LiteralLabel value2) {
        if (value1.getDatatype() != value2.getDatatype()) 
        	return false;
        
        String marker1 = (String) value1.getValue();
        String marker2 = (String) value2.getValue();
        
    	return marker1.equals(marker2);
    }
}
