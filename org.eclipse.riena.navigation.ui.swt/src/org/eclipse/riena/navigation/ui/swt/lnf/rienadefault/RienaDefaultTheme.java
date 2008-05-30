/*******************************************************************************
 * Copyright (c) 2007 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.navigation.ui.swt.lnf.rienadefault;

import java.util.Map;

import org.eclipse.riena.navigation.ui.swt.lnf.ColorLnfResource;
import org.eclipse.riena.navigation.ui.swt.lnf.FontLnfResource;
import org.eclipse.riena.navigation.ui.swt.lnf.ILnfResource;
import org.eclipse.riena.navigation.ui.swt.lnf.ILnfTheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * 
 */
public class RienaDefaultTheme implements ILnfTheme {

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.ILnfTheme#addCustomColors(java.util.Map)
	 */
	public void addCustomColors(Map<String, ILnfResource> table) {
		table.put("EmbeddedTitlebar.foreground", new ColorLnfResource(68, 70, 74));
		table.put("EmbeddedTitlebar.activeBackgroundStartColor", new ColorLnfResource(196, 225, 244));
		table.put("EmbeddedTitlebar.activeBackgroundEndColor", new ColorLnfResource(100, 153, 186));
		table.put("EmbeddedTitlebar.activeBorderColor", new ColorLnfResource(171, 171, 174));
		table.put("EmbeddedTitlebar.passiveBackgroundStartColor", new ColorLnfResource(244, 244, 245));
		table.put("EmbeddedTitlebar.passiveBackgroundEndColor", new ColorLnfResource(220, 220, 220));
		table.put("EmbeddedTitlebar.passiveBorderColor", new ColorLnfResource(213, 213, 216));
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.ILnfTheme#addCustomFonts(java.util.Map)
	 */
	public void addCustomFonts(Map<String, ILnfResource> table) {
		table.put("EmbeddedTitlebar.font", new FontLnfResource(getSystemFont().getName(), 10, SWT.NORMAL));
	}

	private Color getSystemColor(int id) {
		return Display.getCurrent().getSystemColor(id);
	}

	private FontData getSystemFont() {
		FontData[] data = Display.getCurrent().getSystemFont().getFontData();
		if (data.length > 0) {
			return data[0];
		}
		return new FontData("Arial", 10, SWT.NORMAL);
	}

}
