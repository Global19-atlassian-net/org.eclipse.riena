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
package org.eclipse.riena.core.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.log.LogService;

import org.eclipse.equinox.log.Logger;

import org.eclipse.riena.core.Log4r;

/**
 * A {@code WeakRef} tracks instances with a {@code WeakReference}. When the
 * instance gets garbage collected it executes a {@code Runnable} for
 * notification.
 */
public class WeakRef<T> {

	private final Reference<T> ref;

	private static final ReferenceQueue<? super Object> REF_QUEUE = new ReferenceQueue<Object>();
	private static final Map<Reference<?>, Runnable> REMOVE_ACTIONS = new HashMap<Reference<?>, Runnable>();
	static {
		// Kick-off the remover
		new Remover();
	}

	private static final Logger LOGGER = Log4r.getLogger(WeakRef.class);

	/**
	 * Create a {@code WeakRef} for the given {@code referent} with the given
	 * {@code Runnable} for notification.
	 * 
	 * @param referent
	 *            the instance to track for garbage collection
	 * @param runnable
	 *            the {@code Runnable} for notification
	 */
	public WeakRef(final T referent, final Runnable runnable) {
		ref = new WeakReference<T>(referent, REF_QUEUE);
		synchronized (REMOVE_ACTIONS) {
			REMOVE_ACTIONS.put(ref, runnable);
		}
	}

	/**
	 * Get the tracked instance.
	 * 
	 * @return the tracked instance or {@code null} if the instance has been
	 *         garbage collected
	 */
	public T get() {
		return ref.get();
	}

	/**
	 * The {@code Remover} thread waits for gc-ed instances and notifies the
	 * <i>owner</i> of the instance with its {@code Runnable}.
	 */
	private static class Remover extends Thread {

		public Remover() {
			super("WeakRef remover"); //$NON-NLS-1$
			setDaemon(true);
			start();
		}

		@Override
		public void run() {
			try {
				while (true) {
					final Reference<?> removed = REF_QUEUE.remove();
					Runnable runnable;
					synchronized (REMOVE_ACTIONS) {
						runnable = REMOVE_ACTIONS.remove(removed);
					}
					if (runnable != null) {
						try {
							runnable.run();
						} catch (final Throwable t) {
							LOGGER.log(LogService.LOG_ERROR, "Got exception executing remove notifiaction.", t); //$NON-NLS-1$
						}
					}
				}
			} catch (final InterruptedException e) {
				LOGGER.log(LogService.LOG_ERROR, "WeakRef remover has been interrupted.", e); //$NON-NLS-1$
				Thread.currentThread().interrupt();
			}
		}
	}

}
