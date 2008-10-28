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
package org.eclipse.riena.ui.ridgets.marker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.riena.core.util.ListenerList;
import org.eclipse.riena.ui.core.marker.IMessageMarker;
import org.eclipse.riena.ui.ridgets.IMarkableRidget;

/**
 * Common functionality of classes visualizing certain types of message markers.
 */
public abstract class AbstractMessageMarkerViewer implements IMessageMarkerViewer {

	private HashSet<Class<? extends IMessageMarker>> markerTypes;
	private ListenerList<IMarkableRidget> ridgets;
	private boolean visible;

	public AbstractMessageMarkerViewer() {
		markerTypes = new LinkedHashSet<Class<? extends IMessageMarker>>();
		ridgets = new ListenerList<IMarkableRidget>(IMarkableRidget.class);
		visible = true;
		markerTypes.add(ValidationMessageMarker.class);
	}

	public void addRidget(IMarkableRidget markableRidget) {
		ridgets.add(markableRidget);
		showMessages(markableRidget);
	}

	public void removeRidget(IMarkableRidget markableRidget) {
		ridgets.remove(markableRidget);
		hideMessages(markableRidget);
	}

	public void addMarkerType(Class<? extends IMessageMarker> markerClass) {
		markerTypes.add(markerClass);
		showMessages();
	}

	public void removeMarkerType(Class<? extends IMessageMarker> markerClass) {
		markerTypes.remove(markerClass);
		showMessages();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		showMessages();
	}

	private void showMessages() {
		for (IMarkableRidget ridget : getRidgets()) {
			showMessages(ridget);
		}
	}

	protected abstract void showMessages(IMarkableRidget ridget);

	protected abstract void hideMessages(IMarkableRidget ridget);

	protected Collection<IMessageMarker> getMessageMarker(IMarkableRidget markableRidget) {
		return getMessageMarker(markableRidget, false);
	}

	protected Collection<IMessageMarker> getMessageMarker(IMarkableRidget markableRidget, boolean pRemove) {
		List<IMessageMarker> result = new ArrayList<IMessageMarker>();
		for (Class<? extends IMessageMarker> nextMessageMarkerType : markerTypes) {
			Collection<? extends IMessageMarker> nextMessageMarkers = markableRidget
					.getMarkersOfType(nextMessageMarkerType);
			if (nextMessageMarkers != null && nextMessageMarkers.size() > 0) {
				result.addAll(nextMessageMarkers);
			}
		}
		if (pRemove) {
			for (Iterator<IMessageMarker> j = result.iterator(); j.hasNext();) {
				markableRidget.removeMarker((IMessageMarker) j.next());
			}
		}
		Collections.sort(result, new MessageMarkerComparator());
		return result;
	}

	protected Collection<IMarkableRidget> getRidgets() {
		return Arrays.asList(ridgets.getListeners());
	}

	protected static final class MessageMarkerComparator implements Comparator<IMessageMarker>, Serializable {

		private static final long serialVersionUID = 1L;

		public int compare(IMessageMarker o1, IMessageMarker o2) {
			return o1.getMessage().compareTo(o2.getMessage());
		}

	}

}
