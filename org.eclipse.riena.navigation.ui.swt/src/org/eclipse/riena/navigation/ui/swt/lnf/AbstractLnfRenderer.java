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
package org.eclipse.riena.navigation.ui.swt.lnf;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Renderer of a widget or a part of a widget.
 */
public abstract class AbstractLnfRenderer implements ILnfRenderer {

	private Rectangle bounds;

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.ILnfRenderer#paint(org.eclipse.swt.graphics.GC,
	 *      java.lang.Object)
	 */
	public abstract void paint(GC gc, Object value);

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.ILnfRenderer#getBounds()
	 */
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.ILnfRenderer#setBounds(int,
	 *      int, int, int)
	 */
	public void setBounds(int x, int y, int width, int height) {
		setBounds(new Rectangle(x, y, width, height));

	}

	/**
	 * @see org.eclipse.riena.navigation.ui.swt.lnf.ILnfRenderer#setBounds(org.eclipse.swt.graphics.Rectangle)
	 */
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

}
