/*******************************************************************************
 * Copyright (c) 2007, 2014 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.example.client.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.riena.navigation.ui.swt.views.SubModuleView;
import org.eclipse.riena.ui.swt.utils.UIControlsFactory;

/**
 *
 */
public class PartnerSubModuleView extends SubModuleView {

	@Override
	protected void basicCreatePartControl(final Composite parent) {
		parent.setLayout(new RowLayout(SWT.HORIZONTAL));
		UIControlsFactory.createText(parent, SWT.BORDER, "customerNr"); //$NON-NLS-1$
		UIControlsFactory.createButton(parent, "Set", "set"); //$NON-NLS-1$//$NON-NLS-2$
	}
}
