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
package org.eclipse.riena.ui.ridgets.swt.views;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.lnf.renderer.DialogBorderRenderer;
import org.eclipse.riena.ui.swt.utils.ImageStore;

/**
 * Area to grab so that the shell can be resized.
 */
public class DialogGrabCorner extends Composite {

	/**
	 * @param parent
	 * @param style
	 */
	public DialogGrabCorner(Composite parent, int style) {

		super(parent, style);
		setBackground(parent.getBackground());
		setData("sizeexecutor", "grabcorner"); //$NON-NLS-1$ //$NON-NLS-2$

		setLayoutData();

		addPaintListener(new GrabPaintListener());
		new DialogGrabCornerListenerWithTracker(this);
	}

	/**
	 * Sets the (form) layout data for the grab corner.
	 */
	private void setLayoutData() {
		Point grabCornerSize = getGrabCornerSize();
		FormData grabFormData = new FormData();
		grabFormData.width = grabCornerSize.x;
		grabFormData.height = grabCornerSize.y;
		grabFormData.bottom = new FormAttachment(100, 0);
		grabFormData.right = new FormAttachment(100, 0);

		setLayoutData(grabFormData);
	}

	/**
	 * Returns the size of the grab corner (including the border (right,bottom)
	 * of the shell)
	 * 
	 * @return size of grab corner
	 */
	public static Point getGrabCornerSize() {
		Point grabCornerSize = new Point(0, 0);
		Image grabCorner = getGrabCornerImage();

		if ((grabCorner != null) && isResizeable()) {
			grabCornerSize = new Point(grabCorner.getImageData().width, grabCorner.getImageData().height);
		}
		return grabCornerSize;
	}

	/**
	 * Returns if the shell is resizeable or not.
	 * 
	 * @return true if shell is resizeable; otherwise false
	 */
	public static boolean isResizeable() {
		return true; // TODO: make this a value that can be set for each dialog
	}

	/**
	 * Returns the image of the grab corner.
	 * 
	 * @return grab corner image
	 */
	private static Image getGrabCornerImage() {
		Image image = LnfManager.getLnf().getImage(LnfKeyConstants.DIALOG_GRAB_CORNER_IMAGE);
		if (image == null) {
			image = ImageStore.getInstance().getMissingImage();
		}
		return image;
	}

	/**
	 * Returns the width of the shell border.
	 * 
	 * @return border width
	 */
	private static int getShellBorderWidth() {
		DialogBorderRenderer borderRenderer = (DialogBorderRenderer) LnfManager.getLnf().getRenderer(
				LnfKeyConstants.DIALOG_BORDER_RENDERER);
		if (borderRenderer != null) {
			return borderRenderer.getBorderWidth();
		} else {
			return 0;
		}
	}

	/**
	 * This Listener paint the grab corner.
	 */
	private static class GrabPaintListener implements PaintListener {

		public void paintControl(PaintEvent e) {
			GC gc = e.gc;
			Image grabCornerImage = getGrabCornerImage();
			if (grabCornerImage != null) {
				gc.drawImage(grabCornerImage, 0, 0);
			}
		}

	}

}
