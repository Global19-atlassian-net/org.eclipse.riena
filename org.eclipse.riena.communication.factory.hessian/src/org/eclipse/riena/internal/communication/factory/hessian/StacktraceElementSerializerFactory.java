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

import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.JavaDeserializer;
import com.caucho.hessian.io.Serializer;

/**
 *
 */
public class StacktraceElementSerializerFactory extends AbstractRienaSerializerFactory {

	@Override
	public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
		if (cl.equals(StackTraceElement.class)) {
			return new JavaDeserializer(cl) {

				@Override
				protected Object instantiate() throws Exception {
					return new StackTraceElement("x", "x", "x", 1); // just return a dummy, fields will be set by the JavaDeserializer //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			};
		}
		return null;
	}

	@Override
	public Serializer getSerializer(Class cl) throws HessianProtocolException {
		return null;
	}

}
