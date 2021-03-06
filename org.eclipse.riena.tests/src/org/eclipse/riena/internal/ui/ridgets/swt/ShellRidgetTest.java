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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.riena.core.test.collect.UITestCase;
import org.eclipse.riena.core.util.Nop;
import org.eclipse.riena.ui.ridgets.IRidget;

/**
 * Tests of the class {@link ShellRidget}.
 */
@UITestCase
public class ShellRidgetTest extends AbstractSWTRidgetTest {

	@Override
	protected IRidget createRidget() {
		return new MockShellRidget();
	}

	@Override
	protected Widget createWidget(final Composite parent) {
		return getShell();
	}

	@Override
	protected MockShellRidget getRidget() {
		return (MockShellRidget) super.getRidget();
	}

	@Override
	protected Shell getWidget() {
		return (Shell) super.getWidget();
	}

	/**
	 * @see org.eclipse.riena.internal.ui.ridgets.swt.AbstractSWTRidgetTest#testGetFocusable()
	 */
	@Override
	public void testGetFocusable() {

		assertFalse(getRidget().isFocusable());

		getRidget().setFocusable(true);

		assertFalse(getRidget().isFocusable());
	}

	/**
	 * Tests the method {@code hasChanged}.
	 */
	public void testHasChanged() {

		assertTrue(getRidget().hasChanged("a", "b"));
		assertFalse(getRidget().hasChanged("a", "a"));
		assertTrue(getRidget().hasChanged(null, "b"));
		assertTrue(getRidget().hasChanged("a", null));
		assertFalse(getRidget().hasChanged(null, null));
	}

	public void testSetActive() throws Exception {

		getRidget().setActive(false);

		assertFalse(getRidget().isEnabled());
		assertFalse(getWidget().isEnabled());

		getRidget().setActive(true);

		assertTrue(getRidget().isEnabled());
		assertTrue(getWidget().isEnabled());
	}

	@Override
	public void testGetMenuItemCount() {
		final IRidget ridget = getRidget();

		try {
			ridget.getMenuItemCount();
			fail("UnsupportedOperationException expected"); //$NON-NLS-1$
		} catch (final UnsupportedOperationException expected) {
			Nop.reason("UnsupportedOperationException expected"); //$NON-NLS-1$
		}
	}

	@Override
	public void testGetMenuItem() {
		final IRidget ridget = getRidget();

		try {
			ridget.getMenuItem(0);
			fail("UnsupportedOperationException expected"); //$NON-NLS-1$
		} catch (final UnsupportedOperationException expected) {
			Nop.reason("UnsupportedOperationException expected"); //$NON-NLS-1$
		}
	}

	@Override
	public void testAddMenuItem() {
		final IRidget ridget = getRidget();
		final String menuItemWithoutIconText = "MenuItemWithoutIcon"; //$NON-NLS-1$

		try {
			ridget.addMenuItem(menuItemWithoutIconText);
			fail("UnsupportedOperationException expected"); //$NON-NLS-1$
		} catch (final UnsupportedOperationException expected) {
			Nop.reason("UnsupportedOperationException expected"); //$NON-NLS-1$
		}
	}

	@Override
	public void testRemoveMenuItem() {
		final IRidget ridget = getRidget();
		final String menuItemWithIconText = "MenuItemWithIcon"; //$NON-NLS-1$

		try {
			ridget.removeMenuItem(menuItemWithIconText);
			fail("UnsupportedOperationException expected"); //$NON-NLS-1$
		} catch (final UnsupportedOperationException expected) {
			Nop.reason("UnsupportedOperationException expected"); //$NON-NLS-1$
		}
	}

	@Override
	public void testGetMenuItemEmptyContextMenu() {
		try {
			final IRidget ridget = getRidget();
			ridget.getMenuItem(0);
			fail("UnsupportedOperationException expected"); //$NON-NLS-1$
		} catch (final UnsupportedOperationException expected) {
			Nop.reason("UnsupportedOperationException expected"); //$NON-NLS-1$
		}
	}

	@Override
	public void testGetMenuItemNotExistingItem() {
		final IRidget ridget = getRidget();
		try {
			ridget.getMenuItem(0);
			fail("UnsupportedOperationException expected"); //$NON-NLS-1$
		} catch (final UnsupportedOperationException expected) {
			Nop.reason("UnsupportedOperationException expected"); //$NON-NLS-1$
		}
	}

	/**
	 * This class reduces the visibility of some protected method for testing.
	 */
	private static class MockShellRidget extends ShellRidget {

		@Override
		public boolean hasChanged(final Object oldValue, final Object newValue) {
			return super.hasChanged(oldValue, newValue);
		}

	}

}
