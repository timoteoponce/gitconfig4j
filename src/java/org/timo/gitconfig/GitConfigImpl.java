package org.timo.gitconfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class GitConfigImpl implements GitConfig {

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
			builder.append("[ " + section.getName() + " ] \n");
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
			builder.append("[ " + rootSection.getName() + " '"
					+ section.getName() + "' ] \n");
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
	public void readFromFile(final String filename) {
		// TODO Auto-generated method stub
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

	public static void main(final String[] args) {
		GitConfig config = new GitConfigImpl();
		config.setValue("user.name", "Timoteo Ponce");
		config.setValue("user.email", "timo.slack@gmail.com");
		config.setValue("merge.tool.command", "merge");
		config.setValue("merge.tool.path", "/usr/bin");
		config.setValue("source.config.path", "/opt/projects");
		config.setValue("source.config.owner", "Hugo Ponce");

		System.out.println(config.getTextContent());
	}

}
