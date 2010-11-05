package org.timo.gitconfig;

import java.util.Collection;
import java.util.HashMap;
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

	@Override
	public Set<String> getKeySet() {
		final Set<String> keySet = super.getKeySet();
		for (Entry<String, Section> entry : sectionMap.entrySet()) {
			Set<String> sectionKeySet = entry.getValue().getKeySet();
			for (String sectionKey : sectionKeySet) {
				keySet.add(getName() + "." + sectionKey);
			}
		}
		return keySet;
	}

	public Collection<Section> getSections() {
		return sectionMap.values();
	}
}
