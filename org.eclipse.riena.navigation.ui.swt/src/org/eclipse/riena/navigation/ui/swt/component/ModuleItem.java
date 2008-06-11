package org.eclipse.riena.navigation.ui.swt.component;

import org.eclipse.riena.navigation.IModuleNode;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.model.SubModuleNode;
import org.eclipse.riena.navigation.ui.swt.lnf.LnfManager;
import org.eclipse.riena.navigation.ui.swt.utils.SwtUtilities;
import org.eclipse.riena.navigation.ui.swt.win32.SwtOsUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ModuleItem {

	private Composite parent;
	private Composite body;
	private ModuleNavigationComponent moduleCmp;
	private Tree subModuleTree;
	private boolean pressed;
	private boolean hover;
	private Rectangle bounds;

	private SubModuleNode activeSubModule;

	public ModuleItem(Composite parent, ModuleNavigationComponent moduleCmp) {
		this.parent = parent;
		this.moduleCmp = moduleCmp;
		pressed = false;
		hover = false;
		construct(parent);
	}

	protected void createSubModuleTree() {
		this.subModuleTree = new Tree(getBody(), SWT.NONE);
		this.subModuleTree.setBackground(LnfManager.getLnf().getColor("SubModuleTree.background")); //$NON-NLS-1$
		this.subModuleTree.setLinesVisible(false);
		this.subModuleTree.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				TreeItem[] selection = getTree().getSelection();
				activeSubModule = (SubModuleNode) selection[0].getData();
				resize();
			}

		});

		this.subModuleTree.addListener(SWT.Paint, new Listener() {

			public void handleEvent(Event event) {
				onTreePaint(event.gc);
			}

		});

		this.subModuleTree.addListener(SWT.Expand, new Listener() {

			public void handleEvent(Event event) {
				handleExpandCollapse(event, true);
			}

		});

		this.subModuleTree.addListener(SWT.Collapse, new Listener() {

			public void handleEvent(Event event) {
				handleExpandCollapse(event, false);
			}

		});

	}

	private void handleExpandCollapse(Event event, boolean expand) {
		TreeItem item = (TreeItem) event.item;
		INavigationNode<?> node = (INavigationNode<?>) item.getData();
		node.setExpanded(expand);
		resize();
	}

	protected void resize() {
		moduleCmp.updated();
	}

	public SubModuleNode getSelection() {
		return activeSubModule;
	}

	public IModuleNode getModuleNode() {
		return moduleCmp.getModelNode();
	}

	private void construct(Composite parent) {
		body = new Composite(parent, SWT.None);
		body.setLayout(new FillLayout());
		createBodyContent(parent);
	}

	protected void createBodyContent(final Composite parent) {
		createSubModuleTree();
		// getTree().setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	public Composite getBody() {
		return body;
	}

	public int getOpenHeight() {
		IModuleNode moduleNode = getModuleNode();
		int itemHeight = getTree().getItemHeight();
		return moduleNode.calcDepth() * itemHeight + 1;
	}

	public Tree getTree() {
		return subModuleTree;
	}

	/**
	 * Clipps (if necessary) the text of the given tree item and all child
	 * items.
	 * 
	 * @param gc
	 * @param item -
	 *            tree item
	 * @return true: some text was clipped; false: no text was clipped
	 */
	private boolean clipSubModuleTexts(GC gc, TreeItem item) {

		boolean clipped = clipSubModuleText(gc, item);

		TreeItem[] items = item.getItems();
		for (TreeItem childItem : items) {
			if (clipSubModuleTexts(gc, childItem)) {
				clipped = true;
			}
		}

		return clipped;

	}

	/**
	 * Clipps (if necessary) the text of the given tree item.
	 * 
	 * @param gc
	 * @param item -
	 *            tree item
	 * @return true: text was clipped; false: text was not clipped
	 */
	private boolean clipSubModuleText(GC gc, TreeItem item) {

		boolean clipped = false;

		Rectangle bodyBounds = getBody().getBounds();
		SubModuleNode data = (SubModuleNode) item.getData();
		String label = data.getLabel();
		Rectangle itemBounds = item.getBounds();
		int maxWidth = bodyBounds.width - itemBounds.x - 5;
		label = SwtUtilities.clipText(gc, label, maxWidth);
		item.setText(label);

		clipped = !data.getLabel().equals(label);
		return clipped;

	}

	/**
	 * Clipps (if necessary) the text of the tree items and hiddes the scroll
	 * bars.
	 * 
	 * @param gc
	 */
	private void onTreePaint(GC gc) {

		TreeItem[] items = getTree().getItems();
		for (TreeItem item : items) {
			clipSubModuleTexts(gc, item);
		}

		SwtOsUtilities.hiddeSrollBars(getTree());

	}

	/**
	 * @return the pressed
	 */
	public boolean isPressed() {
		return pressed;
	}

	/**
	 * @param pressed
	 *            the pressed to set
	 */
	public void setPressed(boolean pressed) {
		if (this.pressed != pressed) {
			this.pressed = pressed;
			if (!parent.isDisposed()) {
				parent.redraw();
			}
		}
	}

	/**
	 * @return the hover
	 */
	public boolean isHover() {
		return hover;
	}

	/**
	 * @param hover
	 *            the hover to set
	 */
	public void setHover(boolean hover) {
		if (this.hover != hover) {
			this.hover = hover;
			if (!parent.isDisposed()) {
				parent.redraw();
			}
		}
	}

	/**
	 * Disposes this module item.
	 */
	public void dispose() {
		getBody().dispose();
		getTree().dispose();
	}

	/**
	 * Returns a rectangle describing the size and location of this module item.
	 * 
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Sets a rectangle describing the size and location of this module item.
	 * 
	 * @param bounds
	 *            the bounds to set
	 */
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

}
