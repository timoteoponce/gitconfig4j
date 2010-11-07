package org.timo.gitconfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Timoteo Ponce
 * 
 */
class RootSection extends Section {

	private final Map<String, Section> sectionMap = new HashMap<String, Section>();

	public RootSection(final String name) {
		super(name);
	}

	public void setSection(final Section section) {
		if (section instanceof RootSection) {
			throw new IllegalArgumentException(
			"Nested RootSections are not supported.");
		}
		sectionMap.put(section.getName(), section);
	}

	public Section getSection(final String name) {
		return sectionMap.get(name);
	}

	public Section getOrCreateSection(final String name) {
		Section section = sectionMap.get(name);
		if (section == null) {
			section = new Section(name);
			sectionMap.put(name, section);
		}
		return section;
	}

	public Section removeSection(final String subSectionName) {
		return sectionMap.remove(subSectionName);
	}

	@Override
	public Set<String> getKeySet() {
		final Set<String> keySet = getLocalizedKeySet();
		for (Section subSection : sectionMap.values()) {
			Set<String> sectionKeySet = subSection.getKeySet();
			for (String sectionKey : sectionKeySet) {
				keySet.add(getName() + "." + subSection.getName() + "."
						+ sectionKey);
			}
		}
		return keySet;
	}

	private Set<String> getLocalizedKeySet() {
		final Set<String> keySet = new HashSet<String>();
		for (String key : super.getKeySet()) {
			keySet.add(getName() + "." + key);
		}
		return keySet;
	}

	@Override
	public Map<String, String> getVariables() {
		final Map<String, String> variables = getLocalizedVariables();
		for (Section subSection : sectionMap.values()) {
			for (Entry<String, String> subVar : subSection.getVariables()
					.entrySet()) {
				variables.put(getName() + "." + subSection.getName() + "."
						+ subVar.getKey(), subVar.getValue());
			}
		}
		return variables;
	}

	private Map<String, String> getLocalizedVariables() {
		final Map<String, String> variables = new HashMap<String, String>();
		for (Entry<String, String> var : super.getVariables().entrySet()) {
			variables.put(getName() + "." + var.getKey(), var.getValue());
		}
		return variables;
	}

	@Override
	public Collection<String> getValues() {
		final Collection<String> values = super.getValues();
		for (Section subSection : sectionMap.values()) {
			values.addAll(subSection.getValues());
		}
		return values;
	}

	public Collection<Section> getSections() {
		return sectionMap.values();
	}

}
