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
package org.eclipse.riena.navigation.ui.swt;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.riena.navigation.ui.swt.binding.DefaultSwtControlRidgetMapperTest;
import org.eclipse.riena.navigation.ui.swt.component.MenuCoolBarCompositeTest;
import org.eclipse.riena.navigation.ui.swt.component.SubApplicationSwitcherWidgetTest;
import org.eclipse.riena.navigation.ui.swt.lnf.AbstractLnfResourceTest;
import org.eclipse.riena.navigation.ui.swt.lnf.LnfManagerTest;
import org.eclipse.riena.navigation.ui.swt.lnf.renderer.EmbeddedBorderRendererTest;
import org.eclipse.riena.navigation.ui.swt.lnf.renderer.ShellBorderRendererTest;
import org.eclipse.riena.navigation.ui.swt.lnf.renderer.SubApplicationSwitcherRendererTest;
import org.eclipse.riena.navigation.ui.swt.lnf.renderer.SubModuleTreeItemMarkerRendererTest;
import org.eclipse.riena.navigation.ui.swt.viewprovider.SwtViewIdTest;
import org.eclipse.riena.navigation.ui.swt.views.AbstractViewBindingDelegateTest;
import org.eclipse.riena.navigation.ui.swt.views.ApplicationViewAdvisorTest;
import org.eclipse.riena.navigation.ui.swt.views.GrabCornerListenerWithTrackerTest;
import org.eclipse.riena.ui.swt.lnf.renderer.EmbeddedTitlebarRendererTest;
import org.eclipse.riena.ui.swt.lnf.rienadefault.RienaDefaultLnfTest;

/**
 * Tests all test cases within package:
 * 
 * org.eclipse.riena.navigation.ui.swt
 */
public class AllTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());

		suite.addTest(org.eclipse.riena.navigation.ui.swt.viewprovider.AllTests.suite());
		suite.addTest(org.eclipse.riena.navigation.ui.swt.views.AllTests.suite());

		suite.addTestSuite(DefaultSwtControlRidgetMapperTest.class);
		suite.addTestSuite(AbstractLnfResourceTest.class);
		suite.addTestSuite(LnfManagerTest.class);
		suite.addTestSuite(EmbeddedBorderRendererTest.class);
		suite.addTestSuite(EmbeddedTitlebarRendererTest.class);
		suite.addTestSuite(RienaDefaultLnfTest.class);
		suite.addTestSuite(SwtViewIdTest.class);
		suite.addTestSuite(ShellBorderRendererTest.class);
		suite.addTestSuite(SubModuleTreeItemMarkerRendererTest.class);
		suite.addTestSuite(ApplicationViewAdvisorTest.class);
		suite.addTestSuite(GrabCornerListenerWithTrackerTest.class);
		suite.addTestSuite(AbstractViewBindingDelegateTest.class);
		suite.addTestSuite(SubApplicationSwitcherRendererTest.class);
		suite.addTestSuite(SubApplicationSwitcherWidgetTest.class);
		suite.addTestSuite(MenuCoolBarCompositeTest.class);
		return suite;
	}

}
