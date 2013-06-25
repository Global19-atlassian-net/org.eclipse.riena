/*******************************************************************************
 * Copyright (c) 2007, 2013 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.e4.launcher.part;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.internal.workbench.ContributionsAnalyzer;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationElement;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.commands.impl.CommandsFactoryImpl;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MRenderedMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarContribution;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.model.application.ui.menu.MTrimContribution;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuFactoryImpl;
import org.eclipse.e4.ui.workbench.renderers.swt.MenuManagerRenderer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.activities.IIdentifierListener;
import org.eclipse.ui.activities.IdentifierEvent;
import org.eclipse.ui.commands.ICommandImageService;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.menus.CompatibilityWorkbenchWindowControlContribution;
import org.eclipse.ui.internal.menus.ControlContributionRegistry;
import org.eclipse.ui.internal.menus.DynamicMenuContributionItem;
import org.eclipse.ui.internal.menus.MenuLocationURI;
import org.eclipse.ui.internal.registry.IWorkbenchRegistryConstants;
import org.eclipse.ui.internal.services.ServiceLocator;

public class MenuAdditionCacheEntry {
	final static String MAIN_TOOLBAR = "org.eclipse.ui.main.toolbar"; //$NON-NLS-1$

	final static String TRIM_COMMAND1 = "org.eclipse.ui.trim.command1"; //$NON-NLS-1$

	final static String TRIM_COMMAND2 = "org.eclipse.ui.trim.command2"; //$NON-NLS-1$

	final static String TRIM_VERTICAL1 = "org.eclipse.ui.trim.vertical1"; //$NON-NLS-1$

	final static String TRIM_VERTICAL2 = "org.eclipse.ui.trim.vertical2"; //$NON-NLS-1$

	final static String TRIM_STATUS = "org.eclipse.ui.trim.status"; //$NON-NLS-1$

	/**
	 * Test whether the location URI is in one of the pre-defined workbench trim areas.
	 * 
	 * @param location
	 * @return true if the URI is in workbench trim area.
	 */
	static boolean isInWorkbenchTrim(final MenuLocationURI location) {
		final String path = location.getPath();
		return MAIN_TOOLBAR.equals(path) || TRIM_COMMAND1.equals(path) || TRIM_COMMAND2.equals(path) || TRIM_VERTICAL1.equals(path)
				|| TRIM_VERTICAL2.equals(path) || TRIM_STATUS.equals(path);
	}

	private final MApplication application;
	// private IEclipseContext appContext;
	private final IConfigurationElement configElement;
	private final MenuLocationURI location;

	private final String namespaceIdentifier;

	//	private final IActivityManager activityManager;

	public MenuAdditionCacheEntry(final MApplication application, final IEclipseContext appContext, final IConfigurationElement configElement,
			final String attribute, final String namespaceIdentifier) {
		this.application = application;
		// this.appContext = appContext;
		assert appContext.equals(this.application.getContext());
		this.configElement = configElement;
		this.location = new MenuLocationURI(attribute);
		this.namespaceIdentifier = namespaceIdentifier;

		//		final IWorkbench workbench = application.getContext().get(IWorkbench.class);
		//		activityManager = workbench.getActivitySupport().getActivityManager();
	}

	private boolean inToolbar() {
		return location.getScheme().startsWith("toolbar"); //$NON-NLS-1$
	}

	/**
	 * @return <code>true</code> if this is a toolbar contribution
	 */
	public void mergeIntoModel(final ArrayList<MMenuContribution> menuContributions, final ArrayList<MToolBarContribution> toolBarContributions,
			final ArrayList<MTrimContribution> trimContributions) {
		if ("menu:help?after=additions".equals(location.toString())) { //$NON-NLS-1$
			final IConfigurationElement[] menus = configElement.getChildren(IWorkbenchRegistryConstants.TAG_MENU);
			if (menus.length == 1 && "org.eclipse.update.ui.updateMenu".equals(MenuHelper.getId(menus[0]))) { //$NON-NLS-1$
				return;
			}
		}
		if (location.getPath() == null || location.getPath().length() == 0) {
			WorkbenchPlugin.log("MenuAdditionCacheEntry.mergeIntoModel: Invalid menu URI: " + location); //$NON-NLS-1$
			return;
		}
		if (inToolbar()) {
			if (isInWorkbenchTrim(location)) {
				processTrimChildren(trimContributions, toolBarContributions, configElement);
			} else {
				String query = location.getQuery();
				if (query == null || query.length() == 0) {
					query = "after=additions"; //$NON-NLS-1$
				}
				processToolbarChildren(toolBarContributions, configElement, location.getPath(), query);
			}
			return;
		}
		final MMenuContribution menuContribution = MenuFactoryImpl.eINSTANCE.createMenuContribution();
		final String idContrib = MenuHelper.getId(configElement);
		if (idContrib != null && idContrib.length() > 0) {
			menuContribution.setElementId(idContrib);
		}
		if ("org.eclipse.ui.popup.any".equals(location.getPath())) { //$NON-NLS-1$
			menuContribution.setParentId("popup"); //$NON-NLS-1$
		} else {
			menuContribution.setParentId(location.getPath());
		}
		String query = location.getQuery();
		if (query == null || query.length() == 0) {
			query = "after=additions"; //$NON-NLS-1$
		}
		menuContribution.setPositionInParent(query);
		menuContribution.getTags().add("scheme:" + location.getScheme()); //$NON-NLS-1$
		String filter = ContributionsAnalyzer.MC_MENU;
		if ("popup".equals(location.getScheme())) { //$NON-NLS-1$
			filter = ContributionsAnalyzer.MC_POPUP;
		}
		menuContribution.getTags().add(filter);
		menuContribution.setVisibleWhen(MenuHelper.getVisibleWhen(configElement));
		addMenuChildren(menuContribution, configElement, filter);
		menuContributions.add(menuContribution);
		processMenuChildren(menuContributions, configElement, filter);
	}

	/**
	 * @param menuContributions
	 * @param filter
	 */
	private void processMenuChildren(final ArrayList<MMenuContribution> menuContributions, final IConfigurationElement element, final String filter) {
		final IConfigurationElement[] menus = element.getChildren(IWorkbenchRegistryConstants.TAG_MENU);
		if (menus.length == 0) {
			return;
		}
		for (final IConfigurationElement menu : menus) {
			final MMenuContribution menuContribution = MenuFactoryImpl.eINSTANCE.createMenuContribution();
			final String idContrib = MenuHelper.getId(menu);
			if (idContrib != null && idContrib.length() > 0) {
				menuContribution.setElementId(idContrib);
			}
			menuContribution.setParentId(idContrib);
			menuContribution.setPositionInParent("after=additions"); //$NON-NLS-1$
			menuContribution.getTags().add("scheme:" + location.getScheme()); //$NON-NLS-1$
			menuContribution.getTags().add(filter);
			menuContribution.setVisibleWhen(MenuHelper.getVisibleWhen(menu));
			addMenuChildren(menuContribution, menu, filter);
			menuContributions.add(menuContribution);
			processMenuChildren(menuContributions, menu, filter);
		}
	}

	private void addMenuChildren(final MElementContainer<MMenuElement> container, final IConfigurationElement parent, final String filter) {
		final IConfigurationElement[] items = parent.getChildren();
		for (final IConfigurationElement child : items) {
			final String itemType = child.getName();
			final String id = MenuHelper.getId(child);

			if (IWorkbenchRegistryConstants.TAG_COMMAND.equals(itemType)) {
				final MMenuElement element = createMenuCommandAddition(child);
				container.getChildren().add(element);
			} else if (IWorkbenchRegistryConstants.TAG_SEPARATOR.equals(itemType)) {
				final MMenuElement element = createMenuSeparatorAddition(child);
				container.getChildren().add(element);
			} else if (IWorkbenchRegistryConstants.TAG_MENU.equals(itemType)) {
				final MMenu element = createMenuAddition(child, filter);
				container.getChildren().add(element);
			} else if (IWorkbenchRegistryConstants.TAG_TOOLBAR.equals(itemType)) {
				System.out.println("Toolbar: " + id + " in " + location); //$NON-NLS-1$//$NON-NLS-2$
			} else if (IWorkbenchRegistryConstants.TAG_DYNAMIC.equals(itemType)) {
				final ContextFunction generator = new ContextFunction() {
					@Override
					public Object compute(final IEclipseContext context, final String contextKey) {
						final ServiceLocator sl = new ServiceLocator();
						sl.setContext(context);
						final DynamicMenuContributionItem item = new DynamicMenuContributionItem(MenuHelper.getId(child), sl, child);
						return item;
					}
				};

				final MRenderedMenuItem menuItem = MenuFactoryImpl.eINSTANCE.createRenderedMenuItem();
				menuItem.setElementId(id);
				menuItem.setContributionItem(generator);
				menuItem.setVisibleWhen(MenuHelper.getVisibleWhen(child));
				container.getChildren().add(menuItem);
			}
		}
	}

	/**
	 * @param iConfigurationElement
	 * @return
	 */
	private MMenuElement createMenuCommandAddition(final IConfigurationElement commandAddition) {
		final MHandledMenuItem item = MenuFactoryImpl.eINSTANCE.createHandledMenuItem();
		item.setElementId(MenuHelper.getId(commandAddition));
		final String commandId = MenuHelper.getCommandId(commandAddition);
		MCommand commandById = ContributionsAnalyzer.getCommandById(application, commandId);
		if (commandById == null) {
			commandById = CommandsFactoryImpl.eINSTANCE.createCommand();
			commandById.setElementId(commandId);
			commandById.setCommandName(commandId);
			application.getCommands().add(commandById);
		}
		item.setCommand(commandById);
		final Map parms = MenuHelper.getParameters(commandAddition);
		for (final Object obj : parms.entrySet()) {
			final Map.Entry e = (Map.Entry) obj;
			final MParameter parm = CommandsFactoryImpl.eINSTANCE.createParameter();
			parm.setName(e.getKey().toString());
			parm.setValue(e.getValue().toString());
			item.getParameters().add(parm);
		}
		final String iconUrl = MenuHelper.getIconURI(commandAddition, IWorkbenchRegistryConstants.ATT_ICON);

		if (iconUrl == null) {
			final ICommandImageService commandImageService = application.getContext().get(ICommandImageService.class);
			ImageDescriptor descriptor = commandImageService == null ? null : commandImageService.getImageDescriptor(commandId);
			if (descriptor == null) {
				descriptor = commandImageService == null ? null : commandImageService.getImageDescriptor(item.getElementId());
			}
			if (descriptor != null) {
				item.setIconURI(MenuHelper.getImageUrl(descriptor));
			}
		} else {
			item.setIconURI(iconUrl);
		}
		item.setLabel(MenuHelper.getLabel(commandAddition));
		item.setMnemonics(MenuHelper.getMnemonic(commandAddition));
		item.setTooltip(MenuHelper.getTooltip(commandAddition));
		item.setType(MenuHelper.getStyle(commandAddition));
		item.setVisibleWhen(MenuHelper.getVisibleWhen(commandAddition));
		createIdentifierTracker(item);
		return item;
	}

	private class IdListener implements IIdentifierListener {
		public void identifierChanged(final IdentifierEvent identifierEvent) {
			application.getContext().set(identifierEvent.getIdentifier().getId(), identifierEvent.getIdentifier().isEnabled());
		}
	}

	private final IdListener idUpdater = new IdListener();

	private void createIdentifierTracker(final MApplicationElement item) {
		if (item.getElementId() != null && item.getElementId().length() > 0) {
			final String id = namespaceIdentifier + "/" + item.getElementId(); //$NON-NLS-1$
			item.getPersistedState().put(MenuManagerRenderer.VISIBILITY_IDENTIFIER, id);
			//			final IIdentifier identifier = activityManager.getIdentifier(id);
			//			if (identifier != null) {
			//				application.getContext().set(identifier.getId(), identifier.isEnabled());
			//				identifier.addIdentifierListener(idUpdater);
			//			}
		}
	}

	private MMenuElement createMenuSeparatorAddition(final IConfigurationElement sepAddition) {
		final String name = MenuHelper.getName(sepAddition);
		final MMenuElement element = MenuFactoryImpl.eINSTANCE.createMenuSeparator();
		element.setElementId(name);
		if (!MenuHelper.isSeparatorVisible(sepAddition)) {
			element.setVisible(false);
			element.getTags().add(MenuManagerRenderer.GROUP_MARKER);
		}
		return element;
	}

	private MMenu createMenuAddition(final IConfigurationElement menuAddition, final String filter) {
		// Is this for a menu or a ToolBar ? We can't create
		// a menu directly under a Toolbar; we have to add an
		// item of style 'pulldown'
		if (inToolbar()) {
			return null;
		}

		final MMenu menu = MenuHelper.createMenuAddition(menuAddition);
		menu.getTags().add(filter);
		// addMenuChildren(menu, menuAddition, filter);
		return menu;
	}

	private boolean isUndefined(final String query) {
		if (query == null || query.length() == 0) {
			return true;
		}

		final int index = query.indexOf('=');
		return index == -1 || query.substring(index + 1).equals("additions"); //$NON-NLS-1$
	}

	private void processTrimLocation(final MTrimContribution contribution) {
		String query = location.getQuery();
		if (TRIM_COMMAND2.equals(location.getPath())) {
			contribution.setParentId(MAIN_TOOLBAR);
			if (isUndefined(query)) {
				query = "endof"; //$NON-NLS-1$
			}
			contribution.setPositionInParent(query);
		} else {
			contribution.setParentId(location.getPath());
			if (query == null || query.length() == 0) {
				query = "after=additions"; //$NON-NLS-1$
			}
			contribution.setPositionInParent(query);
		}
	}

	private void processTrimChildren(final ArrayList<MTrimContribution> trimContributions, final ArrayList<MToolBarContribution> toolBarContributions,
			final IConfigurationElement element) {
		final IConfigurationElement[] toolbars = element.getChildren(IWorkbenchRegistryConstants.TAG_TOOLBAR);
		if (toolbars.length == 0) {
			return;
		}
		final MTrimContribution trimContribution = MenuFactoryImpl.eINSTANCE.createTrimContribution();
		final String idContrib = MenuHelper.getId(configElement);
		if (idContrib != null && idContrib.length() > 0) {
			trimContribution.setElementId(idContrib);
		}
		processTrimLocation(trimContribution);
		trimContribution.getTags().add("scheme:" + location.getScheme()); //$NON-NLS-1$
		for (final IConfigurationElement toolbar : toolbars) {
			final MToolBar item = MenuFactoryImpl.eINSTANCE.createToolBar();
			item.setElementId(MenuHelper.getId(toolbar));
			item.getTransientData().put("Name", MenuHelper.getLabel(toolbar)); //$NON-NLS-1$
			processToolbarChildren(toolBarContributions, toolbar, item.getElementId(), "after=additions"); //$NON-NLS-1$
			trimContribution.getChildren().add(item);
		}
		trimContributions.add(trimContribution);
	}

	private void processToolbarChildren(final ArrayList<MToolBarContribution> contributions, final IConfigurationElement toolbar, final String parentId,
			final String position) {
		final MToolBarContribution toolBarContribution = MenuFactoryImpl.eINSTANCE.createToolBarContribution();
		final String idContrib = MenuHelper.getId(toolbar);
		if (idContrib != null && idContrib.length() > 0) {
			toolBarContribution.setElementId(idContrib);
		}
		toolBarContribution.setParentId(parentId);
		toolBarContribution.setPositionInParent(position);
		toolBarContribution.getTags().add("scheme:" + location.getScheme()); //$NON-NLS-1$

		final IConfigurationElement[] items = toolbar.getChildren();
		for (final IConfigurationElement item : items) {
			final String itemType = item.getName();

			if (IWorkbenchRegistryConstants.TAG_COMMAND.equals(itemType)) {
				final MToolBarElement element = createToolBarCommandAddition(item);
				toolBarContribution.getChildren().add(element);

			} else if (IWorkbenchRegistryConstants.TAG_SEPARATOR.equals(itemType)) {
				final MToolBarElement element = createToolBarSeparatorAddition(item);
				toolBarContribution.getChildren().add(element);
			} else if (IWorkbenchRegistryConstants.TAG_CONTROL.equals(itemType)) {
				final MToolBarElement element = createToolControlAddition(item);
				toolBarContribution.getChildren().add(element);
			}
		}

		contributions.add(toolBarContribution);
	}

	private MToolBarElement createToolControlAddition(final IConfigurationElement element) {
		final String id = MenuHelper.getId(element);
		final MToolControl control = MenuFactoryImpl.eINSTANCE.createToolControl();
		control.setElementId(id);
		control.setContributionURI(CompatibilityWorkbenchWindowControlContribution.CONTROL_CONTRIBUTION_URI);
		ControlContributionRegistry.add(id, element);
		control.setVisibleWhen(MenuHelper.getVisibleWhen(element));
		createIdentifierTracker(control);
		return control;
	}

	private MToolBarElement createToolBarSeparatorAddition(final IConfigurationElement sepAddition) {
		final String name = MenuHelper.getName(sepAddition);
		final MToolBarElement element = MenuFactoryImpl.eINSTANCE.createToolBarSeparator();
		element.setElementId(name);
		if (!MenuHelper.isSeparatorVisible(sepAddition)) {
			element.setToBeRendered(false);
			element.setVisible(false);
			element.getTags().add(MenuManagerRenderer.GROUP_MARKER);
		}
		return element;
	}

	private MToolBarElement createToolBarCommandAddition(final IConfigurationElement commandAddition) {
		final MHandledToolItem item = MenuFactoryImpl.eINSTANCE.createHandledToolItem();
		item.setElementId(MenuHelper.getId(commandAddition));
		final String commandId = MenuHelper.getCommandId(commandAddition);
		MCommand commandById = ContributionsAnalyzer.getCommandById(application, commandId);
		if (commandById == null) {
			commandById = CommandsFactoryImpl.eINSTANCE.createCommand();
			commandById.setElementId(commandId);
			commandById.setCommandName(commandId);
			application.getCommands().add(commandById);
		}
		item.setCommand(commandById);
		final Map parms = MenuHelper.getParameters(commandAddition);
		for (final Object obj : parms.entrySet()) {
			final Map.Entry e = (Map.Entry) obj;
			final MParameter parm = CommandsFactoryImpl.eINSTANCE.createParameter();
			parm.setName(e.getKey().toString());
			parm.setValue(e.getValue().toString());
			item.getParameters().add(parm);
		}
		String iconUrl = MenuHelper.getIconURI(commandAddition, IWorkbenchRegistryConstants.ATT_ICON);

		if (iconUrl == null) {
			final ICommandImageService commandImageService = application.getContext().get(ICommandImageService.class);
			ImageDescriptor descriptor = commandImageService == null ? null : commandImageService.getImageDescriptor(commandId,
					ICommandImageService.IMAGE_STYLE_TOOLBAR);
			if (descriptor == null) {
				descriptor = commandImageService == null ? null : commandImageService.getImageDescriptor(item.getElementId(),
						ICommandImageService.IMAGE_STYLE_TOOLBAR);
				if (descriptor == null) {
					item.setLabel(MenuHelper.getLabel(commandAddition));
				} else {
					item.setIconURI(MenuHelper.getImageUrl(descriptor));
				}
			} else {
				item.setIconURI(MenuHelper.getImageUrl(descriptor));
			}
		} else {
			item.setIconURI(iconUrl);
		}

		iconUrl = MenuHelper.getIconURI(commandAddition, IWorkbenchRegistryConstants.ATT_DISABLEDICON);
		if (iconUrl == null) {
			final ICommandImageService commandImageService = application.getContext().get(ICommandImageService.class);
			if (commandImageService != null) {
				ImageDescriptor descriptor = commandImageService.getImageDescriptor(commandId, ICommandImageService.TYPE_DISABLED,
						ICommandImageService.IMAGE_STYLE_TOOLBAR);
				if (descriptor == null) {
					descriptor = commandImageService.getImageDescriptor(item.getElementId(), ICommandImageService.TYPE_DISABLED,
							ICommandImageService.IMAGE_STYLE_TOOLBAR);
				}
				if (descriptor != null) {
					iconUrl = MenuHelper.getImageUrl(descriptor);
				}
			}
		}
		if (iconUrl != null) {
			MenuHelper.setDisabledIconURI(item, iconUrl);
		}

		item.setTooltip(MenuHelper.getTooltip(commandAddition));
		item.setType(MenuHelper.getStyle(commandAddition));
		if (MenuHelper.hasPulldownStyle(commandAddition)) {
			final MMenu element = MenuFactoryImpl.eINSTANCE.createMenu();
			final String id = MenuHelper.getId(commandAddition);
			element.setElementId(id);
			item.setMenu(element);
		}
		item.setVisibleWhen(MenuHelper.getVisibleWhen(commandAddition));
		createIdentifierTracker(item);
		return item;
	}
}
