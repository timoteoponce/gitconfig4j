package org.timo.gitconfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Timoteo Ponce
 * 
 */
public class Section {

	private String name;

	private final Map<String, String> variables = new HashMap<String, String>();

	public Section(final String name) {
		this.name = name;
	}

	public void setVariable(final String key, final String value) {
		variables.put(key, value);
	}

	public String getVariable(final String key) {
		return variables.get(key);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getVariables() {
		return variables;
	}

	public void removeVariable(String key) {
		variables.remove(key);
	}

	public Set<String> getKeySet() {
		final Set<String> keySet = new HashSet<String>();
		for (Entry<String, String> entry : variables.entrySet()) {
			keySet.add(name + "." + entry.getKey());
		}
		return keySet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Section other = (Section) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
