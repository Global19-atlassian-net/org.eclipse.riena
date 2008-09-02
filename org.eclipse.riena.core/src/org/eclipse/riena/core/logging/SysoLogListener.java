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
package org.eclipse.riena.core.logging;

import java.util.Date;

import org.eclipse.equinox.log.ExtendedLogEntry;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

public class SysoLogListener implements LogListener {

	public void logged(LogEntry entry) {
		ExtendedLogEntry eEntry = (ExtendedLogEntry) entry;
		StringBuilder buffer = new StringBuilder();
		buffer.append(new Date(eEntry.getTime()).toString()).append(' ');
		String level;
		switch (eEntry.getLevel()) {
		case LogService.LOG_DEBUG:
			level = "DEBUG"; //$NON-NLS-1$
			break;
		case LogService.LOG_WARNING:
			level = "WARNING"; //$NON-NLS-1$
			break;
		case LogService.LOG_ERROR:
			level = "ERROR"; //$NON-NLS-1$
			break;
		case LogService.LOG_INFO:
			level = "INFO"; //$NON-NLS-1$
			break;
		default:
			level = "UNKNOWN"; //$NON-NLS-1$
			break;
		}
		buffer.append(level).append(' ');
		buffer.append("[" + eEntry.getThreadName() + "] "); //$NON-NLS-1$ //$NON-NLS-2$
		buffer.append(eEntry.getLoggerName()).append(' ');
		if (eEntry.getContext() != null) {
			buffer.append(eEntry.getContext()).append(' ');
		}
		buffer.append(entry.getMessage());
		System.out.println(buffer.toString());
		if (eEntry.getException() != null) {
			eEntry.getException().printStackTrace(System.out);
		}
	}
}
