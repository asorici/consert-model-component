package org.aimas.ami.contextrep.utils;

import java.io.InputStream;

import com.hp.hpl.jena.util.Locator;
import com.hp.hpl.jena.util.LocatorClassLoader;

public class ClassLoaderResourceManager implements ResourceManager {
	private static Locator resourceLocator;
	private ClassLoader resourceLoader;
	
	/**
	 * Create a new ClassLoaderResourceManager by specifying the classloader that is used to 
	 * find the required resources (coniguration files, ontology files).
	 * 
	 * @param engineResourceLoader
	 */
	public ClassLoaderResourceManager(ClassLoader engineResourceLoader) {
		this.resourceLoader = engineResourceLoader;
	}
	
	/**
	 * Create a new EngineResourceManager where the resourceLoader is considered to be the one
	 * that loaded this class.
	 */
	public ClassLoaderResourceManager() {
		this(ClassLoaderResourceManager.class.getClassLoader());
	}
	
	
	public ClassLoader getLoader() {
	    return resourceLoader;
    }
	
	@Override
	public InputStream getResourceAsStream(String name) {
		// try the engineResourceLoader first
		return resourceLoader.getResourceAsStream(name);
	}

	@Override
    public Locator getResourceLocator() {
	    if (resourceLocator == null) {
	    	resourceLocator = new LocatorClassLoader(resourceLoader); 
	    }
		
	    return resourceLocator;
    }

	@Override
    public boolean hasResource(String name) {
	    return resourceLoader.getResource(name) != null;
    }
	
}
