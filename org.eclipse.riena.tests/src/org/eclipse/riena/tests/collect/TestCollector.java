/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.tests.collect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.osgi.framework.Bundle;

import org.eclipse.riena.core.util.Iter;

/**
 * A helper for collecting test classes.
 */
public final class TestCollector {

	private static final String PLUS = " + ";

	private TestCollector() {
		// utility
	}

	/**
	 * Create a {@code TestSuite} that contains all test case in the given
	 * bundle.
	 * 
	 * @param bundle
	 *            bundle to collect all {@code TestCase}s
	 * @param withinPackage
	 *            if not null, only {@code TestCase}s within this package are
	 *            collected
	 * @param annotationClasses
	 *            only {@code TestCase}s that have one of these annotations are
	 *            collected
	 * @return
	 */
	public static TestSuite createTestSuiteWith(final Bundle bundle, final Package withinPackage,
			final Class<? extends Annotation>... annotationClasses) {
		return createTestSuiteWith(bundle, withinPackage, false, annotationClasses);
	}

	/**
	 * Create a {@code TestSuite} that contains all test case in the given
	 * bundle.
	 * 
	 * @param bundle
	 *            bundle to collect all {@code TestCase}s
	 * @param withinPackage
	 *            if not null, only {@code TestCase}s within this package are
	 *            collected
	 * @param annotationClasses
	 *            only {@code TestCase}s that have one of these annotations are
	 *            collected
	 * @param subPackages
	 *            on true also collect sub-packages
	 * @return
	 */
	public static TestSuite createTestSuiteWith(final Bundle bundle, final Package withinPackage, boolean subPackages,
			final Class<? extends Annotation>... annotationClasses) {
		StringBuilder bob = new StringBuilder("Tests within bundle '").append(bundle.getSymbolicName()).append(
				"' and package '");
		bob.append(withinPackage == null ? "all" : withinPackage.getName()).append("'").append(" recursive '").append(
				subPackages).append("'");
		bob.append(" and restricted to '");
		for (Class<? extends Annotation> annotationClass : annotationClasses) {
			bob.append(annotationClass.getSimpleName()).append(PLUS);
		}
		if (annotationClasses.length > 0) {
			bob.setLength(bob.length() - PLUS.length());
		} else {
			bob.append("none");
		}
		bob.append("'.");
		TestSuite suite = new TestSuite(bob.toString());
		for (Class<? extends TestCase> clazz : collectWith(bundle, withinPackage, subPackages, annotationClasses)) {
			suite.addTestSuite(clazz);
		}
		return suite;
	}

	/**
	 * Collect all {@code TestCase}e within the given bundle.
	 * 
	 * @param bundle
	 *            bundle to collect all {@code TestCase}s
	 * @param withinPackage
	 *            if not null, only {@code TestCase}s within this package are
	 *            collected
	 * @param annotationClasses
	 *            only {@code TestCase}s that have one of these annotations are
	 *            collected
	 * @param subPackages
	 *            on true also collect sub-packages
	 * @return
	 */
	public static List<Class<? extends TestCase>> collectWith(final Bundle bundle, final Package withinPackage,
			boolean subPackages, final Class<? extends Annotation>... annotationClasses) {
		List<Class<? extends TestCase>> testClasses = new ArrayList<Class<? extends TestCase>>();

		for (Class<? extends TestCase> testClass : collect(bundle, withinPackage, subPackages)) {
			boolean collect = annotationClasses.length == 0 ? true : false;
			for (Class<? extends Annotation> annotationClass : annotationClasses) {
				collect = collect || testClass.isAnnotationPresent(annotationClass);
			}
			if (collect) {
				testClasses.add(testClass);
			}
		}

		return testClasses;
	}

	/**
	 * Collect all unmarked {@code TestCase}s within the given bundle.
	 * 
	 * @param bundle
	 *            bundle to collect all {@code TestCase}s
	 * @param withinPackage
	 *            if not null, only {@code TestCase}s within this package are
	 *            collected
	 * @return
	 */
	public static List<Class<? extends TestCase>> collectUnmarked(final Bundle bundle, final Package withinPackage) {
		List<Class<? extends TestCase>> testClasses = new ArrayList<Class<? extends TestCase>>();

		for (Class<? extends TestCase> testClass : collect(bundle, withinPackage, true)) {
			if (testClass.getAnnotations().length == 0) {
				testClasses.add(testClass);
			}
		}

		return testClasses;
	}

	/**
	 * Collect all badly named {@code TestCase}e within the given bundle.
	 * 
	 * @param bundle
	 *            bundle to collect all {@code TestCase}s
	 * @param withinPackage
	 *            if not null, only {@code TestCase}s within this package are
	 *            collected
	 * @return
	 */
	public static List<Class<? extends TestCase>> collectBadlyNamed(final Bundle bundle, final Package withinPackage) {
		List<Class<? extends TestCase>> testClasses = new ArrayList<Class<? extends TestCase>>();

		for (Class<? extends TestCase> testClass : collect(bundle, withinPackage, true)) {
			if (!testClass.getName().endsWith("Test")) {
				testClasses.add(testClass);
			}
		}

		return testClasses;
	}

	/**
	 * Collect all {@code TestCase}e within the given bundle.
	 * 
	 * @param bundle
	 *            bundle to collect all {@code TestCase}s
	 * @param withinPackage
	 *            if not null, only {@code TestCase}s within this package are
	 *            collected
	 * @param subPackages
	 *            on true also collect sub-packages
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Class<? extends TestCase>> collect(final Bundle bundle, final Package withinPackage,
			boolean subPackages) {
		List<Class<? extends TestCase>> testClasses = new ArrayList<Class<? extends TestCase>>();
		Enumeration<URL> allClasses = bundle.findEntries("", "*.class", true);
		for (URL entryURL : Iter.able(allClasses)) {
			String url = entryURL.toExternalForm();
			if (url.contains("$")) {
				// Skip inner classes
				continue;
			}
			Class<?> clazz = getClass(bundle, entryURL);
			if (clazz == null) {
				trace("Could not get class name from ", url);
				continue;
			}
			if (!TestCase.class.isAssignableFrom(clazz)) {
				trace("Not a TestCase: ", clazz.getName());
				continue;
			}
			String className = clazz.getName();
			if (withinPackage != null) {
				if (subPackages) {
					if (!(className.startsWith(withinPackage.getName()) && className.endsWith(clazz.getSimpleName()))) {
						continue;
					}
				} else {
					if (!className.equals(withinPackage.getName() + "." + clazz.getSimpleName())) {
						continue;
					}
				}
			}
			if (clazz.isAnnotationPresent(NonGatherableTestCase.class)) {
				continue;
			}
			if (Modifier.isAbstract(clazz.getModifiers())) {
				continue;
			}
			testClasses.add((Class<TestCase>) clazz);
		}
		return testClasses;
	}

	private static Class<?> getClass(Bundle bundle, URL entryURL) {
		String entry = entryURL.toExternalForm().replace(".class", "").replace('/', '.');
		// Brute force detecting of how many chars we have to skip to find a class within the url
		String name = entry;
		int dot = 0;
		while ((dot = name.indexOf('.', dot)) != -1) {
			String className = name.substring(dot + 1);
			try {
				return bundle.loadClass(className);
			} catch (ClassNotFoundException e) {
				dot++;
			} catch (NoClassDefFoundError e) {
				dot++;
			}
		}
		return null;
	}

	private static void trace(Object... objects) {
		StringBuilder bob = new StringBuilder();
		for (Object object : objects) {
			bob.append(object);
		}
		System.err.println(bob.toString());
	}

}
