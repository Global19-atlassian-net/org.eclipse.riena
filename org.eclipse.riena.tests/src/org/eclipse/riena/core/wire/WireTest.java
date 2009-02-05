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
package org.eclipse.riena.core.wire;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.riena.internal.tests.Activator;
import org.eclipse.riena.tests.RienaTestCase;
import org.eclipse.riena.tests.collect.NonUITestCase;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Test the {@code Wire} stuff.
 */
@NonUITestCase
public class WireTest extends RienaTestCase {

	private BundleContext context = Activator.getDefault().getContext();
	private ServiceRegistration schtonkReg;
	private ServiceRegistration stunkReg;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.riena.tests.RienaTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		schtonkReg = context.registerService(Schtonk.class.getName(), new Schtonk(), null);
		stunkReg = context.registerService(Stunk.class.getName(), new Stunk(), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.riena.tests.RienaTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		context.ungetService(schtonkReg.getReference());
		context.ungetService(stunkReg.getReference());
		super.tearDown();
	}

	public void testWiringBean() {
		Bean bean = new Bean();
		WirePuller puller = Wire.instance(bean).andStart(context);
		assertTrue(bean.hasSchtonk());
		puller.stop();
	}

	@SuppressWarnings("unchecked")
	public void testWiringDeeplyAndCheckSequenceConstraint() {
		BeanOnBean beanOnBean = new BeanOnBean();
		SequenceUtil.init();
		WirePuller puller = Wire.instance(beanOnBean).andStart(context);
		SequenceUtil.assertExpected(BeanWiring.class, BeanOnBeanWiring.class);
		assertTrue(beanOnBean.hasSchtonk());
		assertTrue(beanOnBean.hasStunk());
		SequenceUtil.init();
		puller.stop();
		SequenceUtil.assertExpected(BeanOnBeanWiring.class, BeanWiring.class);
	}

	public void testWiringDeeply() {
		NoWirableBean noWirableBean = new NoWirableBean();
		WirePuller puller = Wire.instance(noWirableBean).andStart(context);
		assertTrue(noWirableBean.hasSchtonk());
		puller.stop();
	}

	public void testWireMocking() {
		Map<Class<?>, Class<? extends IWiring>> wireWrapMocks = new HashMap<Class<?>, Class<? extends IWiring>>();
		wireWrapMocks.put(Bean.class, BeanWiringMock.class);
		WirePuller.injectWireMocks(wireWrapMocks);
		Bean bean = new Bean();
		WirePuller puller = Wire.instance(bean).andStart(context);
		assertTrue(bean.isSchtonkSchtonk());
		puller.stop();

		WirePuller.injectWireMocks(null);
	}
}
