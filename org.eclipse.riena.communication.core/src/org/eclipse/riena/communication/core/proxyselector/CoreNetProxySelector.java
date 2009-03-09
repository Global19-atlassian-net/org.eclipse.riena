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
import java.net.SocketAddress;
import java.net.URI;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.net.ProxyData;
import org.eclipse.core.internal.net.ProxyManager;
import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.runtime.Assert;
import org.eclipse.equinox.log.Logger;
import org.eclipse.riena.core.Log4r;
import org.eclipse.riena.core.util.Literal;
import org.eclipse.riena.internal.communication.core.Activator;
import org.osgi.service.log.LogService;

/**
 * This {@code ProxySelector} utilize the {@code ProxyManager}.<br>
 * <b>Note:</b> When using this {@code ProxySelector} do not forget to add the
 * optional required bundle org.eclipse.core.net and the accompanying
 * os-specific fragment bundle.
 */
public class CoreNetProxySelector extends ProxySelector {

	private static final List<Proxy> DIRECT_PROXY = Literal.list(Proxy.NO_PROXY);

	private final static Logger LOGGER = Log4r.getLogger(Activator.getDefault(), CoreNetProxySelector.class);

	@SuppressWarnings("restriction")
	public CoreNetProxySelector() {
		ProxyManager.getProxyManager().setProxiesEnabled(true);
		ProxyManager.getProxyManager().setSystemProxiesEnabled(true);
	}

	@SuppressWarnings("restriction")
	@Override
	public List<Proxy> select(URI uri) {
		Assert.isLegal(uri != null, "uri must not be null."); //$NON-NLS-1$
		IProxyData[] proxyDatas = ProxyManager.getProxyManager().select(uri);
		if (proxyDatas == null || proxyDatas.length == 0) {
			return DIRECT_PROXY;
		}
		List<Proxy> proxies = new ArrayList<Proxy>(proxyDatas.length);
		for (IProxyData proxyData : proxyDatas) {
			Type type = null;
			if (proxyData.getType().equals(ProxyData.HTTP_PROXY_TYPE)
					|| proxyData.getType().equals(ProxyData.HTTPS_PROXY_TYPE)) {
				type = Type.HTTP;
			} else if (proxyData.getType().equals(ProxyData.SOCKS_PROXY_TYPE)) {
				type = Type.SOCKS;
			} else {
				LOGGER.log(LogService.LOG_WARNING, "Yet unknown proxy type: " + proxyData.getType() + ". " //$NON-NLS-1$ //$NON-NLS-2$
						+ CoreNetProxySelector.class.getName() + " needs to be extended!"); //$NON-NLS-1$
			}
			if (type != null) {
				InetSocketAddress address = InetSocketAddress
						.createUnresolved(proxyData.getHost(), proxyData.getPort());
				proxies.add(new Proxy(type, address));
			}
		}
		proxies.add(Proxy.NO_PROXY);
		return proxies;
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		// TODO The core.net IProxyService has not yet support for this!
		LOGGER.log(LogService.LOG_DEBUG, "Attempt to connect to uri: " + uri + " on proxy " + sa + " failed.", ioe); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

}
