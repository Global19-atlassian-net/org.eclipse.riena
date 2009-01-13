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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.easymock.EasyMock;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.riena.core.marker.IMarker;
import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.tests.RienaTestCase;
import org.eclipse.riena.tests.UITestHelper;
import org.eclipse.riena.tests.collect.UITestCase;
import org.eclipse.riena.ui.core.marker.DisabledMarker;
import org.eclipse.riena.ui.core.marker.MandatoryMarker;
import org.eclipse.riena.ui.core.marker.OutputMarker;
import org.eclipse.riena.ui.ridgets.IMarkableRidget;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.listener.FocusEvent;
import org.eclipse.riena.ui.ridgets.listener.IFocusListener;
import org.eclipse.riena.ui.tests.base.PropertyChangeEventEquals;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * Tests for the class {@link AbstractSwtRidget}.
 */
@UITestCase
public abstract class AbstractSWTRidgetTest extends RienaTestCase {

	private Shell shell;
	private Widget widget;
	private IRidget ridget;
	private Text otherControl;
	private PropertyChangeListener propertyChangeListenerMock;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Display display = Display.getDefault();

		Realm realm = SWTObservables.getRealm(display);
		assertNotNull(realm);
		ReflectionUtils.invokeHidden(realm, "setDefault", realm);

		shell = new Shell();
		shell.setLayout(new RowLayout(SWT.VERTICAL));

		widget = createWidget(shell);

		ridget = createRidget();
		ridget.setUIControl(widget);
		propertyChangeListenerMock = EasyMock.createMock(PropertyChangeListener.class);
		ridget.addPropertyChangeListener(propertyChangeListenerMock);

		otherControl = new Text(shell, SWT.SINGLE);
		otherControl.setText("other focusable widget");

		shell.setSize(130, 100);
		shell.setLocation(0, 0);
		shell.open();
	}

	@Override
	protected void tearDown() throws Exception {
		ridget = null;
		widget.dispose();
		widget = null;
		otherControl.dispose();
		otherControl = null;
		shell.dispose();
		shell = null;

		super.tearDown();
	}

	// protected methods
	// //////////////////

	protected abstract Widget createWidget(final Composite parent);

	protected abstract IRidget createRidget();

	protected Widget getWidget() {
		return widget;
	}

	protected IRidget getRidget() {
		return ridget;
	}

	protected final Shell getShell() {
		return shell;
	}

	// easy mock helper methods
	// /////////////////////////

	protected final void verifyPropertyChangeEvents() {
		EasyMock.verify(propertyChangeListenerMock);
	}

	protected final void expectNoPropertyChangeEvent() {
		EasyMock.reset(propertyChangeListenerMock);
		EasyMock.replay(propertyChangeListenerMock);
	}

	protected final void expectPropertyChangeEvents(PropertyChangeEvent... propertyChangeEvents) {
		EasyMock.reset(propertyChangeListenerMock);
		for (PropertyChangeEvent propertyChangeEvent : propertyChangeEvents) {
			propertyChangeListenerMock.propertyChange(createArgumentMatcher(propertyChangeEvent));
		}
		EasyMock.replay(propertyChangeListenerMock);
	}

	protected final void expectPropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		expectPropertyChangeEvents(new PropertyChangeEvent(getRidget(), propertyName, oldValue, newValue));
	}

	// test methods
	// /////////////

	public void testIsVisible() {
		shell.open();
		ridget.setVisible(false);
		assertFalse(ridget.isVisible());

		ridget.setVisible(true);
		assertTrue(ridget.isVisible());
	}

	public void testGetToolTip() {

		if (!(getWidget() instanceof Control)) {
			// only Control supports tool tips
			return;
		}

		ridget.setUIControl(null);

		assertEquals(null, ridget.getToolTipText());

		ridget.setToolTipText("foo");

		assertEquals("foo", ridget.getToolTipText());

		Control aControl = (Control) getWidget();
		aControl.setToolTipText(null);
		ridget.setUIControl(aControl);

		assertEquals("foo", ridget.getToolTipText());
		assertEquals("foo", ((Control) ridget.getUIControl()).getToolTipText());
	}

	public void testGetFocusable() {

		if (!(getWidget() instanceof Control)) {
			// only Control supports tool tips
			return;
		}

		IRidget aRidget = getRidget();

		assertTrue(aRidget.isFocusable());

		aRidget.setFocusable(false);

		assertFalse(aRidget.isFocusable());

		aRidget.setFocusable(true);

		assertTrue(aRidget.isFocusable());
	}

	public void testSetFocusable() {

		if (!(getWidget() instanceof Control)) {
			// only Control supports focus
			return;
		}

		IRidget aRidget = getRidget();
		Control aControl = (Control) getWidget();
		otherControl.moveAbove(aControl);

		aControl.setFocus();
		if (aControl.isFocusControl()) { // skip if control cannot receive focus

			aRidget.setFocusable(false);
			otherControl.setFocus();

			assertTrue(otherControl.isFocusControl());

			UITestHelper.sendString(otherControl.getDisplay(), "\t");

			assertFalse(aControl.isFocusControl());

			aRidget.setFocusable(true);

			otherControl.setFocus();
			UITestHelper.sendString(otherControl.getDisplay(), "\t");

			assertTrue(aControl.isFocusControl());
		}
	}

	public void testRequestFocus() throws Exception {

		if (!(getWidget() instanceof Control)) {
			// only Control supports focus
			return;
		}

		Control aControl = (Control) getWidget();
		aControl.setFocus();
		if (aControl.isFocusControl()) { // skip if control cannot receive focus
			assertTrue(otherControl.setFocus());

			assertFalse(aControl.isFocusControl());
			assertFalse(ridget.hasFocus());

			final List<FocusEvent> focusGainedEvents = new ArrayList<FocusEvent>();
			final List<FocusEvent> focusLostEvents = new ArrayList<FocusEvent>();
			IFocusListener focusListener = new IFocusListener() {
				public void focusGained(FocusEvent event) {
					focusGainedEvents.add(event);
				}

				public void focusLost(FocusEvent event) {
					focusLostEvents.add(event);
				}
			};
			ridget.addFocusListener(focusListener);

			ridget.requestFocus();

			assertTrue(aControl.isFocusControl());
			assertTrue(ridget.hasFocus());
			assertEquals(1, focusGainedEvents.size());
			assertEquals(ridget, focusGainedEvents.get(0).getNewFocusOwner());
			assertEquals(0, focusLostEvents.size());

			assertTrue(otherControl.setFocus());

			assertFalse(aControl.isFocusControl());
			assertFalse(ridget.hasFocus());
			assertEquals(1, focusGainedEvents.size());
			assertEquals(1, focusLostEvents.size());
			assertEquals(ridget, focusLostEvents.get(0).getOldFocusOwner());

			ridget.removeFocusListener(focusListener);

			ridget.requestFocus();
			assertTrue(otherControl.setFocus());

			assertEquals(1, focusGainedEvents.size());
			assertEquals(1, focusLostEvents.size());
		}
	}

	public void testFiresTooltipProperty() {
		expectPropertyChangeEvent(IRidget.PROPERTY_TOOLTIP, null, "begood");

		ridget.setToolTipText("begood");

		verifyPropertyChangeEvents();
		expectNoPropertyChangeEvent();

		ridget.setToolTipText("begood");

		verifyPropertyChangeEvents();
		expectPropertyChangeEvent(IRidget.PROPERTY_TOOLTIP, "begood", null);

		ridget.setToolTipText(null);

		verifyPropertyChangeEvents();
	}

	public void testFiresMarkerProperty() {
		if (!(getRidget() instanceof IMarkableRidget)) {
			return;
		}
		IMarkableRidget markableRidget = (IMarkableRidget) getRidget();
		IMarker marker = new MandatoryMarker();
		HashSet<IMarker> before = new HashSet<IMarker>(markableRidget.getMarkers());
		HashSet<IMarker> after = new HashSet<IMarker>(before);
		after.add(marker);

		assertTrue(markableRidget.isEnabled());
		assertEquals(before.size() + 1, after.size());

		expectPropertyChangeEvent(IMarkableRidget.PROPERTY_MARKER, before, after);
		markableRidget.addMarker(marker);
		verifyPropertyChangeEvents();

		expectNoPropertyChangeEvent();
		markableRidget.addMarker(marker);
		verifyPropertyChangeEvents();

		expectPropertyChangeEvent(IMarkableRidget.PROPERTY_MARKER, after, before);
		markableRidget.removeMarker(marker);
		verifyPropertyChangeEvents();

		expectNoPropertyChangeEvent();
		markableRidget.removeMarker(marker);
		verifyPropertyChangeEvents();
	}

	public void testFiresDisabledPropertyUsingSetter() {
		if (!(getRidget() instanceof IMarkableRidget)) {
			return;
		}
		IMarkableRidget markableRidget = (IMarkableRidget) getRidget();
		markableRidget.removePropertyChangeListener(propertyChangeListenerMock);
		markableRidget.addPropertyChangeListener(IMarkableRidget.PROPERTY_ENABLED, propertyChangeListenerMock);

		assertTrue(markableRidget.isEnabled());

		expectNoPropertyChangeEvent();
		markableRidget.setEnabled(true);
		verifyPropertyChangeEvents();

		expectPropertyChangeEvent(IMarkableRidget.PROPERTY_ENABLED, Boolean.TRUE, Boolean.FALSE);
		markableRidget.setEnabled(false);
		verifyPropertyChangeEvents();

		expectNoPropertyChangeEvent();
		markableRidget.setEnabled(false);
		verifyPropertyChangeEvents();

		expectPropertyChangeEvent(IMarkableRidget.PROPERTY_ENABLED, Boolean.FALSE, Boolean.TRUE);
		markableRidget.setEnabled(true);
		verifyPropertyChangeEvents();
	}

	public void testFiresDisabledPropertyUsingAddRemove() {
		if (!(getRidget() instanceof IMarkableRidget)) {
			return;
		}
		IMarkableRidget markableRidget = (IMarkableRidget) getRidget();
		IMarker marker = new DisabledMarker();
		markableRidget.removePropertyChangeListener(propertyChangeListenerMock);
		markableRidget.addPropertyChangeListener(IMarkableRidget.PROPERTY_ENABLED, propertyChangeListenerMock);

		assertTrue(markableRidget.isEnabled());

		expectPropertyChangeEvent(IMarkableRidget.PROPERTY_ENABLED, Boolean.TRUE, Boolean.FALSE);
		markableRidget.addMarker(marker);
		verifyPropertyChangeEvents();

		expectNoPropertyChangeEvent();
		markableRidget.addMarker(marker);
		verifyPropertyChangeEvents();

		expectPropertyChangeEvent(IMarkableRidget.PROPERTY_ENABLED, Boolean.FALSE, Boolean.TRUE);
		markableRidget.removeMarker(marker);
		verifyPropertyChangeEvents();

		expectNoPropertyChangeEvent();
		markableRidget.removeMarker(marker);
		verifyPropertyChangeEvents();
	}

	/**
	 * Check that disabling / enabling works when we don't have a bound control.
	 */
	public void testDisableWithoutUIControl() {
		if (!(getRidget() instanceof IMarkableRidget)) {
			return;
		}
		IMarkableRidget markableRidget = (IMarkableRidget) getRidget();
		markableRidget.setUIControl(null);

		assertTrue(markableRidget.isEnabled());

		markableRidget.setEnabled(false);

		assertFalse(markableRidget.isEnabled());

		markableRidget.setEnabled(true);

		assertTrue(markableRidget.isEnabled());
	}

	public void testFiresOutputPropertyUsingSetter() {
		if (!(getRidget() instanceof IMarkableRidget)) {
			return;
		}
		IMarkableRidget markableRidget = (IMarkableRidget) getRidget();
		markableRidget.removePropertyChangeListener(propertyChangeListenerMock);
		markableRidget.addPropertyChangeListener(IMarkableRidget.PROPERTY_OUTPUT_ONLY, propertyChangeListenerMock);

		assertFalse(markableRidget.isOutputOnly());

		expectNoPropertyChangeEvent();
		markableRidget.setOutputOnly(false);
		verifyPropertyChangeEvents();

		expectPropertyChangeEvent(IMarkableRidget.PROPERTY_OUTPUT_ONLY, Boolean.FALSE, Boolean.TRUE);
		markableRidget.setOutputOnly(true);
		verifyPropertyChangeEvents();

		expectNoPropertyChangeEvent();
		markableRidget.setOutputOnly(true);
		verifyPropertyChangeEvents();

		expectPropertyChangeEvent(IMarkableRidget.PROPERTY_OUTPUT_ONLY, Boolean.TRUE, Boolean.FALSE);
		markableRidget.setOutputOnly(false);
		verifyPropertyChangeEvents();
	}

	public void testFiresOutputPropertyUsingAddRemove() {
		if (!(getRidget() instanceof IMarkableRidget)) {
			return;
		}
		IMarkableRidget markableRidget = (IMarkableRidget) getRidget();
		IMarker marker = new OutputMarker();
		markableRidget.removePropertyChangeListener(propertyChangeListenerMock);
		markableRidget.addPropertyChangeListener(IMarkableRidget.PROPERTY_OUTPUT_ONLY, propertyChangeListenerMock);

		assertFalse(markableRidget.isOutputOnly());

		expectPropertyChangeEvent(IMarkableRidget.PROPERTY_OUTPUT_ONLY, Boolean.FALSE, Boolean.TRUE);
		markableRidget.addMarker(marker);
		verifyPropertyChangeEvents();

		expectNoPropertyChangeEvent();
		markableRidget.addMarker(marker);
		verifyPropertyChangeEvents();

		expectPropertyChangeEvent(IMarkableRidget.PROPERTY_OUTPUT_ONLY, Boolean.TRUE, Boolean.FALSE);
		markableRidget.removeMarker(marker);
		verifyPropertyChangeEvents();

		expectNoPropertyChangeEvent();
		markableRidget.removeMarker(marker);
		verifyPropertyChangeEvents();
	}

	/**
	 * Tests that the combo control becomes visible after toggling
	 * ridget.setVisible().
	 */
	public void testBug257484() {
		Widget theWidget = getWidget();
		if (!(theWidget instanceof Control)) {
			// skip if not a control - only controls can be hidden / visible
			return;
		}
		IRidget theRidget = getRidget();
		Control control = (Control) theWidget;

		assertTrue(theRidget.isVisible());
		assertTrue(control.isVisible());

		theRidget.setVisible(false);

		assertFalse(theRidget.isVisible());
		assertFalse(control.isVisible());

		theRidget.setVisible(true);

		assertTrue(theRidget.isVisible());
		assertTrue(control.isVisible());
	}

	// helping methods
	// ////////////////

	private PropertyChangeEvent createArgumentMatcher(PropertyChangeEvent propertyChangeEvent) {
		return PropertyChangeEventEquals.eqPropertyChangeEvent(propertyChangeEvent);
	}

}
