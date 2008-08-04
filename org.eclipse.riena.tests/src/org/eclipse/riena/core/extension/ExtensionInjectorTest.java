/*******************************************************************************
 * Copyright (c) 2007, 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.core.extension;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.internal.core.config.ConfigSymbolReplace;
import org.eclipse.riena.internal.tests.Activator;
import org.eclipse.riena.tests.RienaTestCase;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ConfigurationPlugin;

/**
 * 
 */
public class ExtensionInjectorTest extends RienaTestCase {

	{
		// If you want printing remove the comments below:
		setPrint(true);
	}

	/**
	 * In some test we have to sleep because of asynchronous processing.
	 */
	private static final int SLEEP_TIME = 500;

	public void testConstructorConstraints() {
		printTestName();
		try {
			Inject.extension(null);
			fail("That should not happen!");
		} catch (RuntimeException e) {
			// ok
		}
	}

	public void testUseTypeConstraints() {
		printTestName();
		try {
			Inject.extension("id").useType(null);
			fail("That should not happen!");
		} catch (RuntimeException e) {
			// ok
		}
		try {
			Inject.extension("id").useType(IData.class).useType(IData.class);
			fail("That should not happen!");
		} catch (RuntimeException e) {
			// ok
		}
		try {
			Inject.extension("id").useType(String.class);
			fail("That should not happen!");
		} catch (RuntimeException e) {
			// ok
		}
	}

	public void testExpectingConstraints() {
		printTestName();
		try {
			Inject.extension("id").expectingMinMax(2, 1);
			fail("That should not happen!");
		} catch (RuntimeException e) {
			// ok
		}
		try {
			Inject.extension("id").expectingMinMax(-1, 0);
			fail("That should not happen!");
		} catch (RuntimeException e) {
			// ok
		}
		try {
			Inject.extension("id").expectingMinMax(0, 0);
			fail("That should not happen!");
		} catch (RuntimeException e) {
			// ok
		}
	}

	public void testInjectIntoConstraints() {
		printTestName();
		try {
			Inject.extension("id").into(null);
			fail("That should not happen!");
		} catch (RuntimeException e) {
			// ok
		}
	}

	public void testWithKnownTypeAndMultipleData() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext1.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext2.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext3.xml");
		ConfigurableThingMultipleData target = new ConfigurableThingMultipleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").useType(IData.class).into(target).andStart(
				getContext());
		assertEquals(3, target.getData().length);
		removeExtension("core.test.extpoint.id1");
		removeExtension("core.test.extpoint.id2");
		removeExtension("core.test.extpoint.id3");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testWithUnknownTypeAndMultipleData() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext1.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext2.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext3.xml");
		ConfigurableThingMultipleData target = new ConfigurableThingMultipleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").into(target).andStart(getContext());
		assertEquals(3, target.getData().length);
		removeExtension("core.test.extpoint.id1");
		removeExtension("core.test.extpoint.id2");
		removeExtension("core.test.extpoint.id3");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testWithKnownTypeAndSingleData() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext1.xml");
		ConfigurableThingSingleData target = new ConfigurableThingSingleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").useType(IData.class).expectingExactly(1)
				.into(target).bind("configure").andStart(getContext());
		assertNotNull(target.getData());
		assertTrue(target.getData().getValue().contains("And Now for Something Completely Different!"));
		assertTrue(target.getData().getRequired());
		assertTrue(target.getData().isRequired());
		assertEquals("test1", target.getData().getText());
		assertEquals(String.class, target.getData().createObjectType().getClass());
		removeExtension("core.test.extpoint.id1");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testNonCachingOfCreateWithKnownTypeAndSingleData() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext1.xml");
		ConfigurableThingSingleData target = new ConfigurableThingSingleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").useType(IData.class).expectingExactly(1)
				.into(target).bind("configure").andStart(getContext());
		assertNotNull(target.getData());
		String created1 = (String) target.getData().createObjectType();
		String created2 = (String) target.getData().createObjectType();
		assertNotSame(created1, created2);

		removeExtension("core.test.extpoint.id1");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testWithUnknownTypeAndSingleData() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext2.xml");
		ConfigurableThingSingleData target = new ConfigurableThingSingleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").expectingExactly(1).into(target).bind(
				"configure").andStart(getContext());
		assertNotNull(target.getData());
		assertFalse(target.getData().getRequired());
		assertFalse(target.getData().isRequired());
		assertEquals("test2", target.getData().getText());
		assertEquals(HashMap.class, target.getData().createObjectType().getClass());
		removeExtension("core.test.extpoint.id2");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testCoerceWithUnknownTypeAndSingleData() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext4.xml");
		ConfigurableThingSingleData target = new ConfigurableThingSingleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").expectingExactly(1).into(target).bind(
				"configure").andStart(getContext());
		assertNotNull(target.getData());
		assertTrue(target.getData().getRequired());
		assertEquals("test4", target.getData().getText());
		assertEquals(ArrayList.class, target.getData().createObjectType().getClass());
		assertEquals(0x2A, target.getData().getJustAByte());
		assertEquals(2.7182818284590452d, target.getData().getDoubleNumber());
		assertEquals(3.14159f, target.getData().getFloatNumber());
		assertEquals(123, target.getData().getIntegerNumber());
		assertEquals(1234567890l, target.getData().getLongNumber());
		assertEquals(1, target.getData().getShortNumber());
		assertEquals('#', target.getData().getDelimCharacter());

		removeExtension("core.test.extpoint.id4");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	/**
	 * This test is brittle because it deals with asynchronous events.
	 * 
	 * @throws InterruptedException
	 */
	public void testTrackingWithKnownTypeAndMultipleData() throws InterruptedException {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		ConfigurableThingMultipleData target = new ConfigurableThingMultipleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").useType(IData.class).into(target).andStart(
				getContext());
		assertEquals(0, target.getData().length);
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext1.xml");
		Thread.sleep(SLEEP_TIME);
		assertEquals(1, target.getData().length);
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext2.xml");
		Thread.sleep(SLEEP_TIME);
		assertEquals(2, target.getData().length);
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext3.xml");
		Thread.sleep(SLEEP_TIME);
		assertEquals(3, target.getData().length);
		removeExtension("core.test.extpoint.id1");
		Thread.sleep(SLEEP_TIME);
		assertEquals(2, target.getData().length);
		removeExtension("core.test.extpoint.id2");
		Thread.sleep(SLEEP_TIME);
		assertEquals(1, target.getData().length);
		removeExtension("core.test.extpoint.id3");
		Thread.sleep(SLEEP_TIME);
		assertEquals(0, target.getData().length);
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testModifyWithUnknownTypeAndSingleData() throws ConfigurationException, InvalidSyntaxException {
		printTestName();
		ServiceReference[] references = getContext().getServiceReferences(ConfigurationPlugin.class.getName(), null);
		boolean configSymbolReplace = false;
		for (ServiceReference reference : references) {
			Object service = getContext().getService(reference);
			if (service instanceof ConfigSymbolReplace) {
				ConfigSymbolReplace config = (ConfigSymbolReplace) service;
				config.addVariable("true", "true");
				configSymbolReplace = true;
			}
		}
		assertTrue("Ok, test needs rework - expected ConfigSymbolReplace is not registered!", configSymbolReplace);
		for (ServiceReference reference : references)
			getContext().ungetService(reference);
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext5.xml");
		ConfigurableThingSingleData target = new ConfigurableThingSingleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").expectingExactly(1).into(target).bind(
				"configure").andStart(getContext());
		assertNotNull(target.getData());
		assertTrue(target.getData().getRequired());
		assertEquals("test5", target.getData().getText());
		assertEquals(String.class, target.getData().createObjectType().getClass());

		removeExtension("core.test.extpoint.id5");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testWithUnknownTypeAndSingleDataAndOneNestedSingleElementAndTwoNestedMultipleElements() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext6.xml");
		ConfigurableThingSingleData target = new ConfigurableThingSingleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").expectingExactly(1).into(target).bind(
				"configure").andStart(getContext());
		assertNotNull(target.getData());
		assertFalse(target.getData().getRequired());
		assertFalse(target.getData().isRequired());
		assertEquals("test6", target.getData().getText());
		assertEquals(String.class, target.getData().createObjectType().getClass());

		IData2 data2 = target.getData().getNews();
		assertNotNull(data2);
		assertEquals("rmation", data2.getInfo());

		IData3[] data3 = target.getData().getMoreData();
		assertNotNull(data3);
		assertEquals(2, data3.length);
		assertEquals("more-one", data3[0].getId());
		assertEquals("Hugo", data3[0].getName());
		assertEquals("more-two", data3[1].getId());
		assertEquals("Hella", data3[1].getName());

		removeExtension("core.test.extpoint.id6");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testCachingWithUnknownTypeAndSingleDataAndOneNestedSingleElementAndTwoNestedMultipleElements() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext6.xml");
		ConfigurableThingSingleData target = new ConfigurableThingSingleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").expectingExactly(1).into(target).bind(
				"configure").andStart(getContext());
		assertNotNull(target.getData());
		assertFalse(target.getData().getRequired());
		assertFalse(target.getData().isRequired());
		assertEquals("test6", target.getData().getText());
		assertEquals(String.class, target.getData().createObjectType().getClass());

		IData2 data21 = target.getData().getNews();
		IData2 data22 = target.getData().getNews();
		assertSame(data21, data22);

		IData3[] data31 = target.getData().getMoreData();
		IData3[] data32 = target.getData().getMoreData();
		assertSame(data31, data32);

		removeExtension("core.test.extpoint.id6");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testBug240766WithMultipleData() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext1-sub.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext2.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext3.xml");
		ConfigurableThingMultipleData target = new ConfigurableThingMultipleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").useType(ISubData.class).into(target)
				.andStart(getContext());
		assertEquals(3, target.getData().length);
		assertTrue(ISubData.class.isInstance(target.getData()[0]));
		assertEquals("This check relies on the order that extensions are added to the extension registry!", "SubSub",
				((ISubData) target.getData()[0]).getSubText());
		removeExtension("core.test.extpoint.id1");
		removeExtension("core.test.extpoint.id2");
		removeExtension("core.test.extpoint.id3");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testBug240766WithSingleData() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext1-sub.xml");
		ConfigurableThingSingleData target = new ConfigurableThingSingleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").useType(ISubData.class).expectingExactly(1)
				.into(target).bind("configure").andStart(getContext());
		assertNotNull(target.getData());
		assertEquals("Dynamic proxy for " + ISubData.class.getName()
				+ ":subText=SubSub,required=true,objectType=java.lang.String,text=test1", target.getData().toString());
		assertTrue(ISubData.class.isInstance(target.getData()));
		assertEquals("SubSub", ((ISubData) target.getData()).getSubText());
		assertTrue(target.getData().getValue().contains("And Now for Something Completely Different!"));
		assertTrue(target.getData().getRequired());
		assertTrue(target.getData().isRequired());
		assertEquals("test1", target.getData().getText());
		assertEquals(String.class, target.getData().createObjectType().getClass());
		removeExtension("core.test.extpoint.id1");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testEqualsAndHashCode() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext1.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext2.xml");
		ConfigurableThingMultipleData target = new ConfigurableThingMultipleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").useType(IData.class).into(target).andStart(
				getContext());
		assertEquals(2, target.getData().length);
		assertEquals(target.getData()[0], target.getData()[0]);
		assertEquals(target.getData()[1], target.getData()[1]);
		assertFalse(target.getData()[0].equals(target.getData()[1]));
		assertFalse(target.getData()[0].equals("no"));
		assertTrue(
				"This test is based on the fact that the hasCode for IConfigurationElement is based on a handle which is a different int for every element.",
				target.getData()[0].hashCode() != target.getData()[1].hashCode());

		removeExtension("core.test.extpoint.id1");
		removeExtension("core.test.extpoint.id2");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testGetContributor() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext1.xml");
		ConfigurableThingMultipleData target = new ConfigurableThingMultipleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").useType(IData.class).into(target).andStart(
				getContext());
		assertEquals(1, target.getData().length);
		assertEquals(Activator.getDefault().getBundle(), target.getData()[0].getContributingBundle());
		removeExtension("core.test.extpoint.id1");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testLazyCreate() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext7.xml");
		ConfigurableThingMultipleData target = new ConfigurableThingMultipleData();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").useType(IData.class).into(target).andStart(
				getContext());
		assertEquals(1, target.getData().length);
		assertFalse(LazyThing.instantiated);
		ILazyThing lazyThing = target.getData()[0].createLazyThing();
		assertFalse(LazyThing.instantiated);
		lazyThing.doSomething();
		assertTrue(LazyThing.instantiated);
		removeExtension("core.test.extpoint.id7");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testWithUnknownTypeAndMultipleDataSpecific() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext1.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext2.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext3.xml");
		ConfigurableThingMultipleDataSpecific target = new ConfigurableThingMultipleDataSpecific();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").into(target).specific().andStart(
				getContext());
		assertEquals(3, target.getExtData().length);
		for (IExtData data : target.getExtData()) {
			if (data.getTest()[0].getText().equals("test1"))
				assertEquals("java.lang.String", data.getTest()[0].createObjectType().getClass().getName());
			else if (data.getTest()[0].getText().equals("test2"))
				assertEquals("java.util.HashMap", data.getTest()[0].createObjectType().getClass().getName());
			else if (data.getTest()[0].getText().equals("test3"))
				assertEquals("java.util.ArrayList", data.getTest()[0].createObjectType().getClass().getName());
			else
				fail("Argh!");
		}
		removeExtension("core.test.extpoint.id1");
		removeExtension("core.test.extpoint.id2");
		removeExtension("core.test.extpoint.id3");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

	public void testWithUnknownTypeAndMultipleDataSpecific2() {
		printTestName();
		addPluginXml(ExtensionInjectorTest.class, "plugin.xml");
		addPluginXml(ExtensionInjectorTest.class, "plugin_ext-a.xml");
		ConfigurableThingMultipleDataSpecific target = new ConfigurableThingMultipleDataSpecific();
		ExtensionInjector injector = Inject.extension("core.test.extpoint").into(target).specific().andStart(
				getContext());
		assertEquals(1, target.getExtData().length);
		for (IExtData extData : target.getExtData()) {
			assertEquals(2, extData.getTest().length);
			for (IData data : extData.getTest()) {
				if (data.getText().equals("test-a1"))
					assertEquals("java.lang.String", data.createObjectType().getClass().getName());
				else if (data.getText().equals("test-a2"))
					assertEquals("java.lang.StringBuffer", data.createObjectType().getClass().getName());
				else
					fail("Argh!");
			}
		}
		removeExtension("core.test.extpoint.id-a");
		removeExtensionPoint("core.test.extpoint");
		injector.stop();
	}

}
