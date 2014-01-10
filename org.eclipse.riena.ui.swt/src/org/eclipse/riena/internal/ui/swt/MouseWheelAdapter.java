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
package org.eclipse.riena.internal.ui.swt;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.riena.internal.ui.swt.facades.WorkbenchFacade;
import org.eclipse.riena.ui.swt.facades.SWTFacade;

/**
 * Handles mouse wheel events on a given control.
 * 
 * @since 6.0
 */
public class MouseWheelAdapter implements Listener {

	/**
	 * Implementations handle the received scroll events.
	 */
	public interface Scroller {

		/**
		 * @return <code>true</code> if scrolling is currently possible
		 */
		boolean mayScroll();

		/**
		 * @param scrollingStep
		 *            the pixels to scroll up
		 */
		void scrollUp(int scrollingStep);

		/**
		 * @param scrollingStep
		 *            the pixels to scroll down
		 */
		void scrollDown(int scrollingStep);

	}

	/**
	 * The pixels to scroll for one mouse wheel step
	 */
	public static final int SCROLLING_STEP = 20;

	private final Control control;
	private final Scroller scroller;

	public MouseWheelAdapter(final Control control, final Scroller scroller) {
		this.control = control;
		this.scroller = scroller;

		final Display display = control.getDisplay();
		SWTFacade.getDefault().addFilterMouseWheel(display, this);
		control.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				SWTFacade.getDefault().removeFilterMouseWheel(display, MouseWheelAdapter.this);
			}
		});
	}

	// for saving last event time
	private int lastEventTime = 0;

	public void handleEvent(final Event event) {
		// only go further if the event has a new time stamp
		if (scroller.mayScroll() && acceptEvent(event)) {
			lastEventTime = event.time;
			final Rectangle navigationComponentBounds = control.getBounds();

			// convert navigation bounds relative to display
			final Point navigationPtAtDisplay = control.toDisplay(0, 0);
			navigationComponentBounds.x = navigationPtAtDisplay.x;
			navigationComponentBounds.y = navigationPtAtDisplay.y;

			if (event.widget instanceof Control) {
				final Control widget = (Control) event.widget;
				// convert widget event point relative to display
				final Point evtPt = widget.toDisplay(event.getBounds().x, event.getBounds().y);
				// now check if inside navigation
				if (navigationComponentBounds.contains(evtPt.x, evtPt.y)) {
					if (event.count > 0) {
						scroller.scrollUp(SCROLLING_STEP);
					} else {
						scroller.scrollDown(SCROLLING_STEP);
					}
				}

			}
		}
	}

	private boolean acceptEvent(final Event event) {
		// check that this is the latest event
		final boolean isCurrent = event.time > lastEventTime;
		// 282089: check this window is has the focus, to avoid scrolling when
		// the mouse pointer happens to be over another overlapping window
		final Control control = (Control) event.widget;
		final Shell activeShell = WorkbenchFacade.getInstance().getActiveWindowShell();
		final boolean isActive = control.getShell() == activeShell;
		// 282091: check that this navigation component is visible. Since
		// we are using a display filter the navigation components of _each_
		// subapplication are notified when scrolling!
		final boolean isVisible = this.control.isVisible();
		return isCurrent && isActive && isVisible;
	}

}