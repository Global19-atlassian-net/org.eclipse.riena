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
package org.eclipse.riena.navigation.ui.swt.presentation.stack;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.presentations.AbstractPresentationFactory;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;

public class TitlelessStackPresentationFactory extends AbstractPresentationFactory {

	private Map<IStackPresentationSite, StackPresentation> presentations;

	public TitlelessStackPresentationFactory() {
		presentations = new HashMap<IStackPresentationSite, StackPresentation>();
	}

	/**
	 * @see org.eclipse.ui.presentations.AbstractPresentationFactory#createEditorPresentation(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.presentations.IStackPresentationSite)
	 */
	@Override
	public StackPresentation createEditorPresentation(Composite parent, IStackPresentationSite site) {
		return getPresentation(parent, site);
	}

	/**
	 * @see org.eclipse.ui.presentations.AbstractPresentationFactory#createStandaloneViewPresentation(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.presentations.IStackPresentationSite, boolean)
	 */
	@Override
	public StackPresentation createStandaloneViewPresentation(Composite parent, IStackPresentationSite site,
			boolean showTitle) {
		return null;
	}

	/**
	 * @see org.eclipse.ui.presentations.AbstractPresentationFactory#createViewPresentation(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.ui.presentations.IStackPresentationSite)
	 */
	@Override
	public StackPresentation createViewPresentation(Composite parent, IStackPresentationSite site) {
		return getPresentation(parent, site);
	}

	private StackPresentation getPresentation(Composite parent, IStackPresentationSite site) {
		if (presentations.get(site) == null) {
			presentations.put(site, new TitlelessStackPresentation(parent, site));
		}
		return getPresentation(site);
	}

	public StackPresentation getPresentation(IStackPresentationSite site) {
		return presentations.get(site);
	}

}
