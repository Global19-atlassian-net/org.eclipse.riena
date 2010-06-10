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
package org.eclipse.riena.communication.publisher.hessian;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;

import org.osgi.service.log.LogService;

import org.eclipse.equinox.log.Logger;

import org.eclipse.riena.communication.core.RemoteServiceDescription;
import org.eclipse.riena.communication.core.zipsupport.ReusableBufferedInputStream;
import org.eclipse.riena.core.Log4r;
import org.eclipse.riena.core.exception.IExceptionHandlerManager;
import org.eclipse.riena.core.service.Service;
import org.eclipse.riena.internal.communication.factory.hessian.RienaSerializerFactory;
import org.eclipse.riena.internal.communication.publisher.hessian.Activator;
import org.eclipse.riena.internal.communication.publisher.hessian.HessianRemoteServicePublisher;
import org.eclipse.riena.internal.communication.publisher.hessian.MessageContext;
import org.eclipse.riena.internal.communication.publisher.hessian.MessageContextHolder;

/**
 * TODO: JavaDoc
 */
@SuppressWarnings("serial")
public class RienaHessianDispatcherServlet extends GenericServlet {

	private SerializerFactory serializerFactory = null;

	private final static Logger LOGGER = Log4r.getLogger(Activator.getDefault(), RienaHessianDispatcherServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		serializerFactory = new SerializerFactory();
		serializerFactory.setAllowNonSerializable(true);
		serializerFactory.addFactory(new RienaSerializerFactory());

		LOGGER.log(LogService.LOG_DEBUG, "initialized"); //$NON-NLS-1$
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

		HttpServletRequest httpReq = (HttpServletRequest) req;
		HttpServletResponse httpRes = (HttpServletResponse) res;

		// set the message context
		MessageContextHolder.setMessageContext(new MessageContext(httpReq, httpRes));

		HessianRemoteServicePublisher publisher = getPublisher();
		if (publisher == null) {
			if (httpReq.getMethod().equals("GET")) { //$NON-NLS-1$
				if (httpReq.getRemoteHost().equals("127.0.0.1")) { //$NON-NLS-1$
					PrintWriter pw = new PrintWriter(res.getOutputStream());
					pw.write("no webservices available"); //$NON-NLS-1$
					pw.flush();
					pw.close();
					return;
				} else {
					httpRes.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Hessian requires POST"); //$NON-NLS-1$
					return;
				}
			} else {
				httpRes.sendError(HttpServletResponse.SC_NOT_FOUND, "webservice not found"); //$NON-NLS-1$
				return;
			}
		}
		String requestURI = httpReq.getRequestURI();
		String contextPath = httpReq.getContextPath();
		if (contextPath.length() > 1) {
			requestURI = requestURI.substring(contextPath.length());
		}
		RemoteServiceDescription rsd = publisher.findService(requestURI);
		log("call " + rsd); //$NON-NLS-1$
		if (httpReq.getMethod().equals("GET")) { //$NON-NLS-1$
			if (httpReq.getRemoteHost().equals("127.0.0.1")) { //$NON-NLS-1$
				PrintWriter pw = new PrintWriter(res.getOutputStream());
				if (rsd == null) {
					pw.write("call received from browser, no remote service registered with this URL"); //$NON-NLS-1$
				} else {
					pw.write("calls " + rsd); //$NON-NLS-1$
				}
				pw.flush();
				pw.close();
				return;
			} else {
				httpRes.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Hessian requires POST"); //$NON-NLS-1$
				return;
			}
		}
		if (rsd == null) {
			httpRes.sendError(HttpServletResponse.SC_NOT_FOUND, "unknown url :" + httpReq.getRequestURI()); //$NON-NLS-1$
			return;
		}
		Object instance = rsd.getService();
		HessianSkeleton sk = new HessianSkeleton(instance, rsd.getServiceInterfaceClass());
		String gzip = httpReq.getHeader("Content-Encoding"); //$NON-NLS-1$
		boolean gzipFlag = false;
		boolean inputWasGZIP = false;

		if (gzip != null && gzip.equals("gzip")) { //$NON-NLS-1$
			gzipFlag = true;
		}
		InputStream requestInputStream = httpReq.getInputStream();
		if (gzipFlag) {
			BufferedInputStream tempInput = new ReusableBufferedInputStream(requestInputStream);
			if (tempInput.markSupported()) {
				tempInput.mark(20);
				int readMAGIC = tempInput.read() + tempInput.read() * 256;
				if (readMAGIC == GZIPInputStream.GZIP_MAGIC) {
					inputWasGZIP = true;
				}
				tempInput.reset();
				requestInputStream = tempInput;
			}
			if (inputWasGZIP) {
				requestInputStream = new GZIPInputStream(requestInputStream);
			}
		}

		Hessian2Input inp = new Hessian2Input(requestInputStream);
		AbstractHessianOutput out;
		inp.setSerializerFactory(serializerFactory);

		int code = inp.read();
		if (code != 'c') {
			throw new IOException("expected 'c' in hessian input at " + code); //$NON-NLS-1$
		}
		int major = inp.read();
		inp.read(); // read/skip the minor version - not used currently

		if (inputWasGZIP) {
			httpRes.setHeader("Content-Encoding", "gzip"); //$NON-NLS-1$//$NON-NLS-2$
		}

		OutputStream outputStream = httpRes.getOutputStream();
		if (inputWasGZIP) {
			outputStream = new GZIPOutputStream(outputStream);
		}
		if (major >= 2) {
			out = new Hessian2Output(outputStream);
		} else {
			out = new HessianOutput(outputStream);
		}

		out.setSerializerFactory(serializerFactory);

		try {
			sk.invoke(inp, out);
		} catch (Throwable t) {
			Throwable t2 = t;
			while (t2.getCause() != null) {
				t2 = t2.getCause();
			}
			LOGGER.log(LogService.LOG_ERROR, t.getMessage(), t2);
			Service.get(IExceptionHandlerManager.class).handleException(t2);
			throw new ServletException(t);
		} finally {
			out.close(); // Hessian2Output forgets to close if the service throws an exception
			if (outputStream instanceof GZIPOutputStream) {
				((GZIPOutputStream) outputStream).finish();
			}
			outputStream.flush();
		}
	}

	/**
	 * 
	 * @return the publisher
	 */
	protected HessianRemoteServicePublisher getPublisher() {
		return Activator.getDefault().getPublisher();
	}

}
