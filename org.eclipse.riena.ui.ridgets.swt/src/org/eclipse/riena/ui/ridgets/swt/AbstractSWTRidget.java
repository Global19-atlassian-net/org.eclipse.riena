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
package org.eclipse.riena.ui.ridgets.swt;

import org.eclipse.core.databinding.BindingException;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.riena.internal.ui.ridgets.swt.AbstractSWTWidgetRidget;
import org.eclipse.riena.ui.core.marker.HiddenMarker;

/**
 * Ridget for an SWT control.
 */
public abstract class AbstractSWTRidget extends AbstractSWTWidgetRidget {

	private FocusListener focusManager = new FocusManager();
	private boolean focusable;

	/**
	 * Checks that the given uiControl is assignable to the the given type.
	 * 
	 * @param uiControl
	 *            a uiControl, may be null
	 * @param type
	 *            a class instance (non-null)
	 * @throws BindingException
	 *             if the uiControl is not of the given type
	 */
	public static void assertType(Object uiControl, Class<?> type) {
		if ((uiControl != null) && !(type.isAssignableFrom(uiControl.getClass()))) {
			String expectedClassName = type.getSimpleName();
			String controlClassName = uiControl.getClass().getSimpleName();
			throw new BindingException("uiControl of  must be a " + expectedClassName + " but was a " //$NON-NLS-1$ //$NON-NLS-2$
					+ controlClassName);
		}
	}

	public AbstractSWTRidget() {
		focusable = true;
	}

	@Override
	public Control getUIControl() {
		return (Control) super.getUIControl();
	}

	@Override
	public final void requestFocus() {
		if (isFocusable()) {
			if (getUIControl() != null) {
				Control control = getUIControl();
				control.setFocus();
			}
		}
	}

	@Override
	public final boolean hasFocus() {
		if (getUIControl() != null) {
			Control control = getUIControl();
			return control.isFocusControl();
		}
		return false;
	}

	@Override
	public final boolean isFocusable() {
		return focusable;
	}

	@Override
	public final void setFocusable(boolean focusable) {
		if (this.focusable != focusable) {
			this.focusable = focusable;
		}
	}

	public boolean isVisible() {
		// check for "hidden.marker". This marker overrules any other visibility rule
		if (!getMarkersOfType(HiddenMarker.class).isEmpty()) {
			return false;
		}

		if (getUIControl() != null) {
			// the swt control is bound
			return getUIControl().isVisible();
		}
		// control is not bound
		return savedVisibleState;
	}

	@Override
	protected void unbindUIControl() {
		// save the state
		savedVisibleState = isVisible();
	}

	// helping methods
	// ////////////////

	/**
	 * Adds listeners to the <tt>uiControl</tt> after it was bound to the
	 * ridget.
	 */
	@Override
	protected final void installListeners() {
		if (getUIControl() != null) {
			getUIControl().addFocusListener(focusManager);
		}
	}

	/**
	 * Removes listeners from the <tt>uiControl</tt> when it is about to be
	 * unbound from the ridget.
	 */
	@Override
	protected final void uninstallListeners() {
		if (getUIControl() != null) {
			getUIControl().removeFocusListener(focusManager);
		}
	}

	@Override
	protected final void updateToolTip() {
		if (getUIControl() != null) {
			getUIControl().setToolTipText(getToolTipText());
		}
	}

	/**
	 * Focus listener that also prevents the widget corresponding to this ridget
	 * from getting the UI focus when the ridget is not focusable.
	 * 
	 * @see AbstractSWTRidget#setFocusable(boolean).
	 */
	private final class FocusManager extends FocusAdapter {

		@Override
		public void focusGained(FocusEvent e) {
			if (focusable) {
				fireFocusGained(new org.eclipse.riena.ui.ridgets.listener.FocusEvent(null, AbstractSWTRidget.this));
			} else {
				Control control = (Control) e.widget;
				Composite parent = control.getParent();
				Control target = findFocusTarget(control, parent.getTabList());
				if (target != null) {
					target.setFocus();
				} else { // no suitable control found, try one level up
					Composite pParent = parent.getParent();
					if (pParent != null) {
						target = findFocusTarget(parent, pParent.getTabList());
						if (target != null) {
							target.setFocus();
						}
					}
				}
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (focusable) {
				fireFocusLost(new org.eclipse.riena.ui.ridgets.listener.FocusEvent(AbstractSWTRidget.this, null));
			}
		}

		private Control findFocusTarget(Control control, Control[] controls) {
			int myIndex = -1;
			// find index for control
			for (int i = 0; myIndex == -1 && i < controls.length; i++) {
				if (controls[i] == control) {
					myIndex = i;
				}
			}
			// find next possible control
			int result = -1;
			for (int i = myIndex + 1; result == -1 && i < controls.length; i++) {
				Control candidate = controls[i];
				if (candidate.isEnabled() && candidate.isVisible()) {
					result = i;
				}
			}
			// find previous possible control
			for (int i = 0; result == -1 && i < myIndex; i++) {
				Control candidate = controls[i];
				if (candidate.isEnabled() && candidate.isVisible()) {
					result = i;
				}
			}
			return result != -1 ? controls[result] : null;
		}
	};

}
