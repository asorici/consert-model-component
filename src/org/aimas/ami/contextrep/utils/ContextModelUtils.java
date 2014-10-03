package org.aimas.ami.contextrep.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.aimas.ami.contextrep.datatype.CalendarIntervalList;
import org.aimas.ami.contextrep.model.ContextAssertion.ContextAssertionType;
import org.aimas.ami.contextrep.vocabulary.ConsertAnnotation;
import org.aimas.ami.contextrep.vocabulary.ConsertCore;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.uuid.JenaUUID;
import com.hp.hpl.jena.shared.uuid.UUID_V4_Gen;
import com.hp.hpl.jena.vocabulary.RDF;

public class ContextModelUtils {
	private static final String XSD_DATETIME_PATTERN_NO_TZ = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	
	static {
		JenaUUID.setFactory(new UUID_V4_Gen());
	}
	
	public static String createUUID(String baseURI, String assertionName) {
		JenaUUID uuid = JenaUUID.generate();
		return baseURI + "/" + assertionName + "-" + uuid.asString();
	}
	
	
	public static String createUUID(Resource res) {
		String resURI = res.getURI();
		resURI = resURI.replaceAll("#", "/");
		
		JenaUUID uuid = JenaUUID.generate();
		return resURI + "-" + uuid.asString();
	}
	
	/**
	 * Utility method to create the corresponding ContextAssertion Store URI that identifies the
	 * named graph where annotations of instances for this ContextAssertion type are kept.
	 * <p/>
	 * The scheme used has the following steps:
	 * <ol>
	 * 		<li>Replace the # character with a / in the <code>assertionResourceURI</code></li>
	 * 		<li>Append the word "Store" to the resulting string</li>
	 * </ol>
	 * @param assertionResourceURI	The URI of the ontology resource defining the ContextAssertion
	 * @return The URI of the ContextAssertion Store named graph.
	 */
	public static String getAssertionStoreURI(String assertionResourceURI) {
		String assertionStoreURI = assertionResourceURI.replaceAll("#", "/");
		assertionStoreURI += "Store";
		
		return assertionStoreURI; 
	}
	
	
	public static List<Statement> createAnnotationStatements(String graphURI, String assertionResourceURI,
			ContextAssertionType assertionType, Calendar timestamp, CalendarIntervalList validity, 
			double accuracy, String sourceURI) {
		List<Statement> annotationStatements = new ArrayList<Statement>();
		
		Resource idGraph = ResourceFactory.createResource(graphURI);
		
		Property assertionTypeProp = ConsertCore.CONTEXT_ASSERTION_TYPE_PROPERTY;
		Property assertionResProp = ConsertCore.CONTEXT_ASSERTION_RESOURCE;
		
		Property hasSourceProp = ConsertAnnotation.HAS_SOURCE;
		Property hasTimestampProp = ConsertAnnotation.HAS_TIMESTAMP;
		Property hasValidityProp = ConsertAnnotation.HAS_VALIDITY;
		Property hasCertaintyProp = ConsertAnnotation.HAS_CERTAINTY;
		
		// Create type statement
		Resource typeIndividual = ResourceFactory.createResource(assertionType.getTypeURI());
		Statement typeStatement = ResourceFactory.createStatement(idGraph, assertionTypeProp, typeIndividual);
		annotationStatements.add(typeStatement);
		
		// Create assertionResource statement
		Resource assertionRes = ResourceFactory.createResource(assertionResourceURI);
		Statement assertionResStmt = ResourceFactory.createStatement(idGraph, assertionResProp, assertionRes);
		annotationStatements.add(assertionResStmt);
		
		// Create validity statement
		Literal validityAnnVal = ResourceFactory.createTypedLiteral(validity);
		Resource validityAnn = ResourceFactory.createResource();
		Statement validityStatement = ResourceFactory.createStatement(idGraph, hasValidityProp, validityAnn);
		Statement validityTypeStatement = ResourceFactory.createStatement(validityAnn, RDF.type, ConsertAnnotation.TEMPORAL_VALIDITY);
		Statement validityValStatement = ResourceFactory.createStatement(validityAnn, ConsertAnnotation.HAS_STRUCTURED_VALUE, validityAnnVal);
		annotationStatements.add(validityStatement);
		annotationStatements.add(validityTypeStatement);
		annotationStatements.add(validityValStatement);
		
		// Create timestamp Literal
		XSDDateTime xsdTimestamp = new XSDDateTime(timestamp);
		Literal timestampValAnn = ResourceFactory.createTypedLiteral(xsdTimestamp);
		Resource timestampAnn = ResourceFactory.createResource();
		Statement timestampStatement = ResourceFactory.createStatement(idGraph, hasTimestampProp, timestampAnn);
		Statement timestampTypeStatement = ResourceFactory.createStatement(timestampAnn, RDF.type, ConsertAnnotation.DATETIME_TIMESTAMP);
		Statement timestampValStatement = ResourceFactory.createStatement(timestampAnn, ConsertAnnotation.HAS_STRUCTURED_VALUE, timestampValAnn);
		annotationStatements.add(timestampStatement);
		annotationStatements.add(timestampTypeStatement);
		annotationStatements.add(timestampValStatement);
		
		/*
		System.out.println("["+ ContextModelUtils.class.getSimpleName() + "] creating annotation timestamp for assertion <"+ assertionResourceURI +">: " 
				+ xsdTimestamp);
		System.out.println("["+ ContextModelUtils.class.getSimpleName() + "] creating annotation validity for assertion <"+ assertionResourceURI +">: " 
				+ validityAnnVal);
		*/
		
		// Create certainty Literal
		Literal certaintyAnnVal = ResourceFactory.createTypedLiteral(new Double(accuracy));
		Resource certaintyAnn = ResourceFactory.createResource();
		Statement certaintyStatement = ResourceFactory.createStatement(idGraph, hasCertaintyProp, certaintyAnn);
		Statement certaintyTypeStatement = ResourceFactory.createStatement(certaintyAnn, RDF.type, ConsertAnnotation.NUMERIC_VALUE_CERTAINTY);
		Statement certaintyValStatement = ResourceFactory.createStatement(certaintyAnn, ConsertAnnotation.HAS_STRUCTURED_VALUE, certaintyAnnVal);
		annotationStatements.add(certaintyStatement);
		annotationStatements.add(certaintyTypeStatement);
		annotationStatements.add(certaintyValStatement);
		
		// Create source Literal
		Literal sourceAnnVal = ResourceFactory.createTypedLiteral(sourceURI, XSDDatatype.XSDanyURI);
		Resource sourceAnn = ResourceFactory.createResource();
		Statement sourceStatement = ResourceFactory.createStatement(idGraph, hasSourceProp, sourceAnn);
		Statement sourceTypeStatement = ResourceFactory.createStatement(sourceAnn, RDF.type, ConsertAnnotation.SOURCE_ANNOTATION);
		Statement sourceValStatement = ResourceFactory.createStatement(sourceAnn, ConsertAnnotation.HAS_UNSTRUCTURED_VALUE, sourceAnnVal);
		annotationStatements.add(sourceStatement);
		annotationStatements.add(sourceTypeStatement);
		annotationStatements.add(sourceValStatement);
		
		return annotationStatements;
	}
	
	public static String calendarToXSDString(Calendar cal) {
		/* We are attempting here to fix an issue with the calendarToXSDString method in com.hp.hpl.jena.sparql.util.Utils.
		 * The apparent bug there is that the datetime formatter is left with the default time zone.
		 * Since the method parses the timezone separately, the conversion of the datetime is done beforehand, with a representation in the DEFAULT timezone,
		 * while the ACTUAL timezone is taken from the original calendar parameter. Thus the resulting timezone representation is skewed by the offset between 
		 * the DEFAULT timezone and the timezone of the calendar given as parameter.
		 * 
		 * Here we provide a fix, by considering the timezone for the formatter, as the one coming from the calendar parameter.
		 */
        SimpleDateFormat dFmt = new SimpleDateFormat(XSD_DATETIME_PATTERN_NO_TZ);
        dFmt.setTimeZone(cal.getTimeZone());
        
        Date date = cal.getTime() ;
        String lex = dFmt.format(date) ;
        lex = lex+calcTimezone(cal) ;
        return lex ;
    }
    
    private static String calcTimezone(Calendar cal) {
        Date date = cal.getTime() ;
        TimeZone z = cal.getTimeZone() ;
        int tzOff = z.getRawOffset() ;
        int tz = tzOff ;

        if ( z.inDaylightTime(date) )
        {
            int tzDst = z.getDSTSavings() ;
            tz = tz + tzDst ;
        }
        
        String sign = "+" ;
        if ( tz < 0 )
        {
            sign = "-" ;
            tz = -tz ;
        }

        int tzH = tz/(60*60*1000) ;             // Integer divide towards zero.
        int tzM = (tz-tzH*60*60*1000)/(60*1000) ;
        
        String tzH_str = Integer.toString(tzH) ;
        String tzM_str = Integer.toString(tzM) ;
        
        if ( tzH < 10 )
            tzH_str = "0"+ tzH_str ;
        if ( tzM < 10 )
            tzM_str = "0"+ tzM_str ;
        return sign+tzH_str+":"+tzM_str ;
    }
}
