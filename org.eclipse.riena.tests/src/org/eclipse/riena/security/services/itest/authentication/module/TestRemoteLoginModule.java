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
package org.eclipse.riena.security.services.itest.authentication.module;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.eclipse.equinox.log.Logger;
import org.eclipse.riena.core.Log4r;
import org.eclipse.riena.internal.tests.Activator;
import org.eclipse.riena.security.common.authentication.RemoteLoginProxy;
import org.osgi.service.log.LogService;

/**
 * Test module that implements the JAAS LoginModule interface
 * 
 */
public class TestRemoteLoginModule implements LoginModule {

	private static final Logger LOGGER = Log4r.getLogger(Activator.getDefault(), TestRemoteLoginModule.class);

	private CallbackHandler callbackHandler;

	private String username;
	private String password;
	// AuthenticationTicket ticket;
	private RemoteLoginProxy remoteLoginProxy;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#abort()
	 */
	public boolean abort() throws LoginException {
		LOGGER.log(LogService.LOG_DEBUG, "abort");
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#commit()
	 */
	public boolean commit() throws LoginException {
		LOGGER.log(LogService.LOG_DEBUG, "commit");
		return remoteLoginProxy.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject
	 * , javax.security.auth.callback.CallbackHandler, java.util.Map,
	 * java.util.Map)
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		if (callbackHandler == null) {
			LOGGER.log(LogService.LOG_ERROR, "callbackhandler cant be null");
			throw new RuntimeException("callbackhandler cant be null");
		}
		LOGGER.log(LogService.LOG_DEBUG, "initialize");
		this.callbackHandler = callbackHandler;
		this.remoteLoginProxy = new RemoteLoginProxy("CentralSecurity", subject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#login()
	 */
	public boolean login() throws LoginException {
		LOGGER.log(LogService.LOG_DEBUG, "login");
		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("username: ");
		callbacks[1] = new PasswordCallback("password: ", false);
		try {
			callbackHandler.handle(callbacks);
			username = ((NameCallback) callbacks[0]).getName();
			password = new String(((PasswordCallback) callbacks[1]).getPassword());
			return remoteLoginProxy.login(callbacks);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedCallbackException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#logout()
	 */
	public boolean logout() throws LoginException {
		LOGGER.log(LogService.LOG_DEBUG, "logout");
		return remoteLoginProxy.logout();
	}

}
