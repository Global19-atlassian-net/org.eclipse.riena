/*******************************************************************************
 * Copyright (c) 2007, 2010 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.internal.core.injector;

import junit.framework.TestCase;

import org.eclipse.riena.internal.core.test.collect.NonUITestCase;

/**
 * Test the {@code ObjectCounter}.
 */
@NonUITestCase
public class ObjectCounterTest extends TestCase {

	private ObjectCounter<String> strings;

	protected void setUp() throws Exception {
		super.setUp();
		strings = new ObjectCounter<String>();
	}

	public void testZero() {
		assertEquals(0, strings.getCount("A"));
	}

	public void testOne() {
		assertEquals(0, strings.getCount("A"));
		assertEquals(1, strings.incrementAndGetCount("A"));
		assertEquals(1, strings.getCount("A"));
		assertEquals(0, strings.decrementAndGetCount("A"));
		assertEquals(0, strings.getCount("A"));
	}

	public void testTwo() {
		assertEquals(0, strings.getCount("A"));
		assertEquals(1, strings.incrementAndGetCount("A"));
		assertEquals(1, strings.getCount("A"));
		assertEquals(2, strings.incrementAndGetCount("A"));
		assertEquals(2, strings.getCount("A"));
		assertEquals(1, strings.decrementAndGetCount("A"));
		assertEquals(1, strings.getCount("A"));
		assertEquals(0, strings.decrementAndGetCount("A"));
		assertEquals(0, strings.getCount("A"));
	}

	public void testTwoNoLessThanZero() {
		assertEquals(0, strings.getCount("A"));
		assertEquals(1, strings.incrementAndGetCount("A"));
		assertEquals(1, strings.getCount("A"));
		assertEquals(2, strings.incrementAndGetCount("A"));
		assertEquals(2, strings.getCount("A"));
		assertEquals(1, strings.decrementAndGetCount("A"));
		assertEquals(1, strings.getCount("A"));
		assertEquals(0, strings.decrementAndGetCount("A"));
		assertEquals(0, strings.getCount("A"));
		// removing again does not go below zero
		assertEquals(0, strings.decrementAndGetCount("A"));
		assertEquals(0, strings.getCount("A"));
		// and again
		assertEquals(0, strings.decrementAndGetCount("A"));
		assertEquals(0, strings.getCount("A"));
	}

}
