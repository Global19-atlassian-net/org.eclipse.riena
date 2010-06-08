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
package org.eclipse.riena.navigation.ui.swt.views;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.riena.core.wire.Wire;
import org.eclipse.riena.internal.core.test.RienaTestCase;
import org.eclipse.riena.internal.core.test.collect.UITestCase;
import org.eclipse.riena.navigation.model.ModuleGroupNode;
import org.eclipse.riena.navigation.model.ModuleNode;
import org.eclipse.riena.navigation.ui.controllers.ModuleController;
import org.eclipse.riena.navigation.ui.controllers.ModuleGroupController;

/**
 * Testcase for NavigationViewFactory
 */
@UITestCase
public class NavigationViewFactoryTest extends RienaTestCase {

	public void testNormalWithoutInjectionBehaviour() {
		NavigationViewFactory viewFactory = new NavigationViewFactory();

		ModuleView moduleView = viewFactory.createModuleView(new Shell());

		assertNotNull(moduleView);
		assertTrue(moduleView.getClass() == ModuleView.class);

		ModuleGroupView moduleGroupView = viewFactory.createModuleGroupView(new Shell());

		assertNotNull(moduleGroupView);
		assertTrue(moduleGroupView.getClass() == ModuleGroupView.class);
	}

	public void testNormalWithInjectionBehaviour() {
		NavigationViewFactory viewFactory = new NavigationViewFactory();
		Wire.instance(viewFactory).andStart(getContext());

		ModuleView moduleView = viewFactory.createModuleView(new Shell());

		assertNotNull(moduleView);
		assertTrue(moduleView.getClass() == ModuleView.class);

		ModuleGroupView moduleGroupView = viewFactory.createModuleGroupView(new Shell());

		assertNotNull(moduleGroupView);
		assertTrue(moduleGroupView.getClass() == ModuleGroupView.class);
	}

	public void testConfiguredNavigationViewFactory() {
		NavigationViewFactory viewFactory = new NavigationViewFactory();
		addPluginXml(this.getClass(), "pluginXmlNavigationViewFactory.xml");

		try {
			Wire.instance(viewFactory).andStart(getContext());

			ModuleView moduleView = viewFactory.createModuleView(new Shell());

			assertNotNull(moduleView);
			assertTrue(moduleView.getClass() == TestModuleView.class);

			ModuleController moduleController = viewFactory.createModuleController(new ModuleNode());
			assertNotNull(moduleController);
			assertTrue(moduleController.getClass() == SWTModuleController.class);

			ModuleGroupView moduleGroupView = viewFactory.createModuleGroupView(new Shell());

			assertNotNull(moduleGroupView);
			assertTrue(moduleGroupView.getClass() == TestModuleGroupView.class);

			ModuleGroupController moduleGroupController = viewFactory
					.createModuleGroupController(new ModuleGroupNode());
			assertNotNull(moduleGroupController);
			assertTrue(moduleGroupController.getClass() == ModuleGroupController.class);
		} finally {
			removeExtension("org.eclipse.riena.test.navigationModuleView");
			removeExtension("org.eclipse.riena.test.navigationModuleGroupView");
		}
	}

	public void testConfiguredNavigationViewFactorySecond() {
		NavigationViewFactory viewFactory = new NavigationViewFactory();
		addPluginXml(this.getClass(), "pluginXmlNavigationViewFactoryWithController.xml");

		try {
			Wire.instance(viewFactory).andStart(getContext());

			ModuleView moduleView = viewFactory.createModuleView(new Shell());

			assertNotNull(moduleView);
			assertTrue(moduleView.getClass() == TestModuleView.class);

			ModuleController moduleController = viewFactory.createModuleController(new ModuleNode());
			assertNotNull(moduleController);
			assertTrue(moduleController.getClass() == TestModuleController.class);

			ModuleGroupView moduleGroupView = viewFactory.createModuleGroupView(new Shell());

			assertNotNull(moduleGroupView);
			assertTrue(moduleGroupView.getClass() == TestModuleGroupView.class);

			ModuleGroupController moduleGroupController = viewFactory
					.createModuleGroupController(new ModuleGroupNode());
			assertNotNull(moduleGroupController);
			assertTrue(moduleGroupController.getClass() == TestModuleGroupController.class);
		} finally {
			removeExtension("org.eclipse.riena.test.navigationModuleView");
			removeExtension("org.eclipse.riena.test.navigationModuleGroupView");
		}
	}
}
