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
package org.eclipse.riena.communication.core.attachment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.tests.RienaTestCase;
import org.eclipse.riena.tests.collect.NonUITestCase;

/**
 * Does a component test on the attachment object. These tests do not involve <br>
 * webservices but are only local.
 * 
 */
@NonUITestCase
public class AttachmentTest extends RienaTestCase {

	private Attachment attach;
	private File file;

	private final static int BUFFER_SIZE = 20;
	private final static String WRITE_BUFFER = "This text file is accessed in AttachmentTest to test the Attachment class.";
	private final static int FILE_SIZE = WRITE_BUFFER.length();

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws Exception {
		super.setUp();
		file = File.createTempFile("attachTest", null);
		PrintWriter printWriter = new PrintWriter(new FileOutputStream(file));
		printWriter.write(WRITE_BUFFER);
		printWriter.close();
		// file = new
		// File(AttachmentTest.class.getResource(ATTACH_FILE).getFile());
		attach = new Attachment(file);
	}

	/**
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 *
	 */
	public void testConstruction() {
		assertTrue("attachment does not have type FILE", (Attachment.Type) ReflectionUtils.invokeHidden(attach,
				"getType") == Attachment.Type.FILE);
		assertTrue("internal file object is not the passed parameter", (File) ReflectionUtils.invokeHidden(attach,
				"getInternalFile") == file);
	}

	/**
	 * @throws IOException
	 */
	public void testReadAsStream() throws IOException {
		InputStream stream = null;
		try {
			stream = attach.readAsStream();
			int attachmentCounter = 0, incCounter = 0;
			byte[] b = new byte[BUFFER_SIZE];
			// read through attachment
			while ((incCounter = stream.read(b)) > 0) {
				attachmentCounter = attachmentCounter + incCounter;
			}
			stream = new FileInputStream(file);
			int physicalFileCounter = 0;
			// read through physical file
			while ((incCounter = stream.read(b)) > 0) {
				physicalFileCounter = physicalFileCounter + incCounter;
			}
			assertTrue("expecting attachment to be same size as physical file",
					physicalFileCounter == attachmentCounter);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	/**
	 * @throws IOException
	 */
	public void testReadAsFile() throws IOException {
		File tmp = attach.readAsFile("tempxxx.txt");
		InputStream stream = null;
		try {
			stream = new FileInputStream(tmp);
			int incCounter, physicalFileCounter = 0;
			byte[] b = new byte[BUFFER_SIZE];
			// read through physical file
			while ((incCounter = stream.read(b)) > 0) {
				physicalFileCounter = physicalFileCounter + incCounter;
			}
			assertTrue("filelength should be " + FILE_SIZE, physicalFileCounter == FILE_SIZE);
			stream.close();
			boolean check = tmp.delete();
			if (!check) {
				System.out.println("temp file " + tmp.getAbsolutePath() + " could not be deleted.");
			}
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	public void testReadURL() throws IOException {
		try {
			SocketReader socketReader = new SocketReader(9999, this.getClass().getResource("test.txt"));
			socketReader.start();
			URL url = new URL("http://localhost:9999/test");
			Attachment urlAttachment = new Attachment(url);
			urlAttachment.readAsStream();
			assertTrue("must only connect once for several calls to attachment but did connect:"
					+ socketReader.getCount() + " times.", socketReader.getCount() == 1);
			socketReader.stop();
		} catch (SocketException e) {
			// SocketException can happen in mavenbuild....thats ok
		}
	}

	/**
	 * class opens a socket, reads the request (without understanding it) and
	 * sends a fixed resource back
	 * 
	 */
	static class SocketReader extends Thread {
		private URL myUrl;
		private int port;
		private int count;

		SocketReader(int port, URL url) {
			myUrl = url;
			this.port = port;
			this.count = 0;
		}

		@Override
		public void run() {
			try {
				ServerSocket serverSocket = new ServerSocket(port);
				while (true) {
					InputStream input = null;
					InputStream localInput = null;
					OutputStream output = null;
					try {
						Socket socket = serverSocket.accept();
						count++;
						input = socket.getInputStream();
						// first wait until there is some input
						while (input.available() == 0) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// do nothing
							}
						}
						// then read until there is no more input
						while (input.available() > 0) {
							input.read();
							if (input.available() == 0) {
								try {
									// thread sleep if there is no input so that
									// we give the other thread
									// some time to come up with some input
									Thread.sleep(200);
								} catch (InterruptedException e) {
									// ignore
								}
							}
						}
						output = socket.getOutputStream();
						localInput = myUrl.openStream();
						byte[] buffer = new byte[100];
						int len;
						while (true) {
							len = localInput.read(buffer, 0, buffer.length);
							if (len == -1) {
								break;
							}
							output.write(buffer, 0, len);
						}
					} catch (IOException e) {
						//
					} finally {
						if (input != null) {
							input.close();
						}
						if (output != null) {
							output.close();
						}
						if (localInput != null) {
							localInput.close();
						}
					}
				}
			} catch (IOException e) {
				// serverSocket did not work
			}
		}

		public int getCount() {
			return count;
		}
	}
}
