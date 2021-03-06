/*******************************************************************************
 * Copyright (c) 2007, 2014 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.internal.ui.ridgets.swt;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;

import org.eclipse.riena.ui.swt.utils.SWTBindingPropertyLocator;

/**
 * This class stores the properties of an item.
 */
public abstract class AbstractItemProperties {

	private final AbstractItemRidget ridget;
	private final int style;
	private final Object data;
	private final String id;
	private final String text;
	private final Image image;
	private final Listener[] selectionListeners;

	/**
	 * Creates a new instance of {@code ItemProperties}.<br>
	 * Reads the properties of the item of the given ridget and stores them.
	 * 
	 * @param ridget
	 *            ridget with item
	 */
	public AbstractItemProperties(final AbstractItemRidget ridget) {
		final Item item = ridget.getUIControl();
		style = item.getStyle();
		data = item.getData();
		id = ridget.getID();
		text = item.getText();
		image = item.getImage();
		selectionListeners = item.getListeners(SWT.Selection);
		this.ridget = ridget;
	}

	/**
	 * Sets the stored properties value to the given item.
	 * 
	 * @param item
	 */
	protected void setAllProperties(final Item item, final boolean addListeners) {
		item.setData(data);
		final SWTBindingPropertyLocator locator = SWTBindingPropertyLocator.getInstance();
		locator.setBindingProperty(item, id);
		item.setText(text);
		if (image == null || !image.isDisposed()) {
			item.setImage(image);
		}
		if (addListeners) {
			for (final Listener listener : selectionListeners) {
				item.addListener(SWT.Selection, listener);
			}
		}

	}

	protected int getStyle() {
		return style;
	}

	protected AbstractItemRidget getRidget() {
		return ridget;
	}

	protected IContributionItem getContributionItem() {
		if (data instanceof IContributionItem) {
			return (IContributionItem) data;
		} else {
			return null;
		}
	}

	/**
	 * Creates a new item for this stored properties.
	 * 
	 * @return created item
	 */
	abstract Item createItem();

}
