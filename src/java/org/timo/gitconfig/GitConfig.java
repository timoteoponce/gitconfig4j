package org.timo.gitconfig;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Timoteo Ponce
 * 
 */
public interface GitConfig{

	String getValue(String composedKey);

	String getValue(String section, String key);

	void setValue(String composedKey, String value);

	void setValue(String ection, String key, String value);

	Set<String> getKeySet();

	Collection<String> getValues();

	Collection<String> getValues(String composedKey);
	
	Map<String,String> getVariables();

	void removeSection(String sectionName);

	void remove(String composedKey);

	void renameSection(String oldName, String newName);

	String getTextContent();

	void writeFile(String fileName);

	void readFromFile(String fileName)throws FileNotFoundException;
	
	void clear();

}
