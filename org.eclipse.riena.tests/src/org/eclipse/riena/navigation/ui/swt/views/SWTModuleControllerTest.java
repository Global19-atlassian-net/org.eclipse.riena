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
package org.eclipse.riena.navigation.ui.swt.views;

import junit.framework.TestCase;

import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.internal.core.test.collect.NonUITestCase;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.navigation.model.ModuleNode;
import org.eclipse.riena.navigation.model.SubModuleNode;

/**
 * Test of the class {@link SWTModuleController}.
 */
@NonUITestCase
public class SWTModuleControllerTest extends TestCase {

	private SWTModuleController controller;
	private IModuleNode moduleNode;

	/**
	 * {@inheritDoc}
	 * <p>
	 * Creates a {@code SWTModuleController}.
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		moduleNode = new ModuleNode();
		controller = new SWTModuleController(moduleNode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		moduleNode.dispose();
		moduleNode = null;
		controller = null;
	}

	/**
	 * Tests the <i>private</i> method {@code collapseSibling(ISubModuleNode)}.
	 * <p>
	 * 
	 * Navigation Model:
	 * 
	 * <pre>
	 *   m
	 *   |
	 *   +-sm1
	 *   |  |
	 *   |  +-sm11
	 *   |  |  |
	 *   |  |  +-sm111
	 *   |  |
	 *   |  +-sm12
	 *   |     |
	 *   |     +-sm121
	 *   |
	 *   +-sm2
	 *   |  |
	 *   |  +-sm21
	 *   |  |  |
	 *   |  |  +-sm211
	 *   |  |
	 *   |  +-sm22
	 *   |     |
	 *   |     +-sm221
	 *   |
	 *   +-sm3
	 *      |
	 *      +-sm31
	 *      |  |
	 *      |  +-sm311
	 *      |
	 *      +-sm32
	 *         |
	 *         +-sm321
	 * </pre>
	 */
	public void testCollapseSibling() {

		// level 1
		ISubModuleNode sm1 = new SubModuleNode(new NavigationNodeId("sm1"));
		moduleNode.addChild(sm1);
		ISubModuleNode sm2 = new SubModuleNode(new NavigationNodeId("sm2"));
		moduleNode.addChild(sm2);
		ISubModuleNode sm3 = new SubModuleNode(new NavigationNodeId("sm3"));
		moduleNode.addChild(sm3);

		// level 2
		ISubModuleNode sm11 = new SubModuleNode(new NavigationNodeId("sm11"));
		sm1.addChild(sm11);
		ISubModuleNode sm12 = new SubModuleNode(new NavigationNodeId("sm12"));
		sm1.addChild(sm12);
		ISubModuleNode sm21 = new SubModuleNode(new NavigationNodeId("sm21"));
		sm2.addChild(sm21);
		ISubModuleNode sm22 = new SubModuleNode(new NavigationNodeId("sm22"));
		sm2.addChild(sm22);
		ISubModuleNode sm31 = new SubModuleNode(new NavigationNodeId("sm31"));
		sm3.addChild(sm31);
		ISubModuleNode sm32 = new SubModuleNode(new NavigationNodeId("sm32"));
		sm3.addChild(sm32);

		// level 3
		ISubModuleNode sm111 = new SubModuleNode(new NavigationNodeId("sm111"));
		sm11.addChild(sm111);
		ISubModuleNode sm121 = new SubModuleNode(new NavigationNodeId("sm121"));
		sm12.addChild(sm121);
		ISubModuleNode sm211 = new SubModuleNode(new NavigationNodeId("sm211"));
		sm21.addChild(sm211);
		ISubModuleNode sm221 = new SubModuleNode(new NavigationNodeId("sm221"));
		sm22.addChild(sm221);
		ISubModuleNode sm311 = new SubModuleNode(new NavigationNodeId("sm311"));
		sm31.addChild(sm311);
		ISubModuleNode sm321 = new SubModuleNode(new NavigationNodeId("sm321"));
		sm32.addChild(sm321);

		expandAll(moduleNode, true);
		ReflectionUtils.invokeHidden(controller, "collapseSibling", sm211);
		assertFalse(sm1.isExpanded());
		assertTrue(sm2.isExpanded());
		assertFalse(sm3.isExpanded());
		assertTrue(sm21.isExpanded());
		assertTrue(sm211.isExpanded());
		assertFalse(sm22.isExpanded());

		expandAll(moduleNode, true);
		ReflectionUtils.invokeHidden(controller, "collapseSibling", sm32);
		assertFalse(sm1.isExpanded());
		assertFalse(sm2.isExpanded());
		assertTrue(sm3.isExpanded());
		assertFalse(sm31.isExpanded());
		assertTrue(sm32.isExpanded());

	}

	/**
	 * Expands or collapsed the given node and all it's children.
	 * 
	 * @param node
	 *            navigation node
	 * @param expanded
	 *            {@code true} expand nodes; {@code false} collapse nodes
	 */
	private void expandAll(INavigationNode<?> node, boolean expanded) {

		node.setExpanded(expanded);
		for (INavigationNode<?> child : node.getChildren()) {
			expandAll(child, expanded);
		}

	}

}
