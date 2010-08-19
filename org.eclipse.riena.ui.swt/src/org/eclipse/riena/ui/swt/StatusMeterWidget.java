/*******************************************************************************
 * Copyright (c) 2007, 2010 compeople AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.riena.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.riena.ui.swt.StatusMeter.StatusMeterBuilder;

/**
 * A widget displaying a StatusMeter image.
 * 
 * @see StatusMeter
 * @since 2.1
 */
public final class StatusMeterWidget extends Composite {
	private int value = 0;
	private int maximum = 100;
	private int minimum = 0;

	private final StatusMeterBuilder builder = StatusMeter.widgetDefault();

	/**
	 * Creates a {@link StatusMeterWidget} without a style.
	 * 
	 * @param parent
	 */
	public StatusMeterWidget(final Composite parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Create a {@link StatusMeterWidget}. The style is equal to the style of
	 * {@link Composite}.
	 * 
	 * @see Composite
	 * @param parent
	 * @param style
	 */
	public StatusMeterWidget(final Composite parent, final int style) {
		super(parent, style);

		this.addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent e) {
				final Rectangle bounds = getBounds();

				if (bounds.width != 0 && bounds.height != 0) {
					e.gc.drawImage(builder.width(bounds.width).height(bounds.height).getImage(), 0, 0);
				}
			}
		});
	}

	/**
	 * Set the value.
	 * 
	 * @see StatusMeterBuilder#value(int)
	 * @param value
	 */
	public void setValue(final int value) {
		this.value = value;
		builder.value(value);
		redraw();
	}

	/**
	 * @return The value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Set the maximum value.
	 * 
	 * @see StatusMeterBuilder#maximum(int)
	 * @param max
	 */
	public void setMaximum(final int max) {
		this.maximum = max;
		builder.maximum(max);
	}

	/**
	 * @return The maximum value
	 */
	public int getMaximum() {
		return maximum;
	}

	/**
	 * Set the minimum.
	 * 
	 * @see StatusMeterBuilder#minimum(int)
	 * @param min
	 */
	public void setMinimum(final int min) {
		this.minimum = min;
		builder.minimum(min);
	}

	/**
	 * @return The minimum value
	 */
	public int getMinimum() {
		return minimum;
	}

	/**
	 * Set the border color.
	 * 
	 * @see StatusMeterBuilder#borderColor(Color)
	 * @param color
	 */
	public void setBorderColor(final Color color) {
		builder.borderColor(color);
	}

	/**
	 * Set the background color.
	 * 
	 * @see StatusMeterBuilder#backgroundColor(Color)
	 * @param color
	 */
	public void setBackgroundColor(final Color color) {
		builder.backgroundColor(color);
	}

	/**
	 * Set the gradient start color.
	 * 
	 * @see StatusMeterBuilder#gradientStartColor(Color)
	 * @param color
	 */
	public void setGradientStartColor(final Color color) {
		builder.gradientStartColor(color);
	}

	/**
	 * Set the gradient end color.
	 * 
	 * @see StatusMeterBuilder#gradientEndColor(Color)
	 * @param color
	 */
	public void setGradientEndColor(final Color color) {
		builder.gradientEndColor(color);
	}
}
