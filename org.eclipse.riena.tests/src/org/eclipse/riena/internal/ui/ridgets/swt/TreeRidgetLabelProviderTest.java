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
package org.eclipse.riena.internal.ui.ridgets.swt;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.ui.ridgets.util.beans.WordNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Tests for the class {@link TreeRidgetLabelProvider}.
 */
public class TreeRidgetLabelProviderTest extends TestCase {

	final String[] columnProperties = { "word", "upperCase" };

	private Shell shell;
	private TreeViewer viewer;
	private TreeRidgetLabelProvider labelProvider;
	private WordNode node;
	private WordNode leaf;

	@Override
	protected void setUp() throws Exception {
		Display display = Display.getDefault();
		Realm realm = SWTObservables.getRealm(display);
		ReflectionUtils.invokeHidden(realm, "setDefault", realm);

		Shell aShell = new Shell(display);
		viewer = new TreeViewer(createTree(aShell));

		IObservableSet elements = createElements();
		labelProvider = TreeRidgetLabelProvider.createLabelProvider(viewer, WordNode.class, elements, columnProperties,
				null);

		viewer.setContentProvider(new FTTreeContentProvider());
		viewer.setLabelProvider(labelProvider);
		viewer.setInput(elements.toArray());
	}

	@Override
	protected void tearDown() throws Exception {
		if (shell != null) {
			shell.dispose();
			shell = null;
		}
	}

	public void testGetText() {
		assertEquals("Node", labelProvider.getText(node));
		assertEquals("LEAF", labelProvider.getText(leaf));
	}

	public void testGetColumnText() {
		assertEquals("Node", labelProvider.getColumnText(node, 0));
		assertEquals("LEAF", labelProvider.getColumnText(leaf, 0));

		assertEquals("false", labelProvider.getColumnText(node, 1));
		assertEquals("true", labelProvider.getColumnText(leaf, 1));

		assertEquals(null, labelProvider.getColumnText(node, 99));
	}

	public void testGetImage() {

		viewer.collapseAll();

		Image siCollapsed = Activator.getSharedImage(SharedImages.IMG_NODE_COLLAPSED);
		assertNotNull(siCollapsed);
		Image nodeCollapsed = labelProvider.getImage(node);
		assertSame(siCollapsed, nodeCollapsed);

		viewer.expandAll();

		Image siExpanded = Activator.getSharedImage(SharedImages.IMG_NODE_EXPANDED);
		assertNotNull(siExpanded);
		Image nodeExpanded = labelProvider.getImage(node);
		assertSame(siExpanded, nodeExpanded);

		viewer.collapseToLevel(node, 1);

		assertSame(siCollapsed, labelProvider.getImage(node));

		viewer.expandToLevel(node, 1);

		assertSame(siExpanded, labelProvider.getImage(node));

		Image siLeaf = Activator.getSharedImage(SharedImages.IMG_LEAF);
		assertNotNull(siLeaf);
		Image imgLeaf = labelProvider.getImage(leaf);
		assertSame(siLeaf, imgLeaf);

		// sanity check
		assertNotSame(nodeExpanded, nodeCollapsed);
		assertNotSame(nodeExpanded, imgLeaf);
		assertNotSame(nodeCollapsed, imgLeaf);
	}

	public void testGetColumnImage() {
		viewer.collapseAll();

		Image siCollapsed = Activator.getSharedImage(SharedImages.IMG_NODE_COLLAPSED);
		assertNotNull(siCollapsed);
		assertSame(siCollapsed, labelProvider.getColumnImage(node, 0));

		Image siLeaf = Activator.getSharedImage(SharedImages.IMG_LEAF);
		assertNotNull(siLeaf);
		assertSame(siLeaf, labelProvider.getColumnImage(leaf, 0));
		assertNotSame(siLeaf, siCollapsed);

		Image siUnchecked = Activator.getSharedImage(SharedImages.IMG_UNCHECKED);
		assertNotNull(siUnchecked);
		assertEquals(siUnchecked, labelProvider.getColumnImage(node, 1));

		Image siChecked = Activator.getSharedImage(SharedImages.IMG_CHECKED);
		assertNotNull(siChecked);
		assertEquals(siChecked, labelProvider.getColumnImage(leaf, 1));

		assertNotSame(siChecked, siUnchecked);

		assertEquals(null, labelProvider.getColumnImage(node, 99));
	}

	public void testGetForeground() {
		WordNode node = new WordNode("test");

		// using upperCase as the enablement accessor; true => enabled; false => disabled
		labelProvider = TreeRidgetLabelProvider.createLabelProvider(viewer, WordNode.class, createElements(),
				columnProperties, "upperCase");

		node.setUpperCase(true);
		Color colorEnabled = labelProvider.getForeground(node);
		assertNull(colorEnabled);

		node.setUpperCase(false);
		Color colorDisabled = labelProvider.getForeground(node);
		assertNotNull(colorDisabled);
	}

	// helping methods
	// ////////////////

	private IObservableSet createElements() {
		Collection<WordNode> collection = new ArrayList<WordNode>();
		node = new WordNode("Node");
		new WordNode(node, "Alpha");
		new WordNode(node, "Bravo");
		leaf = new WordNode("Leaf");
		leaf.setUpperCase(true);
		collection.add(node);
		collection.add(leaf);
		IObservableSet elements = new WritableSet(Realm.getDefault(), collection, WordNode.class);
		return elements;
	}

	private Tree createTree(Shell shell) {
		shell.setLayout(new FillLayout());
		Tree result = new Tree(shell, SWT.SINGLE | SWT.BORDER);
		TreeColumn tc1 = new TreeColumn(result, SWT.NONE);
		tc1.setWidth(200);
		TreeColumn tc2 = new TreeColumn(result, SWT.NONE);
		tc2.setWidth(200);
		return result;
	}

	// helping classes
	// ////////////////

	private static final class FTTreeContentProvider implements ITreeContentProvider {
		public Object[] getChildren(Object element) {
			return ((WordNode) element).getChildren().toArray();
		}

		public Object getParent(Object element) {
			return ((WordNode) element).getParent();
		}

		public boolean hasChildren(Object element) {
			return ((WordNode) element).getChildren().size() > 0;
		}

		public Object[] getElements(Object inputElement) {
			return (Object[]) inputElement;
		}

		public void dispose() {
			// unused
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// unused
		}
	}

}
