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
package org.eclipse.riena.objecttransaction.simple;

import org.eclipse.core.runtime.AssertionFailedException;

import org.eclipse.riena.objecttransaction.IObjectTransaction;
import org.eclipse.riena.objecttransaction.IObjectTransactionExtract;
import org.eclipse.riena.objecttransaction.InvalidTransactionFailure;
import org.eclipse.riena.objecttransaction.ObjectTransactionFactory;
import org.eclipse.riena.objecttransaction.delta.TransactionDelta;
import org.eclipse.riena.objecttransaction.simple.value.Addresse;
import org.eclipse.riena.objecttransaction.simple.value.Kunde;
import org.eclipse.riena.objecttransaction.simple.value.Vertrag;
import org.eclipse.riena.objecttransaction.state.State;
import org.eclipse.riena.tests.RienaTestCase;
import org.eclipse.riena.tests.collect.NonUITestCase;

/**
 * TODO Fehlender Klassen-Kommentar
 * 
 * @author Christian Campo
 */
@NonUITestCase
public class ObjectTransactionVariousSimpleTest extends RienaTestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		loader.setDefaultAssertionStatus(true);
	}

	/**
	 * @throws Exception
	 */
	public void testNullValueAndCommitToObjects() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		objectTransaction.setCleanModus(false);
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		assertEquals("john", kunde.getVorname());
		// objectTransaction.setCleanModus( false );
		assertEquals("john", kunde.getVorname());
		kunde.setVorname(null);
		// System.out.println( kunde.getVorname() );
		assertNull(kunde.getVorname());
		objectTransaction.commitToObjects();
		assertNull(kunde.getVorname());
	}

	/**
	 * @throws Exception
	 */
	public void testRegisterAsDeletedWithCommitToObjects() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		objectTransaction.setCleanModus(false);

		objectTransaction.registerAsDeleted(kunde);

		objectTransaction.commitToObjects();

		assertFalse("kunde must not be registered", objectTransaction.isRegistered(kunde));
	}

	/**
	 * @throws Exception
	 */
	public void testExtractIsImmutable() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");

		objectTransaction.setCleanModus(false);

		kunde.setVorname("john");
		kunde.setNachname("Miller");

		IObjectTransactionExtract extract = objectTransaction.exportExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertTrue("must only be changes for one object ", deltas.length == 1);

		assertTrue("must have only 2 changes ", deltas[0].getChanges().size() == 2);

		kunde.setAddresse(new Addresse(true));

		assertTrue("extract changed:: must only be changes for one object ", deltas.length == 1);

		assertTrue("extract changed:: must have only 2 changes ", deltas[0].getChanges().size() == 2);

	}

	/**
	 * @throws Exception
	 */
	public void testExportModified1() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		Addresse adresse = new Addresse(false);
		kunde.setAddresse(adresse);

		objectTransaction.setCleanModus(false);
		adresse.setOrt("frankfurt");
		adresse.setStrasse("gutleutstrasse");
		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertTrue("should be only one transaction delta", deltas.length == 1);
		assertTrue("single delta should reference adresse", deltas[0].getObjectId() == adresse.getObjectId());
		assertTrue("delta status must be modified", deltas[0].getState() == State.MODIFIED);
	}

	/**
	 * @throws Exception
	 */
	public void testExportModified2() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		Addresse adresse = new Addresse(false);
		objectTransaction.setCleanModus(false);

		kunde.setAddresse(adresse);

		adresse.setOrt("frankfurt");
		adresse.setStrasse("gutleutstrasse");
		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertTrue("should be only two transaction delta", deltas.length == 2);
		// sequence of objects in delta[0] and delta[1] is random. therefore we
		// check that one is adresse and the other is kunde
		// and that there are not equal
		assertTrue("single delta should reference adresse",
				(deltas[0].getObjectId() == adresse.getObjectId() || deltas[1].getObjectId() == adresse.getObjectId()));
		assertTrue("delta status must be both be modified", (deltas[0].getState().equals(State.MODIFIED) && deltas[1]
				.getState().equals(State.MODIFIED)));
		assertTrue("single delta should reference kunde", (deltas[0].getObjectId() == kunde.getObjectId() || deltas[1]
				.getObjectId() == kunde.getObjectId()));
		assertTrue("single delta should reference different objects", (deltas[0].getObjectId() != deltas[1]
				.getObjectId()));

	}

	/**
	 * @throws Exception
	 */
	public void testExportModified3() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		Addresse adresse = new Addresse(false);
		adresse.setOrt("frankfurt");
		adresse.setStrasse("gutleutstrasse");
		objectTransaction.setCleanModus(false);
		kunde.setAddresse(adresse);

		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertTrue("should be only two transaction delta", deltas.length == 2);
		assertTrue("single delta should reference kunde", deltas[0].getObjectId() == kunde.getObjectId());
		assertTrue("delta status must be modified", deltas[0].getState().equals(State.MODIFIED));
		assertTrue("single delta should reference adresse", deltas[1].getObjectId() == adresse.getObjectId());
		assertTrue("delta status must be modified", deltas[1].getState().equals(State.CLEAN));
	}

	/**
	 * @throws Exception
	 */
	public void testExportModified4() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");

		Addresse adresse = new Addresse(false);
		kunde.setAddresse(adresse);

		adresse.setOrt("frankfurt");
		adresse.setStrasse("gutleutstrasse");
		objectTransaction.setCleanModus(false);
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertTrue("should be only one transaction delta", deltas.length == 1);
		assertTrue("single delta should reference kunde", deltas[0].getObjectId() == kunde.getObjectId());
		assertTrue("delta status must be modified", deltas[0].getState().equals(State.MODIFIED));
	}

	/**
	 * @throws Exception
	 */
	public void testExportModified5() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		objectTransaction.setCleanModus(false);
		Vertrag v2 = new Vertrag("456");
		v2.setVertragsBeschreibung("vertrag nummer 456");
		kunde.addVertrag(v2);

		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertEquals("should be only two transaction delta", 2, deltas.length);
		assertTrue("delta must be v2 vertrag or kunde delta", deltas[0].getObjectId() == v2.getObjectId()
				|| deltas[0].getObjectId() == kunde.getObjectId());
		if (deltas[0].getObjectId() == v2.getObjectId()) {
			assertSame("single delta should reference v2", deltas[0].getObjectId(), v2.getObjectId());
			assertEquals("delta status must be modified", State.CREATED, deltas[0].getState());
			assertSame("single delta should reference kunde", deltas[1].getObjectId(), kunde.getObjectId());
			assertEquals("delta status must be modified", State.MODIFIED, deltas[1].getState());
		} else {
			assertSame("single delta should reference v2", deltas[1].getObjectId(), v2.getObjectId());
			assertEquals("delta status must be modified", State.CREATED, deltas[1].getState());
			assertSame("single delta should reference kunde", deltas[0].getObjectId(), kunde.getObjectId());
			assertEquals("delta status must be modified", State.MODIFIED, deltas[0].getState());
		}
	}

	/**
	 * @throws Exception
	 */
	public void testExportModified6() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		objectTransaction.setCleanModus(false);
		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		v2.setVertragsBeschreibung("vertrag nummer 456");
		kunde.addVertrag(v2);

		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertEquals("should be only three transaction delta", 3, deltas.length);
		for (TransactionDelta delta : deltas) {
			if (delta.getObjectId() == v2.getObjectId()) {
				assertEquals("delta status must be modified", State.CREATED, delta.getState());
			} else {
				if (delta.getObjectId() == v1.getObjectId()) {
					assertEquals("delta status must be modified", State.CREATED, delta.getState());
				} else {
					if (delta.getObjectId() == kunde.getObjectId()) {
						assertEquals("delta status must be modified", State.MODIFIED, delta.getState());
					} else {
						fail("object id in delta does not match any of the expected ones");
					}
				}
			}
		}
	}

	/**
	 * @throws Exception
	 */
	public void testExportModified7() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		v2.setVertragsBeschreibung("vertrag nummer 456");
		kunde.addVertrag(v2);
		objectTransaction.setCleanModus(false);
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertTrue("should be only one transaction delta", deltas.length == 1);
		assertTrue("single delta should reference kunde", deltas[0].getObjectId() == kunde.getObjectId());
		assertTrue("delta status must be modified", deltas[0].getState().equals(State.MODIFIED));
	}

	/**
	 * @throws Exception
	 */
	public void testExportModified8() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		v2.setVertragsBeschreibung("vertrag nummer 456");
		objectTransaction.setCleanModus(false);
		kunde.addVertrag(v2);

		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertTrue("should be only two transaction delta", deltas.length == 2);
		assertTrue("single delta should reference kunde", deltas[0].getObjectId() == kunde.getObjectId());
		assertTrue("delta status must be modified", deltas[0].getState().equals(State.MODIFIED));
		assertTrue("single delta should reference v2", deltas[1].getObjectId() == v2.getObjectId());
		assertTrue("delta status must be modified", deltas[1].getState().equals(State.CLEAN));
	}

	/**
	 * @throws Exception
	 */
	public void testExportModified9() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		Vertrag v2 = new Vertrag("456");
		v2.setVertragsBeschreibung("vertrag nummer 456");
		objectTransaction.setCleanModus(false);
		kunde.addVertrag(v1);
		kunde.addVertrag(v2);

		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertTrue("should be only three transaction delta", deltas.length == 3);
		assertTrue("single delta should reference kunde", deltas[0].getObjectId() == kunde.getObjectId());
		assertTrue("delta status must be modified", deltas[0].getState().equals(State.MODIFIED));
		assertTrue("single delta should reference v1", deltas[1].getObjectId() == v1.getObjectId());
		assertTrue("delta status must be modified", deltas[1].getState().equals(State.CLEAN));
		assertTrue("single delta should reference v2", deltas[2].getObjectId() == v2.getObjectId());
		assertTrue("delta status must be modified", deltas[2].getState().equals(State.CLEAN));
	}

	/**
	 * @throws Exception
	 */
	public void testExportModified10() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		Vertrag v1 = new Vertrag("123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		kunde.addVertrag(v2);
		objectTransaction.setCleanModus(false);
		v1.setVertragsBeschreibung("vertrag nummer 123");
		v2.setVertragsBeschreibung("vertrag nummer 456");

		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertTrue("should be only two transaction delta", deltas.length == 2);
		assertTrue("single delta should reference v1", deltas[0].getObjectId() == v2.getObjectId());
		assertTrue("delta status must be modified", deltas[0].getState().equals(State.MODIFIED));
		assertTrue("single delta should reference adresse", deltas[1].getObjectId() == v1.getObjectId());
		assertTrue("delta status must be modified", deltas[1].getState() == State.MODIFIED);
	}

	/**
	 * @throws Exception
	 */
	public void testExportModified11() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		v2.setVertragsBeschreibung("vertrag nummer 456");
		kunde.addVertrag(v2);
		objectTransaction.setCleanModus(false);
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		TransactionDelta[] deltas = extract.getDeltas();
		assertTrue("should be only one transaction delta", deltas.length == 1);
		assertTrue("single delta should reference kunde", deltas[0].getObjectId() == kunde.getObjectId());
		assertTrue("delta status must be modified", deltas[0].getState().equals(State.MODIFIED));
	}

	/**
	 * @throws Exception
	 */
	public void testExportModified12() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		v2.setVertragsBeschreibung("vertrag nummer 456");
		kunde.addVertrag(v2);
		objectTransaction.setCleanModus(false);
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		extract.addCleanTransactedObject(v1);
		TransactionDelta[] deltas = extract.getDeltas();
		assertTrue("should be only one transaction delta", deltas.length == 2);
		assertTrue("single delta should reference kunde", deltas[0].getObjectId() == kunde.getObjectId());
		assertTrue("delta status must be modified", deltas[0].getState().equals(State.MODIFIED));
		assertTrue("single delta should reference kunde", deltas[1].getObjectId() == v1.getObjectId());
		assertTrue("delta status must be modified", deltas[1].getState().equals(State.CLEAN));
	}

	/**
	 * @throws Exception
	 */
	public void testExportModified13() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		v2.setVertragsBeschreibung("vertrag nummer 456");
		kunde.addVertrag(v2);
		objectTransaction.setCleanModus(false);
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();
		try {
			extract.addCleanTransactedObject(kunde);
			fail();
		} catch (AssertionFailedException e) {
			ok();
		}
	}

	/**
	 * @throws Exception
	 */
	public void testImportModified1() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		v2.setVertragsBeschreibung("vertrag nummer 456");
		kunde.addVertrag(v2);
		objectTransaction.setCleanModus(false);
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction objectTransaction2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction2.register(kunde);
		objectTransaction2.importOnlyModifedObjectsFromExtract(extract);
		assertTrue("kunde must be registered", objectTransaction2.isRegistered(kunde));
		assertTrue("v1 must not be registered", (!objectTransaction2.isRegistered(v1)));
		assertTrue("v2 must not be registered", (!objectTransaction2.isRegistered(v2)));
	}

	/**
	 * @throws Exception
	 */
	public void testImportModified2() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		kunde.addVertrag(v2);
		objectTransaction.setCleanModus(false);
		v2.setVertragsBeschreibung("vertrag nummer 456");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction objectTransaction2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction2.register(kunde);
		objectTransaction2.register(v2);
		objectTransaction2.importOnlyModifedObjectsFromExtract(extract);
		assertTrue("kunde must be registered", objectTransaction2.isRegistered(kunde));
		assertTrue("v1 must not be registered", (!objectTransaction2.isRegistered(v1)));
		assertTrue("v2 must be registered", objectTransaction2.isRegistered(v2));
	}

	/**
	 * @throws Exception
	 */
	public void testImportModified3() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		kunde.addVertrag(v2);
		objectTransaction.setCleanModus(false);
		v2.setVertragsBeschreibung("vertrag nummer 456");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction objectTransaction2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction2.register(kunde);
		objectTransaction2.register(v2);
		objectTransaction2.register(v1);
		objectTransaction2.importOnlyModifedObjectsFromExtract(extract);
		assertTrue("kunde must be registered", objectTransaction2.isRegistered(kunde));
		assertTrue("v1 must be registered", objectTransaction2.isRegistered(v1));
		assertTrue("v2 must be registered", objectTransaction2.isRegistered(v2));
	}

	/**
	 * @throws Exception
	 */
	public void testImportModified4() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		v2.setVertragsBeschreibung("vertrag nummer 456");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		objectTransaction.setCleanModus(false);
		kunde.addVertrag(v2);

		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction objectTransaction2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction2.register(kunde);
		try {
			objectTransaction2.importOnlyModifedObjectsFromExtract(extract);
			fail();
		} catch (InvalidTransactionFailure e) {
			ok();
		}
	}

	/**
	 * @throws Exception
	 */
	public void testImportModified5() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");

		Vertrag v1 = new Vertrag("123");
		v1.setVertragsBeschreibung("vertrag nummer 123");
		kunde.addVertrag(v1);
		Vertrag v2 = new Vertrag("456");
		v2.setVertragsBeschreibung("vertrag nummer 456");
		kunde.setVorname("john");
		kunde.setNachname("Miller");
		kunde.addVertrag(v2);
		Addresse adresse = new Addresse(true);
		objectTransaction.setCleanModus(false);
		kunde.setAddresse(adresse);

		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction objectTransaction2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction2.register(kunde);
		try {
			objectTransaction2.importOnlyModifedObjectsFromExtract(extract);
			fail();
		} catch (InvalidTransactionFailure e) {
			ok();
		}
	}

	/**
	 * @throws Exception
	 */
	public void testCheckNullValue1() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		kunde.setVorname("john");
		kunde.setNachname("Miller");

		Addresse adresse = new Addresse(true);
		kunde.setAddresse(adresse);
		objectTransaction.setCleanModus(false);

		kunde.setAddresse(null);
		assertTrue("adresse must be null", kunde.getAddresse() == null);
		kunde.setVorname(null);
		assertTrue("vorname must be null", kunde.getVorname() == null);
	}

	/**
	 * @throws Exception
	 */
	public void testAddDeleteImportExport() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		objectTransaction.registerAsDeleted(kunde);
		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction ot2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		ot2.importExtract(extract);
	}

	/**
	 * @throws Exception
	 */
	public void testAddDeleteImportExport2() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		objectTransaction.registerAsDeleted(kunde);
		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();

		IObjectTransaction ot2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		ot2.importExtract(extract);
	}

	/**
	 * @throws Exception
	 */
	public void testAddDeleteImportExport3() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		objectTransaction.registerAsDeleted(kunde);
		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction ot2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		ot2.importOnlyModifedObjectsFromExtract(extract);
	}

	/**
	 * @throws Exception
	 */
	public void testAddDeleteImportExport4() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		Vertrag v1 = new Vertrag("11");
		Vertrag v2 = new Vertrag("12");
		kunde.addVertrag(v1);
		kunde.addVertrag(v2);
		objectTransaction.registerAsDeleted(kunde);
		objectTransaction.registerAsDeleted(v1);
		objectTransaction.registerAsDeleted(v2);
		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction ot2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		ot2.importOnlyModifedObjectsFromExtract(extract);
	}

	/**
	 * @throws Exception
	 */
	public void testAddRemoveImportExportWithNewObjects() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		Addresse adresse = new Addresse(true);
		kunde.setAddresse(adresse);
		kunde.setAddresse(null);
		objectTransaction.registerAsDeleted(adresse);
		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction ot2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		new Kunde("4711");
		ot2.importExtract(extract);
	}

	/**
	 * @throws Exception
	 */
	public void testAddRemoveImportExportWithExistingObjects() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		objectTransaction.setCleanModus(false);
		Addresse adresse = new Addresse(true);
		kunde.setAddresse(adresse);
		kunde.setAddresse(null);
		objectTransaction.registerAsDeleted(adresse);
		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction ot2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		new Kunde("4711");
		ot2.importExtract(extract);
	}

	/**
	 * @throws Exception
	 */
	public void testAddRemoveImportExportWithExistingObjectsOneToNRelation() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();
		objectTransaction.setCleanModus(true);

		Kunde kunde = new Kunde("4711");
		objectTransaction.setCleanModus(false);
		Vertrag v1 = new Vertrag("11");
		kunde.addVertrag(v1);
		kunde.removeVertrag(v1);
		objectTransaction.registerAsDeleted(v1);
		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction ot2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		ot2.setCleanModus(true);
		new Kunde("4711");
		ot2.setCleanModus(false);
		ot2.importExtract(extract);
	}

	/**
	 * @throws Exception
	 */
	public void testAddRemoveImportExportWithNewObjectsOneToNRelation() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();

		Kunde kunde = new Kunde("4711");
		Vertrag v1 = new Vertrag("11");
		kunde.addVertrag(v1);
		kunde.removeVertrag(v1);
		objectTransaction.registerAsDeleted(v1);
		objectTransaction.registerAsDeleted(kunde);
		IObjectTransactionExtract extract = objectTransaction.exportExtract();

		IObjectTransaction ot2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		ot2.importExtract(extract);
	}

	/**
	 * @throws Exception
	 */
	public void testComplex() throws Exception {
		// simulierter client
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setNachname("m�ller");
		objectTransaction.setCleanModus(false);
		objectTransaction.registerAsDeleted(kunde);
		IObjectTransactionExtract extract = objectTransaction.exportOnlyModifedObjectsToExtract();

		// simulierter server
		IObjectTransaction ot2 = ObjectTransactionFactory.getInstance().createObjectTransaction();
		ot2.setCleanModus(true);
		new Kunde("4711");
		ot2.setCleanModus(false);
		ot2.importExtract(extract);
		ot2.commitToObjects();
		IObjectTransactionExtract extract2 = ot2.exportExtract();

		// back to client
		objectTransaction.commitToObjects();
		objectTransaction.importExtract(extract2);
	}

	/**
	 * Tested ob SubSubTransaction gehen oder eine Exception erzeugen
	 * 
	 * @throws Exception
	 */
	public void testSubSubTransaction() throws Exception {
		IObjectTransaction objectTransaction = ObjectTransactionFactory.getInstance().createObjectTransaction();

		objectTransaction.setCleanModus(true);
		Kunde kunde = new Kunde("4711");
		kunde.setNachname("m�ller");
		objectTransaction.setCleanModus(false);

		IObjectTransaction subOT = objectTransaction.createSubObjectTransaction();
		Vertrag v1 = new Vertrag("4711");
		kunde.addVertrag(v1);

		IObjectTransaction subSubOT = subOT.createSubObjectTransaction();
		Vertrag v2 = new Vertrag("1015");
		kunde.addVertrag(v2);

		Kunde kunde2 = new Kunde("4712");
		kunde2.setNachname("campo");

		subSubOT.commit();
		subOT.commit();
		objectTransaction.commitToObjects();
		kunde2.setNachname("Campo");
		kunde.setNachname("Schramm");
		v1.setVertragsBeschreibung("xxx");
		v2.setVertragsBeschreibung("yyyy");

	}
}