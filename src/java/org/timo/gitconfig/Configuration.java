package org.timo.gitconfig;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

/**
 * {@link Properties}-like component allowing a configuration structure:
 * 
 * <pre>
 * - Section [0..*] 
 * 	- Variable [0..*]
 * 	- Sub-Section [0..*]
 * 
 *  e.g. 
 * [project 'config'] 
 * 	 	owner = Hugo Ponce
 * 		path = /opt/projects
 * 
 * [merge 'externalTool'] 
 * 		path = /usr/bin
 * 		command = merge
 * 
 * [user] 
 * 		email = timo.slack@gmail.com
 * 		name = Timoteo Ponce
 * </pre>
 * 
 * As a feature, this {@link Configuration} does not expose implementation details 
 * or internal components, giving a simple and generic public API.
 * 
 * @author Timoteo Ponce
 * 
 */
public interface Configuration extends Iterable<Entry<String, String>>{

	/**
	 * @param composedKey
	 * @return
	 */
	String getValue(String composedKey);

	/**
	 * @param sectionName
	 * @param key
	 * @return
	 */
	String getValue(String sectionName, String key);

	/**
	 * @param sectionName
	 * @param subSectionName
	 * @param key
	 * @return
	 */
	String getValue(String sectionName, String subSectionName, String key);

	/**
	 * @param composedKey
	 * @param value
	 */
	void setValue(String composedKey, String value);

	/**
	 * @param sectionName
	 * @param key
	 * @param value
	 */
	void setValue(String sectionName, String key, String value);

	/**
	 * @param sectionName
	 * @param subSectionName
	 * @param key
	 * @param value
	 */
	void setValue(String sectionName, String subSectionName, String key,
			String value);

	Set<String> getKeySet();

	Collection<String> getValues();

	Collection<String> getValues(String composedKey);

	/**
	 * Returns a {@link Map} containing all variables in a format:
	 * 
	 * <pre>
	 * - [variableKey=variableValue] => e.g. "core.path.format = non-iso "
	 * </pre>
	 * 
	 * @return
	 */
	Map<String, String> getVariables();

	/**
	 * Removes a complete configuration section that matches with given
	 * sectionName. If section does not exists, nothings happens.
	 * 
	 * @param sectionName
	 *            name of the section to remove
	 */
	void removeSection(String sectionName);

	/**
	 * Removes a complete configuration sub-section contained in given section.
	 * If the section or the sub-section don't exist, nothing happens.
	 * 
	 * @param sectionName
	 * @param subSectionName
	 */
	void removeSection(String sectionName, String subSectionName);

	/**
	 * Removes given variable from configuration, if the variable or the path to
	 * the variable are missing, nothing happens. e.g.
	 * 
	 * <pre>
	 * - remove("core.path.format") : removes "core.path.format" variable
	 * </pre>
	 * 
	 * @param composedKey
	 *            path of the variable to remove
	 */
	void remove(String composedKey);

	/**
	 * Removes given variable from configuration, if the variable or the path to
	 * the variable are missing, nothing happens. e.g.
	 * 
	 * <pre>
	 * - remove("core","user") : removes "core.user" variable
	 * - remove("core.path","format") : removes "core.path.format" variable
	 * </pre>
	 * 
	 * @param sectionName
	 *            parent section
	 * @param key
	 *            variable to remove
	 */
	void remove(String sectionName, String key);

	/**
	 * Removes given variable from configuration, if the variable or the path to
	 * the variable are missing, nothing happens. e.g.
	 * 
	 * <pre>
	 * - remove("core","path","format") : removes "core.path.format" variable
	 * </pre>
	 * 
	 * @param sectionName
	 *            parent section
	 * @param subSectionName
	 *            variable sub-section
	 * @param key
	 *            variable to remove
	 */
	void remove(String sectionName, String subSectionName, String key);

	/**
	 * Renames a given section or sub-section matching with oldName parameter.
	 * If section or sub-section does not exists, nothing happens. e.g.
	 * 
	 * <pre>
	 * - renameSection("core","main") : renames 'core' sub-section to 'main'
	 * - renameSection("general","principal") : renames 'general' sub-section to 'principal'
	 * - renameSection("core.path","url") : renames 'core.path' sub-section to 'core.url'
	 * - renameSection("application.config","resources"): renames 'application.config' sub-section to 'application.resources'
	 * </pre>
	 * 
	 * @param oldName
	 *            name of section or path of sub-section
	 * @param newName
	 *            new name of section or sub-section
	 */
	void renameSection(String oldName, String newName);

	/**
	 * Renames a sub-section matching with oldName parameter. If section does
	 * not exists, nothing happens. e.g.
	 * 
	 * <pre>
	 * - renameSection("core","path","url") : renames 'core.path' sub-section to 'core.url'
	 * - renameSection("application","config","resources"): renames 'application.config' sub-section to 'application.resources'
	 * </pre>
	 * 
	 * @param sectionName
	 *            parent section
	 * @param oldName
	 *            current name of sub-section
	 * @param newName
	 *            new name for sub-section
	 */
	void renameSection(String sectionName, String oldName, String newName);

	/**
	 * Returns configuration string content formatted as it will be stored in
	 * configuration file.
	 * 
	 * @return text-formatted configuration content
	 */
	String getTextContent();

	/**
	 * Writes configuration values in a given file, this option will override
	 * any previous configuration.
	 * 
	 * @param fileName
	 *            target file for configuration variables
	 */
	void writeFile(String fileName);

	/**
	 * Reads all variables from given file, appending them to current
	 * configuration variables and overriding entries if are present in file.
	 * 
	 * @param fileName
	 *            configuration source file
	 * @throws FileNotFoundException
	 *             if file can't be read
	 */
	void readFromFile(String fileName) throws FileNotFoundException;

	/**
	 * Clear all sections and sub-sections in configuration.
	 */
	void clear();

}
