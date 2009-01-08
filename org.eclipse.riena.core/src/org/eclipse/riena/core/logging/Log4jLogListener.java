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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.apache.log4j.xml.SAXErrorHandler;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ContributorFactoryOSGi;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.log.ExtendedLogEntry;
import org.eclipse.riena.core.util.VariableManagerUtil;
import org.eclipse.riena.internal.core.Activator;
import org.osgi.framework.Bundle;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The <code>Log4LogListener</code> reroutes all logging within Riena into the
 * Log4J logging system.<br>
 * To activate it is necessary to contribute to the extension point
 * "org.eclipse.riena.core.logging.listeners". Within that configuration it is
 * possible to pass a �log4j.xml� as a resource to configure Log4j, e.g.
 * 
 * <pre>
 * &lt;extension point=&quot;org.eclipse.riena.core.logging.listeners&quot;&gt;
 *     &lt;logListener name=&quot;Log4j&quot; listener-class=&quot;org.eclipse.riena.core.logging.Log4jLogListener:/log4j.xml&quot; sync=&quot;true&quot;/&gt;
 * &lt;/extension&gt;
 * </pre>
 * 
 * <b>Note:</b> The logger configuration (log4j.xml) might contain substitution
 * strings, e.g. to specify the target log location of a {@code FileAppender},
 * e.g.
 * 
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot; ?&gt;
 * &lt;!DOCTYPE log4j:configuration SYSTEM &quot;log4j.dtd&quot;&gt;
 * &lt;log4j:configuration xmlns:log4j=&quot;http://jakarta.apache.org/log4j/&quot;&gt;
 * &lt;appender name=&quot;LOGFILE&quot; class=&quot;org.apache.log4j.FileAppender&quot;&gt;
 *     &lt;param name=&quot;File&quot;   value=&quot;${log4j.log.home}/scp_example.log&quot; /&gt;
 *     &lt;layout class=&quot;org.apache.log4j.PatternLayout&quot;&gt;
 *         &lt;param name=&quot;ConversionPattern&quot; value=&quot;%-5p %-17d{yyyy-MM-dd HH:mm:ss} [%t] %c %m%n&quot;/&gt;
 *     &lt;/layout&gt;
 * &lt;/appender&gt;
 *     &lt;root&gt;
 *         &lt;level value=&quot;debug&quot; /&gt;
 *         &lt;appender-ref ref=&quot;LOGFILE&quot; /&gt;
 *     &lt;/root&gt;
 * &lt;/log4j:configuration&gt;
 * </pre>
 * 
 * Such substitutions can be defined with {@code StringVariableManager}
 * extension points, e.g.
 * 
 * <pre>
 * &lt;extension point=&quot;org.eclipse.core.variables.valueVariables&quot;&gt;
 *     &lt;variable
 *         description=&quot;Location for the log4j log&quot;
 *         name=&quot;log4j.log.home&quot;
 *         readOnly=&quot;true&quot;
 *         initialValue=&quot;c:/projects/&quot;/&gt;
 * &lt;/extension&gt;
 * </pre>
 */
public class Log4jLogListener implements LogListener, IExecutableExtension {

	/**
	 * The default log4j configuration file (xml).
	 */
	public static final String DEFAULT_CONFIGURATION = "/log4j.default.xml"; //$NON-NLS-1$

	public Log4jLogListener() {
	}

	public void logged(LogEntry entry) {
		ExtendedLogEntry extendedEntry = (ExtendedLogEntry) entry;
		String loggerName = extendedEntry.getLoggerName();
		Logger logger = Logger.getLogger(loggerName != null ? loggerName : "<unknown-logger-name>"); //$NON-NLS-1$

		Level level;
		switch (extendedEntry.getLevel()) {
		case LogService.LOG_DEBUG:
			level = Level.DEBUG;
			break;
		case LogService.LOG_WARNING:
			level = Level.WARN;
			break;
		case LogService.LOG_ERROR:
			level = Level.ERROR;
			break;
		case LogService.LOG_INFO:
			level = Level.INFO;
			break;
		default:
			// Custom log level assumed
			level = CustomLevel.create(extendedEntry.getLevel());
			break;
		}
		logger.log(level, extendedEntry.getMessage(), extendedEntry.getException());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org
	 * .eclipse.core.runtime.IConfigurationElement, java.lang.String,
	 * java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		if (data == null) {
			data = DEFAULT_CONFIGURATION;
		}
		if (!(data instanceof String)) {
			return;
		}
		Bundle bundle = ContributorFactoryOSGi.resolve(config.getContributor());
		configure(bundle, (String) data);
	}

	private void configure(Bundle bundle, String configuration) throws CoreException {
		// fetch the URL of given log4j configuration file via context
		// the context is the context of the bundle from which the log was
		// initiated
		URL url = bundle.getResource(configuration);

		if (url != null) {
			Element root = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(true);
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				db.setErrorHandler(new SAXErrorHandler());
				db.setEntityResolver(new Log4jEntityResolver());
				String xml = VariableManagerUtil.substitute(read(url.openStream()));
				InputSource inputSource = new InputSource(new StringReader(xml));
				inputSource.setSystemId("dummy://log4j.dtd"); //$NON-NLS-1$
				Document doc = db.parse(inputSource);
				root = doc.getDocumentElement();
			} catch (ParserConfigurationException e) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Could not configure log4j. Parser configuration error.", e)); //$NON-NLS-1$
			} catch (SAXException e) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Could not configure log4j. Unable to parse xml configuration.", e)); //$NON-NLS-1$
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not configure log4j.", e)); //$NON-NLS-1$
			}
			// workaround to fix class loader problems with log4j
			// implementation. see "eclipse rich client platform, eclipse
			// series, page 340.
			Thread thread = Thread.currentThread();
			ClassLoader loader = thread.getContextClassLoader();
			thread.setContextClassLoader(this.getClass().getClassLoader());
			try {
				// configure the log4j with given log4j.xml
				DOMConfigurator.configure(root);
			} finally {
				thread.setContextClassLoader(loader);
			}
		} else {
			new ConsoleLogger(Log4jLogListener.class.getName()).log(LogService.LOG_ERROR,
					"Could not find specified log4j configuration '" + configuration //$NON-NLS-1$
							+ "' within bundle '" //$NON-NLS-1$
							+ bundle.getSymbolicName() + "'."); //$NON-NLS-1$
		}

	}

	/**
	 * @param openStream
	 * @return
	 * @throws IOException
	 */
	private String read(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder bob = new StringBuilder();
		int ch;
		while ((ch = reader.read()) != -1) {
			bob.append((char) ch);
		}
		return bob.toString();
	}

	/**
	 * A generic custom log level for log4j.
	 */
	private static final class CustomLevel extends Level {
		private static Map<Integer, CustomLevel> map = new HashMap<Integer, CustomLevel>();
		private static final int LOG4J_LEVEL = Priority.FATAL_INT * 2;
		// This value is duplicated here (because of access restrictions) from log4j SyslogAppender
		private static final int SYSLOG_APPENDER_LOG_USER = 1 << 3;

		/**
		 * Create a generic custom log level.We assume that the custom osgi log
		 * levels are all below 1!
		 * 
		 * @param osgiLogLevel
		 * @return
		 */
		private static synchronized CustomLevel create(int osgiLogLevel) {
			Assert.isTrue(osgiLogLevel < 1, "custom osgi log levels must be below 1"); //$NON-NLS-1$
			CustomLevel customLevel = map.get(osgiLogLevel);
			if (customLevel != null) {
				return customLevel;
			}
			customLevel = new CustomLevel(LOG4J_LEVEL + Math.abs(osgiLogLevel), "Custom(" + osgiLogLevel + ")", //$NON-NLS-1$ //$NON-NLS-2$
					SYSLOG_APPENDER_LOG_USER);
			map.put(osgiLogLevel, customLevel);
			return customLevel;
		}

		/**
		 * @param level
		 * @param levelStr
		 * @param syslogEquivalent
		 */
		private CustomLevel(int level, String levelStr, int syslogEquivalent) {
			super(level, levelStr, syslogEquivalent);
		}

	}
}
