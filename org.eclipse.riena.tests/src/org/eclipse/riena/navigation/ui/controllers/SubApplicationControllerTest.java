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
package org.eclipse.riena.navigation.ui.controllers;

import junit.framework.TestCase;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.internal.ui.ridgets.swt.ActionRidget;
import org.eclipse.riena.internal.ui.ridgets.swt.LabelRidget;
import org.eclipse.riena.navigation.model.SubApplicationNode;
import org.eclipse.riena.tests.collect.NonUITestCase;
import org.eclipse.riena.ui.ridgets.IActionRidget;
import org.eclipse.swt.widgets.Display;

/**
 * Tests of the class {@link SubApplicationController}.
 */
@NonUITestCase
public class SubApplicationControllerTest extends TestCase {

	private SubApplicationController controller;
	private SubApplicationNode node;

	@Override
	protected void setUp() throws Exception {

		Display display = Display.getDefault();
		Realm realm = SWTObservables.getRealm(display);
		assertNotNull(realm);
		ReflectionUtils.invokeHidden(realm, "setDefault", realm);

		node = new SubApplicationNode();
		controller = new SubApplicationController(node);

	}

	@Override
	protected void tearDown() throws Exception {
		controller = null;
		node = null;
	}

	/**
	 * Tests the method {@code getMenuActionRidget(String)}.
	 */
	public void testGetMenuActionRidget() {

		controller.addRidget(IActionRidget.BASE_ID_MENUACTION + "id1", new LabelRidget());
		ActionRidget menuAction = new ActionRidget();
		controller.addRidget("id2", menuAction);
		controller.addRidget(IActionRidget.BASE_ID_MENUACTION + "id3", menuAction);
		controller.addRidget(IActionRidget.BASE_ID_TOOLBARACTION + "id4", menuAction);

		assertNull(controller.getMenuActionRidget("id1"));
		assertNull(controller.getMenuActionRidget("id2"));
		assertSame(menuAction, controller.getMenuActionRidget("id3"));
		assertNull(controller.getMenuActionRidget("id4"));

	}

	/**
	 * Tests the method {@code getToolbarActionRidget(String)}.
	 */
	public void testGetToolbarActionRidget() {

		controller.addRidget(IActionRidget.BASE_ID_TOOLBARACTION + "id1", new LabelRidget());
		ActionRidget menuAction = new ActionRidget();
		controller.addRidget("id2", menuAction);
		controller.addRidget(IActionRidget.BASE_ID_TOOLBARACTION + "id3", menuAction);
		controller.addRidget(IActionRidget.BASE_ID_MENUACTION + "id4", menuAction);

		assertNull(controller.getToolbarActionRidget("id1"));
		assertNull(controller.getToolbarActionRidget("id2"));
		assertSame(menuAction, controller.getToolbarActionRidget("id3"));
		assertNull(controller.getToolbarActionRidget("id4"));

	}

}
