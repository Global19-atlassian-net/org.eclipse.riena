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
package org.eclipse.riena.example.client.controllers;

import org.eclipse.core.databinding.beans.BeansObservables;

import org.eclipse.riena.beans.common.AbstractBean;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.ISpinnerRidget;
import org.eclipse.riena.ui.ridgets.ITraverseRidget;

/**
 * Controller of the sub-module to show some examples of {@code ITraverseRidget}
 * s.
 */
public class TraverseSubModuleController extends SubModuleController {

	private Temperature temperature;

	/**
	 * Creates a new instance of {@code TraverseSubModuleController} and
	 * initializes the <i>domain</i> model of the controller.
	 */
	public TraverseSubModuleController() {
		super();
		temperature = new Temperature();
		temperature.setKelvin(273.15f);
	}

	@Override
	public void configureRidgets() {

		super.configureRidgets();

		TemperatureListener listener = new TemperatureListener();

		ISpinnerRidget fahrenheitSpinner = (ISpinnerRidget) getRidget("fahrenheitSpinner"); //$NON-NLS-1$
		fahrenheitSpinner.setIncrement(1);
		fahrenheitSpinner.setMaximum(122);
		fahrenheitSpinner.setMinimum(32);
		fahrenheitSpinner.bindToModel(BeansObservables.observeValue(temperature,
				Temperature.PROPERTY_DEGREE_FAHRENHEITN));
		fahrenheitSpinner.updateFromModel();
		fahrenheitSpinner.addListener(listener);

		ITraverseRidget celsiusScale = (ITraverseRidget) getRidget("celsiusScale"); //$NON-NLS-1$
		celsiusScale.setIncrement(1);
		celsiusScale.setMaximum(50);
		celsiusScale.setMinimum(0);
		celsiusScale.bindToModel(BeansObservables.observeValue(temperature, Temperature.PROPERTY_DEGREE_CELSIUS));
		celsiusScale.updateFromModel();
		celsiusScale.addListener(listener);

		ITraverseRidget kelvinProgressBar = (ITraverseRidget) getRidget("kelvinProgressBar"); //$NON-NLS-1$
		kelvinProgressBar.setIncrement(1);
		kelvinProgressBar.setMaximum(323);
		kelvinProgressBar.setMinimum(273);
		kelvinProgressBar.bindToModel(BeansObservables.observeValue(temperature, Temperature.PROPERTY_KELVIN));
		kelvinProgressBar.updateFromModel();

	}

	/**
	 * Bean to store a temperature in different measuring units.
	 */
	private class Temperature extends AbstractBean {

		static final String PROPERTY_DEGREE_CELSIUS = "degreeCelsius"; //$NON-NLS-1$
		static final String PROPERTY_DEGREE_FAHRENHEITN = "degreeFahrenheit"; //$NON-NLS-1$
		static final String PROPERTY_KELVIN = "kelvin"; //$NON-NLS-1$

		private float kelvin;
		private int degreeCelsius;
		private int degreeFahrenheit;

		public void setDegreeCelsius(int degreeCelsius) {
			setDegreeCelsius(degreeCelsius, true);
		}

		private void setDegreeCelsius(int degreeCelsius, boolean updateKelvin) {
			int oldValue = this.degreeCelsius;
			this.degreeCelsius = degreeCelsius;
			if (updateKelvin) {
				float k = degreeCelsius + 273.15f;
				setKelvin(k);
				updateFahrenheit();
			}
			firePropertyChanged(PROPERTY_DEGREE_CELSIUS, oldValue, degreeCelsius);
		}

		public int getDegreeCelsius() {
			return degreeCelsius;
		}

		public void setDegreeFahrenheit(int degreeFahrenheit) {
			setDegreeFahrenheit(degreeFahrenheit, true);
		}

		private void setDegreeFahrenheit(int degreeFahrenheit, boolean updateKelvin) {
			int oldValue = this.degreeFahrenheit;
			this.degreeFahrenheit = degreeFahrenheit;
			if (updateKelvin) {
				float c = (degreeFahrenheit - 32) / 1.8f;
				float k = c + 273.15f;
				setKelvin(k);
				updateCelsius();
			}
			firePropertyChanged(PROPERTY_DEGREE_FAHRENHEITN, oldValue, degreeFahrenheit);
		}

		public int getDegreeFahrenheit() {
			return degreeFahrenheit;
		}

		public void setKelvin(float kelvin) {
			this.kelvin = kelvin;
			System.out.println("TraverseSubModuleController.Temperature.setKelvin() " + kelvin);
		}

		public float getKelvin() {
			return kelvin;
		}

		private void updateCelsius() {
			int c = Math.round(getKelvin() - 273.15f);
			setDegreeCelsius(c, false);
		}

		private void updateFahrenheit() {
			int c = Math.round(getKelvin() - 273.15f);
			int f = Math.round(c * 1.8f + 32);
			setDegreeFahrenheit(f, false);
		}

	}

	private class TemperatureListener implements IActionListener {

		public void callback() {
			updateAllRidgetsFromModel();
		}

	}

}
