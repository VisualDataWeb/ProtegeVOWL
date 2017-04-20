package org.visualdataweb.vowl.storage;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class ControlViewStorage {

	private static Button _renderToggleButton;
	private static JSlider _gravityLiteralsSlider;
	private static JSlider _gravityClassesSlider;

	/**
	 * default constructor
	 */
	public ControlViewStorage() {}

	/**
	 * constructor
	 *
	 * @param b the render toggle button
	 * @param classes the JSLider for class gravity
	 * @param literals the JSlider for literal gravity
	 */
	public ControlViewStorage(Button b, JSlider classes, JSlider literals) {
		_renderToggleButton = b;
		_gravityLiteralsSlider = literals;
		_gravityClassesSlider = classes;
	}

	/**
	 * @return the render toggle button
	 */
	public static Button getRenderToggleButton() {
		return _renderToggleButton;
	}

	/**
	 * @return the JSlider for literal gravity
	 */
	public static JSlider getGravitiyLiteralsSlider() {
		return _gravityLiteralsSlider;
	}

	/**
	 * @return the JSlider for class gravity
	 */
	public static JSlider getGravitiyClassesSlider() {
		return _gravityClassesSlider;
	}
}
