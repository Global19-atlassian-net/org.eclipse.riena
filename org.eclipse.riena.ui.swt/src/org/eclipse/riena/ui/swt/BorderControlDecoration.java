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
package org.eclipse.riena.ui.swt;

import org.osgi.service.log.LogService;

import org.eclipse.equinox.log.Logger;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.riena.core.Log4r;
import org.eclipse.riena.internal.ui.swt.Activator;

/**
 * This class renders a border decoration around the control.
 * <p>
 * 
 * @since 2.0
 */
public class BorderControlDecoration {

	private static final Logger LOGGER = Log4r.getLogger(Activator.getDefault(), BorderControlDecoration.class);
	private static final int DEFAULT_BORDER_WIDTH = 1;

	private Control control;
	private DisposeListener disposeListener;
	private PaintListener paintListener;
	private boolean visible;
	private Color borderColor;
	private int borderWidth = DEFAULT_BORDER_WIDTH;

	/**
	 * Creates a new instance of {@code ControlDecoration} for decorating the
	 * specified control.
	 * 
	 * @param control
	 *            the control to be decorated
	 */
	public BorderControlDecoration(Control control) {

		this(control, DEFAULT_BORDER_WIDTH);

	}

	/**
	 * Creates a new instance of {@code ControlDecoration} for decorating the
	 * specified control.
	 * 
	 * @param control
	 *            the control to be decorated
	 */
	public BorderControlDecoration(Control control, int borderWidth) {

		this.control = control;
		// workaround for DatePicker
		if (this.control.getParent() instanceof DatePickerComposite) {
			this.control = this.control.getParent();
		}
		this.borderWidth = borderWidth;
		if (this.borderWidth < 0) {
			borderWidth = 0;
			LOGGER.log(LogService.LOG_WARNING, "BorderWidth is lower eqauls 0!"); //$NON-NLS-1$
		}

		visible = false;
		addControlListeners();

	}

	/**
	 * Add any listeners needed on the target control and on the composite where
	 * the decoration is to be rendered.
	 */
	private void addControlListeners() {

		disposeListener = new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				dispose();
			}
		};
		control.addDisposeListener(disposeListener);

		paintListener = new PaintListener() {
			public void paintControl(PaintEvent event) {
				if (shouldShowDecoration()) {
					Control uiControl = (Control) event.widget;
					Rectangle rect = getDecorationRectangle(uiControl);
					onPaint(event.gc, rect);
				}
			}
		};

		// We do not know which parent in the control hierarchy
		// is providing the decoration space, so hook all the way up, until
		// the shell or the specified parent composite is reached.
		Composite c = control.getParent();
		Rectangle controlBounds = control.getBounds();
		while (c != null) {
			installCompositeListeners(c);
			if (c instanceof Shell) {
				// We just installed on a shell, so don't go further
				c = null;
			} else {
				Rectangle b1 = getDecorationRectangle(c);
				if (((b1.x >= 0) && (b1.y >= 0))
						&& ((controlBounds.width + getBorderWidth() <= b1.width) && (controlBounds.height
								+ getBorderWidth() <= b1.height))) {
					c = null;
				} else {
					c = c.getParent();
				}
			}
		}

	}

	/**
	 * Disposes this {@code BorderControlDecoration}. Unhooks any listeners that
	 * have been installed on the target control. This method has no effect if
	 * the receiver is already disposed.
	 */
	public void dispose() {
		if (control == null) {
			return;
		}
		removeControlListeners();
		control = null;
	}

	/**
	 * Removes every listeners installed on the controls.
	 **/
	private void removeControlListeners() {

		if (control == null) {
			return;
		}
		control.removeDisposeListener(disposeListener);
		disposeListener = null;

		Composite c = control.getParent();
		while (c != null) {
			removeCompositeListeners(c);
			if (c instanceof Shell) {
				// We previously installed listeners only up to the first Shell
				// encountered, so stop.
				c = null;
			} else {
				c = c.getParent();
			}
		}
		paintListener = null;
	}

	private void installCompositeListeners(Composite c) {
		if (!c.isDisposed()) {
			c.addPaintListener(paintListener);
		}
	}

	private void removeCompositeListeners(Composite c) {
		if (!c.isDisposed()) {
			c.removePaintListener(paintListener);
		}
	}

	/**
	 * Draws the border.
	 * 
	 * @param gc
	 *            graphical context
	 * @param rect
	 *            the rectangle to draw
	 */
	private void onPaint(GC gc, Rectangle rect) {

		Color previousForeground = gc.getForeground();
		if (getBorderColor() != null) {
			gc.setForeground(getBorderColor());
		} else {
			LOGGER.log(LogService.LOG_WARNING, "BorderColor is null!"); //$NON-NLS-1$
		}
		for (int i = 0; i < getBorderWidth(); i++) {
			gc.drawRectangle(rect.x + i, rect.y + i, rect.width - i * 2, rect.height - i * 2);
		}
		gc.setForeground(previousForeground);

	}

	/**
	 * Returns whether the decoration should be shown or it should not.
	 * 
	 * @return {@code true} if the decoration should be shown, {@code false} if
	 *         it should not.
	 */
	private boolean shouldShowDecoration() {
		if (!visible) {
			return false;
		}
		if ((control == null) || control.isDisposed()) {
			return false;
		}
		if (!control.isVisible()) {
			return false;
		}
		if (getBorderWidth() <= 0) {
			return false;
		}
		return true;
	}

	/**
	 * Shows the control decoration. This message has no effect if the
	 * decoration is already showing.
	 */
	public void show() {
		if (!visible) {
			visible = true;
			update();
		}
	}

	/**
	 * Hides the control decoration and any associated hovers. This message has
	 * no effect if the decoration is already hidden.
	 */
	public void hide() {
		if (visible) {
			visible = false;
			update();
		}
	}

	/**
	 * Something has changed, requiring redraw. Redraw the decoration.
	 */
	private void update() {
		if ((control == null) || control.isDisposed() || getBorderWidth() <= 0) {
			return;
		}
		Rectangle rect = getDecorationRectangle(control.getShell());
		// Redraw this rectangle in all children
		control.getShell().redraw(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2, true);
		control.getShell().update();
	}

	/**
	 * Return the rectangle in which the decoration should be rendered, in
	 * coordinates relative to the specified control.
	 * 
	 * @param targetControl
	 *            the control whose coordinates should be used
	 * @return the rectangle in which the decoration should be rendered
	 */
	private Rectangle getDecorationRectangle(Control targetControl) {

		if (control == null) {
			return new Rectangle(0, 0, 0, 0);
		}

		Rectangle controlBounds = control.getBounds();
		int x = controlBounds.x - getBorderWidth();
		int y = controlBounds.y - getBorderWidth();
		Point globalPoint = control.getParent().toDisplay(x, y);
		Point targetPoint;
		if (targetControl == null) {
			targetPoint = globalPoint;
		} else {
			targetPoint = targetControl.toControl(globalPoint);
		}
		int width = controlBounds.width + getBorderWidth() * 2 - 1;
		int height = controlBounds.height + getBorderWidth() * 2 - 1;
		return new Rectangle(targetPoint.x, targetPoint.y, width, height);

	}

	/**
	 * Sets the color of the border.
	 * 
	 * @param borderColor
	 *            border color
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		update();
	}

	/**
	 * Returns the color of the border.
	 * 
	 * @return border color
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Returns the width of the border.
	 * 
	 * @return border width
	 */
	public int getBorderWidth() {
		return borderWidth;
	}

}
