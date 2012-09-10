package org.eclipse.riena.ui.ridgets.javafx.uibinding;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import org.eclipse.riena.core.singleton.SingletonProvider;
import org.eclipse.riena.internal.ui.ridgets.javafx.JavaFxLabelRidget;
import org.eclipse.riena.internal.ui.ridgets.javafx.JavaFxTextFieldRidget;
import org.eclipse.riena.ui.ridgets.uibinding.AbstractControlRidgetMapper;

public class JavaFxControlRidgetMapper extends AbstractControlRidgetMapper {

	private static final SingletonProvider<JavaFxControlRidgetMapper> SCRM = new SingletonProvider<JavaFxControlRidgetMapper>(
			JavaFxControlRidgetMapper.class);

	/**
	 * Answer the singleton <code>SwtControlRidgetMapper</code>
	 * 
	 * @return the SwtControlRidgetMapper singleton
	 */
	public static JavaFxControlRidgetMapper getInstance() {
		return SCRM.getInstance();
	}

	private JavaFxControlRidgetMapper() {
		initDefaultMappings();
	}

	/**
	 * Sets the default mapping of UI control-classes to a ridget-classes
	 */
	private void initDefaultMappings() {
		addMapping(Label.class, JavaFxLabelRidget.class);
		addMapping(TextField.class, JavaFxTextFieldRidget.class);
	}

}
