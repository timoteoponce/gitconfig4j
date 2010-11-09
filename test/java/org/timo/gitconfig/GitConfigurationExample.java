package org.timo.gitconfig;

import java.io.IOException;
import java.util.logging.Logger;

public class GitConfigurationExample {

	private static final Logger LOG = Logger
	.getLogger(GitConfigurationExample.class.getName());

	public static void main(final String[] args) throws IOException {
		final Configuration config = new GitConfiguration();
		config.load("resources/config-1");

		LOG.info("Configuration content :\n" + config.getTextContent());

		LOG.info("Renaming section 'user' to 'custom '");
		config.renameSection("user", "custom");

		LOG.info("Configuration content :\n" + config.getTextContent());

		LOG.info("Renaming section 'remote.origin' to 'external.source'");
		config.renameSection("remote.origin", "external.source");

		LOG.info("Configuration content :\n" + config.getTextContent());
	}

}
