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
package org.eclipse.riena.internal.communication.publisher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import org.eclipse.riena.communication.core.RemoteServiceDescription;
import org.eclipse.riena.communication.core.hooks.AbstractHooksProxy;
import org.eclipse.riena.communication.core.hooks.IServiceHook;
import org.eclipse.riena.communication.core.hooks.IServiceMessageContext;
import org.eclipse.riena.communication.core.hooks.IServiceMessageContextAccessor;
import org.eclipse.riena.communication.core.hooks.ServiceContext;
import org.eclipse.riena.core.wire.InjectService;
import org.eclipse.riena.core.wire.Wire;

public class ServiceHooksProxy extends AbstractHooksProxy implements InvocationHandler {

	private final Set<IServiceHook> serviceHooks = new HashSet<IServiceHook>();
	private RemoteServiceDescription rsd;
	private IServiceMessageContextAccessor mca;
	private Subject subject;

	public ServiceHooksProxy(Object serviceInstance) {
		super(serviceInstance);
		Wire.instance(this).andStart(Activator.getDefault().getContext());
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (method.getName().equals("equals") || method.getName().equals("hashCode")) { //$NON-NLS-1$ //$NON-NLS-2$
			return new UnsupportedOperationException("method :" + method.getName() + " not supported for " //$NON-NLS-1$ //$NON-NLS-2$
					+ this.getServiceInstance());
		}
		if (method.getName().equals("toString")) { //$NON-NLS-1$
			return this.getServiceInstance().toString();
		}
		IServiceMessageContext mc = null;
		if (mca != null) {
			mc = mca.getMessageContext();
		}

		ServiceContext context = null;
		// only create context (it might be expensive), if you have serviceHooks
		if (serviceHooks.size() > 0) {
			context = new ServiceContext(rsd, method, getServiceInstance(), mc);

			// call before service hook
			for (IServiceHook sHook : serviceHooks) {
				sHook.beforeService(context);
			}

			Object s = context.getProperty("riena.subject"); //$NON-NLS-1$
			if (s instanceof Subject) {
				subject = (Subject) s;
			}
		}

		try {
			return super.invoke(proxy, method, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		} finally {
			// context might be null, but serviceHooks were injected during invoke
			if (context != null) {
				for (IServiceHook sHook : serviceHooks) {
					sHook.afterService(context);
				}
			}
		}
	}

	@InjectService(useRanking = true)
	public void bind(IServiceHook serviceHook) {
		serviceHooks.add(serviceHook);
	}

	public void unbind(IServiceHook serviceHook) {
		serviceHooks.remove(serviceHook);
	}

	public Object getServiceInstance() {
		return getProxiedInstance();
	}

	public void setRemoteServiceDescription(RemoteServiceDescription rsd) {
		this.rsd = rsd;
	}

	public void setMessageContextAccessor(IServiceMessageContextAccessor mca) {
		this.mca = mca;
	}

	@Override
	public Subject getSubject() {
		return subject;
	}
}
