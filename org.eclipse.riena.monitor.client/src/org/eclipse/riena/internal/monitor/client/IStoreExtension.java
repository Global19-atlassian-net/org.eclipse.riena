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
package org.eclipse.riena.internal.monitor.client;

import org.eclipse.riena.core.injector.extension.DoNotWireExecutable;
import org.eclipse.riena.core.injector.extension.ExtensionInterface;
import org.eclipse.riena.core.injector.extension.MapName;
import org.eclipse.riena.monitor.client.IStore;

/**
 * Extension interface for the {@code IStore} definition.
 */
@ExtensionInterface
public interface IStoreExtension {

	String ID = "org.eclipse.riena.monitor.store"; //$NON-NLS-1$

	String getName();

	@MapName("class")
	@DoNotWireExecutable
	IStore createStore();

}
