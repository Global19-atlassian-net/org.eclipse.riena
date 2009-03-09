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
package org.eclipse.riena.communication.core.exception;

import java.io.IOException;

import org.eclipse.equinox.log.Logger;
import org.eclipse.riena.communication.core.IRemoteServiceRegistration;
import org.eclipse.riena.communication.core.RemoteFailure;
import org.eclipse.riena.communication.core.factory.Register;
import org.eclipse.riena.core.Log4r;
import org.eclipse.riena.core.exception.ExceptionFailure;
import org.eclipse.riena.core.exception.Failure;
import org.eclipse.riena.internal.tests.Activator;
import org.eclipse.riena.sample.app.common.exception.IExceptionService;
import org.eclipse.riena.tests.RienaTestCase;
import org.eclipse.riena.tests.collect.IntegrationTestCase;
import org.osgi.service.log.LogService;

/**
 * This class is an test client for the exception service.
 * 
 * 
 */
@IntegrationTestCase
public class ExceptionClientITest extends RienaTestCase {
	private IExceptionService exceptionService;
	private IRemoteServiceRegistration regExceptionService;

	private final static Logger LOGGER = Log4r.getLogger(Activator.getDefault(), ExceptionClientITest.class);

	protected void setUp() throws Exception {
		super.setUp();
		regExceptionService = Register.remoteProxy(IExceptionService.class).usingUrl(
				"http://localhost:8080/hessian/ExceptionService").withProtocol("hessian").andStart(
				Activator.getDefault().getContext());
		exceptionService = (IExceptionService) Activator.getDefault().getContext().getService(
				Activator.getDefault().getContext().getServiceReference(IExceptionService.class.getName()));
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
		super.tearDown();
		regExceptionService.unregister();
		exceptionService = null;
	}

	/**
	 * tests the exception service by requesting certain exceptions
	 * 
	 * @throws Exception
	 */
	public void testStandardExceptions1() throws Exception {

		try {
			exceptionService.throwException("java.io.IOException");
			fail("This call should throw an exception!");
		} catch (Throwable e) {
			LOGGER.log(LogService.LOG_INFO, "Exception: " + e);
			LOGGER.log(LogService.LOG_INFO, "Exception Type: " + e.getClass());
			LOGGER.log(LogService.LOG_INFO, "Exception.getMessage(): " + e.getMessage());

			assertTrue(e instanceof IOException);
			assertTrue(e.getMessage().trim().equals("ExceptionService: Here is your requested java.io.IOException..."));
			assertNotNull(e.getStackTrace());
			assertNull(e.getCause());
		}
	}

	/**
	 * tests the exception service by requesting certain exceptions
	 * 
	 * @throws Exception
	 */
	public void testStandardExceptions2() throws Exception {

		try {
			exceptionService.throwException("java.lang.ClassNotFoundException");
			fail("This call should throw an exception!");
		} catch (Throwable e) {
			LOGGER.log(LogService.LOG_INFO, "Exception: " + e);
			LOGGER.log(LogService.LOG_INFO, "Exception Type: " + e.getClass());
			LOGGER.log(LogService.LOG_INFO, "Exception.getMessage(): " + e.getMessage());

			assertTrue(e instanceof ClassNotFoundException);
			assertTrue(e.getMessage().trim().equals(
					"ExceptionService: Here is your requested java.lang.ClassNotFoundException..."));
			assertNull(e.getCause());
			assertNotNull(e.getStackTrace());
		}
	}

	/**
	 * tests the exception service by requesting certain exceptions
	 * 
	 * @throws Exception
	 */
	public void testStandardExceptions3() throws Exception {

		try {
			exceptionService.throwException("java.lang.NullPointerException");
			fail("This call should throw an exception!");
		} catch (Throwable e) {
			LOGGER.log(LogService.LOG_INFO, "Exception: " + e);
			LOGGER.log(LogService.LOG_INFO, "Exception Type: " + e.getClass());
			LOGGER.log(LogService.LOG_INFO, "Exception.getMessage(): " + e.getMessage());

			assertTrue(e instanceof NullPointerException);
			assertTrue(e.getMessage().trim().equals(
					"ExceptionService: Here is your requested java.lang.NullPointerException..."));
			assertNull(e.getCause());
			assertNotNull(e.getStackTrace());
		}
	}

	/**
	 * tests the exception service by requesting certain exceptions
	 * 
	 * @throws Exception
	 */
	public void testStandardExceptions4() throws Exception {

		try {
			exceptionService.throwException("java.lang.ArrayIndexOutOfBoundsException");
			fail("This call should throw an exception!");
		} catch (Throwable e) {
			LOGGER.log(LogService.LOG_INFO, "Exception: " + e);
			LOGGER.log(LogService.LOG_INFO, "Exception Type: " + e.getClass());
			LOGGER.log(LogService.LOG_INFO, "Exception.getMessage(): " + e.getMessage());

			assertTrue(e instanceof java.lang.ArrayIndexOutOfBoundsException);
			assertTrue(e.getMessage().trim().equals(
					"ExceptionService: Here is your requested java.lang.ArrayIndexOutOfBoundsException..."));
			assertNull(e.getCause());
			assertNotNull(e.getStackTrace());
		}
	}

	/**
	 * tests the exception service by requesting certain exceptions
	 * 
	 * @throws Exception
	 */
	public void texxstStandardExceptions5() throws Exception {

		try {
			exceptionService.throwRuntimeException("java.lang.RuntimeException");
			fail("This call should throw an exception!");
		} catch (Throwable e) {
			LOGGER.log(LogService.LOG_INFO, "Exception: " + e);
			LOGGER.log(LogService.LOG_INFO, "Exception Type: " + e.getClass());
			LOGGER.log(LogService.LOG_INFO, "Exception.getMessage(): " + e.getMessage());

			assertTrue(e instanceof java.lang.RuntimeException);
			assertTrue(e.getMessage().trim().equals(
					"ExceptionService: Here is your requested java.lang.RuntimeException..."));
			assertNull(e.getCause());
			assertNotNull(e.getStackTrace());
		}
	}

	/**
	 * tests the exception service by requesting certain exceptions
	 * 
	 * @throws Exception
	 */
	public void testRienaFailure() throws Exception {

		try {
			exceptionService.throwException("org.eclipse.riena.core.exception.ExceptionFailure");
			fail("This call should throw an exception!");
		} catch (Throwable e) {
			assertTrue(e instanceof ExceptionFailure);
			Failure failure = (Failure) e;
			assertNotNull(failure.getMessage());
			assertTrue(failure.getMessage().indexOf(
					"ExceptionService: Here is your requested org.eclipse.riena.core.exception.ExceptionFailure...") > -1);
			// in this case even the cause is supposed to be null.
			assertNull(failure.getCause());
			assertNotNull(failure.getId());
			assertNotNull(failure.getTimestamp());
			assertNotNull(failure.getCallerClassName());
			assertNotNull(failure.getCallerMethodName());
			// assertEquals( failure.getCallerMethodName(), "throwException" );
			assertNotNull(failure.getJavaVersion());
			assertNotNull(failure.getStackTrace());
			assertTrue(failure.getStackTrace().length > 10);
		}
	}

	/**
	 * tests the exception service by requesting certain exceptions
	 * 
	 * @throws Exception
	 */
	public void testRienaFailure2() throws Exception {

		try {
			exceptionService.throwException("org.eclipse.riena.communication.core.RemoteFailure");
			fail("This call should throw an exception!");
		} catch (Throwable e) {
			assertTrue(e instanceof RemoteFailure);
			Failure failure = (Failure) e;
			assertNotNull(failure.getMessage());
			assertTrue(failure.getMessage().indexOf(
					"ExceptionService: Here is your requested org.eclipse.riena.communication.core.RemoteFailure...") > -1);
			// in this case even the cause is supposed to be null.
			assertNull(failure.getCause());
			assertNotNull(failure.getId());
			assertNotNull(failure.getTimestamp());
			assertNotNull(failure.getCallerClassName());
			assertNotNull(failure.getCallerMethodName());
			// assertEquals( failure.getCallerMethodName(), "throwException" );
			assertNotNull(failure.getJavaVersion());
			assertNotNull(failure.getStackTrace());
		}
	}

	public void testTryNestedException() throws Exception {
		try {
			exceptionService.throwNestedException();
			fail("This call should throw an exception!");
		} catch (Throwable e) {
			assertTrue(e instanceof Exception);
			assertTrue(e.getCause() instanceof NullPointerException);
			// e.printStackTrace();
			assertTrue(e.getCause() != e);
		}

	}
}