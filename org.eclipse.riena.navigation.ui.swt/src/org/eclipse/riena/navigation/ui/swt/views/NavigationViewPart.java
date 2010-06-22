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
package org.eclipse.riena.navigation.ui.swt.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.riena.core.marker.IMarker;
import org.eclipse.riena.core.wire.Wire;
import org.eclipse.riena.internal.navigation.ui.swt.Activator;
import org.eclipse.riena.navigation.IModuleGroupNode;
import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.listener.ModuleGroupNodeListener;
import org.eclipse.riena.navigation.listener.NavigationTreeObserver;
import org.eclipse.riena.navigation.listener.SubApplicationNodeListener;
import org.eclipse.riena.navigation.listener.SubModuleNodeListener;
import org.eclipse.riena.navigation.model.ModuleGroupNode;
import org.eclipse.riena.navigation.model.ModuleNode;
import org.eclipse.riena.navigation.ui.swt.lnf.renderer.EmbeddedBorderRenderer;
import org.eclipse.riena.navigation.ui.swt.lnf.renderer.ModuleGroupRenderer;
import org.eclipse.riena.navigation.ui.swt.presentation.SwtViewProvider;
import org.eclipse.riena.navigation.ui.swt.presentation.stack.TitlelessStackPresentation;
import org.eclipse.riena.ui.core.marker.HiddenMarker;
import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.utils.WidgetIdentificationSupport;

public class NavigationViewPart extends ViewPart implements IModuleNavigationComponentProvider {

	public final static String ID = "org.eclipse.riena.navigation.ui.swt.views.navigationViewPart"; //$NON-NLS-1$

	private IViewFactory viewFactory;
	private Composite parent;
	private ResizeListener resizeListener;
	private Composite scrolledComposite;
	private ScrollingSupport scrollingSupport;
	private final List<ModuleGroupView> moduleGroupViews = new ArrayList<ModuleGroupView>();
	private Composite navigationMainComposite;
	private NavigationTreeObserver navigationTreeObserver;
	private final Map<INavigationNode<?>, ModuleGroupView> moduleGroupNodesToViews;
	private final Map<INavigationNode<?>, ModuleView> moduleNodesToViews;

	public NavigationViewPart() {
		markAsNavigation();
		moduleGroupNodesToViews = new HashMap<INavigationNode<?>, ModuleGroupView>();
		moduleNodesToViews = new HashMap<INavigationNode<?>, ModuleView>();
	}

	protected IViewFactory getViewFactory() {
		if (viewFactory == null) {
			viewFactory = new NavigationViewFactory();
			Wire.instance(viewFactory).andStart(Activator.getDefault().getContext());
		}
		return viewFactory;
	}

	private void markAsNavigation() {
		setPartProperty(TitlelessStackPresentation.PROPERTY_NAVIGATION, String.valueOf(Boolean.TRUE));
	}

	public ISubApplicationNode getSubApplicationNode() {
		final String perspectiveID = getViewSite().getPage().getPerspective().getId();
		return SwtViewProvider.getInstance().getNavigationNode(perspectiveID, ISubApplicationNode.class);
	}

	@Override
	public void createPartControl(final Composite parent) {
		this.parent = parent;
		initLayoutParts();
		// build the view hierarchy
		buildNavigationViewHierarchy();
		initModelObserver();
	}

	private void initLayoutParts() {
		// configure layout
		parent.setLayout(new FormLayout());
		parent.setBackground(LnfManager.getLnf().getColor(LnfKeyConstants.NAVIGATION_BACKGROUND));
		resizeListener = new ResizeListener();
		parent.addControlListener(resizeListener);

		navigationMainComposite = new Composite(parent, SWT.DOUBLE_BUFFERED);
		navigationMainComposite.setLayout(new FormLayout());
		navigationMainComposite.setBackground(LnfManager.getLnf().getColor(LnfKeyConstants.NAVIGATION_BACKGROUND));

		scrolledComposite = new Composite(navigationMainComposite, SWT.DOUBLE_BUFFERED);

		scrolledComposite.setBackground(LnfManager.getLnf().getColor(LnfKeyConstants.NAVIGATION_BACKGROUND));
		scrolledComposite.setLayout(new FormLayout());
		WidgetIdentificationSupport.setIdentification(scrolledComposite, "NavigationView"); //$NON-NLS-1$

		scrollingSupport = new ScrollingSupport(parent, SWT.NONE, this);
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(100, -15);
		navigationMainComposite.setLayoutData(formData);

		formData = new FormData();
		formData.top = new FormAttachment(navigationMainComposite, 0);
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		scrollingSupport.getScrollComposite().setLayoutData(formData);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();

		if (parent != null && !parent.isDisposed() && resizeListener != null) {
			parent.removeControlListener(resizeListener);
		}
	}

	private void buildNavigationViewHierarchy() {
		// get the root of the SubApplication
		final ISubApplicationNode subApplicationNode = getSubApplicationNode();
		for (final IModuleGroupNode moduleGroupNode : subApplicationNode.getChildren()) {
			createModuleGroupView(moduleGroupNode);
		}
		updateNavigationSize();
	}

	private void initModelObserver() {
		navigationTreeObserver = new NavigationTreeObserver();
		navigationTreeObserver.addListener(new SubApplicationListener());
		navigationTreeObserver.addListener(new ModuleGroupListener());
		navigationTreeObserver.addListener(new SubModuleListener());
		navigationTreeObserver.addListenerTo(getSubApplicationNode());
	}

	/**
	 * Update the size of the navigation area when the application is resized
	 * (fix for bug 270620).
	 */
	private final class ResizeListener extends ControlAdapter {
		@Override
		public void controlResized(final ControlEvent e) {
			updateNavigationSize();
			parent.layout();
		}
	}

	/**
	 * The Listener updates the size of the navigation, if a child is added or
	 * removed.
	 */
	private class SubApplicationListener extends SubApplicationNodeListener {

		/**
		 * @see org.eclipse.riena.navigation.listener.NavigationNodeListener#childAdded(org.eclipse.riena.navigation.INavigationNode,
		 *      org.eclipse.riena.navigation.INavigationNode)
		 */
		@Override
		public void childAdded(final ISubApplicationNode source, final IModuleGroupNode child) {
			// create moduleGroupView
			createModuleGroupView(child);
			updateNavigationSize();
		}

		//		@Override
		//		public void filterAdded(ISubApplicationNode source, IUIFilter filter) {
		//			super.filterAdded(source, filter);
		//			updateNavigationSize();
		//		}
		//
		//		@Override
		//		public void filterRemoved(ISubApplicationNode source, IUIFilter filter) {
		//			super.filterRemoved(source, filter);
		//			updateNavigationSize();
		//		}
		//
		/**
		 * @see org.eclipse.riena.navigation.listener.NavigationNodeListener#childRemoved(org.eclipse.riena.navigation.INavigationNode,
		 *      org.eclipse.riena.navigation.INavigationNode)
		 */
		@Override
		public void childRemoved(final ISubApplicationNode source, final IModuleGroupNode child) {
			unregisterModuleGroupView(child);
			if (source.isSelected()) {
				updateNavigationSize();
			}
		}

		@Override
		public void markerChanged(final ISubApplicationNode source, final IMarker marker) {
			if (marker instanceof HiddenMarker) {
				updateNavigationSize();
			}
		}

	}

	private class ModuleGroupListener extends ModuleGroupNodeListener {

		//		@Override
		//		public void filterAdded(IModuleGroupNode source, IUIFilter filter) {
		//			super.filterAdded(source, filter);
		//			updateNavigationSize();
		//		}
		//
		//		@Override
		//		public void filterRemoved(IModuleGroupNode source, IUIFilter filter) {
		//			super.filterRemoved(source, filter);
		//			updateNavigationSize();
		//		}

		@Override
		public void childAdded(final IModuleGroupNode source, final IModuleNode child) {
			// createModuleView
			final ModuleGroupView moduleGroupView = getModuleGroupViewForNode(source);
			createModuleView(child, moduleGroupView);
			// childAdded.activate();
			updateNavigationSize();
		}

		@Override
		public void childRemoved(final IModuleGroupNode source, final IModuleNode child) {
			moduleNodesToViews.remove(child);
			// updateNavigationSize();
		}

		@Override
		public void disposed(final IModuleGroupNode source) {
			super.disposed(source);
			unregisterModuleGroupView(source);
			// updateNavigationSize();
		}

		@Override
		public void markerChanged(final IModuleGroupNode source, final IMarker marker) {
			if (marker instanceof HiddenMarker) {
				updateNavigationSize();
			}
		}

	}

	private class SubModuleListener extends SubModuleNodeListener {

		@Override
		public void markerChanged(final ISubModuleNode source, final IMarker marker) {
			if (marker instanceof HiddenMarker) {
				updateNavigationSize();
			}
		}
	}

	public ModuleGroupView getModuleGroupViewForNode(final IModuleGroupNode source) {
		return moduleGroupNodesToViews.get(source);
	}

	/**
	 * @since 1.2
	 */
	public ModuleView getModuleViewForNode(final IModuleNode source) {
		return moduleNodesToViews.get(source);
	}

	private void createModuleGroupView(final IModuleGroupNode moduleGroupNode) {
		// ModuleGroupView are directly rendered into the bodyComposite
		final ModuleGroupView moduleGroupView = getViewFactory().createModuleGroupView(scrolledComposite);
		NodeIdentificationSupport.setIdentification(moduleGroupView, "moduleGroupView", moduleGroupNode); //$NON-NLS-1$
		moduleGroupNodesToViews.put(moduleGroupNode, moduleGroupView);
		moduleGroupView.addUpdateListener(new ModuleGroupViewObserver());

		registerModuleGroupView(moduleGroupView);
		// create controller. the controller is implicit registered as
		// presentation in the node
		// now the ModuleGroupController can be replaced by your own implementation
		getViewFactory().createModuleGroupController(moduleGroupNode);
		//new ModuleGroupController(moduleGroupNode);
		moduleGroupView.bind((ModuleGroupNode) moduleGroupNode);

		moduleGroupView.setLayout(new FormLayout());
		final Composite moduleGroupBody = new Composite(moduleGroupView, SWT.NONE);
		moduleGroupBody.setBackground(LnfManager.getLnf().getColor(LnfKeyConstants.MODULE_GROUP_WIDGET_BACKGROUND));
		final FormData formData = new FormData();
		final int padding = getModuleGroupPadding();
		formData.top = new FormAttachment(0, padding);
		formData.left = new FormAttachment(0, padding);
		formData.bottom = new FormAttachment(100, -padding);
		formData.right = new FormAttachment(100, -padding);
		moduleGroupBody.setLayoutData(formData);

		// now it's time for the module views
		for (final IModuleNode moduleNode : moduleGroupNode.getChildren()) {
			createModuleView(moduleNode, moduleGroupView);
		}

	}

	private final class ModuleGroupViewObserver implements IComponentUpdateListener {

		public void update(final INavigationNode<?> node) {

			updateNavigationSize();
		}
	}

	/**
	 * Adds the give view to the list of the module views that are belonging to
	 * the sub-application of this navigation.
	 * 
	 * @param moduleView
	 *            view to register
	 */
	private void registerModuleGroupView(final ModuleGroupView moduleGroupView) {
		moduleGroupViews.add(moduleGroupView);
		final ModuleGroupViewComparator comparator = new ModuleGroupViewComparator();
		Collections.sort(moduleGroupViews, comparator);
	}

	class ModuleGroupViewComparator implements Comparator<ModuleGroupView> {

		public int compare(final ModuleGroupView moduleGroupView1, final ModuleGroupView moduleGroupView2) {
			final ModuleGroupNode moduleGroupNode1 = moduleGroupView1.getNavigationNode();
			final ModuleGroupNode moduleGroupNode2 = moduleGroupView2.getNavigationNode();
			return getSubApplicationNode().getIndexOfChild(moduleGroupNode1) < getSubApplicationNode().getIndexOfChild(
					moduleGroupNode2) ? -1 : 1;
		}

	}

	/**
	 * Removes that view that is belonging to the given node from the list of
	 * the module group views.
	 * 
	 * @param moduleGroupNode
	 *            node whose according view should be unregistered
	 */
	public void unregisterModuleGroupView(final IModuleGroupNode moduleGroupNode) {
		for (final ModuleGroupView moduleGroupView : moduleGroupViews) {
			if (moduleGroupView.getNavigationNode() == moduleGroupNode) {
				unregisterModuleGroupView(moduleGroupView, moduleGroupNode);
				break;
			}
		}
	}

	/**
	 * Remove the given view from the list of the module group views.
	 * 
	 * @param moduleGroupView
	 *            view to remove
	 */
	private void unregisterModuleGroupView(final ModuleGroupView moduleGroupView, final IModuleGroupNode node) {
		moduleGroupNodesToViews.remove(node);
		moduleGroupViews.remove(moduleGroupView);
	}

	private void createModuleView(final IModuleNode moduleNode, final ModuleGroupView moduleGroupView) {
		final Composite moduleGroupBody = (Composite) moduleGroupView.getChildren()[0];
		final FormLayout layout = new FormLayout();
		moduleGroupBody.setLayout(layout);

		final ModuleView moduleView = viewFactory.createModuleView(moduleGroupBody);
		moduleView.setModuleGroupNode(moduleGroupView.getNavigationNode());
		NodeIdentificationSupport.setIdentification(moduleView.getTitle(), "titleBar", moduleNode); //$NON-NLS-1$
		NodeIdentificationSupport.setIdentification(moduleView.getTree(), "tree", moduleNode); //$NON-NLS-1$
		moduleNodesToViews.put(moduleNode, moduleView);
		// now the SWTModuleController implementation can be replaced by your own implementation
		getViewFactory().createModuleController(moduleNode);
		//		new SWTModuleController(moduleNode);
		moduleView.bind((ModuleNode) moduleNode);
		// the size of the module group depends on the module views
		moduleGroupView.registerModuleView(moduleView);
	}

	public void updateNavigationSize() {
		calculateBounds();
		scrolledComposite.layout();
		navigationMainComposite.layout();
		scrollingSupport.scroll();
	}

	public IModuleGroupNode getActiveModuleGroupNode() {
		IModuleGroupNode active = null;
		final Iterator<IModuleGroupNode> it = getSubApplicationNode().getChildren().iterator();
		while (active == null && it.hasNext()) {
			active = it.next();
			if (!active.isActivated()) {
				active = null;
			}
		}
		return active;
	}

	public int calculateBounds() {
		int yPosition = 0;
		Collections.sort(moduleGroupViews, new ModuleGroupViewComparator());
		for (final ModuleGroupView moduleGroupView : moduleGroupViews) {
			yPosition = moduleGroupView.calculateBounds(yPosition);
		}
		return yPosition;
	}

	/**
	 * Returns the renderer of the module group.
	 * 
	 * @return the renderer instance
	 */
	private ModuleGroupRenderer getModuleGroupRenderer() {

		ModuleGroupRenderer renderer = (ModuleGroupRenderer) LnfManager.getLnf().getRenderer(
				LnfKeyConstants.MODULE_GROUP_RENDERER);
		if (renderer == null) {
			renderer = new ModuleGroupRenderer();
		}
		return renderer;

	}

	/**
	 * Returns the renderer of the module group.
	 * 
	 * @return the renderer instance
	 */
	private EmbeddedBorderRenderer getLnfBorderRenderer() {

		EmbeddedBorderRenderer renderer = (EmbeddedBorderRenderer) LnfManager.getLnf().getRenderer(
				LnfKeyConstants.SUB_MODULE_VIEW_BORDER_RENDERER);
		if (renderer == null) {
			renderer = new EmbeddedBorderRenderer();
		}
		return renderer;

	}

	private int getModuleGroupPadding() {
		return getModuleGroupRenderer().getModuleGroupPadding() + getLnfBorderRenderer().getBorderWidth();
	}

	@Override
	public void setFocus() {
		navigationMainComposite.setFocus();
	}

	public Composite getNavigationComponent() {
		return navigationMainComposite;
	}

	public Composite getScrolledComponent() {
		return scrolledComposite;
	}

}
