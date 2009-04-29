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
package org.eclipse.riena.internal.security.server;

import java.security.Principal;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;

import org.eclipse.riena.communication.core.hooks.IServiceHook;
import org.eclipse.riena.core.RienaPlugin;
import org.eclipse.riena.core.cache.GenericObjectCache;
import org.eclipse.riena.core.cache.IGenericObjectCache;
import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.security.common.ISubjectHolderService;
import org.eclipse.riena.security.common.session.ISessionHolderService;
import org.eclipse.riena.security.server.session.ISessionService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends RienaPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.compeople.scp.security.server"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Activator.plugin = this;
		GenericObjectCache<String, Principal[]> principalCache = new GenericObjectCache<String, Principal[]>();
		principalCache.setName("principalCache"); //$NON-NLS-1$
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put("cache.type", "PrincipalCache"); //$NON-NLS-1$ //$NON-NLS-2$
		context.registerService(IGenericObjectCache.class.getName(), principalCache, props);
		createSecurityServiceHookAndInjectors();
	}

	private void createSecurityServiceHookAndInjectors() {
		// create SecurityServiceHook
		IServiceHook securityServiceHook = new SecurityServiceHook();
		getContext().registerService(IServiceHook.class.getName(), securityServiceHook, null);

		// create and Start Injectors
		Inject.service(IGenericObjectCache.class.getName())
				.useFilter("(cache.type=PrincipalCache)").into(securityServiceHook) //$NON-NLS-1$
				.andStart(Activator.getDefault().getContext());
		Inject.service(ISessionService.class.getName()).useRanking().into(securityServiceHook).andStart(
				Activator.getDefault().getContext());
		Inject.service(ISubjectHolderService.class.getName()).useRanking().into(securityServiceHook).andStart(
				Activator.getDefault().getContext());
		Inject.service(ISessionHolderService.class.getName()).useRanking().into(securityServiceHook).andStart(
				Activator.getDefault().getContext());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.plugin = null;
		super.stop(context);
	}

	/**
	 * Get the plugin instance.
	 * 
	 * @return
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
