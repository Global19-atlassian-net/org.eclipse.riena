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
package org.eclipse.riena.communication.core.attachment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.SecureRandom;

import org.eclipse.riena.communication.core.IRemoteServiceRegistration;
import org.eclipse.riena.communication.core.RemoteFailure;
import org.eclipse.riena.communication.core.factory.Register;
import org.eclipse.riena.core.util.Nop;
import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.internal.core.test.RienaTestCase;
import org.eclipse.riena.internal.core.test.collect.ManualTestCase;
import org.eclipse.riena.internal.tests.Activator;
import org.eclipse.riena.sample.app.common.attachment.IAttachmentService;

/**
 * Integration test for testing attachment together with the AttachmentService.
 * 
 */
@ManualTestCase
public final class AttachmentClientITest extends RienaTestCase {

	private static final String STRING1 = "das sind testdaten, die wir mal einfach so verschicken um et+das sind "
			+ "testdaten, die wir mal einfach so verschicken um et";
	private static final String STRING2 = "first+das sind testdaten, die wir mal einfach so verschicken um et+second+"
			+ "das sind testdaten, die wir mal einfach so verschicken um et+third+2";
	private static final String TESTDATA1 = "das sind testdaten, die wir mal einfach so verschicken um etwas zu testen.";
	private static final String TESTDATA2 = "das sind testdaten, die wir mal einfach so verschicken um etwas zu testen. (2.test)";
	private IAttachmentService attachService;
	private IRemoteServiceRegistration regAttachmentService;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		regAttachmentService = Register.remoteProxy(IAttachmentService.class).usingUrl(
				"http://localhost:8080/hessian/AttachmentService").withProtocol("hessian").andStart(
				Activator.getDefault().getContext());
		attachService = (IAttachmentService) Activator.getDefault().getContext().getService(
				Activator.getDefault().getContext().getServiceReference(IAttachmentService.class.getName()));
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		regAttachmentService.unregister();
		attachService = null;
	}

	/**
	 * test for sending a simple single file using attachments and webservices
	 * 
	 * @throws FileNotFoundException
	 * 
	 */
	public void testSendSimpleFile() throws IOException {
		Attachment attachment = new Attachment(setupTestFile(TESTDATA1));
		String str = attachService.sendSingleAttachment(attachment);
		assertTrue("expecting a certain string", str != null
				&& str.equals("das sind testdaten, die wir mal einfach so verschicken um et"));
		trace("testSendSimpleFile " + str);
	}

	/**
	 * test for sending two files
	 * 
	 * @throws FileNotFoundException
	 */
	public void testSendTwoFiles() throws IOException {
		Attachment attachment = new Attachment(setupTestFile(TESTDATA1));
		Attachment attachment2 = new Attachment(setupTestFile(TESTDATA2));
		String str = attachService.sendTwoAttachments(attachment, attachment2);
		assertTrue("expecting a certain string", str != null && str.equals(STRING1));
		trace("testSendTwoFiles " + str);
	}

	/**
	 * test for sending multiple files plus regular java objects in one call
	 * 
	 * @throws FileNotFoundException
	 */
	public void testSendFileAndData() throws IOException {
		Attachment attachment = new Attachment(setupTestFile(TESTDATA1));
		Attachment attachment2 = new Attachment(setupTestFile(TESTDATA2));
		String str = attachService.sendAttachmentsAndData("first", attachment, "second", attachment2, "third", 2);
		assertTrue("expecting a certain string", str != null && str.equals(STRING2));
		trace("testSendFileAndData " + str);
	}

	/**
	 * @throws IOException
	 */
	public void testReturn() throws IOException {
		Attachment attachment = attachService.returnAttachment();
		trace("testReturn as String " + readAttachmentStart(attachment));
	}

	/**
	 * @throws IOException
	 */
	public void testReturnOnRequest() throws IOException {
		Attachment attachment = attachService.returnAttachmentForRequest("validfilename");
		trace("testReturn as String " + readAttachmentStart(attachment));
	}

	/**
	 * @throws FileNotFoundException
	 */
	public void testReturnOnRequestInvalidFile() throws IOException {
		try {
			attachService.returnAttachmentForRequest("invalidfilename");
			fail("the requested file does not exist and the webservice should throw an exception");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// expect exception
		}
	}

	/**
	 * @throws FileNotFoundException
	 */
	public void testSendAndReturn() throws IOException {
		Attachment attachment = new Attachment(setupTestFile(TESTDATA1));
		Attachment attachment2 = attachService.sendAndReturnAttachment(attachment);
		trace("testReturn as String " + readAttachmentStart(attachment2));
		assertEquals("The webservice echos the request, how the result is not the same as it was sent in the request.",
				readAttachmentStart(attachment), readAttachmentStart(attachment2));
	}

	/**
	 * @throws Exception
	 */
	public void testReadFileAsInputStream() throws Exception {
		InputStream input = attachService.getFile();
		byte[] b = new byte[50];
		int rc = input.read(b);
		while (rc != -1) {
			String s = new String(b, 0, rc);
			trace(s);
			rc = input.read(b);
		}
	}

	/**
	 * @throws Exception
	 */
	public void testReadInputWithError() throws Exception {
		try {
			Attachment attachment = attachService.getBytesFromSampleWithError();
			attachment.readAsStream();
			fail();
		} catch (RemoteFailure e) {
			ok();
		}
	}

	/**
	 * @throws Exception
	 */
	public void testReadUrlWithErrorAtStart() throws Exception {
		try {
			Attachment attachment = attachService.getBytesFromUrlWithErrorAtStart();
			String output = readAttachmentStart(attachment);
			trace(output);
			fail();
		} catch (RuntimeException e) {
			ok();
		}
	}

	/**
	 * @throws Exception
	 */
	public void testReadUrlWithError() throws Exception {
		Attachment attachment = attachService.getBytesFromUrlWithError();
		String output = readAttachmentStart(attachment);
		trace(output);
	}

	/**
	 * @throws Exception
	 */
	public void testReadUrlWithoutError() throws Exception {
		Attachment attachment = attachService.getBytesFromUrlWithoutError();
		String output = readAttachmentStart(attachment);
		trace(output);
	}

	private final static int ATTACHMENT_START = 60;

	private String readAttachmentStart(Attachment attachment) {
		byte[] byteArray = new byte[ATTACHMENT_START];
		try {
			InputStream input = attachment.readAsStream();
			int nbrBytes = input.read(byteArray, 0, byteArray.length);
			if (nbrBytes < 1) {
				throw new IOException("Empty Attachment.");
			}
			return new String(byteArray, 0, nbrBytes);
		} catch (IOException e) {
			return "[can't read " + attachment + "]";
		}
	}

	/**
	 * nomen est omen
	 * 
	 * @throws Exception
	 */
	public void testReadAttachmentCreatedWithInvalidUrl() throws Exception {
		try {
			Attachment attachment = attachService.getBytesFromInvalidUrl();
			trace(attachment);
			fail();
		} catch (IOException e) {
			ok();
		}
	}

	/**
	 * @throws IOException
	 */
	public void testSendAndDeleteFile() throws IOException {
		// TODO warning suppression. Ignoring FindBugs problem about
		// hard coded reference to an absolute pathname. Appears to
		// be ok for testing.
		File file = new File("/testattachments.txt");
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			for (int i = 0; i < 200; i++) {
				out.write(i);
			}
		} finally {
			out.close();
		}

		// attachService.sendFile(new Attachment(file));
		boolean deleted = file.delete();
		assertTrue(deleted);
		assertFalse("file must be deleted by now", file.exists());

	}

	public void testRetrieveAttachmentAsObject() throws IOException {
		Object a = attachService.getAttachmentAsObject();
		assertTrue(a instanceof Attachment);
	}

	public void testEmptyAttachment() throws Exception {
		Attachment attachment = attachService.getEmptyAttachment();
		InputStream input = attachment.readAsStream();
		assertTrue(input != null);
		assertTrue(input.read() == -1);
	}

	/**
	 * Nomen est Omen
	 * 
	 * @throws Exception
	 */
	public void testSendLargeAttachments() throws Exception {
		// attachService =
		// ServiceAccessor.fetchService(PROXIEDATTACHMENTSERVICE,
		// IAttachmentService.class);
		System.out.println("generating 15 Mio bytes attachment");
		Attachment attachment = generateLargeAttachment(15000000);
		System.out.println("sending it");
		int i = attachService.sendAttachmentAndReturnSize(attachment);
		System.out.println("done");
		assertTrue(i == 15000000);
	}

	public void testSendFileAndTestIfItIsClosed() throws Exception {
		File file = File.createTempFile("attachTest", null);
		PrintWriter printWriter = new PrintWriter(new FileOutputStream(file));
		printWriter.write("This text file is accessed in AttachmentTest to test the Attachment class.");
		printWriter.close();
		Attachment attach = new Attachment(file);
		assertTrue(file.exists());
		// the object gets serialized
		AttachmentSerialized attach2 = (AttachmentSerialized) attach.writeReplace();
		ByteArrayDataSource byteArray = (ByteArrayDataSource) ReflectionUtils.getHidden(attach2, "internalDataSource");
		InputStream inputStream = byteArray.getInputStream();
		while (inputStream.read() != -1) {
			Nop.reason("nothing to do");
		}
		inputStream.close();
		assertTrue(file.exists());
		assertTrue(file.delete());
		assertFalse(file.exists());

	}

	private Attachment generateLargeAttachment(final int countInBytes) throws IOException {
		return new Attachment(new InputStream() {

			private int count = countInBytes;
			private SecureRandom random = new SecureRandom();

			@Override
			public int read() throws IOException {
				count--;
				if (count >= 0) {
					return random.nextInt();
				} else {
					return -1;
				}
			}
		});
	}

	private File setupTestFile(String string) {
		File file;
		try {
			file = File.createTempFile("attachTest", null);
			PrintWriter printWriter = new PrintWriter(new FileOutputStream(file));
			printWriter.write(string);
			printWriter.close();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void trace(Object object) {
		System.out.println(object.toString());
	}

}