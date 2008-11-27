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
package org.eclipse.riena.example.client.communication;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.riena.communication.core.progressmonitor.AbstractRemoteProgressMonitor;
import org.eclipse.riena.communication.core.progressmonitor.RemoteProgressMonitorEvent;
import org.eclipse.riena.navigation.ApplicationNodeManager;
import org.eclipse.riena.navigation.ISubApplicationNode;
import org.eclipse.riena.navigation.model.ApplicationNode;
import org.eclipse.riena.navigation.ui.application.VisualizerFactory;
import org.eclipse.riena.ui.core.uiprocess.ProcessInfo;
import org.eclipse.riena.ui.core.uiprocess.UICallbackDispatcher;
import org.eclipse.riena.ui.core.uiprocess.UIProcess;

public class ServiceProgressVisualizer extends AbstractRemoteProgressMonitor {

	private final static double NORMALIZED_TOTAL_WORK = 10000;

	private IProgressMonitor progressMonitor;

	private String taskName;

	private Map<CommunicationDirection, CommunicationChannel> channels = new HashMap<CommunicationDirection, CommunicationChannel>();

	enum CommunicationDirection {
		REQUEST, RESPONSE;
	}

	class CommunicationChannel {

		int actualTotalWork = 0;
		int actualWorkedUnits = 0;

		int getActualTotalWork() {
			return actualTotalWork;
		}

		void setActualTotalWork(int actualTotalWork) {
			this.actualTotalWork = actualTotalWork;
		}

		int getActualWorkedUnits() {
			return actualWorkedUnits;
		}

		void setActualWorkedUnits(int actualWorkedUnits) {
			this.actualWorkedUnits = actualWorkedUnits;
		}

		void workUnitsDone(int bytes) {
			setActualWorkedUnits(getActualWorkedUnits() + bytes);
		}

		int normalizeActualWorkedUnits() {
			double workMultiplier = Double.valueOf(actualWorkedUnits) / Double.valueOf(actualTotalWork);
			return (int) ((NORMALIZED_TOTAL_WORK / 2) * workMultiplier);
		}

	}

	public ServiceProgressVisualizer(String taskname) {
		this.taskName = taskname;
		initChannels();
		UICallbackDispatcher callBackDispatcher = new UICallbackDispatcher(UIProcess
				.getSynchronizerFromExtensionPoint());
		ISubApplicationNode subApplicationNode = locateActiveSubApplicationNode();
		callBackDispatcher.addUIMonitor(new VisualizerFactory().getProgressVisualizer(subApplicationNode));
		progressMonitor = callBackDispatcher.createThreadSwitcher();
		configureProcessInfo(callBackDispatcher, subApplicationNode);
	}

	private void initChannels() {
		channels.put(CommunicationDirection.REQUEST, new CommunicationChannel());
		channels.put(CommunicationDirection.RESPONSE, new CommunicationChannel());
	}

	private void configureProcessInfo(UICallbackDispatcher callBackDispatcher, ISubApplicationNode applicationNode) {
		ProcessInfo processInfo = callBackDispatcher.getProcessInfo();
		processInfo.setNote(taskName);
		processInfo.setTitle(taskName);
		processInfo.setDialogVisible(true);
		processInfo.setContext(applicationNode);
		processInfo.addPropertyChangeListener(new RemoteServiceCancelListener());
	}

	protected ISubApplicationNode locateActiveSubApplicationNode() {
		ApplicationNode applicationNode = (ApplicationNode) ApplicationNodeManager.getApplicationNode();
		for (ISubApplicationNode child : applicationNode.getChildren()) {
			if (child.isActivated()) {
				return child;
			}
		}
		return null;
	}

	class RemoteServiceCancelListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (ProcessInfo.PROPERTY_CANCELED.equals(evt.getPropertyName())) {

			}
		}
	}

	@Override
	public void start(RemoteProgressMonitorEvent event) {
		super.start(event);
		progressMonitor.beginTask(taskName, (int) NORMALIZED_TOTAL_WORK);
	}

	CommunicationChannel getChannel(CommunicationDirection communicationDrection) {
		return channels.get(communicationDrection);
	}

	@Override
	public void request(int bytes, int totalBytes) {
		workUntisCompleted(bytes, totalBytes, getChannel(CommunicationDirection.REQUEST));
	}

	public void request(RemoteProgressMonitorEvent event) {
		request(event.getBytesProcessed(), event.getTotalBytes());
	}

	@Override
	public void response(int bytes, int totalBytes) {
		workUntisCompleted(bytes, totalBytes, getChannel(CommunicationDirection.RESPONSE));
	}

	public void response(RemoteProgressMonitorEvent event) {
		response(event.getBytesProcessed(), event.getTotalBytes());
	}

	private void workUntisCompleted(int bytes, int totalBytes, CommunicationChannel communicationChannel) {
		communicationChannel.actualTotalWork = totalBytes;
		updateWorked(bytes, communicationChannel);
		if (transferComplete()) {
			progressMonitor.done();
			return;
		}
		progressMonitor.worked(calculateTotalNormalizedProgress());
	}

	private boolean transferComplete() {
		return calculateTotalNormalizedProgress() >= NORMALIZED_TOTAL_WORK;
	}

	private int calculateTotalNormalizedProgress() {
		int sum = 0;
		for (CommunicationChannel channel : channels.values()) {
			sum += channel.normalizeActualWorkedUnits();
		}
		return sum;
	}

	private void updateWorked(int bytes, CommunicationChannel communicationChannel) {
		communicationChannel.workUnitsDone(bytes);
	}

}
