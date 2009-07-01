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
package org.eclipse.riena.demo.client.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.riena.demo.common.Customer;
import org.eclipse.riena.demo.common.Email;
import org.eclipse.riena.ui.ridgets.ITableRidget;
import org.eclipse.riena.ui.ridgets.swt.DateColumnFormatter;

/**
 *
 */
public class EmailCustomerController extends AbstractEmailController {

	private List<Email> customerEmailsList = new ArrayList<Email>();

	/*
	 * @seeorg.eclipse.riena.navigation.ui.controllers.SubModuleController#
	 * configureRidgets()
	 */
	@Override
	public void configureRidgets() {
		super.configureRidgets();

		ITableRidget emails = (ITableRidget) getRidget("emailsTable"); //$NON-NLS-1$

		String[] columnHeaders = { "Subject", "Date" }; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		String[] columnPropertyNames = { "emailSubject", "emailDate" }; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
		emails.bindToModel(emailsResult, "emails", Email.class, columnPropertyNames, columnHeaders); //$NON-NLS-1$

		emails.setColumnFormatter(1, new DateColumnFormatter("dd.MMM. HH:mm") { //$NON-NLS-1$
					@Override
					protected Date getDate(Object element) {
						return ((Email) element).getEmailDate();
					}
				});
		if (getNavigationNode().getNavigationArgument().getParameter() instanceof Customer) {
			Customer customer = (Customer) getNavigationNode().getNavigationArgument().getParameter();
			String emailAddress = customer.getEmailAddress();
			customerEmailsList = mailDemoService.findEmailsForCustomer(emailAddress);

		}
		// show all mails concerning this customer
		emailsResult.setEmails(customerEmailsList);
		getRidget("emailsTable").updateFromModel(); //$NON-NLS-1$

	}
}
