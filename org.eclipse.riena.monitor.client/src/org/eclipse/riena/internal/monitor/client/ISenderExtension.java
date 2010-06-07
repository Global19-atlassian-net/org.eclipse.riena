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
package org.eclipse.riena.internal.monitor.client;

import org.eclipse.riena.core.injector.extension.DoNotWireExecutable;
import org.eclipse.riena.core.injector.extension.ExtensionInterface;
import org.eclipse.riena.core.injector.extension.MapName;
import org.eclipse.riena.monitor.client.ISender;

/**
 * Extension interface for the {@code ISender} definition.
 * <p>
 * <b>Note:</b> The "org.eclipse.riena.monitor.sender" is @deprecated.
 */
@ExtensionInterface
public interface ISenderExtension {

	String ID = "org.eclipse.riena.monitor.sender,sender"; //$NON-NLS-1$

	/**
	 * Return the descriptive name of the sender.
	 * 
	 * @return the descriptive name of the sender
	 */
	String getName();

	/**
	 * Create the configured {@code ISender}.
	 * 
	 * @return the sender
	 */
	@MapName("class")
	@DoNotWireExecutable
	ISender createSender();
}
