package org.aimas.ami.contextrep.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.aimas.ami.contextrep.model.exceptions.ContextModelConfigException;
import org.aimas.ami.contextrep.vocabulary.ConsertAnnotation;
import org.aimas.ami.contextrep.vocabulary.ConsertConstraint;
import org.aimas.ami.contextrep.vocabulary.ConsertCore;
import org.aimas.ami.contextrep.vocabulary.ConsertFunctions;
import org.aimas.ami.contextrep.vocabulary.ConsertRules;
import org.topbraid.spin.util.JenaUtil;
import org.topbraid.spin.vocabulary.SP;
import org.topbraid.spin.vocabulary.SPIN;
import org.topbraid.spin.vocabulary.SPL;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.shared.NotFoundException;
import com.hp.hpl.jena.util.Locator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

public class ContextModelLoader {
	public static final String MODEL_RESOURCE_PATH = "etc/model/";
	public static final String MODEL_ONTOLOGIES_PATH = MODEL_RESOURCE_PATH + "ontologies/";
	public static final String MODEL_PROPERTIES_FILE = MODEL_RESOURCE_PATH + "model.properties";
	
	public static final String CONSERT_ONT_DOCMGR_FILE = MODEL_RESOURCE_PATH + "consert-ont-policy.rdf";
	public static final String SPIN_ONT_DOCMGR_FILE = MODEL_RESOURCE_PATH + "spin-ont-policy.rdf";
	
	// Model properties keys
	///////////////////////////////////////////////////////////////////////////////////////
	public static final String CONSERT_ONT_DOCMGR_KEY = "consert.ontology.documentmgr.file";
	public static final String SPIN_ONT_DOCMGR_KEY = "spin.ontology.documentmgr.file";
	public static final String DOMAIN_ONT_DOCMGR_KEY = "context.model.documentmgr.file";
	
	public static final String DOMAIN_ONT_CORE_URI_KEY = "context.model.ontology.core.uri";
	public static final String DOMAIN_ONT_ANNOTATION_URI_KEY = "context.model.ontology.annotation.uri";
	public static final String DOMAIN_ONT_CONSTRAINT_URI_KEY = "context.model.ontology.constraints.uri";
	public static final String DOMAIN_ONT_FUNCTIONS_URI_KEY = "context.model.ontology.functions.uri";
	public static final String DOMAIN_ONT_RULES_URI_KEY = "context.model.ontology.rules.uri";
	
	public static final String[] DOMAIN_ONT_MODULE_KEYS = new String[] {
		DOMAIN_ONT_CORE_URI_KEY, DOMAIN_ONT_ANNOTATION_URI_KEY, DOMAIN_ONT_CONSTRAINT_URI_KEY,
		DOMAIN_ONT_FUNCTIONS_URI_KEY, DOMAIN_ONT_RULES_URI_KEY
	};
	
	
	public ContextModelLoader(ResourceManager resourceManager, Dictionary<String, String> modelDefinitionFiles) 
			throws ContextModelConfigException {
		
		this.modelResourceManager = resourceManager;
		configure(modelDefinitionFiles);
	}
	
	
	// Context Model configuration
	///////////////////////////////////////////////////////////////////////////////////////
	private ResourceManager modelResourceManager;
	private Properties modelConfigProperties;
	
	private void configure(Dictionary<String, String> modelDefinitionFiles) throws ContextModelConfigException {
		
		if (modelDefinitionFiles != null) {
			// If we have a modelDefinition dictionary, use it to retrieve the module files
			String domainDocMgrFile = modelDefinitionFiles.get(ContextModelLoader.DOMAIN_ONT_DOCMGR_KEY);
			String domainCoreFile = modelDefinitionFiles.get(ContextModelLoader.DOMAIN_ONT_CORE_URI_KEY);
			String domainAnnotationsFile = modelDefinitionFiles.get(ContextModelLoader.DOMAIN_ONT_ANNOTATION_URI_KEY);
			String domainConstraintsFile = modelDefinitionFiles.get(ContextModelLoader.DOMAIN_ONT_CONSTRAINT_URI_KEY);
			String domainFunctionsFile = modelDefinitionFiles.get(ContextModelLoader.DOMAIN_ONT_FUNCTIONS_URI_KEY);
			String domainRulesFile = modelDefinitionFiles.get(ContextModelLoader.DOMAIN_ONT_RULES_URI_KEY);
			
			modelConfigProperties = new Properties();
			
			if (domainDocMgrFile != null)
				modelConfigProperties.setProperty(DOMAIN_ONT_DOCMGR_KEY, domainDocMgrFile);
			
			if (domainCoreFile != null)
				modelConfigProperties.setProperty(DOMAIN_ONT_CORE_URI_KEY, domainCoreFile);
			
			if (domainAnnotationsFile != null)
				modelConfigProperties.setProperty(DOMAIN_ONT_ANNOTATION_URI_KEY, domainAnnotationsFile);
			
			if (domainConstraintsFile != null)
				modelConfigProperties.setProperty(DOMAIN_ONT_CONSTRAINT_URI_KEY, domainConstraintsFile);
			
			if (domainFunctionsFile != null)
				modelConfigProperties.setProperty(DOMAIN_ONT_FUNCTIONS_URI_KEY, domainFunctionsFile);
			
			if (domainRulesFile != null)
				modelConfigProperties.setProperty(DOMAIN_ONT_RULES_URI_KEY, domainRulesFile);
		}
		else {
			// Otherwise, resort to searching for them in the model.properties file
			InputStream modelConfigStream = modelResourceManager.getResourceAsStream(MODEL_PROPERTIES_FILE);
			if (modelConfigStream == null) {
				throw new ContextModelConfigException("Context Model configuration file: " + MODEL_PROPERTIES_FILE + " not found in resources.");
			}
			
			modelConfigProperties = readModelConfig(modelConfigStream);
		}
		
		validateModelConfig(modelConfigProperties);
	}
	
	private Properties readModelConfig(InputStream configStream) throws ContextModelConfigException {
		try {
			// load the properties file
			Properties modelConfiguration = new Properties();
			modelConfiguration.load(configStream);
			
			return modelConfiguration;
		}
        catch (IOException e) {
        	throw new ContextModelConfigException("model.properties file could not be loaded", e);
        }
	}
	
	private static void validateModelConfig(Properties modelConfiguration) throws ContextModelConfigException {
		// Step 1) Check for existence of Context Model Core URI (this MUST exist)
		if (modelConfiguration.get(DOMAIN_ONT_CORE_URI_KEY) == null) {
				throw new ContextModelConfigException("No value for required" 
					+ "Context Domain core module key: " + DOMAIN_ONT_CORE_URI_KEY);
		}
		
		// Step 2) Check for existence of the Context Model document manager file key
		if (modelConfiguration.get(DOMAIN_ONT_DOCMGR_KEY) == null) {
			throw new ContextModelConfigException("Configuration properties has no value for "
					+ "Context Domain ontology document manager key: " + DOMAIN_ONT_DOCMGR_KEY);
		}
    }
	
	// Context Model Loading
	///////////////////////////////////////////////////////////////////////////////////////
	
	private Map<String, OntModel> baseContextModelMap;
	private OntDocumentManager modelGlobalDocManager;
	
	public void loadModel() throws ContextModelConfigException {
		// Check that the model configuration properties have been loaded
		if (modelConfigProperties == null) {
			throw new ContextModelConfigException("No model configuration properties exist. "
					+ "Did you call configure(ResourceManager resourceManager)?");
		}
		
		// Get the context model URI map
		Map<String, String> contextModelURIMap = getContextModelURIs(modelConfigProperties);
		baseContextModelMap = setupContextModelModules(modelConfigProperties, contextModelURIMap);
	}
	
	public void closeModel() {
		for (String moduleKey : baseContextModelMap.keySet()) {
			OntModel m = baseContextModelMap.get(moduleKey);
			m.close();
		}
	}
	
	public OntModel load(String filenameOrURI) throws ContextModelConfigException {
		try {
			// setup the ontology model doc managers
			setupOntologyDocManagers(modelConfigProperties);
			
			// create the OntModelSpec
			OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
			modelSpec.setDocumentManager(modelGlobalDocManager);
			
			// create the agent configuration OntModel
			OntModel model = ModelFactory.createOntologyModel(modelSpec);
			model.add(modelGlobalDocManager.getFileManager().loadModel(filenameOrURI));
			modelGlobalDocManager.loadImports(model);
			
			return model;
		} 
		catch(NotFoundException e) {
			throw new ContextModelConfigException("The filename or URI " + filenameOrURI + 
					" could not be found within the resources managed by this loader.", e);
		}
		catch(JenaException e) {
			throw new ContextModelConfigException("Syntax error for model loaded from filename or URI " + filenameOrURI, e);
		}
	}
	
	/**
	 * Use the configuration file to create the map of base URIs for each module of the domain Context Model:
	 * <i>core, annotation, constraints, functions, rules</i>
	 * @return A map of the base URIs for each type of module within the current domain Context Model
	 */
	private static Map<String, String> getContextModelURIs(Properties contextModelConfig) throws ContextModelConfigException {
	    Map<String, String> contextModelURIMap = new HashMap<String, String>();
	    
	    /* build the mapping from context domain model keys to the corresponding URIs (if defined)
	     * If certain module keys are non-existent, only the default CONSERT Engine specific elements 
	     * (e.g. Annotations, Functions) will be loaded and indexed.
	     */
	    String domainCoreURI = contextModelConfig.getProperty(DOMAIN_ONT_CORE_URI_KEY);
	    String domainAnnotationURI = contextModelConfig.getProperty(DOMAIN_ONT_ANNOTATION_URI_KEY);
	    String domainConstraintURI = contextModelConfig.getProperty(DOMAIN_ONT_CONSTRAINT_URI_KEY);
	    String domainFunctionsURI = contextModelConfig.getProperty(DOMAIN_ONT_FUNCTIONS_URI_KEY);
	    String domainRulesURI = contextModelConfig.getProperty(DOMAIN_ONT_RULES_URI_KEY);
	    
	    // the Context Model core URI must exist
	    contextModelURIMap.put(DOMAIN_ONT_CORE_URI_KEY, domainCoreURI);
	    
	    if (domainAnnotationURI != null) 
	    	contextModelURIMap.put(DOMAIN_ONT_ANNOTATION_URI_KEY, domainAnnotationURI);
	    
	    if (domainConstraintURI != null) 
	    	contextModelURIMap.put(DOMAIN_ONT_CONSTRAINT_URI_KEY, domainConstraintURI);
	    
	    if (domainFunctionsURI != null) 
	    	contextModelURIMap.put(DOMAIN_ONT_FUNCTIONS_URI_KEY, domainFunctionsURI);
	    
	    if (domainRulesURI != null) 
	    	contextModelURIMap.put(DOMAIN_ONT_RULES_URI_KEY, domainRulesURI);
	    
	    return contextModelURIMap;
    }
	
	
	/**
	 * Setup the document managers for the CONSERT, SPIN and Context Domain ontologies 
	 * with configuration files taken from the config.properties file
	 */
	private Map<String, OntDocumentManager> setupOntologyDocManagers(Properties contextModelConfig) throws ContextModelConfigException {
		Map<String, OntDocumentManager> ontDocumentManagers = new HashMap<String, OntDocumentManager>();
		Locator contextModelLocator = modelResourceManager.getResourceLocator();
		
		String domainOntDocMgrFile = contextModelConfig.getProperty(DOMAIN_ONT_DOCMGR_KEY);
		
		try {
			// ======== create a document manager configuration for the CONSERT ontology ========
	        Model consertDocMgrModel = ModelFactory.createDefaultModel();
	        InputStream consertDocMgrStream = modelResourceManager.getResourceAsStream(CONSERT_ONT_DOCMGR_FILE);
	        consertDocMgrModel.read(consertDocMgrStream, null);
	        consertDocMgrStream.close();
	        
	        // create the consert ont doc manager and add this bundle's classloader as a Locator
	        OntDocumentManager consertDocManager = new OntDocumentManager(consertDocMgrModel);
	        consertDocManager.getFileManager().addLocator(contextModelLocator);
	        ontDocumentManagers.put(CONSERT_ONT_DOCMGR_KEY, consertDocManager);
	        
	        // ======== create a document manager configuration for the SPIN ontology ========
	        Model spinDocMgrModel = ModelFactory.createDefaultModel();
	        InputStream spinDocMgrStream = modelResourceManager.getResourceAsStream(SPIN_ONT_DOCMGR_FILE);
	        spinDocMgrModel.read(spinDocMgrStream, null);
	        spinDocMgrStream.close();
	        
	        // create the spin ont doc manager and add this bundle's classloader as a Locator
	        OntDocumentManager spinDocManager = new OntDocumentManager(spinDocMgrModel);
	        spinDocManager.getFileManager().addLocator(contextModelLocator);
	        ontDocumentManagers.put(SPIN_ONT_DOCMGR_KEY, spinDocManager);
	        
			// ======== create a document manager configuration for the Context Domain ========
	        Model domainDocMgrModel = ModelFactory.createDefaultModel();
	        consertDocMgrStream = modelResourceManager.getResourceAsStream(CONSERT_ONT_DOCMGR_FILE);
	        InputStream domainDocMgrStream = modelResourceManager.getResourceAsStream(domainOntDocMgrFile);
	        
	        // read the CONSERT and domain specific document manager config into it
	        domainDocMgrModel.read(consertDocMgrStream, null);
	        domainDocMgrModel.read(domainDocMgrStream, null);
	        consertDocMgrStream.close();
	        domainDocMgrStream.close();
	        
	        // create the domain ont doc manager and add this bundle's classloader as a Locator
	        OntDocumentManager domainDocManager = new OntDocumentManager(domainDocMgrModel);
	        domainDocManager.getFileManager().addLocator(contextModelLocator);
	        ontDocumentManagers.put(DOMAIN_ONT_DOCMGR_KEY, domainDocManager);
	        
	        // ======== setup the model-global document manager to block no imports and define the path to everything ========
	        Model globalDocMgrModel = ModelFactory.createDefaultModel();
	        consertDocMgrStream = modelResourceManager.getResourceAsStream(CONSERT_ONT_DOCMGR_FILE); 
	        spinDocMgrStream = modelResourceManager.getResourceAsStream(SPIN_ONT_DOCMGR_FILE); 
	        domainDocMgrStream = modelResourceManager.getResourceAsStream(domainOntDocMgrFile);
	        
	        
	        globalDocMgrModel.read(spinDocMgrStream, null);
	        globalDocMgrModel.read(consertDocMgrStream, null);
	        globalDocMgrModel.read(domainDocMgrStream, null);
	        
	        consertDocMgrStream.close(); 
	        spinDocMgrStream.close(); 
	        domainDocMgrStream.close();
	        
	        modelGlobalDocManager = new OntDocumentManager();
	        modelGlobalDocManager.configure(globalDocMgrModel);
	        modelGlobalDocManager.getFileManager().addLocator(contextModelLocator);
	        
	        // remove all the import restrictions in previous documentManagers
	        for (Iterator<String> ignoreIt = consertDocManager.listIgnoredImports(); ignoreIt.hasNext();) {
	        	String ignoredURI = ignoreIt.next();
	        	modelGlobalDocManager.removeIgnoreImport(ignoredURI);
	        }
	        
	        for (Iterator<String> ignoreIt = domainDocManager.listIgnoredImports(); ignoreIt.hasNext();) {
	        	String ignoredURI = ignoreIt.next();
	        	modelGlobalDocManager.removeIgnoreImport(ignoredURI);
	        }
		}
		catch(FileNotFoundException ex) {
			throw new ContextModelConfigException("Failed to load context model document managers.", ex);
		}
        catch (IOException ex) {
        	throw new ContextModelConfigException("Failed to load context model document managers.", ex);
        }
		
		return ontDocumentManagers;
	}
	
	/**
	 * Use the configuration file to create the map of ontology models (<b>basic, no inference</b>) 
	 * for each module of the domain Context Model: <i>core, annotation, constraints, functions, rules</i>
	 * @param contextModelURIMap The Context Model URI map built by calling {@code getContextModelURIs}
	 * @return A map of the models for each type of module within the current domain Context Model
	 * @see getContextModelURIs
	 */
	private Map<String, OntModel> setupContextModelModules(Properties contextModelConfig, 
			Map<String, String> contextModelURIMap) throws ContextModelConfigException {
		Map<String, OntModel> contextModelMap = new HashMap<String, OntModel>();
		
		// ======== setup document managers for ontology importing ========
        Map<String, OntDocumentManager> ontDocumentManagers = setupOntologyDocManagers(contextModelConfig);
		
        OntDocumentManager domainDocManager = ontDocumentManagers.get(DOMAIN_ONT_DOCMGR_KEY);
        OntModelSpec domainContextModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
        domainContextModelSpec.setDocumentManager(domainDocManager);
        
        // ======== now we are ready to load all context ontology modules ========
        // 1) build the core context model
        String contextModelCoreURI = contextModelURIMap.get(DOMAIN_ONT_CORE_URI_KEY);
        OntModel contextModelCore = ModelFactory.createOntologyModel(domainContextModelSpec);
        
        contextModelCore.add(domainDocManager.getFileManager().loadModel(contextModelCoreURI));
        contextModelCore.addSubModel(domainDocManager.getFileManager().loadModel(ConsertCore.BASE_URI));
        domainDocManager.loadImports(contextModelCore);
        contextModelMap.put(DOMAIN_ONT_CORE_URI_KEY, contextModelCore);
        
        // 2) build the annotation context model
        String contextModelAnnotationURI = contextModelURIMap.get(DOMAIN_ONT_ANNOTATION_URI_KEY);
        OntModel contextModelAnnotations = ModelFactory.createOntologyModel(domainContextModelSpec);
        
        if (contextModelAnnotationURI != null) {
        	contextModelAnnotations.add(domainDocManager.getFileManager().loadModel(contextModelAnnotationURI));
        	contextModelAnnotations.addSubModel(domainDocManager.getFileManager().loadModel(ConsertAnnotation.BASE_URI));
        }
        else {
        	contextModelAnnotations.add(domainDocManager.getFileManager().loadModel(ConsertAnnotation.BASE_URI));
        }
        domainDocManager.loadImports(contextModelAnnotations);
        contextModelMap.put(DOMAIN_ONT_ANNOTATION_URI_KEY, contextModelAnnotations);
        
        // 3) build the constraints context model
        String contextModelConstraintsURI = contextModelURIMap.get(DOMAIN_ONT_CONSTRAINT_URI_KEY);
        OntModel contextModelConstraints = ModelFactory.createOntologyModel(domainContextModelSpec);
        
        if (contextModelConstraintsURI != null) {
        	contextModelConstraints.add(domainDocManager.getFileManager().loadModel(contextModelConstraintsURI));
        	contextModelConstraints.addSubModel(domainDocManager.getFileManager().loadModel(ConsertConstraint.BASE_URI));
        }
        else {
        	contextModelConstraints.add(domainDocManager.getFileManager().loadModel(ConsertConstraint.BASE_URI));
        }
        domainDocManager.loadImports(contextModelConstraints);
        contextModelMap.put(DOMAIN_ONT_CONSTRAINT_URI_KEY, contextModelConstraints);
        
        
        // 4) build the functions context model
        String contextModelFunctionsURI = contextModelURIMap.get(DOMAIN_ONT_FUNCTIONS_URI_KEY);
        OntModel contextModelFunctions = ModelFactory.createOntologyModel(domainContextModelSpec);
        
        if (contextModelFunctionsURI != null) {
        	contextModelFunctions.add(domainDocManager.getFileManager().loadModel(contextModelFunctionsURI));
        	contextModelFunctions.addSubModel(domainDocManager.getFileManager().loadModel(ConsertFunctions.BASE_URI));
        }
        else {
        	contextModelFunctions.add(domainDocManager.getFileManager().loadModel(ConsertFunctions.BASE_URI));
        }
        domainDocManager.loadImports(contextModelFunctions);
        contextModelMap.put(DOMAIN_ONT_FUNCTIONS_URI_KEY, contextModelFunctions);
        
        // 5) build the rules context model
        String contextModelRulesURI = contextModelURIMap.get(DOMAIN_ONT_RULES_URI_KEY);
        OntModel contextModelRules = ModelFactory.createOntologyModel(domainContextModelSpec);
        
        if (contextModelRulesURI != null) {
        	contextModelRules.add(domainDocManager.getFileManager().loadModel(contextModelRulesURI));
        	contextModelRules.addSubModel(domainDocManager.getFileManager().loadModel(ConsertRules.BASE_URI));
        }
        else {
        	contextModelRules.add(domainDocManager.getFileManager().loadModel(ConsertRules.BASE_URI));
        }
        domainDocManager.loadImports(contextModelRules);
        contextModelMap.put(DOMAIN_ONT_RULES_URI_KEY, contextModelRules);
        
	    return contextModelMap;
    }
	
	
	public OntModel getTransitiveInferenceModel(OntModel basicContextModel) {
		OntModel transitiveModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_TRANS_INF);
		transitiveModel.addSubModel(basicContextModel);
		
		return transitiveModel;
	}
	
	public OntModel getRDFSInferenceModel(OntModel basicContextModel) {
		//Model inferenceHolder = ModelFactory.createDefaultModel();
		//Model inferenceBase = inferenceHolder.union(basicContextModel);
		//return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF, inferenceBase);
		
		OntModel rdfsModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);
		rdfsModel.addSubModel(basicContextModel);
		return rdfsModel;
	}
	
	public OntModel getOWLInferenceModel(OntModel basicContextModel) {
		OntModel owlModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		owlModel.addSubModel(basicContextModel);
		
		return owlModel;
	}
	
	/**
	 * Return an extended {@link OntModel} context domain ontology module (with no entailement specification), 
	 * where the SPL, SPIN and SP namespaces have been added to the <code>baseContextModelModule</code>.
	 * @param baseContextModelModule The context domain ontology module to extend with the SPIN imports
	 * @return An {@link OntModel} with no entailement specification, extended with the contents of the 
	 * 		SPL, SPIN and SP ontologies
	 */
	public OntModel ensureSPINImported(OntModel baseContextModelModule) {
		
		// First create a new OWL_MEM OntModelSpec; NO inference should be run on the SPIN ontology
		// suite because for some reason it messes up the parsing process.
		// Then add the model-global instance of the document manager.
		OntModelSpec enrichedModelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
		enrichedModelSpec.setDocumentManager(modelGlobalDocManager);
		
		// Now create a new model with the new specification 
		OntModel enrichedModel = ModelFactory.createOntologyModel(enrichedModelSpec, baseContextModelModule);
		modelGlobalDocManager.loadImport(enrichedModel, SP.BASE_URI);
		modelGlobalDocManager.loadImport(enrichedModel, SPIN.BASE_URI);
		modelGlobalDocManager.loadImport(enrichedModel, SPL.BASE_URI);
		
		//enrichedModel.read(SP.BASE_URI, "TTL");
		//enrichedModel.read(SPIN.BASE_URI, "TTL");
		//enrichedModel.read(SPL.BASE_URI, "TTL");
		
		return enrichedModel;
	}
	
	
	/**
	 * Return an extended Jena {@link Model}, where the SPL, SPIN and SP namespaces have been added to 
	 * the <code>baseModel</code>.
	 * @param baseModel The model to extend with the SPIN imports.
	 * @return An {@link Model} extended with the contents of the SPL, SPIN and SP ontologies.
	 */
	public static Model ensureSPINImported(Model baseModel) {
		Graph baseGraph = baseModel.getGraph();
		MultiUnion spinUnion = JenaUtil.createMultiUnion();
		
		ensureImported(baseGraph, spinUnion, SP.BASE_URI, SP.getModel());
		ensureImported(baseGraph, spinUnion, SPL.BASE_URI, SPL.getModel());
		ensureImported(baseGraph, spinUnion, SPIN.BASE_URI, SPIN.getModel());
		Model unionModel = ModelFactory.createModelForGraph(spinUnion);
		
		return unionModel;
	}
	
	private static void ensureImported(Graph baseGraph, MultiUnion union, String baseURI, Model model) {
		if(!baseGraph.contains(Triple.create(Node.createURI(baseURI), RDF.type.asNode(), OWL.Ontology.asNode()))) {
			union.addGraph(model.getGraph());
		}
	}
	
	
	// Context Model Access
	///////////////////////////////////////////////////////////////////////////////////////
	
	public OntModel getCoreContextModel() {
		return baseContextModelMap.get(DOMAIN_ONT_CORE_URI_KEY);
	}
	
	public OntModel getAnnotationContextModel() {
		return baseContextModelMap.get(DOMAIN_ONT_ANNOTATION_URI_KEY);
	}
	
	public OntModel getConstraintContextModel() {
		return baseContextModelMap.get(DOMAIN_ONT_CONSTRAINT_URI_KEY);
	}
	
	public OntModel getFunctionContextModel() {
		return baseContextModelMap.get(DOMAIN_ONT_FUNCTIONS_URI_KEY);
	}
	
	public OntModel getRuleContextModel() {
		return baseContextModelMap.get(DOMAIN_ONT_RULES_URI_KEY);
	}
}
