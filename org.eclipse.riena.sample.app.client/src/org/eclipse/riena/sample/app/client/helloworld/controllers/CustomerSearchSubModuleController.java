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
package org.eclipse.riena.sample.app.client.helloworld.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.internal.sample.app.client.Activator;
import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.model.SubModuleNode;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import org.eclipse.riena.sample.app.client.helloworld.views.CustomerDetailsSubModuleView;
import org.eclipse.riena.sample.app.common.model.Customer;
import org.eclipse.riena.sample.app.common.model.ICustomerSearch;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IActionRidget;
import org.eclipse.riena.ui.ridgets.ITableRidget;
import org.eclipse.riena.ui.ridgets.ITextRidget;
import org.eclipse.riena.ui.workarea.WorkareaManager;

public class CustomerSearchSubModuleController extends SubModuleController {

	private ICustomerSearch service;

	private ITableRidget tableRidget;
	private IActionRidget searchAction;
	private IActionRidget clearAction;
	private IActionRidget openAction;

	private ITextRidget firstNameRidget;
	private ITextRidget lastNameRidget;

	private ResultContainer searchResult;
	private WritableValue tableSelection;

	private Customer sample;

	public CustomerSearchSubModuleController(ISubModuleNode navigationNode) {
		super(navigationNode);
		searchResult = new ResultContainer();
		sample = new Customer();
		Inject.service(ICustomerSearch.class.getName()).into(this).andStart(Activator.getDefault().getContext());
	}

	public void bind(ICustomerSearch service) {
		this.service = service;
	}

	public void unbind(ICustomerSearch service) {
		if (this.service == service) {
			this.service = null;
		}
	}

	private class SearchCallback implements IActionListener {

		public void callback() {
			searchCustomers();
		}
	}

	protected void searchCustomers() {
		Customer[] result = service.findCustomer(sample);
		this.searchResult.setList(Arrays.asList(result));
		tableRidget.updateFromModel();
	}

	/**
	 * @see org.eclipse.riena.ui.ridgets.IRidgetContainer#configureRidgets()
	 */
	public void configureRidgets() {
		tableRidget = (ITableRidget) getRidget("tableRidget"); //$NON-NLS-1$
		searchAction = (IActionRidget) getRidget("searchAction"); //$NON-NLS-1$
		clearAction = (IActionRidget) getRidget("clearAction"); //$NON-NLS-1$
		openAction = (IActionRidget) getRidget("openAction"); //$NON-NLS-1$
		firstNameRidget = (ITextRidget) getRidget("firstNameRidget"); //$NON-NLS-1$
		lastNameRidget = (ITextRidget) getRidget("lastNameRidget"); //$NON-NLS-1$
	}

	@Override
	public void afterBind() {
		super.afterBind();
		String[] columnProperties = new String[] { Customer.PROPERTY_CUSTOMER_NUMBER, Customer.PROPERTY_LAST_NAME,
				Customer.PROPERTY_FIRST_NAME, Customer.PROPERTY_PHONE_BUSINESS };
		tableRidget.bindToModel(searchResult, "list", Customer.class, columnProperties, new String[] { "Number", //$NON-NLS-1$ //$NON-NLS-2$
				"Lastname", "Firstname", "phone" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		tableSelection = new WritableValue();
		tableRidget.bindSingleSelectionToModel(tableSelection);
		searchAction.addListener(new SearchCallback());
		firstNameRidget.bindToModel(sample, "firstName"); //$NON-NLS-1$
		lastNameRidget.bindToModel(sample, "lastName"); //$NON-NLS-1$
		clearAction.addListener(new ClearCallback());
		openAction.addListener(new OpenCallback());
	}

	private class OpenCallback implements IActionListener {
		public void callback() {
			Object selectedValue = tableSelection.getValue();
			if (selectedValue == null) {
				return;
			}
			if (!(selectedValue instanceof Customer)) {
				throw new RuntimeException("invalid datatype for selected value"); //$NON-NLS-1$
			}
			Customer selected = (Customer) selectedValue;
			ISubModuleNode node = getNavigationNode();
			SubModuleNode cNode = new SubModuleNode(null, selected.getFirstName());
			cNode.setContext(Customer.class.getName(), selected);
			WorkareaManager.getInstance().registerDefinition(cNode, CustomerDetailsSubModuleView.ID);
			node.addChild(cNode);
			cNode.activate();
		}
	}

	private class ClearCallback implements IActionListener {

		public void callback() {
			sample.setCustomerNumber(null);
			sample.setFirstName(""); //$NON-NLS-1$
			sample.setLastName(""); //$NON-NLS-1$
			firstNameRidget.updateFromModel();
			lastNameRidget.updateFromModel();
		}
	}

	public static class ResultContainer {

		private List<Customer> list = new ArrayList<Customer>();

		public List<Customer> getList() {
			return list;
		}

		public void setList(List<Customer> list) {
			this.list = list;
		}

	}
}
