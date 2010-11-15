package org.timo.gitconfig;

import java.io.IOException;
import java.util.logging.Logger;

public class GitConfigurationExample {

	private static final Logger LOG = Logger
			.getLogger(GitConfigurationExample.class.getName());

	/**
	 * Test file contents:
	 * 
	 * <pre>
	 * # Comment 1
	 * ; comment 2
	 * [core]  
	 *      autocrlf = false
	 * 		excludesfile = /Users/timoteo/.gitignore
	 * 		; inner comment
	 * 		editor = emacs		
	 * 	
	 * 	# end comment
	 * [alias]
	 *      last = cat-file commit HEAD
	 * 		
	 * [user]
	 *      name = Timoteo Ponce
	 * 		email = timoteo.ponce@swissbytes.ch
	 * 
	 * [color]
	 *      diff = auto
	 * 		status = auto
	 * 		branch = auto
	 * 		interactive = auto		
	 * 
	 * [remote "origin"]
	 *      url = http://git.kernel.org/pub/scm/git/git.git
	 *         ; inner comment 2
	 * 		fetch = +refs/heads/*:refs/remotes/origin/*
	 * </pre>
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final Configuration config = new GitConfiguration();
		// load configuration fike
		config.load("resources/config-1");
		// show its content
		LOG.info("Configuration content :\n" + config.getTextContent());
		
		// get some variables
		LOG.info("color.branch=" + config.getValue("color.branch") );
		LOG.info("user.name=" + config.getValue("user.name") );
		LOG.info("remote.origin.url=" + config.getValue("remote.origin.url") );
		
		// create a new section
		config.setValue("mysection.editor", "emacs");
		config.setValue("mysection.emacs.command", "/usr/bin/emacs");
		// show updated content
		LOG.info("Configuration content :\n" + config.getTextContent());

		LOG.info("Renaming section 'user' to 'custom '");
		config.renameSection("user", "custom");

		LOG.info("Configuration content :\n" + config.getTextContent());

		LOG.info("Renaming section 'remote.origin' to 'external.source'");
		config.renameSection("remote.origin", "external.source");

		LOG.info("Configuration content :\n" + config.getTextContent());
	}

}
