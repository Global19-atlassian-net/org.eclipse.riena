/*******************************************************************************
 * Copyright (c) 2007, 2010 compeople AG and others.
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.riena.beans.common.StringBean;
import org.eclipse.riena.internal.core.test.collect.UITestCase;
import org.eclipse.riena.internal.ui.swt.test.UITestHelper;
import org.eclipse.riena.ui.ridgets.IBrowserRidget;
import org.eclipse.riena.ui.ridgets.swt.uibinding.SwtControlRidgetMapper;

/**
 * Tests for {@link BrowserRidget}
 */
@UITestCase
public class BrowserRidgetTest extends AbstractSWTRidgetTest {

	@Override
	protected Browser createWidget(final Composite parent) {
		return new Browser(parent, SWT.NONE);
	}

	@Override
	protected IBrowserRidget createRidget() {
		return new BrowserRidget();
	}

	@Override
	protected Browser getWidget() {
		return (Browser) super.getWidget();
	}

	@Override
	protected IBrowserRidget getRidget() {
		return (IBrowserRidget) super.getRidget();
	}

	public void testRidgetMapping() {
		final SwtControlRidgetMapper mapper = SwtControlRidgetMapper.getInstance();
		assertSame(BrowserRidget.class, mapper.getRidgetClass(getWidget()));
	}

	@Override
	public void testSetFocusable() {
		// skipping super.testSetFocusable() because of Bug 84532
		ok();
	}

	@Override
	public void testRequestFocus() {
		// skipping testRequestFocus() because of Bug 84532
		ok();
	}

	public void testBindToModel() {
		final IBrowserRidget ridget = getRidget();
		final String url1 = "http://www.redview.org";
		final String url2 = "http://www.eclipse.org";

		final StringBean bean = new StringBean(url1);
		ridget.bindToModel(bean, StringBean.PROP_VALUE);

		assertNull(ridget.getUrl());

		ridget.updateFromModel();

		assertEquals(url1, bean.getValue());
		assertEquals(url1, ridget.getUrl());

		bean.setValue(url2);

		assertEquals(url2, bean.getValue());
		assertEquals(url1, ridget.getUrl());

		ridget.updateFromModel();

		assertEquals(url2, bean.getValue());
		assertEquals(url2, ridget.getUrl());

		ridget.setUrl(url1);

		assertEquals(url1, bean.getValue());
		assertEquals(url1, ridget.getUrl());
	}

	public void testSetText() {
		final IBrowserRidget ridget = getRidget();
		final String text = "<html><body><h1>Riena</h1></body></html>";

		ridget.setText(text);

		assertEquals(text, ridget.getText());

		ridget.setText("");

		assertEquals("", ridget.getText());

		ridget.setText(null);

		assertEquals(null, ridget.getText());
	}

	public void testSetTextClearsUrl() {
		final IBrowserRidget ridget = getRidget();
		final String text = "<html><body><p>riena</p></body></html>";

		ridget.setUrl("http://eclipse.org");
		ridget.setText(text);

		assertEquals(null, ridget.getUrl());
		assertEquals(text, ridget.getText());
	}

	public void testSetTextOnOutputOnly() {
		final IBrowserRidget ridget = getRidget();
		final Browser control = getWidget();

		assertNull(ridget.getText());

		// allow ridget.setText() on output only
		ridget.setOutputOnly(true);
		final String text = "<hmtl><body><h2>Riena</h2></body></html>";
		ridget.setText(text);
		UITestHelper.readAndDispatch(control);

		assertEquals(text, ridget.getText());
		// browser may add line breaks - just check if 'Riena' is in the output
		assertTrue("control.text:" + control.getText(), control.getText().contains("Riena"));
	}

	public void testSetUrl() {
		final IBrowserRidget ridget = getRidget();

		ridget.setUrl("http://www.redview.org");

		assertEquals("http://www.redview.org", ridget.getUrl());
		// control.getUrl() is not reliable because of timing + network acccess
		// so I'm not testing that...

		ridget.setUrl("b o g u s");

		assertEquals("b o g u s", ridget.getUrl());

		ridget.setUrl("");

		assertEquals("", ridget.getUrl());

		ridget.setUrl(null);

		assertEquals(null, ridget.getUrl());

		ridget.setUrl("about:blank");

		assertEquals("about:blank", ridget.getUrl());
	}

	public void testSetUrlClearsText() {
		final IBrowserRidget ridget = getRidget();

		ridget.setText("riena");
		ridget.setUrl("http://eclipse.org");

		assertEquals("http://eclipse.org", ridget.getUrl());
		assertEquals(null, ridget.getText());
	}

	public void testSetUrlOnOutputOnly() {
		final IBrowserRidget ridget = getRidget();

		assertNull(ridget.getUrl());

		// allow ridget.setUrl() on output only
		ridget.setOutputOnly(true);
		ridget.setUrl("http://www.redview.org");
		// ridget.addPropertyChangeListener(new PropertyChangeListener() {
		//	public void propertyChange(PropertyChangeEvent evt) {
		//		System.out.println(evt.getPropertyName() + " " + evt.getNewValue());
		//	}
		// });

		assertEquals("http://www.redview.org", ridget.getUrl());

		// disallow widget.setUrl() on output only - can't test this because
		// widget.getUrl() is not reliable because of timing + network access
	}

	public void testSettersFireUrlEvents() {
		final IBrowserRidget ridget = getRidget();
		final String newValue = "http://www.redview.org";
		final String oldValue = ridget.getUrl();

		assertFalse(newValue.equals(ridget.getUrl()));

		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, "urlInternal", oldValue, newValue),
				new PropertyChangeEvent(ridget, IBrowserRidget.PROPERTY_URL, oldValue, newValue));
		ridget.setUrl(newValue);
		verifyPropertyChangeEvents();

		expectNoPropertyChangeEvent();
		ridget.setUrl(newValue);
		verifyPropertyChangeEvents();

		expectPropertyChangeEvents(new PropertyChangeEvent(ridget, "urlInternal", newValue, null),
				new PropertyChangeEvent(ridget, IBrowserRidget.PROPERTY_URL, newValue, null));
		ridget.setText("<html><body><h1>h1</h1></body></html>");
		verifyPropertyChangeEvents();

		expectNoPropertyChangeEvent();
		ridget.setText("<html><body><h1>h1</h1></body></html>");
		verifyPropertyChangeEvents();
	}

	public void testApplyTextOnRebind() throws Exception {
		final IBrowserRidget ridget = getRidget();
		final Browser control1 = getWidget();

		final String text = "<html><body><h1>Riena</h1></body></html>";
		ridget.setText(text);
		UITestHelper.readAndDispatch(control1);

		retry(new Runnable() {
			public void run() {
				// browser may add line breaks - just check if 'Riena' is in the output
				assertTrue("control1.text:" + control1.getText(), control1.getText().contains("Riena"));
			}
		}, control1, 3);

		final Browser control2 = createWidget(getShell());
		ridget.setUIControl(control2);
		UITestHelper.readAndDispatch(control2);

		retry(new Runnable() {
			public void run() {
				assertTrue("control2.text:" + control2.getText(), control2.getText().contains("Riena"));
			}
		}, control2, 3);
	}

	// helping methods
	//////////////////

	/**
	 * Execute the 'closure' op up to {@code tries}-times and process the ui
	 * queue between tries.
	 */
	private void retry(final Runnable op, final Control withControl, int tries) throws Exception {
		while (tries > 0) {
			try {
				op.run();
				tries = 0;
			} catch (final Exception exc) {
				tries--;
				if (tries > 0) {
					Thread.sleep(500);
					UITestHelper.readAndDispatch(withControl);
				} else {
					throw exc;
				}
			} catch (final Error error) {
				tries--;
				if (tries > 0) {
					Thread.sleep(500);
					UITestHelper.readAndDispatch(withControl);
				} else {
					throw error;
				}
			}
		}
	}
}
