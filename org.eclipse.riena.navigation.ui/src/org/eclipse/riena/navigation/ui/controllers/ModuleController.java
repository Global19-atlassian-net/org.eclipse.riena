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
package org.eclipse.riena.navigation.ui.controllers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.riena.navigation.IModuleGroupNode;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.listener.ModuleNodeListener;
import org.eclipse.riena.ui.ridgets.IWindowRidget;
import org.eclipse.riena.ui.ridgets.listener.IWindowRidgetListener;

/**
 * Default implementation for a ModuleController.
 */
public class ModuleController extends NavigationNodeController<IModuleNode> {

	private IWindowRidget windowRidget;
	private IWindowRidgetListener windowListener;
	private boolean closeable;
	private boolean dragEnabled;

	/**
	 * @param navigationNode
	 */
	public ModuleController(IModuleNode navigationNode) {
		super(navigationNode);

		closeable = true;
		dragEnabled = true;
		getNavigationNode().addListener(new MyModuleNodeListener());
		windowListener = new WindowListener();

	}

	/**
	 * Listener observes the node of the module. If the lable or the icon
	 * changed, the window ridget will be updated.
	 */
	private final class MyModuleNodeListener extends ModuleNodeListener {

		/**
		 * @see org.eclipse.riena.navigation.model.NavigationNodeAdapter#labelChanged
		 *      (org.eclipse.riena.navigation.INavigationNode)
		 */
		@Override
		public void labelChanged(IModuleNode moduleNode) {
			updateLabel();
		}

		/**
		 * @see org.eclipse.riena.navigation.model.NavigationNodeAdapter#iconChanged
		 *      (org.eclipse.riena.navigation.INavigationNode)
		 */
		@Override
		public void iconChanged(IModuleNode source) {
			updateIcon();
		}
	}

	/**
	 * @param windowRidget
	 *            the windowRidget to set
	 */
	public void setWindowRidget(IWindowRidget windowRidget) {
		if (getWindowRidget() != null) {
			getWindowRidget().removeWindowRidgetListener(windowListener);
		}
		this.windowRidget = windowRidget;
		if (getWindowRidget() != null) {
			getWindowRidget().addWindowRidgetListener(windowListener);
		}
	}

	/**
	 * @return the windowRidget
	 */
	public IWindowRidget getWindowRidget() {
		return windowRidget;
	}

	/**
	 * @see org.eclipse.riena.ui.internal.ridgets.IRidgetContainer#configureRidgets()
	 */
	public void configureRidgets() {
		setCloseable(getNavigationNode().isClosable());
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.controllers.NavigationNodeController#afterBind()
	 */
	@Override
	public void afterBind() {
		super.afterBind();
		updateLabel();
		updateIcon();
		updateCloseable();
		updateActive();
	}

	private void updateIcon() {
		updateIcon(getWindowRidget());
	}

	private void updateLabel() {
		if (getWindowRidget() != null) {
			getWindowRidget().setTitle(getNavigationNode().getLabel());
		}
	}

	private void updateCloseable() {
		if (getWindowRidget() != null) {
			getWindowRidget().setCloseable(getNavigationNode().isClosable());
		}
	}

	private void updateActive() {
		if (getWindowRidget() != null) {
			getWindowRidget().setActive(getNavigationNode().isActivated());
		}
	}

	@Deprecated
	public boolean isPresentGroupMember() {
		return ((IModuleGroupNode) getNavigationNode().getParent()).isPresentGroupNode();
	}

	public boolean hasSingleLeafChild() {

		List<INavigationNode<?>> children = getVisibleChildren(getNavigationNode());
		return children.size() == 1 && children.get(0).isLeaf();
	}

	/**
	 * Returns a list of all visible children of the given node.
	 * 
	 * @param parent
	 *            parent node
	 * @return list of visible child nodes
	 */
	public List<INavigationNode<?>> getVisibleChildren(INavigationNode<?> parent) {

		List<INavigationNode<?>> visibleChildren = new ArrayList<INavigationNode<?>>();

		for (Object child : parent.getChildren()) {
			if (child instanceof INavigationNode<?>) {
				INavigationNode<?> childNode = (INavigationNode<?>) child;
				if (childNode.isVisible()) {
					visibleChildren.add(childNode);
				}
			}
		}

		return visibleChildren;

	}

	public boolean isFirstChild() {

		return getNavigationNode().getParent().getChild(0) == getNavigationNode();
	}

	/**
	 * @return the closeable
	 */
	public boolean isCloseable() {
		return closeable;
	}

	/**
	 * @param closeable
	 *            the closeable to set
	 */
	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
	}

	/**
	 * @return the dragEnabled
	 */
	public boolean isDragEnabled() {
		return dragEnabled;
	}

	/**
	 * @param dragEnabled
	 *            the dragEnabled to set
	 */
	public void setDragEnabled(boolean dragEnabled) {
		this.dragEnabled = dragEnabled;
	}

	private class WindowListener implements IWindowRidgetListener {

		/**
		 * @see org.eclipse.riena.ui.internal.ridgets.listener.IWindowRidgetListener#activated()
		 */
		public void activated() {
			getNavigationNode().activate();
		}

		/**
		 * @see org.eclipse.riena.ui.internal.ridgets.listener.IWindowRidgetListener#closed()
		 */
		public void closed() {
			getNavigationNode().dispose();
		}

	}

}
