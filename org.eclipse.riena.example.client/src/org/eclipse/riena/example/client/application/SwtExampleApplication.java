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
package org.eclipse.riena.example.client.application;

import org.osgi.framework.Bundle;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import org.eclipse.riena.internal.example.client.Activator;
import org.eclipse.riena.navigation.IApplicationNode;
import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.navigation.listener.NavigationTreeObserver;
import org.eclipse.riena.navigation.listener.SubModuleNodeListener;
import org.eclipse.riena.navigation.model.ApplicationNode;
import org.eclipse.riena.navigation.model.SubApplicationNode;
import org.eclipse.riena.navigation.ui.controllers.ApplicationController;
import org.eclipse.riena.navigation.ui.swt.application.SwtApplication;
import org.eclipse.riena.ui.ridgets.IStatuslineRidget;
import org.eclipse.riena.ui.workarea.WorkareaManager;

/**
 * Define the model of the application.
 */
public class SwtExampleApplication extends SwtApplication {

	@Override
	protected ApplicationController createApplicationController(IApplicationNode node) {
		ApplicationController controller = super.createApplicationController(node);
		controller.setMenubarVisible(true);
		return controller;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ExampleActionBarAdvisor(configurer);
	}

	@Override
	protected IApplicationNode createModel() {
		ISubApplicationNode subApplication = null;

		String bundleVersion = (String) Activator.getDefault().getBundle().getHeaders().get("Bundle-Version"); //$NON-NLS-1$

		final IApplicationNode applicationNode = new ApplicationNode(
				new NavigationNodeId("application"), "Example & Playground - " + bundleVersion); //$NON-NLS-1$ //$NON-NLS-2$
		applicationNode.setIcon(ExampleIcons.ICON_APPLICATION);

		// Navigation SubApplication
		applicationNode.create(new NavigationNodeId("org.eclipse.riena.example.navigation.subapplication")); //$NON-NLS-1$

		applicationNode.create(new NavigationNodeId("org.eclipse.riena.example.navigate.form")); //$NON-NLS-1$

		// Playground SubApplication
		subApplication = new SubApplicationNode(new NavigationNodeId("playground"), "Pla&yground"); //$NON-NLS-1$ //$NON-NLS-2$
		subApplication.setIcon(ExampleIcons.ICON_SAMPLE);
		//		presentation.present(subApplication, "subapplication.2"); //$NON-NLS-1$
		WorkareaManager.getInstance().registerDefinition(subApplication, "subapplication.2", false); //$NON-NLS-1$

		applicationNode.addChild(subApplication);

		// shared view demo
		applicationNode.create(new NavigationNodeId("org.eclipse.riena.example.sharedViews")); //$NON-NLS-1$

		// uiProcess demo
		applicationNode.create(new NavigationNodeId("org.eclipse.riena.example.uiProcesses")); //$NON-NLS-1$

		applicationNode.create(new NavigationNodeId("org.eclipse.riena.example.playground")); //$NON-NLS-1$

		applicationNode.create(new NavigationNodeId("org.eclipse.riena.example.filters")); //$NON-NLS-1$

		applicationNode.create(new NavigationNodeId("org.eclipse.riena.example.logcollector")); //$NON-NLS-1$

		NavigationTreeObserver navigationTreeObserver = new NavigationTreeObserver();
		navigationTreeObserver.addListener(new SubModuleListener());
		navigationTreeObserver.addListenerTo(applicationNode);
		return applicationNode;
	}

	@Override
	protected Bundle getBundle() {
		return Activator.getDefault().getBundle();
	}

	@Override
	protected String getKeyScheme() {
		return "org.eclipse.riena.example.client.scheme"; //$NON-NLS-1$
	}

	// helping classes
	//////////////////

	/**
	 * Update the status line, when a navigation node is activated
	 */
	private static final class SubModuleListener extends SubModuleNodeListener {
		@Override
		public void activated(ISubModuleNode source) {
			ApplicationNode appNode = source.getParentOfType(ApplicationNode.class);
			ApplicationController controller = (ApplicationController) appNode.getNavigationNodeController();
			IStatuslineRidget statusline = controller.getStatusline();
			if (statusline != null) {
				statusline.getStatuslineNumberRidget().setNumberString(source.getLabel());
			}
		}
	}
}
