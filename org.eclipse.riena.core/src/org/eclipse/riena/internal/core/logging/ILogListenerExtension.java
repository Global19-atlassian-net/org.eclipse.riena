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
package org.eclipse.riena.internal.core.logging;

import org.osgi.service.log.LogListener;

import org.eclipse.equinox.log.LogFilter;

import org.eclipse.riena.core.injector.extension.ExtensionInterface;
import org.eclipse.riena.core.injector.extension.MapName;
import org.eclipse.riena.internal.core.Activator;

/**
 * Define a n{@code ILogListener}.
 */
@ExtensionInterface
public interface ILogListenerExtension {

	String ID = Activator.PLUGIN_ID + ".logging.listeners,logListeners"; //$NON-NLS-1$

	/**
	 * The descriptive name of the {@code ILogListener}
	 * 
	 * @return the descriptive name of the {@code ILogListener}
	 */
	String getName();

	/**
	 * Create an instance of the {@code LogListener}.
	 * 
	 * @return the {@code ILogListener}
	 */
	@MapName("listener-class")
	LogListener createLogListener();

	/**
	 * Create an instance of the {@code LogFilter}
	 * 
	 * @return the {@code LogFilter}
	 */
	@MapName("filter-class")
	LogFilter createLogFilter();

	/**
	 * Defines whether logging on the defined {@code LogListener} shall by
	 * synchronous or asynchronous.
	 * 
	 * @return true for synchronous otherwise asynchronous
	 */
	@MapName("sync")
	boolean isSynchronous();
}
