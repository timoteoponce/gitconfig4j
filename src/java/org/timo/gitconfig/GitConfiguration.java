package org.timo.gitconfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitConfiguration implements Configuration {

	private static final Logger LOG = Logger.getLogger(GitConfiguration.class
			.getName());

	private final Map<String, RootSection> rootSectionsMap = new HashMap<String, RootSection>();

	private static final Pattern SECTION_PATTERN = Pattern
	.compile("(\\w)*[^\\s'\"\\[\\]]");

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#getKeySet()
	 */
	@Override
	public Set<String> getKeySet() {
		final Set<String> keySet = new HashSet<String>();
		for (final Entry<String, RootSection> entry : rootSectionsMap
				.entrySet()) {
			keySet.addAll(entry.getValue().getKeySet());
		}
		return keySet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#getTextContent()
	 */
	@Override
	public String getTextContent() {
		final StringBuilder builder = new StringBuilder();
		for (final Entry<String, RootSection> entry : rootSectionsMap
				.entrySet()) {
			appendSection(builder, entry.getValue());
		}
		return builder.toString();
	}

	/**
	 * @param builder
	 * @param rootSection
	 */
	private void appendSection(final StringBuilder builder,
			final RootSection rootSection) {
		if (!rootSection.isEmpty()) {
			builder.append("[" + rootSection.getName() + "] \n");
		}
		appendVariables(builder, rootSection);
		appendSubSections(builder, rootSection);
	}

	/**
	 * @param builder
	 * @param section
	 */
	private void appendVariables(final StringBuilder builder,
			final Section section) {
		for (final Entry<String, String> entry : section.getVariables()
				.entrySet()) {
			builder.append("\t\t " + entry.getKey() + " = " + entry.getValue()
					+ "\n");
		}
		if (section.getVariables().size() > 0) {
			builder.append("\n");
		}
	}

	/**
	 * @param builder
	 * @param rootSection
	 */
	private void appendSubSections(final StringBuilder builder,
			final RootSection rootSection) {
		for (final Section section : rootSection.getSections()) {
			builder.append("[" + rootSection.getName() + " '"
					+ section.getName() + "'] \n");
			appendVariables(builder, section);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#getValue(java.lang.String)
	 */
	@Override
	public String getValue(final String composedKey) {
		final String[] keys = splitKeys(composedKey);
		if (keys.length < 2) {
			throw new IllegalArgumentException("Invalid variable key : "
					+ composedKey);
		}
		final String rootSection = keys[0];
		if (keys.length > 2) {
			return getValue(rootSection, keys[1], keys[2]);
		} else {
			return getValue(rootSection, keys[1]);
		}
	}

	/**
	 * @param composedKey
	 * @return
	 */
	private String[] splitKeys(final String composedKey) {
		return composedKey.split("\\.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#getValue(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public String getValue(final String sectionName,
			final String subSectionName, final String key) {
		final RootSection rootSection = rootSectionsMap.get(sectionName);
		String value = "";
		if (rootSection != null
				&& rootSection.getSection(subSectionName) != null) {
			final Section subSection = rootSection.getSection(subSectionName);
			value = subSection.getVariable(key);
		}

		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#getValue(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String getValue(final String sectionName, final String key) {
		final RootSection rootSection = rootSectionsMap.get(sectionName);
		String value = "";
		if (rootSection != null) {
			value = rootSection.getVariable(key);
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#getValues()
	 */
	@Override
	public Collection<String> getValues() {
		final Collection<String> values = new ArrayList<String>();
		for (final RootSection rootSection : rootSectionsMap.values()) {
			values.addAll(rootSection.getValues());
		}
		return values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#getValues(java.lang.String)
	 */
	@Override
	public Collection<String> getValues(final String composedKey) {
		final Collection<String> values = new ArrayList<String>();
		for (final RootSection rootSection : rootSectionsMap.values()) {
			values.addAll(rootSection.getValues());
		}
		return values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#readFromFile(java.lang.String)
	 */
	@Override
	public void readFromFile(final String fileName)
	throws FileNotFoundException {
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(fileName);
			bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					final Section section = readSection(line);
					readVariables(bufferedReader, section);
				}
			}
		} catch (final IOException e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
					fileReader.close();
				}
			} catch (final IOException e) {
				LOG.info("Swallowing exception : " + e.getMessage());
			}
		}
	}

	/**
	 * @param bufferedReader
	 * @param section
	 * @throws IOException
	 */
	private void readVariables(final BufferedReader bufferedReader,
			final Section section) throws IOException {
		final StringBuilder variablesBuffer = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0 || line.startsWith("[")) {
				break;
			}
			variablesBuffer.append(line + "\n");
		}
		// variable = value
		final StringTokenizer tokenizer = new StringTokenizer(variablesBuffer
				.toString(), "\n=");

		while (tokenizer.hasMoreTokens()) {
			final String key = tokenizer.nextToken().trim();
			section.setVariable(key, tokenizer.nextToken().trim());
		}
	}

	/**
	 * @param line
	 * @return
	 */
	private Section readSection(final String line) {
		LOG.info("Reading section from line : " + line);
		if (line.startsWith("[") && line.endsWith("]")) {
			final Matcher matcher = SECTION_PATTERN.matcher(line);
			matcher.find();// find the first match
			final String sectionName = matcher.group().trim();

			final boolean isSubSection = matcher.find();
			// [ sectionName 'subSection' ]
			Section section;
			if (isSubSection) {
				final String subSection = matcher.group().trim();
				LOG.info("Reading subSection: " + sectionName + "->"
						+ subSection);
				final RootSection rootSection = getOrCreateSection(sectionName);
				section = rootSection.getOrCreateSection(subSection);
			} else {
				LOG.info("Reading section: " + sectionName);
				section = getOrCreateSection(sectionName);
			}
			return section;
		} else {
			throw new IllegalArgumentException(
					"Unreadable section declaration [ sectionName *'subSectionName'] :"
					+ line);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#remove(java.lang.String)
	 */
	@Override
	public void remove(final String composedKey) {
		final String[] keys = splitKeys(composedKey);
		if (keys.length == 2) {
			remove(keys[0], keys[1]);
		} else {
			remove(keys[0], keys[1], keys[2]);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#remove(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void remove(final String sectionName, final String key) {
		final String[] keys = splitKeys(sectionName);
		if (keys.length > 1) {
			remove(keys[0], keys[1], key);
		} else {
			rootSectionsMap.remove(sectionName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#remove(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void remove(final String sectionName, final String subSectionName,
			final String key) {
		final RootSection rootSection = rootSectionsMap.get(sectionName);
		if (rootSection != null
				&& rootSection.getSection(subSectionName) != null) {
			final Section section = rootSection.getSection(subSectionName);
			section.removeVariable(key);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#removeSection(java.lang.String)
	 */
	@Override
	public void removeSection(final String sectionName) {
		final String[] keys = splitKeys(sectionName);
		if (keys.length == 2) {
			removeSection(keys[0], keys[1]);
		} else {
			rootSectionsMap.remove(sectionName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#removeSection(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void removeSection(final String sectionName, final String subSection) {
		final RootSection rootSection = rootSectionsMap.get(sectionName);
		rootSection.removeSection(subSection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#renameSection(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void renameSection(final String oldName, final String newName) {
		final String[] keys = splitKeys(oldName);
		if (keys.length > 1) {
			renameSection(keys[0], keys[1], newName);
		} else {
			final RootSection rootSection = rootSectionsMap.remove(oldName);
			if (rootSection != null) {
				rootSection.setName(newName);
				LOG.info("Renaming section '" + oldName + "' to '" + newName
						+ "'");
				rootSectionsMap.put(newName, rootSection);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#renameSection(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void renameSection(final String sectionName, final String oldName,
			final String newName) {
		final RootSection rootSection = rootSectionsMap.get(sectionName);
		if (rootSection != null && rootSection.getSection(oldName) != null) {
			LOG.info("Renaming sub-section '" + sectionName + "." + oldName
					+ "' to '" + newName + "'");
			final Section section = rootSection.removeSection(oldName);
			section.setName(newName);
			rootSection.setSection(section);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#setValue(java.lang.String,
	 * java.lang.String)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#setValue(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void setValue(final String sectionName, final String key,
			final String value) {
		if (value == null) {
			throw new NullPointerException("Null values are not allowed");
		}
		final RootSection rootSection = getOrCreateSection(sectionName);
		rootSection.setVariable(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#setValue(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void setValue(final String sectionName, final String subSectionName,
			final String key, final String value) {
		if (value == null) {
			throw new NullPointerException("Null values are not allowed");
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.timo.gitconfig.Configuration#writeFile(java.lang.String)
	 */
	@Override
	public void writeFile(final String fileName) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(fileName);
			fileWriter.append(getTextContent());
		} catch (final IOException e) {
			LOG.info("Swallowing IO exception : " + e.getMessage());
		} finally {
			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (final IOException e) {
				LOG.info("Swallowing IO exception : " + e.getMessage());
			}
		}
	}

	@Override
	public Map<String, String> getVariables() {
		final Map<String, String> variables = new HashMap<String, String>();
		for (final RootSection rootSection : rootSectionsMap.values()) {
			variables.putAll(rootSection.getVariables());
		}
		return variables;
	}

	@Override
	public void clear() {
		this.rootSectionsMap.clear();
	}

	@Override
	public Iterator<Entry<String, String>> iterator() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public static void main(final String[] args) throws FileNotFoundException {
		final Configuration config = new GitConfiguration();
		config.setValue("user.name", "Timoteo Ponce");
		config.setValue("user.email", "timo.slack@gmail.com");
		config.setValue("merge.tool.command", "merge");
		config.setValue("merge.tool.path", "/usr/bin");
		config.setValue("source.config.path", "/opt/projects");
		config.setValue("source.config.owner", "Hugo Ponce");

		LOG.info(config.getTextContent());
		config.renameSection("source", "project");
		config.renameSection("merge.tool", "externalTool");
		LOG.info(config.getTextContent());

		LOG.info(config.getKeySet().toString());

		config.writeFile("resources/config-2");

		config.clear();
		config.readFromFile("resources/config-1");
		LOG.info(config.getTextContent());

		LOG.info("keySet :" + config.getKeySet());

		LOG.info("values :" + config.getValues());

		LOG.info("variables :" + config.getVariables());
	}

}
