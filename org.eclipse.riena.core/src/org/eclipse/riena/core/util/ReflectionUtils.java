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
package org.eclipse.riena.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.eclipse.core.runtime.Assert;
import org.eclipse.riena.internal.core.Activator;
import org.eclipse.riena.internal.core.ignore.IgnoreFindBugs;
import org.osgi.framework.Bundle;

/**
 * The <code>ReflectionUtils</code> class is a collection of useful helpers when
 * working with reflection. <br>
 * <b>Note:</b> All methods only throw Failures (i.e. RuntimeExceptions)!<br>
 * <b>Note:</b> Use this helper only for test code!!!
 */
public final class ReflectionUtils {

	/**
	 * Private default constructor.
	 */
	private ReflectionUtils() {
		// Utility class
	}

	/**
	 * Create a new instance of type �className� by invoking the constructor
	 * with the given list of arguments.
	 * 
	 * @param className
	 *            the type of new instance.
	 * @param args
	 *            the arguments for the constructor.
	 * @return the new instance.
	 * @pre className != null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className, Object... args) {
		Assert.isNotNull(className, "className must be given!"); //$NON-NLS-1$

		try {
			return (T) newInstance(false, loadClass(className), args);
		} catch (Exception e) {
			throw new ReflectionFailure("Error creating instance for " + className + " with parameters " //$NON-NLS-1$ //$NON-NLS-2$
					+ Arrays.asList(args) + "!", e); //$NON-NLS-1$
		}
	}

	/**
	 * Create a new instance of type �className� by invoking the constructor
	 * with the given list of arguments.
	 * 
	 * @param className
	 *            the type of new instance.
	 * @param args
	 *            the arguments for the constructor.
	 * @return the new instance.
	 * @pre className != null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstanceHidden(String className, Object... args) {
		Assert.isNotNull(className, "className must be given!"); //$NON-NLS-1$

		try {
			return (T) newInstance(true, loadClass(className), args);
		} catch (Exception e) {
			throw new ReflectionFailure("Error creating instance for " + className + " with parameters " //$NON-NLS-1$ //$NON-NLS-2$
					+ Arrays.asList(args) + "!", e); //$NON-NLS-1$
		}
	}

	/**
	 * Create a new instance of type �clazz� by invoking the constructor with
	 * the given list of arguments.
	 * 
	 * @param clazz
	 *            the type of new instance.
	 * @param args
	 *            the arguments for the constructor.
	 * @return the new instance.
	 * @pre clazz != null
	 */
	public static <T> T newInstance(Class<T> clazz, Object... args) {
		return newInstance(false, clazz, args);
	}

	/**
	 * Create a new instance of type �clazz� by invoking the constructor with
	 * the given list of arguments.
	 * 
	 * @param clazz
	 *            the type of new instance.
	 * @param args
	 *            the arguments for the constructor.
	 * @return the new instance.
	 * @pre clazz != null
	 */
	public static <T> T newInstanceHidden(Class<T> clazz, Object... args) {
		return newInstance(true, clazz, args);
	}

	/**
	 * Create a new instance of type �clazz� by invoking the constructor with
	 * the given list of arguments.
	 * 
	 * @param open
	 *            if true it is tried to make it accessible.
	 * @param clazz
	 *            the type of new instance.
	 * @param args
	 *            the arguments for the constructor.
	 * @return the new instance.
	 * @pre clazz != null
	 */
	private static <T> T newInstance(boolean open, Class<T> clazz, Object... args) {
		Assert.isNotNull(clazz, "clazz must be given!"); //$NON-NLS-1$

		try {
			Class<?>[] clazzes = classesPrimitiveFromObjects(args);
			Constructor<T> constructor = findMatchingConstructor(clazz, clazzes);
			if (constructor == null) {
				clazzes = classesFromObjects(args);
				constructor = findMatchingConstructor(clazz, clazzes);
			}
			if (open) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance(args);
		} catch (Throwable t) {
			throw new ReflectionFailure("Error creating instance for " + clazz.getName() + " with parameters " //$NON-NLS-1$ //$NON-NLS-2$
					+ Arrays.asList(args) + "!", t); //$NON-NLS-1$
		}
	}

	/**
	 * Create a new proxy instance of type �interfaceName�.
	 * 
	 * @param interfaceName
	 *            the type of new instance.
	 * @param invocationHandler
	 *            the invocation handler for the proxy.
	 * @return the new instance.
	 * @pre interfaceName != null
	 * @pre invocationHandler != null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String interfaceName, InvocationHandler invocationHandler) {
		Assert.isNotNull(interfaceName, "interfaceName must be given!"); //$NON-NLS-1$
		Assert.isNotNull(invocationHandler, "invocationHandler must be given!"); //$NON-NLS-1$
		try {
			return (T) newInstance(Class.forName(interfaceName), invocationHandler);
		} catch (Exception e) {
			throw new ReflectionFailure("Error creating proxy instance for " + interfaceName + " !", e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Create a new proxy instance of type �interfaze�.
	 * 
	 * @param interfaze
	 *            the type of new instance.
	 * @param invocationHandler
	 *            the invocation handler for the proxy.
	 * @return the new instance.
	 * @pre interfaze != null
	 * @pre invocationHandler != null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> interfaze, InvocationHandler invocationHandler) {
		Assert.isNotNull(interfaze, "interfaceName must be given"); //$NON-NLS-1$
		Assert.isNotNull(invocationHandler, "invocationHandler must be given"); //$NON-NLS-1$

		Class proxyClass = Proxy.getProxyClass(interfaze.getClassLoader(), new Class<?>[] { interfaze });
		try {
			Constructor<T> constructor = proxyClass.getConstructor(new Class[] { InvocationHandler.class });
			T object = constructor.newInstance(new Object[] { invocationHandler });
			return object;
		} catch (Throwable throwable) {
			throw new ReflectionFailure("Error creating proxy instance for " + interfaze.getName() + " !", throwable); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Invoke an accessible method on a given object with the given parameters.
	 * 
	 * @param instance
	 *            the instance to invoke the method on, or the class for static
	 *            methods.
	 * @param methodName
	 *            the name of the method to invoke.
	 * @param args
	 *            the actual arguments of the method to invoke.
	 * @return the result of the invocation.
	 * @pre instance != null
	 * @pre methodName != null
	 */
	public static <T> T invoke(Object instance, String methodName, Object... args) {
		return invoke(false, instance, methodName, args);
	}

	/**
	 * Invoke a �hidden� (private, protected, ...) method on a given object. <br>
	 * <b>This method should not be used for production code but only for unit
	 * tests! </b>
	 * 
	 * @param instance
	 *            the instance to invoke the method on, or the class for static
	 *            methods.
	 * @param methodName
	 *            the name of the method to invoke.
	 * @param args
	 *            the actual arguments of the method to invoke.
	 * @return the result of the invocation.
	 * @pre instance != null
	 * @pre methodName != null
	 */
	public static <T> T invokeHidden(Object instance, String methodName, Object... args) {
		return invoke(true, instance, methodName, args);
	}

	/**
	 * Invoke an acccesible method on a given object allowing this method to
	 * throw the given type of exception.<br>
	 * <b>This method should not be used for production code but only for unit
	 * tests! </b>
	 * 
	 * @param <T>
	 * @param instance
	 * @param methodName
	 * @param expectedException
	 * @param args
	 * @return
	 * @throws T
	 * @pre instance != null
	 * @pre methodName != null
	 * @pre expectedException != null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Throwable> Object invoke(Object instance, String methodName, Class<T> expectedException,
			Object... args) throws T {
		Assert.isNotNull(expectedException, "expectedException should not be null!"); //$NON-NLS-1$

		try {
			return invoke(instance, methodName, args);
		} catch (InvocationTargetFailure e) {
			if (expectedException.isAssignableFrom(e.getCause().getClass())) {
				throw (T) e.getCause();
			}
			throw e;
		}
	}

	/**
	 * Invoke a �hidden� (private, protected, ...) method on a given object
	 * allowing this method to throw the given type of exception.<br>
	 * <b>This method should not be used for production code but only for unit
	 * tests! </b>
	 * 
	 * @param <T>
	 * @param instance
	 * @param methodName
	 * @param expectedException
	 * @param args
	 * @return
	 * @throws T
	 * @pre instance != null
	 * @pre methodName != null
	 * @pre expectedException != null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Throwable> Object invokeHidden(Object instance, String methodName,
			Class<T> expectedException, Object... args) throws T {
		Assert.isNotNull(expectedException, "expectedException should not be null!"); //$NON-NLS-1$

		try {
			return invokeHidden(instance, methodName, args);
		} catch (InvocationTargetFailure e) {
			if (expectedException.isAssignableFrom(e.getCause().getClass())) {
				throw (T) e.getCause();
			}
			throw e;
		}
	}

	/**
	 * Invoke a (private, protected, ...) method on a given object. If specified
	 * try to make the method accessible.
	 * 
	 * @param open
	 *            if true it is tried to make it accessible.
	 * @param instance
	 *            the instance to invoke the method on, or the class for static
	 *            methods.
	 * @param methodName
	 *            the name of the method to invoke.
	 * @param args
	 *            the actual arguments of the method to invoke.
	 * @return the result of the invocation.
	 * @pre instance != null
	 * @pre methodName != null
	 */
	@SuppressWarnings("unchecked")
	private static <T> T invoke(boolean open, Object instance, String methodName, Object... args) {
		Assert.isNotNull(instance, "instance must be given!"); //$NON-NLS-1$
		Assert.isNotNull(methodName, "methodName must be given!"); //$NON-NLS-1$

		Class<?> clazz = getClass(instance);
		while (clazz != null) {
			Class<?>[] clazzes = classesPrimitiveFromObjects(args);
			Method method = findMatchingMethod(clazz, methodName, clazzes);
			if (method == null) {
				clazzes = classesFromObjects(args);
				method = findMatchingMethod(clazz, methodName, clazzes);
			}
			if (method != null) {
				if (open) {
					method.setAccessible(true);
				}
				try {
					return (T) method.invoke(instance, args);
				} catch (InvocationTargetException ite) {
					throw new InvocationTargetFailure("Calling #" + methodName + " on " + instance + " failed.", ite //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							.getTargetException());
				} catch (IllegalArgumentException e) {
					throw new ReflectionFailure("Calling #" + methodName + " on " + instance + " failed.", e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				} catch (IllegalAccessException e) {
					throw new ReflectionFailure("Calling #" + methodName + " on " + instance + " failed.", e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
			clazz = clazz.getSuperclass();
		}

		throw new ReflectionFailure("Could not invoke hidden method " + methodName + " on " //$NON-NLS-1$ //$NON-NLS-2$
				+ instance.getClass().getName() + "!"); //$NON-NLS-1$
	}

	/**
	 * Set a �hidden� (private, protected, ...) field on a given object. <br>
	 * <b>This field should not be used for production code but only for unit
	 * test. </b>
	 * 
	 * @param instance
	 *            the instance to set the field, or the class for static fields.
	 * @param fieldName
	 *            the name of the field to set.
	 * @param value
	 *            the new value for the field (use wrappers for primitive
	 *            types).
	 * @pre instance != null
	 * @pre fieldName != null
	 * @pre value != null
	 */
	@IgnoreFindBugs(value = "DP_DO_INSIDE_DO_PRIVILEGED", justification = "only intended for unit tests")
	public static void setHidden(Object instance, String fieldName, Object value) {
		Assert.isNotNull(instance, "instance must be given!"); //$NON-NLS-1$
		Assert.isNotNull(fieldName, "fieldName must be given!"); //$NON-NLS-1$

		Class<?> clazz = getClass(instance);
		try {
			Field field = getDeepField(clazz, fieldName);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (Exception e) {
			throw new ReflectionFailure("Could not set hidden field " + fieldName + " on " + clazz.getName() + "!", e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	/**
	 * Get a �hidden� (private, protected, ...) field from a given object. <br>
	 * <b>This method should not be used for production code but only for unit
	 * test. </b>
	 * 
	 * @param instance
	 *            the instance to get the field, or the class for static fields
	 * @param fieldName
	 *            the name of the field to set.
	 * @return the value of the field (returns wrappers for primitive types).
	 * @pre instance != null
	 * @pre fieldName != null
	 */
	@SuppressWarnings("unchecked")
	@IgnoreFindBugs(value = "DP_DO_INSIDE_DO_PRIVILEGED", justification = "only intended for unit tests")
	public static <T> T getHidden(Object instance, String fieldName) {
		Assert.isNotNull(instance, "instance must be given!"); //$NON-NLS-1$
		Assert.isNotNull(fieldName, "fieldName must be given!"); //$NON-NLS-1$

		Class<?> clazz = getClass(instance);
		try {
			Field field = getDeepField(clazz, fieldName);
			field.setAccessible(true);
			return (T) field.get(instance);
		} catch (Exception e) {
			throw new ReflectionFailure("Could not get hidden field " + fieldName + " on " + clazz.getName() + "!", e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	/**
	 * @param <T>
	 * @param interfaze
	 * @param clazz
	 * @param args
	 * @return
	 */
	public static <T> T newLazyInstance(Class<T> interfaze, Class<? extends T> clazz, Object... args) {
		return newInstance(interfaze, new LazyInstantiationHandler(clazz, args));
	}

	/**
	 * @param <T>
	 * @param interfaze
	 * @param clazz
	 * @param args
	 * @return
	 */
	public static <T> T newLazyInstance(Class<T> interfaze, String clazz, Object... args) {
		return newInstance(interfaze, new LazyInstantiationHandler(clazz, args));
	}

	private static class LazyInstantiationHandler implements InvocationHandler {

		private Object instance;
		private Class<?> clazz;
		private String clazzName;
		private Object[] params;

		/**
		 * @param clazz
		 * @param args
		 */
		public LazyInstantiationHandler(Class<?> clazz, Object[] args) {
			this.clazz = clazz;
			this.params = args;
		}

		/**
		 * @param clazz
		 * @param args
		 */
		public LazyInstantiationHandler(String clazz, Object[] args) {
			this.clazzName = clazz;
			this.params = args;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (instance == null) {
				instance = clazz != null ? newInstance(clazz, params) : newInstance(clazzName, params);
			}
			// try {
			return ReflectionUtils.invoke(instance, method.getName(), args);
			// } catch (Throwable e) {
			// if ( isDeclaredException( method, e )) {
			// throw e;
			// }
			// throw new ReflectionFailure("", e);
			// }
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> Constructor<T> findMatchingConstructor(Class<T> clazz, Class<?>[] clazzes) {
		assert clazz != null;

		try {
			if (clazzes == null) {
				return clazz.getConstructor();
			}

			Constructor[] constructors = clazz.getConstructors();
			for (Constructor constructor : constructors) {
				Class<?>[] expectedParameterTypes = constructor.getParameterTypes();
				if (expectedParameterTypes.length == clazzes.length) {
					boolean stop = false;
					for (int j = 0; j < expectedParameterTypes.length && !stop; j++) {
						if (!expectedParameterTypes[j].isAssignableFrom(clazzes[j])) {
							stop = true;
						}
					}
					if (!stop) {
						return constructor;
					}
				}
			}
			throw new ReflectionFailure("Could not find a matching constructor for " + clazz.getName()); //$NON-NLS-1$
		} catch (NoSuchMethodException nsme) {
			throw new ReflectionFailure("Could not find a matching constructor for " + clazz.getName(), nsme); //$NON-NLS-1$
		}
	}

	private static Method findMatchingMethod(Class<?> clazz, String name, Class<?>[] clazzes) {
		assert clazz != null;
		assert name != null;

		try {
			if (clazzes == null) {
				return clazz.getDeclaredMethod(name);
			}

			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(name)) {
					Class<?>[] expectedParameterTypes = method.getParameterTypes();
					if (expectedParameterTypes.length == clazzes.length) {
						boolean stop = false;
						for (int j = 0; j < expectedParameterTypes.length && !stop; j++) {
							if (!expectedParameterTypes[j].isAssignableFrom(clazzes[j])) {
								stop = true;
							}
						}
						if (!stop) {
							return method;
						}
					}
				}
			}
			return null;
		} catch (NoSuchMethodException nsme) {
			return null;
		}
	}

	private static Class<? extends Object>[] classesPrimitiveFromObjects(Object[] objects) {
		Class<?>[] clazzes = null;
		if (objects != null) {
			clazzes = new Class<?>[objects.length];
			for (int i = 0; i < objects.length; i++) {
				Class<?> argClass = Object.class;
				if (objects[i] != null) {
					argClass = objects[i].getClass();
					if (argClass == Integer.class) {
						argClass = int.class;
					} else if (argClass == Long.class) {
						argClass = long.class;
					} else if (argClass == Short.class) {
						argClass = short.class;
					} else if (argClass == Boolean.class) {
						argClass = boolean.class;
					} else if (argClass == Byte.class) {
						argClass = byte.class;
					} else if (argClass == Float.class) {
						argClass = float.class;
					} else if (argClass == Double.class) {
						argClass = double.class;
					} else if (argClass == Character.class) {
						argClass = char.class;
					}
				}
				clazzes[i] = argClass;
			}
		}
		return clazzes;
	}

	private static Class<? extends Object>[] classesFromObjects(Object[] objects) {
		Class<?>[] clazzes = null;
		if (objects != null) {
			clazzes = new Class<?>[objects.length];
			for (int i = 0; i < objects.length; i++) {
				if (objects[i] != null) {
					Class<?> argClass = objects[i].getClass();
					clazzes[i] = argClass;
				} else {
					clazzes[i] = Object.class;
				}
			}
		}
		return clazzes;
	}

	private static Class<?> getClass(Object instance) {
		assert instance != null;

		return (instance instanceof Class) ? (Class<?>) instance : instance.getClass();
	}

	private static Field getDeepField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
		assert clazz != null;
		assert fieldName != null;

		Class<?> lookIn = clazz;
		while (lookIn != null) {
			try {
				return lookIn.getDeclaredField(fieldName);
			} catch (NoSuchFieldException nsfe) {
				lookIn = lookIn.getSuperclass();
			}
		}
		throw new NoSuchFieldException("Could not find field " + fieldName + " within class " + clazz + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> loadClass(String className) {
		ClassNotFoundException cnfe = null;
		try {
			return (Class<T>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			cnfe = e;
		}
		// ok, do it the hard way!!
		Bundle[] bundles = Activator.getDefault().getContext().getBundles();
		Class<T> foundClass = null;
		for (Bundle bundle : bundles) {
			try {
				if (foundClass != null) {
					throw new ReflectionFailure(
							"Could not load class " + className + " because it exists in at least two bundles."); //$NON-NLS-1$ //$NON-NLS-2$
				}
				cnfe = null;
				foundClass = bundle.loadClass(className);
				return bundle.loadClass(className);
			} catch (ClassNotFoundException e) {
				cnfe = e;
			}
		}
		if (foundClass == null) {
			throw new ReflectionFailure("Could not load class " + className + ".", cnfe); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return foundClass;
	}
}
