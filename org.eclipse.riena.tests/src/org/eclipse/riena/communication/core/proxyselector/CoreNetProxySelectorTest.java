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
package org.eclipse.riena.communication.core.proxyselector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.riena.tests.RienaTestCase;
import org.eclipse.riena.tests.collect.ManualTestCase;
import org.eclipse.riena.tests.nanohttp.TestServer;

/**
 * Tests the {@code CoreNetProxySelector}.
 */
@ManualTestCase
public class CoreNetProxySelectorTest extends RienaTestCase {

	/**
	 * To run this test it is necessary that "http://localhost/pac42.js" is
	 * entered in the "automatic configuration script" of the "internet options"
	 * dialog.
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public void testPac() {
		TestServer server = null;
		try {
			server = new TestServer(80, getFile("pac42.js").getParentFile());
			ProxySelector selector = new CoreNetProxySelector();
			List<Proxy> proxies = selector.select(new URI("http://www.eclipse.org"));
			assertEquals(2, proxies.size());
			assertEquals("idproxy", ((InetSocketAddress) proxies.get(0).address()).getHostName());
			assertEquals(3128, ((InetSocketAddress) proxies.get(0).address()).getPort());
		} catch (Throwable e) {
			fail();
		} finally {
			server.stop();
		}
	}
}
