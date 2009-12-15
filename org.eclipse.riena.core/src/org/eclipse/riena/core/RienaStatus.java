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
package org.eclipse.riena.core;

import org.eclipse.riena.internal.core.Activator;

/**
 * Utility to check the Riena status.
 */
public final class RienaStatus {

	/**
	 * This system property controls {@code RienaStatus.isDevelopment()}
	 */
	public static final String RIENA_DEVELOPMENT_SYSTEM_PROPERTY = "riena.development"; //$NON-NLS-1$

	/**
	 * This system property controls {@code RienaStatus.isTest()}
	 */
	public static final String RIENA_TEST_SYSTEM_PROPERTY = "riena.test"; //$NON-NLS-1$

	/**
	 * This is the default value (i.e. if the value is not explicitly defined)
	 * for the system property {@code RIENA_DEVELOPMENT_SYSTEM_PROPERTY}
	 */
	public static final String DEVELOPMENT_DEFAULT = "true"; //$NON-NLS-1$

	/**
	 * This is the default value (i.e. if the value is not explicitly defined)
	 * for the system property {@code RIENA_TEST_SYSTEM_PROPERTY}
	 */
	public static final String TEST_DEFAULT = "false"; //$NON-NLS-1$

	private RienaStatus() {
		// Utility
	}

	/**
	 * Riena already active?
	 * 
	 * @return
	 */
	public static boolean isActive() {
		return Activator.getDefault().isActive();
	}

	/**
	 * Are we in <i>development</i>?<br>
	 * If {@code true} certain services/functionalities behave more appropriate
	 * for development time, e.g.
	 * <ul>
	 * <li>default logging is enabled</li>
	 * <li>the store of the client monitoring is cleaned up on each start
	 * <li>..</li>
	 * </ul>
	 * 
	 * @return
	 */
	public static boolean isDevelopment() {
		return Boolean.parseBoolean(System.getProperty(RIENA_DEVELOPMENT_SYSTEM_PROPERTY, DEVELOPMENT_DEFAULT));
	}

	/**
	 * Are we running a <i>test case</i>?<br>
	 * If {@code true} certain services/functionalities behave more appropriate
	 * for testing, e.g. ridgets are cretated on the fly
	 * 
	 * @return
	 */
	public static boolean isTest() {
		return Boolean.parseBoolean(System.getProperty(RIENA_TEST_SYSTEM_PROPERTY, TEST_DEFAULT));
	}
}
