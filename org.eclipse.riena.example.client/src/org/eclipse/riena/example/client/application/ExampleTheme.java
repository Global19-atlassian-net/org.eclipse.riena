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
package org.eclipse.riena.example.client.application;

import java.util.Map;

import org.eclipse.riena.navigation.ui.swt.lnf.ColorLnfResource;
import org.eclipse.riena.navigation.ui.swt.lnf.FontLnfResource;
import org.eclipse.riena.navigation.ui.swt.lnf.ILnfKeyConstants;
import org.eclipse.riena.navigation.ui.swt.lnf.ILnfResource;
import org.eclipse.riena.navigation.ui.swt.lnf.ImageLnfResource;
import org.eclipse.riena.navigation.ui.swt.lnf.rienadefault.RienaDefaultTheme;
import org.eclipse.swt.SWT;

/**
 * Theme of the Look and Feel (Lnf) of the example application.
 */
public class ExampleTheme extends RienaDefaultTheme {

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.rienadefault.RienaDefaultTheme#addCustomColors(java.util.Map)
	 */
	public void addCustomColors(Map<String, ILnfResource> table) {
		super.addCustomColors(table);

		table
				.put(ILnfKeyConstants.EMBEDDED_TITLEBAR_ACTIVE_BACKGROUND_START_COLOR, new ColorLnfResource(234, 231,
						158));
		table.put(ILnfKeyConstants.EMBEDDED_TITLEBAR_ACTIVE_BACKGROUND_END_COLOR, new ColorLnfResource(225, 220, 114));
		table.put(ILnfKeyConstants.EMBEDDED_TITLEBAR_ACTIVE_BORDER_COLOR, new ColorLnfResource(171, 171, 174));
		table.put(ILnfKeyConstants.EMBEDDED_TITLEBAR_PASSIVE_BACKGROUND_START_COLOR,
				new ColorLnfResource(222, 224, 240));
		table.put(ILnfKeyConstants.EMBEDDED_TITLEBAR_PASSIVE_BACKGROUND_END_COLOR, new ColorLnfResource(186, 193, 225));
		table.put(ILnfKeyConstants.EMBEDDED_TITLEBAR_PASSIVE_BORDER_COLOR, new ColorLnfResource(151, 150, 180));

	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.rienadefault.RienaDefaultTheme#addCustomImages(java.util.Map)
	 */
	@Override
	public void addCustomImages(Map<String, ILnfResource> table) {
		super.addCustomImages(table);
		String imagePath = "org.eclipse.riena.example.client" + ":" + "/icons/ledred.png"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		table.put("treeDocumentLeaf.icon", new ImageLnfResource(imagePath)); //$NON-NLS-1$
		imagePath = "org.eclipse.riena.example.client" + ":" + "/icons/folder_favorite.png"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		table.put(ILnfKeyConstants.TREE_FOLDER_CLOSED_ICON, new ImageLnfResource(imagePath));
		table.put(ILnfKeyConstants.TREE_FOLDER_OPEN_ICON, new ImageLnfResource(imagePath));
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.ILnfTheme#addCustomSettings(java.util.Map)
	 */
	public void addCustomSettings(Map<String, Object> table) {
		table.put(ILnfKeyConstants.SUB_APPLICATION_SWITCHER_HORIZONTAL_TAB_POSITION, SWT.LEFT);
		table.put(ILnfKeyConstants.SUB_APPLICATION_SWITCHER_TAB_SHOW_ICON, true);
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.rienadefault.RienaDefaultTheme#getPrimaryForeground()
	 */
	@Override
	protected ColorLnfResource getPrimaryForeground() {
		return new ColorLnfResource(0, 0, 0);
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.rienadefault.RienaDefaultTheme#getPrimaryBackground()
	 */
	@Override
	protected ColorLnfResource getPrimaryBackground() {
		return new ColorLnfResource(255, 255, 215);
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.rienadefault.RienaDefaultTheme#getPrimaryFont()
	 */
	@Override
	protected FontLnfResource getPrimaryFont() {
		return new FontLnfResource("Arial", 11, SWT.BOLD); //$NON-NLS-1$
	}
}
