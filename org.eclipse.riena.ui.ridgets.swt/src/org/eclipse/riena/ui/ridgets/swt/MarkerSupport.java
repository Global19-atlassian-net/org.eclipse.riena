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
package org.eclipse.riena.ui.ridgets.swt;

import java.beans.PropertyChangeSupport;
import java.util.Iterator;

import org.osgi.service.log.LogService;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.log.Logger;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import org.eclipse.riena.core.Log4r;
import org.eclipse.riena.core.util.Nop;
import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.internal.ui.ridgets.swt.Activator;
import org.eclipse.riena.internal.ui.ridgets.swt.SharedColors;
import org.eclipse.riena.internal.ui.ridgets.swt.SharedImages;
import org.eclipse.riena.ui.core.marker.MandatoryMarker;
import org.eclipse.riena.ui.core.marker.NegativeMarker;
import org.eclipse.riena.ui.ridgets.IBasicMarkableRidget;
import org.eclipse.riena.ui.ridgets.IControlDecoration;
import org.eclipse.riena.ui.ridgets.IMarkableRidget;
import org.eclipse.riena.ui.swt.CompletionCombo;
import org.eclipse.riena.ui.swt.facades.SWTFacade;
import org.eclipse.riena.ui.swt.lnf.LnfKeyConstants;
import org.eclipse.riena.ui.swt.lnf.LnfManager;
import org.eclipse.riena.ui.swt.lnf.rienadefault.RienaDefaultLnf;

/**
 * Helper class for SWT Ridgets to delegate their marker issues to.
 */
public class MarkerSupport extends BasicMarkerSupport {

	private static final Logger LOGGER = Log4r.getLogger(Activator.getDefault(), MarkerSupport.class);
	private static final String PRE_MANDATORY_BACKGROUND_KEY = "MaSu.preMaBa"; //$NON-NLS-1$
	private static final String PRE_OUTPUT_BACKGROUND_KEY = "MaSu.preOuBa"; //$NON-NLS-1$
	private static final String PRE_NEGATIVE_FOREGROUND_KEY = "MaSu.preNeFo"; //$NON-NLS-1$
	private static final long FLASH_DURATION_MS = 300;

	/**
	 * This flag defines the default value that defines whether disabled ridgets
	 * do hide their content. Since v2.0 the default value is {@code false}. It
	 * can be overridden by setting the system property
	 * {@code  'HIDE_DISABLED_RIDGET_CONTENT'} to {@code true}.
	 * <p>
	 * Note: A Look&Feel constants exists to define whether disabled ridgets do
	 * hide their content: {@code LnfKeyConstants.DISABLED_MARKER_HIDE_CONTENT}.
	 * The default value is only used, if the current Look&Feel doesn't use
	 * {@code LnfKeyConstants.DISABLED_MARKER_HIDE_CONTENT}.
	 */
	private static final boolean HIDE_DISABLED_RIDGET_CONTENT = Boolean.parseBoolean(System.getProperty(
			"HIDE_DISABLED_RIDGET_CONTENT", Boolean.FALSE.toString())); //$NON-NLS-1$
	private static Boolean hideDisabledRidgetContent;

	private IControlDecoration errorDecoration;
	private boolean isFlashInProgress;

	/**
	 * Returns whether the content of a disabled ridget should be visible (
	 * {@code false}) or hidden {@code true}.
	 * 
	 * @return ({@code false}): visible; {@code true}: hidden
	 * @since 2.0
	 */
	public static boolean isHideDisabledRidgetContent() {
		if (hideDisabledRidgetContent == null) {
			hideDisabledRidgetContent = LnfManager.getLnf().getBooleanSetting(
					LnfKeyConstants.DISABLED_MARKER_HIDE_CONTENT, HIDE_DISABLED_RIDGET_CONTENT);
		}
		return hideDisabledRidgetContent;
	}

	public MarkerSupport() {
		super();
	}

	public MarkerSupport(final IBasicMarkableRidget ridget, final PropertyChangeSupport propertyChangeSupport) {
		super(ridget, propertyChangeSupport);
	}

	// protected methods
	////////////////////

	@Override
	public synchronized final void flash() {
		final Control control = getUIControl();
		if (!isFlashInProgress && control != null) {
			isFlashInProgress = true;

			if (errorDecoration == null) {
				errorDecoration = createErrorDecoration(control);
			}

			final boolean isShowing = errorDecoration.isVisible();
			if (isShowing) {
				errorDecoration.hide();
			} else {
				errorDecoration.show();
			}

			final Display display = control.getDisplay();
			final Runnable op = new Runnable() {
				public void run() {
					try {
						Thread.sleep(FLASH_DURATION_MS);
					} catch (final InterruptedException e) {
						Nop.reason("ignore"); //$NON-NLS-1$
					} finally {
						display.syncExec(new Runnable() {
							public void run() {
								if (!control.isDisposed()) {
									updateError(control);
								}
							}
						});
						isFlashInProgress = false;
					}
				}
			};
			new Thread(op).start();
		}
	}

	@Override
	protected IMarkableRidget getRidget() {
		return (IMarkableRidget) super.getRidget();
	}

	protected void addError(final Control control) {
		if (errorDecoration == null) {
			errorDecoration = createErrorDecoration(control);
		}
		errorDecoration.show();
	}

	// helping methods
	//////////////////

	protected void addMandatory(final Control control) {
		if (control.getData(PRE_MANDATORY_BACKGROUND_KEY) == null) {
			control.setData(PRE_MANDATORY_BACKGROUND_KEY, control.getBackground());
		}
		final RienaDefaultLnf lnf = LnfManager.getLnf();
		Color color = lnf.getColor(LnfKeyConstants.MANDATORY_MARKER_BACKGROUND);
		if (color == null) {
			color = Activator.getSharedColor(control.getDisplay(), SharedColors.COLOR_MANDATORY);
		}
		if (control instanceof CompletionCombo) {
			((CompletionCombo) control).setTextBackground(color);
		} else {
			control.setBackground(color);
		}
	}

	// helping methods
	//////////////////

	protected void addOutput(final Control control, final Color color) {
		if (control.getData(PRE_OUTPUT_BACKGROUND_KEY) == null) {
			control.setData(PRE_OUTPUT_BACKGROUND_KEY, control.getBackground());
			control.setBackground(color);
		}
	}

	@Override
	protected void clearAllMarkers(final Control control) {
		super.clearAllMarkers(control);
		clearError(control);
		clearMandatory(control);
		clearNegative(control);
		clearOutput(control);
	}

	protected void clearError(final Control control) {
		if (errorDecoration != null) {
			errorDecoration.hide();
		}
	}

	protected void clearMandatory(final Control control) {
		if (control.getData(PRE_MANDATORY_BACKGROUND_KEY) != null) {
			control.setBackground((Color) control.getData(PRE_MANDATORY_BACKGROUND_KEY));
			control.setData(PRE_MANDATORY_BACKGROUND_KEY, null);
		}
	}

	protected void clearOutput(final Control control) {
		if (control.getData(PRE_OUTPUT_BACKGROUND_KEY) != null) {
			control.setBackground((Color) control.getData(PRE_OUTPUT_BACKGROUND_KEY));
			control.setData(PRE_OUTPUT_BACKGROUND_KEY, null);
		}
	}

	/**
	 * Creates a decoration with an error marker for the given control.
	 * <p>
	 * The decoration draws an image before or after the UI control.
	 * 
	 * @param control
	 *            the control to be decorated with an error marker; never null.
	 * @since 3.0
	 */
	protected IControlDecoration createErrorDecoration(final Control control) {
		return new MarkerControlDecoration(control);
	}

	/**
	 * Precedence of visibility and marker states for a ridget:
	 * <ol>
	 * <li>ridget is hidden - no decorations are not shown</li>
	 * <li>disabled on - all other states not shown on the ridget</li>
	 * <li>output on - output decoration is shown</li>
	 * <li>mandatory on - mandatory decoration is shown</li>
	 * <li>error on - error decoration is shown</li>
	 * <li>negative on - negative decoration is shown</li>
	 * <ol>
	 */
	@Override
	protected void updateUIControl(final Control control) {
		updateVisible(control);
		updateDisabled(control);
		updateOutput(control);
		updateMandatory(control);
		updateError(control);
		updateNegative(control);
	}

	// helping methods
	//////////////////

	private void addNegative(final Control control) {
		if (control.getData(PRE_NEGATIVE_FOREGROUND_KEY) == null) {
			control.setData(PRE_NEGATIVE_FOREGROUND_KEY, control.getForeground());
			control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_RED));
		}
	}

	private void clearNegative(final Control control) {
		if (control.getData(PRE_NEGATIVE_FOREGROUND_KEY) != null) {
			control.setForeground((Color) control.getData(PRE_NEGATIVE_FOREGROUND_KEY));
			control.setData(PRE_NEGATIVE_FOREGROUND_KEY, null);
		}
	}

	private boolean isButton(final Control control) {
		return control instanceof Button || getRidget() instanceof AbstractActionRidget;
	}

	private boolean isMandatory(final IMarkableRidget ridget) {
		boolean result = false;
		final Iterator<MandatoryMarker> iter = getRidget().getMarkersOfType(MandatoryMarker.class).iterator();
		while (!result && iter.hasNext()) {
			result = !iter.next().isDisabled();
		}
		return result;
	}

	private void updateError(final Control control) {
		if (getRidget().isErrorMarked() && getRidget().isEnabled() && getRidget().isVisible()) {
			if (!(isButton(control) && getRidget().isOutputOnly())) {
				addError(control);
			} else {
				clearError(control);
			}
		} else {
			clearError(control);
		}
	}

	private void updateMandatory(final Control control) {
		if (isMandatory(getRidget()) && !getRidget().isOutputOnly() && getRidget().isEnabled()) {
			addMandatory(control);
		} else {
			clearMandatory(control);
		}
	}

	private void updateNegative(final Control control) {
		if (!getRidget().getMarkersOfType(NegativeMarker.class).isEmpty() && getRidget().isEnabled()) {
			addNegative(control);
		} else {
			clearNegative(control);
		}
	}

	private void updateOutput(final Control control) {
		if (getRidget().isOutputOnly() && getRidget().isEnabled()) {
			clearMandatory(control);
			clearOutput(control);
			final RienaDefaultLnf lnf = LnfManager.getLnf();
			if (isMandatory(getRidget())) {
				Color color = lnf.getColor(LnfKeyConstants.MANDATORY_OUTPUT_MARKER_BACKGROUND);
				if (color == null) {
					color = Activator.getSharedColor(control.getDisplay(), SharedColors.COLOR_MANDATORY_OUTPUT);
				}
				addOutput(control, color);
			} else {
				final Color color = lnf.getColor(LnfKeyConstants.OUTPUT_MARKER_BACKGROUND);
				addOutput(control, color);
			}
		} else {
			clearOutput(control);
		}
	}

	// helping classes
	//////////////////

	/**
	 * {@inheritDoc}
	 * <p>
	 * The constructor of this implementation sets the margin width and the
	 * image to avoid unnecessary updates.
	 */
	private static class MarkerControlDecoration extends ControlDecoration implements IControlDecoration {

		public MarkerControlDecoration(final Control control) {
			super(control, getDecorationPosition(), getScrolledComposite(control));
			setMarginWidth(getErrorMarginWidth());
			setImage(getDecorationImage());
			if (SWTFacade.isRCP()) {
				ReflectionUtils.invokeHidden(this, "update", (Object[]) null); //$NON-NLS-1$
			}
			control.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent e) {
					dispose();
				}
			});
		}

		private static Image getDecorationImage() {
			Image result = null;
			final RienaDefaultLnf lnf = LnfManager.getLnf();
			if (Platform.getBundle(Activator.PLUGIN_ID) != null) {
				// ensure OSGi is available before calling this
				result = lnf.getImage(LnfKeyConstants.ERROR_MARKER_ICON);
			}
			if (result == null) {
				result = Activator.getSharedImage(SharedImages.IMG_ERROR_DECO);
			}
			return result;
		}

		private static int getDecorationPosition() {
			int result = SWT.NONE;
			final RienaDefaultLnf lnf = LnfManager.getLnf();
			final int hPos = lnf.getIntegerSetting(LnfKeyConstants.ERROR_MARKER_HORIZONTAL_POSITION, SWT.LEFT);
			if (hPos == SWT.RIGHT || hPos == SWT.LEFT) {
				result |= hPos;
			} else {
				LOGGER.log(LogService.LOG_WARNING, "Invalid horizonal error marker position!"); //$NON-NLS-1$
				result |= SWT.LEFT;
			}
			final int vPos = lnf.getIntegerSetting(LnfKeyConstants.ERROR_MARKER_VERTICAL_POSITION, SWT.TOP);
			if (vPos == SWT.TOP || vPos == SWT.CENTER || vPos == SWT.BOTTOM) {
				result |= vPos;
			} else {
				LOGGER.log(LogService.LOG_WARNING, "Invalid vertical error marker position!"); //$NON-NLS-1$
				result |= SWT.TOP;
			}
			return result;
		}

		private static int getErrorMarginWidth() {
			final RienaDefaultLnf lnf = LnfManager.getLnf();
			return lnf.getIntegerSetting(LnfKeyConstants.ERROR_MARKER_MARGIN, 1);
		}

		private static ScrolledComposite getScrolledComposite(final Control control) {
			if (control == null) {
				return null;
			} else if (control instanceof ScrolledComposite) {
				return (ScrolledComposite) control;
			} else {
				return getScrolledComposite(control.getParent());
			}
		}

	}

}
