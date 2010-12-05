package org.timo.gitconfig;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Timoteo Ponce
 * 
 */
public class GitConfigurationTest {
	
	private static final Logger LOG = Logger.getLogger(GitConfigurationTest.class.getName());

	@Test
	public void getInvalidValue() {
		final Configuration config = new GitConfiguration();
		final String testValue = "staticValue";
		config.setValue("main.testKey", testValue);

		Assert.assertEquals("", config.getValue("core", "testKey"));
	}

	@Test
	public void getValue() {
		final Configuration config = new GitConfiguration();
		final String testValue = "staticValue";
		config.setValue("main.testKey", testValue);

		Assert.assertEquals(testValue, config.getValue("main", "testKey"));
	}

	@Test
	public void getPathValue() {
		final Configuration config = new GitConfiguration();
		final String testValue = "staticValue";
		config.setValue("main.testKey", testValue);

		Assert.assertEquals(testValue, config.getValue("main.testKey"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidKey() {
		final Configuration config = new GitConfiguration();
		final String testValue = "staticValue";
		config.setValue("main", testValue);
	}

	@Test
	public void setSubSectionValue() {
		final Configuration config = new GitConfiguration();
		config.setValue("main", "test", "testKey", "testValue");

		Assert.assertEquals("testValue", config.getValue("main.test.testKey"));
	}

	@Test
	public void removeVariable(){
		final Configuration config = new GitConfiguration();
		config.setValue("main.test1", "testValue1");
		config.setValue("main.sub.key", "testValue2");
		config.setValue("main.sub.removeKey", "testValue2");

		config.remove("main.test1");
		config.remove("main", "sub", "removeKey");

		Assert.assertEquals("", config.getValue("main.test1"));
		Assert.assertEquals("", config.getValue("main.sub.removeKey"));
	}

	@Test
	public void removeSection() {
		final Configuration config = new GitConfiguration();
		config.setValue("main.test1", "testValue1");
		config.setValue("main.test2", "testValue2");
		config.setValue("core.test1", "testValue3");
		config.setValue("core.test2", "testValue4");
		config.setValue("core.test3", "testValue5");
		config.setValue("core.test4", "testValue6");

		config.removeSection("core");

		Assert.assertEquals("", config.getValue("core.test1"));
		Assert.assertEquals("", config.getValue("core.test2"));
		Assert.assertEquals("", config.getValue("core.test3"));
		Assert.assertEquals("", config.getValue("core.test4"));

		Assert.assertEquals("testValue1", config.getValue("main.test1"));
		Assert.assertEquals("testValue2", config.getValue("main.test2"));

	}

	@Test
	public void renameSection() {
		final Configuration config = new GitConfiguration();
		config.setValue("main.test1", "testValue1");
		config.setValue("main.test2", "testValue2");
		config.setValue("core.test1", "testValue3");
		config.setValue("core.test2", "testValue4");
		config.setValue("core.test3", "testValue5");
		config.setValue("core.test4", "testValue6");

		config.renameSection("core", "renamedCore");

		Assert.assertEquals("", config.getValue("core.test1"));
		Assert.assertEquals("", config.getValue("core.test2"));
		Assert.assertEquals("", config.getValue("core.test3"));
		Assert.assertEquals("", config.getValue("core.test4"));

		Assert.assertEquals("testValue3", config.getValue("renamedCore.test1"));
		Assert.assertEquals("testValue4", config.getValue("renamedCore.test2"));
		Assert.assertEquals("testValue5", config.getValue("renamedCore.test3"));
		Assert.assertEquals("testValue6", config.getValue("renamedCore.test4"));

	}

	@Test
	public void getFilteredVariables() {
		final Configuration config = new GitConfiguration();
		config.setValue("main.test1", "testValue1");
		config.setValue("main.test2", "testValue2");
		config.setValue("main.sub.test1", "testValue2");
		config.setValue("main.sub.test2", "testValue2");
		config.setValue("core.test1", "testValue3");
		config.setValue("core.test2", "testValue4");
		config.setValue("core.test3", "testValue5");
		config.setValue("core.test4", "testValue6");

		final Map<String, String> filteredVars = config.getVariables("main");
		Assert.assertTrue(filteredVars.containsKey("main.test1"));
		Assert.assertTrue(filteredVars.containsKey("main.test2"));
		Assert.assertTrue(filteredVars.containsKey("main.sub.test1"));
		Assert.assertTrue(filteredVars.containsKey("main.sub.test2"));
		Assert.assertFalse(filteredVars.containsKey("core.test1"));
	}
	
	@Test
	public void iterateConfiguration(){
		final Configuration config = new GitConfiguration();
		config.setValue("main.test1", "testValue1");
		config.setValue("main.test2", "testValue2");
		config.setValue("main.sub.test1", "testValue2");
		config.setValue("main.sub.test2", "testValue2");
		config.setValue("core.test1", "testValue3");
		config.setValue("core.test2", "testValue4");
		config.setValue("core.test3", "testValue5");
		config.setValue("core.test4", "testValue6");
		
		for(Entry<String, String> variable: config){
			LOG.info("Variable "+variable.getKey() + " = "+variable.getValue());			
		}
	}

}
