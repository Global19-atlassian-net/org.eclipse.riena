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
package org.eclipse.riena.core.util;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.riena.core.wire.Wire;
import org.osgi.framework.BundleContext;

/**
 * The {@code ServiceAccessor} can be used to define <code>Accessor</code>s for
 * services.<br>
 * For example a {@code accessor} implemented with the {@code ServiceAccessor}
 * could look like this:
 * 
 * <pre>
 * &#064;WireWrap(UIFilterProviderAccessorWireWrap.class)
 * public final class UIFilterProviderAccessor extends ServiceAccessor&lt;UIFilterProvider&gt; {
 * 
 * 	private final static UIFilterProviderAccessor FILTER_PROVIDER_ACCESSOR = new UIFilterProviderAccessor();
 * 
 * 	private UIFilterProviderAccessor() {
 * 		super(Activator.getDefault().getContext());
 * 	}
 * 
 * 	static public IUIFilterProvider getFilterProvider() {
 * 		return FILTER_PROVIDER_ACCESSOR.getService();
 * 	}
 * }
 * </pre>
 * 
 * Each {@code ServiceAccessor} will attempt to wire itself. See {@code Wire}.<br>
 * If a <code>accessor</code> needs specific handling of bound and unboud
 * services it can define a {@code IBindHook} and pass it to the to the {@code
 * ServiceAccessor} super class via its constructor.
 */
public abstract class ServiceAccessor<S> {

	private S service;
	private final BundleContext context;
	private final IBindHook<S> bindHook;
	private final AtomicBoolean initialized = new AtomicBoolean();

	public ServiceAccessor(BundleContext context) {
		this(context, null);
	}

	public ServiceAccessor(BundleContext context, IBindHook<S> bindHook) {
		this.context = context;
		this.bindHook = bindHook;
	}

	public S getService() {
		if (!initialized.getAndSet(true)) {
			initialize();
		}
		return service;
	}

	public void bind(final S service) {
		this.service = service;
		if (bindHook != null) {
			bindHook.onBind(service);
		}
	}

	public void unbind(final S service) {
		this.service = null;
		if (bindHook != null) {
			bindHook.onUnbind(service);
		}
	}

	private void initialize() {
		Wire.instance(this).andStart(context);
	}

	/**
	 * A place to get hooked into the bind/unbind calls.
	 * 
	 * @param <S>
	 */
	public interface IBindHook<S> {

		/**
		 * Gets called on a service bind.
		 * 
		 * @param service
		 */
		void onBind(final S service);

		/**
		 * Gets called on a service unbind.
		 * 
		 * @param service
		 */
		void onUnbind(final S service);
	}
}
