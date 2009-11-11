/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *    Florian Pirchner - FontDescriptor
 *******************************************************************************/
package org.eclipse.riena.ui.swt.lnf;

import org.eclipse.riena.ui.core.marker.AttentionMarker;
import org.eclipse.riena.ui.core.marker.ErrorMarker;
import org.eclipse.riena.ui.core.marker.MandatoryMarker;
import org.eclipse.riena.ui.core.marker.UIProcessFinishedMarker;

/**
 * Keys of the look and feel of riena.
 * <p>
 * Some keys are in a special Wiki page of Eclipsepedia: <a
 * href="http://wiki.eclipse.org/Riena_Look_and_Feel">Riena Look and Feel</a>
 * 
 * 
 */
public final class LnfKeyConstants {

	/**
	 * Color keys
	 */
	public static final String TITLELESS_SHELL_FOREGROUND = "TitlelessShell.foreground"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_PASSIVE_FOREGROUND = "TitlelessShell.passiveForeground"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_BACKGROUND = "TitlelessShell.background"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_BORDER_BOTTOM_RIGHT_COLOR = "TitlelessShell.bottomRightColor"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_BORDER_TOP_LEFT_COLOR = "TitlelessShell.borderTopLeftColor"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_INNER_BORDER_BOTTOM_RIGHT_COLOR = "TitlelessShell.innerBorderBottomRightColor"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_INNER_BORDER_TOP_LEFT_COLOR = "TitlelessShell.innerBorderTopLeftColor"; //$NON-NLS-1$

	public static final String COOLBAR_BACKGROUND = "Coolbar.background"; //$NON-NLS-1$

	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_FOREGROUND = "Dialog.foreground"; //$NON-NLS-1$
	public static final String DIALOG_PASSIVE_FOREGROUND = "Dialog.passiveForeground"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_BORDER_BOTTOM_RIGHT_COLOR = "Dialog.bottomRightColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_BORDER_TOP_LEFT_COLOR = "Dialog.borderTopLeftColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_INNER_BORDER_BOTTOM_RIGHT_COLOR = "Dialog.innerBorderBottomRightColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_INNER_BORDER_TOP_LEFT_COLOR = "Dialog.innerBorderTopLeftColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_TITLEBAR_BACKGROUND_START_COLOR = "DialogTitleBar.backgroundStartColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_TITLEBAR_BACKGROUND_END_COLOR = "DialogTitleBar.backgroundEndColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_TITLEBAR_BACKGROUND_TOP_COLOR_1 = "DialogTitleBar.backgroundTopColor1"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_TITLEBAR_BACKGROUND_TOP_COLOR_2 = "DialogTitleBar.backgroundTopColor2"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_TITLEBAR_BACKGROUND_TOP_COLOR_3 = "DialogTitleBar.backgroundTopColor3"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_TITLEBAR_BACKGROUND_BOTTOM_COLOR_1 = "DialogTitleBar.backgroundBottomColor1"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_TITLEBAR_BACKGROUND_BOTTOM_COLOR_2 = "DialogTitleBar.backgroundBottomColor2"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_TITLEBAR_BACKGROUND_BOTTOM_COLOR_3 = "DialogTitleBar.backgroundBottomColor3"; //$NON-NLS-1$

	/**
	 * @since 1.2
	 */
	public static final String SUB_APPLICATION_SWITCHER_ACTIVE_FOREGROUND = "SubApplicationSwitcher.foreground"; //$NON-NLS-1$
	/**
	 * @since 1.2
	 */
	public static final String SUB_APPLICATION_SWITCHER_PASSIVE_FOREGROUND = "SubApplicationSwitcher.passiveForeground"; //$NON-NLS-1$
	@Deprecated
	public static final String SUB_APPLICATION_SWITCHER_FOREGROUND = SUB_APPLICATION_SWITCHER_ACTIVE_FOREGROUND;
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_DISABLED_FOREGROUND = "SubApplicationSwitcher.disabledForeground"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_BORDER_BOTTOM_LEFT_COLOR = "SubApplicationSwitcher.borderBottomLeftColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_BORDER_TOP_RIGHT_COLOR = "SubApplicationSwitcher.borderTopRightColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_INNER_BORDER_COLOR = "SubApplicationSwitcher.innerBorderColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_ACTIVE_BACKGROUND_END_COLOR = "SubApplicationSwitcher.activeBackgroundEndColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_ACTIVE_BACKGROUND_START_COLOR = "SubApplicationSwitcher.activeBackgroundStartColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_PASSIVE_BACKGROUND_END_COLOR = "SubApplicationSwitcher.passiveBackgroundEndColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_PASSIVE_BACKGROUND_START_COLOR = "SubApplicationSwitcher.passiveBackgroundStartColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_PROCESS_FINISHED_BACKGROUND_END_COLOR = "SubApplicationSwitcher.processFinishedBackgroundEndColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_PROCESS_FINISHED_BACKGROUND_START_COLOR = "SubApplicationSwitcher.processFinishedBackgroundStartColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_TOP_SELECTION_COLOR = "SubApplicationSwitcher.topSelectionColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_DISABLED_BORDER_BOTTOM_LEFT_COLOR = "SubApplicationSwitcher.borderBottomLeftColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_DISABLED_BORDER_TOP_RIGHT_COLOR = "SubApplicationSwitcher.borderTopRightColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_INNER_DISABLED_BORDER_COLOR = "SubApplicationSwitcher.innerDisabledBorderColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_INNER_PROCESS_FINISHED_BORDER_COLOR = "SubApplicationSwitcher.innerProcessFinishedBorderColor"; //$NON-NLS-1$

	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_ACTIVE_FOREGROUND = "EmbeddedTitlebar.foreground"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_PASSIVE_FOREGROUND = "EmbeddedTitlebar.passiveForeground"; //$NON-NLS-1$
	@Deprecated
	public static final String EMBEDDED_TITLEBAR_FOREGROUND = EMBEDDED_TITLEBAR_ACTIVE_FOREGROUND;
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_DISABLED_FOREGROUND = "EmbeddedTitlebar.disabledForeground"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_DISABLED_BORDER_COLOR = "EmbeddedTitlebar.disabledBorderColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_ACTIVE_BORDER_COLOR = "EmbeddedTitlebar.activeBorderColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_ACTIVE_BACKGROUND_END_COLOR = "EmbeddedTitlebar.activeBackgroundEndColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_ACTIVE_BACKGROUND_START_COLOR = "EmbeddedTitlebar.activeBackgroundStartColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_PASSIVE_BORDER_COLOR = "EmbeddedTitlebar.passiveBorderColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_PASSIVE_BACKGROUND_END_COLOR = "EmbeddedTitlebar.passiveBackgroundEndColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_PASSIVE_BACKGROUND_START_COLOR = "EmbeddedTitlebar.passiveBackgroundStartColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_HOVER_BORDER_END_COLOR = "EmbeddedTitlebar.hoverBorderEndColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_HOVER_BORDER_START_COLOR = "EmbeddedTitlebar.hoverBorderStartColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_HOVER_BORDER_BOTTOM_COLOR = "EmbeddedTitlebar.hoverBorderBottomColor"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_HOVER_BORDER_TOP_COLOR = "EmbeddedTitlebar.hoverBorderTopColor"; //$NON-NLS-1$

	public static final String NAVIGATION_BACKGROUND = "Navigation.background"; //$NON-NLS-1$
	public static final String MODULE_GROUP_WIDGET_BACKGROUND = "ModuleGroupWidget.background"; //$NON-NLS-1$
	public static final String SUB_MODULE_TREE_BACKGROUND = "SubModuleTree.background"; //$NON-NLS-1$
	public static final String SUB_MODULE_BACKGROUND = "SubModule.background"; //$NON-NLS-1$

	public static final String SUB_MODULE_ITEM_TOOLTIP_BACKGROUND = "SubModuleItemToolTip.background"; //$NON-NLS-1$
	public static final String SUB_MODULE_ITEM_TOOLTIP_FOREGROUND = "SubModuleItemToolTip.foreground"; //$NON-NLS-1$

	public static final String MODULE_ITEM_TOOLTIP_BACKGROUND = "ModuleItemToolTip.background"; //$NON-NLS-1$
	public static final String MODULE_ITEM_TOOLTIP_FOREGROUND = "ModuleItemToolTip.foreground"; //$NON-NLS-1$

	public static final String STATUSLINE_BACKGROUND = "statusline.background"; //$NON-NLS-1$
	public static final String STATUSLINE_UI_PROCESS_LIST_BACKGROUND = "statusline.uiProcessListBackground"; //$NON-NLS-1$

	public static final String GRAB_CORNER_BACKGROUND = "grabCorner.background"; //$NON-NLS-1$
	/**
	 * @since 1.2
	 */
	public static final String MANDATORY_MARKER_BACKGROUND = "mandatoryMarker.background"; //$NON-NLS-1$

	/**
	 * @since 1.2
	 */
	public static final String OUTPUT_MARKER_BACKGROUND = "outputMarker.background"; //$NON-NLS-1$

	/**
	 * Font keys
	 */
	public static final String TITLELESS_SHELL_FONT = "TitlelessShell.font"; //$NON-NLS-1$

	public static final String DIALOG_FONT = "Dialog.font"; //$NON-NLS-1$

	public static final String SUB_APPLICATION_SWITCHER_FONT = "SubApplicationSwitcher.font"; //$NON-NLS-1$

	public static final String MODULE_ITEM_TOOLTIP_FONT = "ModuleItemToolTip.font"; //$NON-NLS-1$

	public static final String SUB_MODULE_ITEM_FONT = "SubModuleItem.font"; //$NON-NLS-1$
	public static final String SUB_MODULE_ITEM_TOOLTIP_FONT = "SubModuleItemToolTip.font"; //$NON-NLS-1$

	public static final String EMBEDDED_TITLEBAR_FONT = "EmbeddedTitlebar.font"; //$NON-NLS-1$

	/**
	 * Icon keys
	 */
	public static final String TITLELESS_SHELL_BACKGROUND_IMAGE = "TitlelessShell.backgroundImage"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_LOGO = "TitlelessShell.logo"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_CLOSE_ICON = "TitlelessShell.closeIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_CLOSE_HOVER_ICON = "TitlelessShell.closeHoverIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_CLOSE_HOVER_SELECTED_ICON = "TitlelessShell.closeHoverSelectedIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_CLOSE_INACTIVE_ICON = "TitlelessShell.closeInactiveIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_MAX_ICON = "TitlelessShell.maxIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_MAX_HOVER_ICON = "TitlelessShell.maxHoverIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_MAX_HOVER_SELECTED_ICON = "TitlelessShell.maxHoverSelectedIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_MAX_INACTIVE_ICON = "TitlelessShell.maxInactiveIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_MIN_ICON = "TitlelessShell.minIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_MIN_HOVER_ICON = "TitlelessShell.minHoverIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_MIN_HOVER_SELECTED_ICON = "TitlelessShell.minHoverSelectedIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_MIN_INACTIVE_ICON = "TitlelessShell.minInactiveIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_RESTORE_ICON = "TitlelessShell.restoreIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_RESTORE_HOVER_ICON = "TitlelessShell.restoreHoverIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_RESTORE_HOVER_SELECTED_ICON = "TitlelessShell.restoreHoverSelectedIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_RESTORE_INACTIVE_ICON = "TitlelessShell.restoreInactiveIcon"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_GRAB_IMAGE = "TitlelessShell.grabImage"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_HAND_IMAGE = "TitlelessShell.handImage"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_GRAB_CORNER_IMAGE = "TitlelessShell.grabCornerImage"; //$NON-NLS-1$

	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_CLOSE_ICON = "Dialog.closeIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_CLOSE_HOVER_ICON = "Dialog.closeHoverIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_CLOSE_HOVER_SELECTED_ICON = "Dialog.closeHoverSelectedIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_CLOSE_INACTIVE_ICON = "Dialog.closeInactiveIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_MAX_ICON = "Dialog.maxIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_MAX_HOVER_ICON = "Dialog.maxHoverIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_MAX_HOVER_SELECTED_ICON = "Dialog.maxHoverSelectedIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_MAX_INACTIVE_ICON = "Dialog.maxInactiveIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_MIN_ICON = "Dialog.minIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_MIN_HOVER_ICON = "Dialog.minHoverIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_MIN_HOVER_SELECTED_ICON = "Dialog.minHoverSelectedIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_MIN_INACTIVE_ICON = "Dialog.minInactiveIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_RESTORE_ICON = "Dialog.restoreIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_RESTORE_HOVER_ICON = "Dialog.restoreHoverIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_RESTORE_HOVER_SELECTED_ICON = "Dialog.restoreHoverSelectedIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_RESTORE_INACTIVE_ICON = "Dialog.restoreInactiveIcon"; //$NON-NLS-1$

	@Deprecated
	public static final String DEFAULT_NODE_ICON = "defaultNode.icon"; //$NON-NLS-1$

	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Module_Tree">
	 *      Riena Wiki</a>
	 */
	public static final String SUB_MODULE_TREE_FOLDER_OPEN_ICON = "subModuleTreeFolderOpen.icon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Module_Tree">
	 *      Riena Wiki</a>
	 */
	public static final String SUB_MODULE_TREE_FOLDER_CLOSED_ICON = "subModuleTreeFolderClosed.icon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Module_Tree">
	 *      Riena Wiki</a>
	 */
	public static final String SUB_MODULE_TREE_DOCUMENT_LEAF_ICON = "subModuleTreeDocumentLeaf.icon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Module_Tree">
	 *      Riena Wiki</a>
	 * 
	 * @since 1.2
	 */
	public static final String SUB_MODULE_TREE_ERROR_MARKER_ICON = "subModuleTreeErrorMarker.icon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Module_Tree">
	 *      Riena Wiki</a>
	 * 
	 * @since 1.2
	 */
	public static final String SUB_MODULE_TREE_MANDATORY_MARKER_ICON = MandatoryMarker.MARKER_KEY;
	/**
	 * @since 1.2
	 */
	public static final String SUB_MODULE_TREE_ATTENTION_MARKER_ICON = AttentionMarker.MARKER_KEY;
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Module_Tree">
	 *      Riena Wiki</a>
	 * 
	 * @since 1.2
	 */
	public static final String SUB_MODULE_TREE_PROCESSED_FINISHED_MARKER_ICON = UIProcessFinishedMarker.MARKER_KEY;

	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_CLOSE_ICON = "EmbeddedTitlebar.closeIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_CLOSE_HOVER_ICON = "EmbeddedTitlebar.closeHoverIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_CLOSE_HOVER_SELECTED_ICON = "EmbeddedTitlebar.closeHoverSelectedIcon"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a
	 *      href="http://wiki.eclipse.org/Riena_Look_and_Feel#Embedded_Title_Bar">
	 *      Riena Wiki</a>
	 */
	public static final String EMBEDDED_TITLEBAR_CLOSE_INACTIVE_ICON = "EmbeddedTitlebar.closeInactiveIcon"; //$NON-NLS-1$

	public static final String STATUSLINE_SPACER_ICON = "statusline.spacerIcon"; //$NON-NLS-1$
	public static final String STATUSLINE_ERROR_ICON = "statusline.errorIcon"; //$NON-NLS-1$
	public static final String STATUSLINE_INFO_ICON = "statusline.infoIcon"; //$NON-NLS-1$
	public static final String STATUSLINE_WARNING_ICON = "statusline.warningIcon"; //$NON-NLS-1$

	public static final String NAVIGATION_SCROLL_UP_ICON = "navigation.scrollup"; //$NON-NLS-1$
	public static final String NAVIGATION_SCROLL_DOWN_ICON = "navigation.scrollDown"; //$NON-NLS-1$

	public static final String ERROR_MARKER_ICON = ErrorMarker.MARKER_KEY;

	/**
	 * Setting keys
	 */

	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Shell"> Riena
	 *      Wiki</a>
	 */
	public static final String SHELL_HIDE_OS_BORDER = "Shell.hideOsBorder"; //$NON-NLS-1$

	public static final String TITLELESS_SHELL_PADDING = "TitlelessShell.padding"; //$NON-NLS-1$
	/**
	 * @since 1.2
	 */
	public static final String TITLELESS_SHELL_NAVIGATION_HORIZONTAL_GAP = "TitlelessShellNavigationHorizontal.gap"; //$NON-NLS-1$
	/**
	 * @since 1.2
	 */
	public static final String TITLELESS_SHELL_SUB_MODULE_HORIZONTAL_GAP = "TitlelessShellSubModuleHorizontal.gap"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_HORIZONTAL_LOGO_POSITION = "TitlelessShell.horizontalLogoPosition"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_VERTICAL_LOGO_POSITION = "TitlelessShell.verticalLogoPosition"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_HORIZONTAL_LOGO_MARGIN = "TitlelessShell.horizontalLogoMargin"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_VERTICAL_LOGO_MARGIN = "TitlelessShell.verticalLogoMargin"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_HORIZONTAL_TEXT_POSITION = "TitlelessShell.horizontalTextPosition"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_SHOW_MAX = "TitlelessShell.showMax"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_SHOW_MIN = "TitlelessShell.showMin"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_SHOW_CLOSE = "TitlelessShell.showClose"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_RESIZEABLE = "TitlelessShell.resizeable"; //$NON-NLS-1$

	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href="http://wiki.eclipse.org/Riena_Look_and_Feel#Dialog">Riena
	 *      Wiki</a>
	 */
	public static final String DIALOG_HIDE_OS_BORDER = "Dialog.hideOsBorder"; //$NON-NLS-1$

	public static final String SUB_APPLICATION_SWITCHER_TOP_MARGIN = "SubApplicationSwitcher.topMargin"; //$NON-NLS-1$
	public static final String SUB_APPLICATION_SWITCHER_HEIGHT = "SubApplicationSwitcher.height"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_HORIZONTAL_TAB_POSITION = "SubApplicationSwitcher.horizontalTabPosition"; //$NON-NLS-1$
	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Sub-Application_Switcher"
	 *      >Riena Wiki</a>
	 */
	public static final String SUB_APPLICATION_SWITCHER_TAB_SHOW_ICON = "SubApplicationSwitcher.tabShowIcon"; //$NON-NLS-1$
	/**
	 * @since 1.2
	 */
	public static final String SUB_APPLICATION_SWITCHER_TAB_MIN_WIDTH = "SubApplicationSwitcher.tabMinWidth"; //$NON-NLS-1$

	public static final String SUB_MODULE_ITEM_TOOLTIP_POPUP_DELAY = "SubModuleItemToolTip.popupDelay"; //$NON-NLS-1$

	/**
	 * This constant is described in Eclipsepedia.
	 * 
	 * @see <a href=
	 *      "http://wiki.eclipse.org/Riena_Look_and_Feel#Navigation">Riena
	 *      Wiki</a>
	 * 
	 * @since 1.2
	 */
	public static final String NAVIGATION_WIDTH = "navigation.width"; //$NON-NLS-1$

	/**
	 * @since 1.2
	 */
	public static final String TOOLBAR_WORK_AREA_VERTICAL_GAP = "toolbar.workarea.vertical.gap"; //$NON-NLS-1$

	/**
	 * @since 1.2
	 */
	public static final String STATUSLINE_HEIGHT = "statusline.height"; //$NON-NLS-1$

	public static final String MODULE_ITEM_TOOLTIP_POPUP_DELAY = "ModuleItemToolTip.popupDelay"; //$NON-NLS-1$

	/**
	 * @since 1.2
	 */
	public static final String NAVIGATION_SUB_MODULE_GAP = "NavigationSubModule.gap"; //$NON-NLS-1$
	/**
	 * @since 1.2
	 */
	public static final String MENUBAR_TOP_MARGIN = "Menubar.topMargin"; //$NON-NLS-1$
	/**
	 * @since 1.2
	 */
	public static final String TOOLBAR_TOP_MARGIN = "Toolbar.topMargin"; //$NON-NLS-1$

	/**
	 * @since 1.2
	 */
	public static final String ERROR_MARKER_HORIZONTAL_POSITION = "errorMarker.horizontalPosition"; //$NON-NLS-1$
	/**
	 * @since 1.2
	 */
	public static final String ERROR_MARKER_VERTICAL_POSITION = "errorMarker.verticalPosition"; //$NON-NLS-1$
	/**
	 * @since 1.2
	 */
	public static final String ERROR_MARKER_MARGIN = "errorMarker.margin"; //$NON-NLS-1$

	/**
	 * @since 1.2
	 */
	public static final String FONTDESCRIPTOR_DEFAULT_HEIGHT = "FontDescriptor.defaultHeight"; //$NON-NLS-1$

	/**
	 * Renderer keys
	 */
	public static final String TITLELESS_SHELL_RENDERER = "TitlelessShell.renderer"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_BORDER_RENDERER = "TitlelessShell.borderRenderer"; //$NON-NLS-1$
	public static final String TITLELESS_SHELL_LOGO_RENDERER = "TitlelessShell.logoRenderer"; //$NON-NLS-1$

	public static final String DIALOG_RENDERER = "Dialog.renderer"; //$NON-NLS-1$
	public static final String DIALOG_BORDER_RENDERER = "Dialog.borderRenderer"; //$NON-NLS-1$

	public static final String SUB_APPLICATION_TAB_RENDERER = "SubApplication.tabRenderer"; //$NON-NLS-1$
	public static final String SUB_APPLICATION_SWITCHER_RENDERER = "SubApplication.switcherRenderer"; //$NON-NLS-1$

	public static final String MODULE_GROUP_RENDERER = "ModuleGroup.renderer"; //$NON-NLS-1$

	public static final String SUB_MODULE_TREE_ITEM_MARKER_RENDERER = "SubModuleTreeItemMarker.renderer"; //$NON-NLS-1$

	public static final String SUB_MODULE_VIEW_RENDERER = "SubModuleView.renderer"; //$NON-NLS-1$
	public static final String SUB_MODULE_VIEW_HOVER_BORDER_RENDERER = "SubModuleView.hoverBorderRenderer"; //$NON-NLS-1$
	public static final String SUB_MODULE_VIEW_TITLEBAR_RENDERER = "SubModuleView.titlebarRenderer"; //$NON-NLS-1$
	public static final String SUB_MODULE_VIEW_BORDER_RENDERER = "SubModuleView.borderRenderer"; //$NON-NLS-1$

	/**
	 * @since 1.2
	 */
	public static final String SUB_MODULE_VIEW_SEPARATOR_LINE_RENDERER = "SubModuleView.separatorLineRenderer"; //$NON-NLS-1$

	private LnfKeyConstants() {
		super();
	}

}
