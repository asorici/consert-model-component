package org.aimas.ami.contextrep.utils;

import java.io.InputStream;
import com.hp.hpl.jena.util.Locator;

public interface ResourceManager {
	
	public boolean hasResource(String name);
	
	public InputStream getResourceAsStream(String name);
	
	public Locator getResourceLocator();
}
