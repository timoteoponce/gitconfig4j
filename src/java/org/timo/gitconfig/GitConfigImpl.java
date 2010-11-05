package org.timo.gitconfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitConfigImpl implements GitConfig {

	private static final Logger LOG = Logger.getLogger(GitConfigImpl.class
			.getName());

	private final Map<String, RootSection> rootSectionsMap = new HashMap<String, RootSection>();

	@Override
	public Set<String> getKeySet() {
		final Set<String> keySet = new HashSet<String>();
		for (Entry<String, RootSection> entry : rootSectionsMap.entrySet()) {
			keySet.addAll(entry.getValue().getKeySet());
		}
		return keySet;
	}

	@Override
	public String getTextContent() {
		final StringBuilder builder = new StringBuilder();
		for (Entry<String, RootSection> entry : rootSectionsMap.entrySet()) {
			appendSection(builder, entry.getValue());
		}
		return builder.toString();
	}

	private void appendSection(final StringBuilder builder,
			final RootSection section) {
		if (!section.getVariables().isEmpty()) {
			builder.append("[" + section.getName() + "] \n");
		}
		appendVariables(builder, section);
		appendSubSections(builder, section);
	}

	private void appendVariables(final StringBuilder builder,
			final Section section) {
		for (Entry<String, String> entry : section.getVariables().entrySet()) {
			builder.append("\t " + entry.getKey() + " = " + entry.getValue()
					+ "\n");
		}
	}

	private void appendSubSections(final StringBuilder builder,
			final RootSection rootSection) {
		for (Section section : rootSection.getSections()) {
			builder.append("[" + rootSection.getName() + " '"
					+ section.getName() + "'] \n");
			appendVariables(builder, section);
		}
	}

	@Override
	public String getValue(final String composedKey) {
		final String[] keys = splitKeys(composedKey);
		if (keys.length < 2) {
			throw new IllegalArgumentException("Invalid variable key : "
					+ composedKey);
		}
		final String section = keys[0];
		if (keys.length > 2) {
			return getValue(section, keys[1], keys[2]);
		} else {
			return getValue(section, keys[1]);
		}
	}

	private String[] splitKeys(final String composedKey) {
		return composedKey.split("\\.");
	}

	private String getValue(final String sectionName,
			final String subSectionName, final String key) {
		RootSection rootSection = rootSectionsMap.get(sectionName);
		Section subSection = rootSection.getSection(subSectionName);
		return subSection.getVariable(key);
	}

	@Override
	public String getValue(final String sectionName, final String key) {
		RootSection rootSection = rootSectionsMap.get(sectionName);
		return rootSection.getVariable(key);
	}

	@Override
	public Collection<String> getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getValues(final String composedKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readFromFile(final String fileName)
			throws FileNotFoundException {
		FileReader fileReader;
		BufferedReader bufferedReader;
		try {
			fileReader = new FileReader(fileName);
			bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					final Section section = readSection(line);
					readVariables(bufferedReader,section);
				}
			}
		} catch (IOException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void readVariables(BufferedReader bufferedReader, Section section) throws IOException {
		final StringBuilder variablesBuffer = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim();
			if(line.length() == 0 || line.startsWith("[")){
				break;				
			}
			variablesBuffer.append(line+"\n");
		}		
		StringTokenizer tokenizer = new StringTokenizer(variablesBuffer.toString(),"\n=");
		// variable = value
		while(tokenizer.hasMoreTokens()){
			final String key = tokenizer.nextToken().trim();
			section.setVariable(key, tokenizer.nextToken().trim());
		}
	}

	private Section readSection(final String line) {
		LOG.info("Reading section from line : "+line);
		if (line.startsWith("[") && line.endsWith("]")) {
			String sectionName = line.substring(1,line.length()-1).trim();			
			final boolean isSubSection = sectionName.contains("'");
			// [ sectionName 'subSection' ]
			Section section;
			if(isSubSection){				
				LOG.info("Reading subSection: "+sectionName);
				RootSection rootSection = getOrCreateSection(sectionName.substring(0,sectionName.indexOf("'")-1).trim());
				section = rootSection.getOrCreateSection(sectionName.substring(sectionName.indexOf("'"),sectionName.length()).trim());
			}else{
				LOG.info("Reading section: "+sectionName);
				section = getOrCreateSection(sectionName);
			}
			return section;
		} else {
			throw new IllegalArgumentException(
					"Unreadable section declaration [ sectionName *'subSectionName'] :"
							+ line);
		}
	}

	@Override
	public void remove(final String composedKey) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSection(final String sectionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void renameSection(final String oldName, final String newName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(final String composedKey, final String value) {
		final String[] keys = splitKeys(composedKey);
		if (keys.length < 2) {
			throw new IllegalArgumentException("Invalid variable key : "
					+ keys.length);
		}
		if (keys.length > 2) {
			setValue(keys[0], keys[1], keys[2], value);
		} else {
			setValue(keys[0], keys[1], value);
		}
	}

	public void setValue(final String sectionName, final String subSectionName,
			final String key, final String value) {
		final RootSection rootSection = getOrCreateSection(sectionName);
		final Section subSection = rootSection
				.getOrCreateSection(subSectionName);
		subSection.setVariable(key, value);
	}

	private RootSection getOrCreateSection(final String sectionName) {
		RootSection rootSection = rootSectionsMap.get(sectionName);
		if (rootSection == null) {
			rootSection = new RootSection(sectionName);
			rootSectionsMap.put(sectionName, rootSection);
		}
		return rootSection;
	}

	@Override
	public void setValue(final String sectionName, final String key,
			final String value) {
		final RootSection rootSection = getOrCreateSection(sectionName);
		rootSection.setVariable(key, value);
	}

	@Override
	public void writeFile(final String filename) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> getVariables() {
		return null;
	}

	@Override
	public void clear() {
		this.rootSectionsMap.clear();
	}

	public static void main(final String[] args) throws FileNotFoundException {
		GitConfig config = new GitConfigImpl();
		config.setValue("user.name", "Timoteo Ponce");
		config.setValue("user.email", "timo.slack@gmail.com");
		config.setValue("merge.tool.command", "merge");
		config.setValue("merge.tool.path", "/usr/bin");
		config.setValue("source.config.path", "/opt/projects");
		config.setValue("source.config.owner", "Hugo Ponce");

		final String text = config.getTextContent();
		LOG.info(text);

		StringTokenizer tokenizer = new StringTokenizer(text,"' \t\n\r\f");
		while (tokenizer.hasMoreTokens()) {
			LOG.info("token: " + tokenizer.nextToken());
		}

		LOG.info(config.getKeySet().toString());
		
		config.clear();
		config.readFromFile("resources/config-1");
		LOG.info(config.getTextContent());
	}

}
