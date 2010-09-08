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
package org.eclipse.riena.ui.core.uiprocess;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;

import org.eclipse.riena.core.singleton.SingletonProvider;

/**
 * A job can be presented by several instances of {@link ProgressProvider}. This
 * one delegates to those providers.
 */
public class ProgressProviderBridge extends ProgressProvider {

	private static final SingletonProvider<ProgressProviderBridge> PPB = new SingletonProvider<ProgressProviderBridge>(
			ProgressProviderBridge.class);

	private IProgressVisualizerLocator visualizerLocator;
	private final Map<Job, UIProcess> jobUiProcess;

	public ProgressProviderBridge() {
		jobUiProcess = Collections.synchronizedMap(new HashMap<Job, UIProcess>());
	}

	public static ProgressProviderBridge instance() {
		return PPB.getInstance();
	}

	public void setVisualizerFactory(final IProgressVisualizerLocator visualizerLocator) {
		this.visualizerLocator = visualizerLocator;
	}

	@Override
	public IProgressMonitor createMonitor(final Job job) {
		final ProgressProvider provider = queryProgressProvider(job);
		return provider.createMonitor(job);
	}

	private ProgressProvider queryProgressProvider(final Job job) {
		UIProcess uiprocess = jobUiProcess.get(job);
		final Object context = getContext(job);
		if (uiprocess == null) {
			uiprocess = createDefaultUIProcess(job);
		}
		final UICallbackDispatcher dispatcher = (UICallbackDispatcher) uiprocess.getAdapter(UICallbackDispatcher.class);
		dispatcher.addUIMonitor(visualizerLocator.getProgressVisualizer(context));
		return dispatcher;
	}

	private Object getContext(final Job job) {
		return job.getProperty(UIProcess.PROPERTY_CONTEXT);
	}

	private UIProcess createDefaultUIProcess(final Job job) {
		return new UIProcess(job);
	}

	public void registerMapping(final Job job, final UIProcess process) {
		jobUiProcess.put(job, process);
	}

	public void unregisterMapping(final Job job) {
		jobUiProcess.remove(job);
	}
}
