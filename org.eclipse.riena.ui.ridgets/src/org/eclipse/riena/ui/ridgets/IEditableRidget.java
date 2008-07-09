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
package org.eclipse.riena.ui.ridgets;

import java.util.Collection;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.riena.ui.core.marker.IMessageMarker;
import org.eclipse.riena.ui.ridgets.validation.IValidationRule;
import org.eclipse.riena.ui.ridgets.validation.IValidationRuleStatus;

/**
 * Ridget with a value that can be edited, validated and converted.
 */
public interface IEditableRidget extends IValueRidget, IValidationCallback {

	/**
	 * @return The converter used when updating from the UI-control to the
	 *         model.
	 */
	IConverter getUIControlToModelConverter();

	/**
	 * Sets the converter used when updating from the UI-control to the model.
	 * 
	 * @param converter
	 *            The new converter.
	 */
	void setUIControlToModelConverter(IConverter converter);

	/**
	 * @return The validation rules.
	 * @see #addValidationRule(IValidator)
	 */
	Collection<IValidator> getValidationRules();

	/**
	 * Adds a validator. By default validations will be performed when updating
	 * from the UI-control to the model and before a conversion. By default
	 * failed validations will mark the UI-control with an ErrorMarker and not
	 * block any user input. The time of the validation can be changed by using
	 * an IValidationRule. The reaction to a failed validation can be changed by
	 * using a validator that returns an IValidationRuleStatus.
	 * 
	 * @see IValidationRule
	 * @see IValidationRuleStatus
	 * 
	 * @param validationRule
	 *            The validation rule to add (non-null).
	 * @throws RuntimeException
	 *             if validationRule is null.
	 * @deprecated use {@link #addValidationRule(IValidator, boolean)}
	 */
	void addValidationRule(IValidator validationRule);

	/**
	 * Adds a validator to this ridget.
	 * <p>
	 * By default validators will be evaluated when updating from the UI-control
	 * to the ridget ("on edit") and when updating from the ridget to the model
	 * ("on update").
	 * <p>
	 * Failed validators cause an error marker to apper next to the UI-control.
	 * "On edit" validators may choose to block user input. The reaction to a
	 * failed validation can be changed by using a validator that returns an
	 * IValidationRuleStatus.
	 * 
	 * @see IValidator
	 * @see IValidationRuleStatus
	 * 
	 * @param validator
	 *            The validator to add (non-null).
	 * @param validateOnEdit
	 *            true will cause the validator to be evaluated "on edit", false
	 *            will cause the validator to be evaluated "on update"
	 * @throws RuntimeException
	 *             if the validator is null.
	 */
	void addValidationRule(IValidator validator, boolean validateOnEdit);

	/**
	 * Removes a validator.
	 * 
	 * @param validator
	 *            The validation rule to remove.
	 */
	void removeValidationRule(IValidator validator);

	/**
	 * Adds a message to be displayed when any validation rule of the ridget
	 * fails.
	 * 
	 * @param message
	 *            A message related to the failed validation.
	 */
	void addValidationMessage(String message);

	/**
	 * Adds a message to be displayed when the specified validation rule fails.
	 * This will not add the rule to the ridget. If the specified rule was not
	 * added to the ridget the message will never be displayed.
	 * 
	 * @see #addValidationRule(IValidator)
	 * @param message
	 *            A message related to the failed validation.
	 * @param validationRule
	 *            The validation rule related to the message.
	 */
	void addValidationMessage(String message, IValidator validationRule);

	/**
	 * Adds an IMessageMarker to be added to the ridget automatically when and
	 * only when a validation rule fails.
	 * 
	 * @see org.eclipse.riena.core.marker.IMarkable#addMarker(org.eclipse.riena.core.marker.IMarker)
	 * @see org.eclipse.riena.core.marker.IMarkable#removeMarker(org.eclipse.riena.core.marker.IMarker)
	 * @param messageMarker
	 *            An IMessageMarker related to the failed validation.
	 */
	void addValidationMessage(IMessageMarker messageMarker);

	/**
	 * Adds an IMessageMarker to be added to the ridget automatically when and
	 * only when the specified validation rule fails. This will not add the rule
	 * to the ridget. If the specified rule was not added to the ridget the
	 * message will never be displayed.
	 * 
	 * @see #addValidationRule(IValidator)
	 * @see org.eclipse.riena.core.marker.IMarkable#addMarker(org.eclipse.riena.core.marker.IMarker)
	 * @see org.eclipse.riena.core.marker.IMarkable#removeMarker(org.eclipse.riena.core.marker.IMarker)
	 * @param messageMarker
	 *            An IMessageMarker related to the failed validation.
	 * @param validationRule
	 *            The validation rule related to the IMessageMarker.
	 */
	void addValidationMessage(IMessageMarker messageMarker, IValidator validationRule);

	/**
	 * Removes a message to be displayed when any validation rule of the ridget
	 * fails. If the message was never added this method does nothing.
	 * 
	 * @param message
	 *            The message to remove.
	 */
	void removeValidationMessage(String message);

	/**
	 * Removes a message to be displayed when the specified validation rule of
	 * the ridget fails. If the message was never added this method does
	 * nothing.
	 * 
	 * @param message
	 *            The message to remove.
	 * @param validationRule
	 *            The validation rule related to the message.
	 */
	void removeValidationMessage(String message, IValidator validationRule);

	/**
	 * Removes an IMessageMarker to be added to the ridget automatically when
	 * and only when a validation rule fails. If the IMessageMarker was never
	 * added this method does nothing.
	 * 
	 * @param messageMarker
	 *            The IMessageMarker to remove.
	 */
	void removeValidationMessage(IMessageMarker messageMarker);

	/**
	 * Removes an IMessageMarker to be added to the ridget automatically when
	 * and only when the specified validation rule fails. If the IMessageMarker
	 * was never added this method does nothing.
	 * 
	 * @param messageMarker
	 *            The IMessageMarker to remove.
	 * @param validationRule
	 *            The validation rule related to the IMessageMarker.
	 */
	void removeValidationMessage(IMessageMarker messageMarker, IValidator validationRule);

}
