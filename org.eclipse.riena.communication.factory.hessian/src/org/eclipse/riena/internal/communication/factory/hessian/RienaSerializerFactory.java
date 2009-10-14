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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.caucho.hessian.io.AbstractSerializerFactory;
import com.caucho.hessian.io.Deserializer;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.Serializer;
import com.caucho.hessian.io.SerializerFactory;

import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.core.util.ReflectionUtils;

/**
 * The {@code RienaSerializerFactory} is a delegating {@code
 * AbstractSerializerFactory}. It�s main purpose is to act like a regular
 * {@code AbstractSerializerFactory} but internally (not visible to hessian) it
 * manages a configurable list of {@code AbstractRienaSerializerFactory} and
 * takes care of their �salience�.<br>
 */
public class RienaSerializerFactory extends AbstractSerializerFactory {

	private List<AbstractRienaSerializerFactory> serializerFactories = new ArrayList<AbstractRienaSerializerFactory>();

	public RienaSerializerFactory() {
		prepareHessianSerializerFactory();
		Inject.extension(ISerializerFactoryExtension.EXTENSION_ID).into(this).andStart(
				Activator.getDefault().getContext());
	}

	@Override
	public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
		synchronized (this) {
			for (AbstractRienaSerializerFactory serializerFactory : serializerFactories) {
				Deserializer deserializer = serializerFactory.getDeserializer(cl);
				if (deserializer != null) {
					return deserializer;
				}
			}
		}
		return null;
	}

	@Override
	public Serializer getSerializer(Class cl) throws HessianProtocolException {
		synchronized (this) {
			for (AbstractRienaSerializerFactory serializerFactory : serializerFactories) {
				Serializer serializer = serializerFactory.getSerializer(cl);
				if (serializer != null) {
					return serializer;
				}
			}
		}
		return null;
	}

	public void update(ISerializerFactoryExtension[] serializerFactoryDefinitions) {
		List<AbstractRienaSerializerFactory> rienaSerializerFactories = new ArrayList<AbstractRienaSerializerFactory>(
				serializerFactoryDefinitions.length);
		for (ISerializerFactoryExtension serializerFactoryExtension : serializerFactoryDefinitions) {
			rienaSerializerFactories.add(serializerFactoryExtension.createImplementation());
		}
		// sort and than make active
		Collections.sort(rienaSerializerFactories, new AbstractRienaSerializerFactory.SalienceComparator());
		synchronized (this) {
			serializerFactories = rienaSerializerFactories;
		}
	}

	/**
	 * Tweak the hessian {@code SerializerFactory}, i.e. remove stuff we think
	 * we can do better.
	 */
	private void prepareHessianSerializerFactory() {
		HashMap<?, ?> staticDeSerMap = ReflectionUtils.getHidden(SerializerFactory.class, "_staticDeserializerMap"); //$NON-NLS-1$
		staticDeSerMap.remove(java.io.InputStream.class);
		staticDeSerMap.remove(StackTraceElement.class);
		HashMap<?, ?> staticSerMap = ReflectionUtils.getHidden(SerializerFactory.class, "_staticSerializerMap"); //$NON-NLS-1$
		staticSerMap.remove(java.io.InputStream.class);
	}

}
