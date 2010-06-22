/*******************************************************************************
 * Copyright (c) 2007, 2010 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.internal.core.ignore;

/**
 * Provide a simple solution for documenting empty code blocks, e.g. empty
 * try-catch clauses.
 * 
 * @deprecated Use instead {@code org.eclipse.riena.core.util.Nop}
 */
@Deprecated
public final class Nop {

	private Nop() {
		// utility
	}

	/**
	 * Reason for the nop.
	 * 
	 * @param reason
	 * @deprecated Use instead {@code org.eclipse.riena.core.util.Nop}
	 */
	@Deprecated
	public static void reason(final String reason) {
		// nothing
	}
}
