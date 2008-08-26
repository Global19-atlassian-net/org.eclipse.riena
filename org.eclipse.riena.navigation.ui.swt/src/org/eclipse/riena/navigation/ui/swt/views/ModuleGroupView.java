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
package org.eclipse.riena.navigation.ui.swt.views;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.riena.navigation.IModuleGroupNode;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.listener.ModuleGroupNodeListener;
import org.eclipse.riena.navigation.listener.ModuleNodeListener;
import org.eclipse.riena.navigation.model.ModuleGroupNode;
import org.eclipse.riena.navigation.model.ModuleNode;
import org.eclipse.riena.navigation.ui.swt.lnf.renderer.ModuleGroupRenderer;
import org.eclipse.riena.ui.ridgets.controller.IController;
import org.eclipse.riena.ui.swt.lnf.ILnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * View of a module group.
 */
public class ModuleGroupView extends Composite implements INavigationNodeView<IController, ModuleGroupNode> {

	private static final int MODULE_GROUP_GAP = 3;

	private ModuleGroupNode moduleGroupNode;
	private ModuleGroupListener moduleGroupListener;
	private ModuleListener moduleListener;
	private PaintDelegation paintDelegation;
	private List<IComponentUpdateListener> updateListeners;
	private Map<ModuleNode, ModuleView> registeredModuleViews;

	public ModuleGroupView(Composite parent, int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		updateListeners = new ArrayList<IComponentUpdateListener>();
		registeredModuleViews = new LinkedHashMap<ModuleNode, ModuleView>();
		setData(getClass().getName());
	}

	protected List<ModuleView> getAllModuleViews() {
		return new ArrayList<ModuleView>(registeredModuleViews.values());
	}

	protected ModuleNode getNodeForView(ModuleView view) {
		for (ModuleNode node : registeredModuleViews.keySet()) {
			ModuleView moduleView = registeredModuleViews.get(node);
			if (moduleView.equals(view)) {
				return node;
			}
		}

		return null;
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.views.INavigationNodeView#bind(org.eclipse.riena.navigation.INavigationNode)
	 */
	public void bind(ModuleGroupNode node) {
		moduleGroupNode = node;
		addListeners();
	}

	/**
	 * Adds listeners to this view and also to node of the module group.
	 */
	protected void addListeners() {

		moduleGroupListener = new ModuleGroupListener();
		getNavigationNode().addListener(moduleGroupListener);
		moduleListener = new ModuleListener();

		paintDelegation = new PaintDelegation();
		addPaintListener(paintDelegation);
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.views.INavigationNodeView#unbind()
	 */
	public void unbind() {
		getNavigationNode().removeListener(moduleGroupListener);
		removePaintListener(paintDelegation);
		moduleGroupNode = null;
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.views.INavigationNodeView#getNavigationNode()
	 */
	public ModuleGroupNode getNavigationNode() {
		return moduleGroupNode;
	}

	/**
	 * The Listener fires updates, if a child (sub-module) is added or removed.
	 */
	private final class ModuleListener extends ModuleNodeListener {

		@Override
		public void childAdded(IModuleNode source, ISubModuleNode child) {
			super.childAdded(source, child);
			fireUpdated(child);
		}

		@Override
		public void childRemoved(IModuleNode source, ISubModuleNode child) {
			super.childRemoved(source, child);
			fireUpdated(child);
		}
	}

	/**
	 * The Listener fires updates, if a child (module) is added or removed.
	 */
	private final class ModuleGroupListener extends ModuleGroupNodeListener {

		@Override
		public void childAdded(IModuleGroupNode source, IModuleNode child) {
			fireUpdated(child);
		}

		@Override
		public void childRemoved(IModuleGroupNode source, IModuleNode child) {
			unregisterModuleView(child);
			fireUpdated(child);
		}

		@Override
		public void deactivated(IModuleGroupNode source) {
			super.deactivated(source);
			redraw();
		}

		@Override
		public void disposed(IModuleGroupNode source) {
			super.disposed(source);
			dispose();
		}

	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.views.INavigationNodeView#calculateBounds(int)
	 */
	public int calculateBounds(int positionHint) {
		Point p = computeSize(SWT.DEFAULT, SWT.DEFAULT);
		FormData fd = new FormData();
		fd.top = new FormAttachment(0, positionHint);
		fd.left = new FormAttachment(0, 0);
		positionHint += p.y;
		fd.width = p.x;
		fd.bottom = new FormAttachment(0, positionHint);
		setLayoutData(fd);
		layout();
		update();
		positionHint += MODULE_GROUP_GAP;
		return positionHint;
	}

	@Override
	public Point computeSize(int wHint, int hHint) {
		GC gc = new GC(Display.getCurrent());
		getRenderer().setItems(getAllModuleViews());
		Point size = getRenderer().computeSize(gc, wHint, hHint);
		gc.dispose();
		return size;
	}

	/**
	 * Returns the module at the given point.
	 * 
	 * @param point
	 *            - point over module item
	 * @return module item; or null, if not item was found
	 */
	public ModuleView getItem(Point point) {

		for (ModuleView moduleView : getAllModuleViews()) {
			if (moduleView.getBounds() == null) {
				continue;
			}
			if (moduleView.getBounds().contains(point)) {
				return moduleView;
			}
		}

		return null;

	}

	private class PaintDelegation implements PaintListener {

		/**
		 * Computes the size of the widget of the module group and paints it.
		 * 
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		public void paintControl(PaintEvent e) {
			setBackground(LnfManager.getLnf().getColor(ILnfKeyConstants.MODULE_GROUP_WIDGET_BACKGROUND));
			getRenderer().setItems(getAllModuleViews());
			Point size = getRenderer().computeSize(e.gc, SWT.DEFAULT, SWT.DEFAULT);
			getRenderer().setBounds(0, 0, size.x, size.y);
			getRenderer().paint(e.gc, null);
		}
	}

	private ModuleGroupRenderer getRenderer() {
		return (ModuleGroupRenderer) LnfManager.getLnf().getRenderer(ILnfKeyConstants.MODULE_GROUP_RENDERER);
	}

	/**
	 * Adds the give view to the list of the module views that are belonging to
	 * this module group.
	 * 
	 * @param moduleView
	 *            - view to register
	 */
	public void registerModuleView(ModuleView moduleView) {
		moduleView.getNavigationNode().addListener(moduleListener);
		// we need that to calculate the bounds of the ModuleGroupView
		registeredModuleViews.put(moduleView.getNavigationNode(), moduleView);
		// observer moduleView for expand/collapse
		moduleView.addUpdateListener(new ModuleViewObserver());
	}

	/**
	 * Removes that view that is belonging to the given node from the list of
	 * the module views.
	 * 
	 * @param moduleNode
	 *            - node whose according view should be unregistered
	 */
	public void unregisterModuleView(IModuleNode moduleNode) {
		for (ModuleView moduleView : getAllModuleViews()) {
			if (moduleView.getNavigationNode() == moduleNode || moduleView.getNavigationNode() == null) {
				unregisterModuleView(moduleView);
				break;
			}
		}
	}

	/**
	 * Remove the given view from the list of the module views.
	 * 
	 * @param moduleView
	 *            - view to remove
	 */
	public void unregisterModuleView(ModuleView moduleView) {
		ModuleNode node = getNodeForView(moduleView);
		node.removeListener(moduleListener);
		registeredModuleViews.remove(node);
	}

	private class ModuleViewObserver implements IComponentUpdateListener {

		public void update(INavigationNode<?> node) {
			fireUpdated(node);
		}

	}

	protected void fireUpdated(INavigationNode<?> node) {
		for (IComponentUpdateListener listener : updateListeners) {
			listener.update(node);
		}
	}

	public void addUpdateListener(IComponentUpdateListener listener) {
		updateListeners.add(listener);
	}

}
