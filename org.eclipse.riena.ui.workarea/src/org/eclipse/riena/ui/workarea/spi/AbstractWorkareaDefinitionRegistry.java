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
package org.eclipse.riena.ui.workarea.spi;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.riena.navigation.ApplicationModelFailure;
import org.eclipse.riena.ui.workarea.IWorkareaDefinition;

public abstract class AbstractWorkareaDefinitionRegistry implements IWorkareaDefinitionRegistry {

	protected Map<Object, IWorkareaDefinition> workareas = new HashMap<Object, IWorkareaDefinition>();

	public IWorkareaDefinition getDefinition(Object id) {
		return workareas.get(id);
	}

	public IWorkareaDefinition register(Object id, IWorkareaDefinition definition) {
		if (getDefinition(id) != null) {
			IWorkareaDefinition existingDefinition = getDefinition(id);
			if ((existingDefinition.getControllerClass() == null && definition.getControllerClass() != null)
					|| (existingDefinition.getControllerClass() != null && !existingDefinition.getControllerClass()
							.equals(definition.getControllerClass()))) {
				throw new ApplicationModelFailure(
						"Inconsistent workarea definition: a definition for submodules with typeId=\"" + id //$NON-NLS-1$
								+ "\" already exists and it has a different controller (class " //$NON-NLS-1$
								+ existingDefinition.getControllerClass() + " instead of " //$NON-NLS-1$
								+ definition.getControllerClass() + ")."); //$NON-NLS-1$
			}
			if ((existingDefinition.getViewId() == null && definition.getViewId() != null)
					|| (existingDefinition.getViewId() != null && !existingDefinition.getViewId().equals(
							definition.getViewId()))) {
				throw new ApplicationModelFailure(
						"Inconsistent workarea definition: a definition for submodules with typeId=\"" + id //$NON-NLS-1$
								+ "\" already exists and it has a different view (" + existingDefinition.getViewId() //$NON-NLS-1$
								+ " instead of " + definition.getViewId() + ")."); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (existingDefinition.isViewShared() != definition.isViewShared()) {
				throw new ApplicationModelFailure(
						"Inconsistent workarea definition: a definition for submodules with typeId=\"" + id //$NON-NLS-1$
								+ "\" already exists and it has a different shared value."); //$NON-NLS-1$
			}
			return existingDefinition;
		}
		workareas.put(id, definition);
		return definition;
	}

}
