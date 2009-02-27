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
package org.eclipse.riena.internal.communication.factory.hessian;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.riena.communication.core.RemoteFailure;

import com.caucho.hessian.io.AbstractDeserializer;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.AbstractSerializer;
import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;

/**
 * SerializerFactory used to serialize and deserialize InputStream, used for the
 * Attachment object.
 */
public class InputStreamSerializerFactory extends AbstractSerializerFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caucho.hessian.io.AbstractSerializerFactory#getDeserializer(java.
	 * lang.Class)
	 */
	@Override
	public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
		if (isInputStream(cl)) {
			return new AbstractDeserializer() {

				@Override
				public Object readObject(AbstractHessianInput in) throws IOException {
					try {
						byte[] bytes = in.readBytes();
						return new ByteArrayInputStream(bytes);
					} catch (HessianProtocolException e) {
						throw new RemoteFailure(
								"Error while reading Attachment content. Probably incomplete or interrupted Attachment inputstream. " + e.getMessage()); //$NON-NLS-1$
					}
				}
			};
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.caucho.hessian.io.AbstractSerializerFactory#getSerializer(java.lang
	 * .Class)
	 */
	@Override
	public Serializer getSerializer(Class cl) throws HessianProtocolException {
		if (isInputStream(cl)) {
			return new AbstractSerializer() {

				@Override
				public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
					InputStream is = (InputStream) obj;
					try {

						if (is == null) {
							out.writeNull();
						} else {
							byte[] buf = new byte[1024];
							int len = 0;

							while (true) {
								try {
									len = is.read(buf, 0, buf.length);
								} catch (IOException e) {
									// catch the exception only for the inputstream and close
									// write null so that the client gets a "hick-up" and can tell that there is something wrong
									out.writeNull();
									break;
								}
								if (len > 0) {
									out.writeByteBufferPart(buf, 0, len);
								} else {
									break;
								}
							}

							out.writeByteBufferEnd(buf, 0, 0);
						}
					} finally {
						is.close();
					}

				}
			};
		}
		return null;
	}

	private boolean isInputStream(Class cl) {
		if (cl == InputStream.class) {
			return true;
		}
		Class superCl = cl;
		while (superCl != null && superCl != Object.class) {
			superCl = superCl.getSuperclass();
			if (superCl == InputStream.class) {
				return true;
			}
		}
		return false;
	}

}
