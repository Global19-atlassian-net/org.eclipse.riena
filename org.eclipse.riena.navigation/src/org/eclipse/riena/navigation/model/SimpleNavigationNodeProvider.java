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
package org.eclipse.riena.navigation.model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.osgi.service.log.LogService;

import org.eclipse.core.runtime.Assert;
import org.eclipse.equinox.log.Logger;

import org.eclipse.riena.core.Log4r;
import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.core.util.StringUtils;
import org.eclipse.riena.internal.navigation.Activator;
import org.eclipse.riena.navigation.IAssemblerProvider;
import org.eclipse.riena.navigation.IGenericNavigationAssembler;
import org.eclipse.riena.navigation.INavigationAssembler;
import org.eclipse.riena.navigation.INavigationAssemblyExtension;
import org.eclipse.riena.navigation.INavigationNode;
import org.eclipse.riena.navigation.INavigationNodeProvider;
import org.eclipse.riena.navigation.INodeExtension;
import org.eclipse.riena.navigation.NavigationArgument;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.navigation.NodePositioner;
import org.eclipse.riena.navigation.StartupNodeInfo;
import org.eclipse.riena.navigation.StartupNodeInfo.Level;

/**
 * This class provides service methods to get information provided by
 * WorkAreaDefinitions.
 * 
 * @since 1.2
 */
public class SimpleNavigationNodeProvider implements INavigationNodeProvider, IAssemblerProvider {

	private final static Logger LOGGER = Log4r.getLogger(Activator.getDefault(), SimpleNavigationNodeProvider.class);
	private static Random random = null;

	private final Map<String, INavigationAssembler> assemblyId2AssemblerCache = new HashMap<String, INavigationAssembler>();

	/**
	 * 
	 */
	public SimpleNavigationNodeProvider() {
		// symbols are not replaced here - this happens upon creation of the NavigationNode
		Inject.extension(getNavigationAssemblyExtensionPointSafe()).useType(getNavigationAssemblyExtensionIFSafe())
				.into(this).andStart(Activator.getDefault().getContext());
	}

	private String getNavigationAssemblyExtensionPointSafe() {
		if (getNavigationAssemblyExtensionPoint() != null && getNavigationAssemblyExtensionPoint().trim().length() != 0) {
			return getNavigationAssemblyExtensionPoint();
		} else {
			return INavigationAssemblyExtension.EXTENSIONPOINT;
		}
	}

	/**
	 * Override this method if you intend to use a different extension point
	 * 
	 * @return The extension point used to contribute navigation assemblies
	 */
	public String getNavigationAssemblyExtensionPoint() {
		return INavigationAssemblyExtension.EXTENSIONPOINT;
	}

	private Class<? extends INavigationAssemblyExtension> getNavigationAssemblyExtensionIFSafe() {
		if (getNavigationAssemblyExtensionIF() != null && getNavigationAssemblyExtensionIF().isInterface()) {
			return getNavigationAssemblyExtensionIF();
		} else {
			return INavigationAssemblyExtension.class;
		}
	}

	public Class<? extends INavigationAssemblyExtension> getNavigationAssemblyExtensionIF() {
		return INavigationAssemblyExtension.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	protected INavigationNode<?> provideNodeHook(INavigationNode<?> sourceNode, NavigationNodeId targetId,
			NavigationArgument argument) {

		INavigationNode<?> targetNode = findNode(getRootNode(sourceNode), targetId);
		if (targetNode == null) {
			if (LOGGER.isLoggable(LogService.LOG_DEBUG)) {
				LOGGER.log(LogService.LOG_DEBUG, "createNode: " + targetId); //$NON-NLS-1$
			}

			INavigationAssembler assembler = getNavigationAssembler(targetId, argument);
			if (assembler != null) {
				NavigationNodeId parentTypeId = getParentTypeId(argument, assembler);
				// Call of findNode() on the result of method provideNodeHook() fixes problem for the case when the result 
				// is not the node with typeId parentTypeId but one of the nodes parents (i.e. when the nodes assembler also 
				// builds some of its parent nodes). 
				INavigationNode parentNode = findNode(provideNodeHook(sourceNode, parentTypeId, null), parentTypeId);
				prepareNavigationAssembler(targetId, assembler, parentNode);
				targetNode = assembler.buildNode(targetId, argument);
				if (targetNode.getNodeId() == null && assembler.getAssembly().getId().equals(targetId.getTypeId())) {
					targetNode.setNodeId(targetId);
				}

				NodePositioner nodePositioner = argument != null ? argument.getNodePositioner()
						: NodePositioner.ADD_END;
				nodePositioner.addChildToParent(parentNode, targetNode);

			} else {
				throw new ExtensionPointFailure("No assembler found for ID=" + targetId.getTypeId()); //$NON-NLS-1$
			}
		}
		if (argument != null) {
			// store the NavigationArgument in node context
			targetNode.setContext(NavigationArgument.CONTEXTKEY_ARGUMENT, argument);
			if (argument.isPrepareAll()) {
				prepareAll(targetNode);
			}
		}

		return targetNode;
	}

	/**
	 * Prepares the given node and all its children.
	 * 
	 * @param node
	 *            navigation node to prepare.
	 */
	private void prepareAll(INavigationNode<?> node) {
		node.prepare();
		for (INavigationNode<?> child : node.getChildren()) {
			prepareAll(child);
		}
	}

	private NavigationNodeId getParentTypeId(NavigationArgument argument, INavigationAssembler assembler) {
		if (argument != null && argument.getParentNodeId() != null) {
			return argument.getParentNodeId();
		} else {
			String parentTypeId = assembler.getAssembly().getParentTypeId();
			if (StringUtils.isEmpty(parentTypeId)) {
				throw new ExtensionPointFailure("parentTypeId cannot be null or blank for assembly ID=" //$NON-NLS-1$
						+ assembler.getAssembly().getId());
			}
			return new NavigationNodeId(parentTypeId);
		}
	}

	protected GenericNavigationAssembler createDefaultAssembler() {
		return new GenericNavigationAssembler();
	}

	/**
	 * {@inheritDoc}
	 */
	public INavigationNode<?> provideNode(INavigationNode<?> sourceNode, NavigationNodeId targetId,
			NavigationArgument argument) {
		return provideNodeHook(sourceNode, targetId, argument);
	}

	public List<StartupNodeInfo> getSortedStartupNodeInfos() {
		List<StartupNodeInfo> startups = new ArrayList<StartupNodeInfo>();

		for (INavigationAssembler assembler : getNavigationAssemblers()) {
			Integer sequence = getAutostartSequence(assembler.getAssembly());
			if (sequence != null) {
				StartupNodeInfo startupNodeInfo = createStartupSortable(assembler.getAssembly(), sequence);
				if (startupNodeInfo != null) {
					startups.add(startupNodeInfo);
				}
			}
		}
		Collections.sort(startups);
		return startups;
	}

	private Integer getAutostartSequence(INavigationAssemblyExtension assembly) {
		try {
			return assembly.getAutostartSequence();
		} catch (Exception ignore) {
			// does not seem to be an integer, assume no autostart
			return null;
		}
	}

	private StartupNodeInfo createStartupSortable(final INavigationAssemblyExtension assembly, final Integer sequence) {
		String id = getTypeId(assembly.getSubApplicationNode());
		if (id != null) {
			return new StartupNodeInfo(Level.SUBAPPLICATION, sequence, id);
		}
		id = getTypeId(assembly.getModuleGroupNode());
		if (id != null) {
			return new StartupNodeInfo(Level.MODULEGROUP, sequence, id);
		}
		id = getTypeId(assembly.getModuleNode());
		if (id != null) {
			return new StartupNodeInfo(Level.MODULE, sequence, id);
		}
		id = getTypeId(assembly.getSubModuleNode());
		if (id != null) {
			return new StartupNodeInfo(Level.SUBMODULE, sequence, id);
		}
		Assert.isNotNull(assembly.getNavigationAssembler(), "Assembly '" + assembly.getId() //$NON-NLS-1$
				+ "' must have an assembler specified since no immediate child has a typeId."); //$NON-NLS-1$
		id = assembly.getId();
		if (id != null) {
			return new StartupNodeInfo(Level.CUSTOM, sequence, id);
		}
		return null;
	}

	private String getTypeId(INodeExtension extension) {
		return extension == null ? null : extension.getTypeId();
	}

	/**
	 * Used to prepare the assembler in a application specific way.
	 * 
	 * @param targetId
	 * @param assembler
	 * @param parentNode
	 */
	protected void prepareNavigationAssembler(NavigationNodeId targetId, INavigationAssembler assembler,
			INavigationNode<?> parentNode) {
		if (assembler instanceof IGenericNavigationAssembler) {
			((IGenericNavigationAssembler) assembler).setAssemblerProvider(this);
		}
	}

	public Collection<INavigationAssembler> getNavigationAssemblers() {
		return assemblyId2AssemblerCache.values();
	}

	public void registerNavigationAssembler(String id, INavigationAssembler assembler) {
		INavigationAssembler oldAssembler = assemblyId2AssemblerCache.put(id, assembler);
		if (oldAssembler != null) {
			String msg = String.format("There are two assembly extension definitions for '%s'.", id); //$NON-NLS-1$
			RuntimeException runtimeExc = new IllegalStateException(msg);
			LOGGER.log(LogService.LOG_ERROR, msg, runtimeExc);
			throw runtimeExc;
		}
	}

	public INavigationAssembler getNavigationAssembler(String assemblyId) {
		return assemblyId2AssemblerCache.get(assemblyId);
	}

	public INavigationAssembler getNavigationAssembler(NavigationNodeId nodeId, NavigationArgument argument) {

		if (nodeId != null && nodeId.getTypeId() != null) {
			for (INavigationAssembler probe : getNavigationAssemblers()) {
				if (probe.acceptsToBuildNode(nodeId, argument)) {
					return probe;
				}
			}
		}

		return null;
	}

	/**
	 * @param node
	 * @return
	 */
	protected INavigationNode<?> getRootNode(INavigationNode<?> node) {
		if (node.getParent() == null) {
			return node;
		}
		return getRootNode(node.getParent());
	}

	/**
	 * @param node
	 * @param targetId
	 * @return
	 */
	protected INavigationNode<?> findNode(INavigationNode<?> node, NavigationNodeId targetId) {
		if (targetId == null) {
			return null;
		}
		if (targetId.equals(node.getNodeId())) {
			return node;
		}
		for (INavigationNode<?> child : node.getChildren()) {
			INavigationNode<?> foundNode = findNode(child, targetId);
			if (foundNode != null) {
				return foundNode;
			}
		}
		return null;
	}

	public void register(INavigationAssemblyExtension assembly) {

		String assemblyId = assembly.getId();

		if (assemblyId == null) {
			if (random == null) {
				try {
					random = SecureRandom.getInstance("SHA1PRNG"); //$NON-NLS-1$
				} catch (NoSuchAlgorithmException e) {
					random = new Random(System.currentTimeMillis());
				}
			}
			assemblyId = "Riena.random.assemblyid." + Long.valueOf(random.nextLong()).toString(); //$NON-NLS-1$
			LOGGER.log(LogService.LOG_DEBUG, "Assembly has no id. Generated a random '" + assemblyId //$NON-NLS-1$
					+ "'. For Assembler=" + assembly.getNavigationAssembler()); //$NON-NLS-1$
		}

		INavigationAssembler assembler = assembly.createNavigationAssembler();
		if (assembler == null) {
			assembler = createDefaultAssembler();
		}
		assembler.setAssembly(assembly);

		registerNavigationAssembler(assemblyId, assembler);

	}

	/**
	 * @see org.eclipse.riena.navigation.INavigationNodeProvider#cleanUp()
	 */
	public void cleanUp() {
		assemblyId2AssemblerCache.clear();
	}

	/**
	 * This is called by extension injection to provide the extension points
	 * found
	 * 
	 * @param data
	 *            The navigation assemblies contributed by all extension points
	 */
	public void update(INavigationAssemblyExtension[] data) {

		if (data == null) {
			throw new RuntimeException();
		}
		cleanUp();
		for (INavigationAssemblyExtension assembly : data) {
			register(assembly);
		}
	}

}
