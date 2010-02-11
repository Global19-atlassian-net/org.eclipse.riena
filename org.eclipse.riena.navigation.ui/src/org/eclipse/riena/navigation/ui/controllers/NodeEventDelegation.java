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
package org.eclipse.riena.navigation.ui.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.model.SimpleNavigationNodeAdapter;
import org.eclipse.riena.ui.ridgets.IContextUpdateListener;
import org.eclipse.riena.ui.ridgets.IVisualContextManager;

public class NodeEventDelegation extends SimpleNavigationNodeAdapter implements IVisualContextManager {

	public NodeEventDelegation() {
	}

	private List<IContextUpdateListener> listeners = new ArrayList<IContextUpdateListener>();
	private Map<Object, List<IContextUpdateListener>> context2Observers = new HashMap<Object, List<IContextUpdateListener>>();

	@Override
	public void activated(INavigationNode<?> source) {
		contextUpdated(source);
	}

	@Override
	public void beforeDeactivated(INavigationNode<?> source) {
		for (IContextUpdateListener listener : listeners) {
			listener.beforeContextUpdate(source);
		}
	}

	@Override
	public void deactivated(INavigationNode<?> source) {
		contextUpdated(source);
	}

	private void contextUpdated(INavigationNode<?> source) {
		List<IContextUpdateListener> toDelete = new ArrayList<IContextUpdateListener>();
		for (IContextUpdateListener listener : listeners) {
			if (listener.contextUpdated(source)) {
				toDelete.add(listener);
			}
		}
		listeners.removeAll(toDelete);
	}

	public List<Object> getActiveContexts(List<Object> contexts) {
		List<Object> nodes = new ArrayList<Object>();
		for (Object object : contexts) {
			if (object instanceof INavigationNode<?>) {
				INavigationNode<?> node = (INavigationNode<?>) object;
				if (node.isActivated()) {
					nodes.add(node);
				}
			}
		}
		return nodes;
	}

	public void addContextUpdateListener(IContextUpdateListener listener, Object context) {
		if (context instanceof INavigationNode<?>) {
			INavigationNode<?> node = (INavigationNode<?>) context;
			node.addSimpleListener(this);
			registerObserver(context, listener);
			listeners.add(listener);
		}
	}

	private void registerObserver(Object context, IContextUpdateListener listener) {
		List<IContextUpdateListener> observers = context2Observers.get(context);
		if (observers == null) {
			observers = new LinkedList<IContextUpdateListener>();
			context2Observers.put(context, observers);
		}
		observers.add(listener);
	}

	public void removeContextUpdateListener(IContextUpdateListener listener, Object context) {
		List<IContextUpdateListener> observers = context2Observers.get(context);

		if (observers == null) {
			//should never happen
			return;
		}
		listeners.remove(listener);
		if (observers.size() == 1) {
			//don�t need observation delegation anymore
			context2Observers.remove(context);
			INavigationNode<?> node = null;
			if (context instanceof INavigationNode<?>) {
				node = INavigationNode.class.cast(context);
				// don�t need to observe anymore
				node.removeSimpleListener(this);
			}
			return;
		}

		observers.remove(listener);

	}

}