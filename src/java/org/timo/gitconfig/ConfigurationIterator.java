package org.timo.gitconfig;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * @author Timoteo Ponce
 * 
 */
public class ConfigurationIterator implements Iterator<Entry<String, String>> {

	private final Configuration configuration;

	private final Iterator<String> keySetIterator;

	private String currentKey;

	public ConfigurationIterator(final Configuration configuration) {
		this.configuration = configuration;
		this.keySetIterator = this.configuration.getKeySet().iterator();
	}

	@Override
	public boolean hasNext() {
		return keySetIterator.hasNext();
	}

	@Override
	public Entry<String, String> next() {
		currentKey = keySetIterator.next();
		final String value = configuration.getValue(currentKey);
		return new AbstractMap.SimpleEntry(currentKey, value);
	}

	@Override
	public void remove() {
		if (currentKey != null && !currentKey.isEmpty()) {
			configuration.remove(currentKey);
			currentKey = null;
		}
	}

	public static void main(final String[] args) {
		final Configuration config = new GitConfiguration();
		config.setValue("main.test1", "testValue1");
		config.setValue("main.test2", "testValue2");
		config.setValue("main.sub.test1", "testValue2");
		config.setValue("main.sub.test2", "testValue2");
		config.setValue("core.test1", "testValue3");
		config.setValue("core.test2", "testValue4");
		config.setValue("core.test3", "testValue5");
		config.setValue("core.test4", "testValue6");

		int i = 0;
		for (final Iterator<Entry<String, String>> it = config.iterator(); it
		.hasNext();) {
			final Entry<String, String> entry = it.next();
			i++;
			if (i == 1) {
				it.remove();
			}
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}

		System.out.println("====================================");
		for (final Iterator<Entry<String, String>> it = config.iterator(); it
		.hasNext();) {
			final Entry<String, String> entry = it.next();
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}

	}

}
