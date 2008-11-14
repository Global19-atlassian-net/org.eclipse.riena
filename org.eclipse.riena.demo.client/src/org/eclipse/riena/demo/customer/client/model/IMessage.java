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
package org.eclipse.riena.demo.customer.client.model;

/**
 * Interface of a message
 * 
 */
public interface IMessage {
	/**
	 * Gibt den Typ der Meldung zur�ck.
	 * 
	 * @return MessageType
	 */
	MessageType getTyp();

	/**
	 * Gibt die Nummer der Meldung zur�ck.
	 * 
	 * @return String
	 */
	String getMex();

	/**
	 * Gibt den Meldungstext zur�ck.
	 * 
	 * @return String
	 */
	String getMeldungsText();

	/**
	 * Gibt den Infotext zur�ck.
	 * 
	 * @return String
	 */
	String getInfoText();

	/**
	 * Gibt den Hinweistext zur�ck.
	 * 
	 * @return String
	 */
	String getHinweis();

	/**
	 * Gibt den Text f�r die Ansprache zur�ck.
	 * 
	 * @return String
	 */
	String getAnsprache();

	/**
	 * Gibt den Text f�r die Statuszeile zur�ck.
	 * 
	 * @return String
	 */
	String getStatusZeile();

	/**
	 * Gibt die Texte f�r die Schaltfl�chen zur�ck.
	 * 
	 * @return String
	 */
	String[] getButtons();

	/**
	 * Setzt die Texte f�r die Schaltfl�chen.
	 * 
	 * @param buttons
	 */
	void setButtons(String[] buttons);
}
