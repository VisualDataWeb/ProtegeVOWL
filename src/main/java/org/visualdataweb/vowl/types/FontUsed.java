package org.visualdataweb.vowl.types;

import prefuse.util.FontLib;

import java.awt.*;

/**
 * this class stores the used font
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class FontUsed {

	private static Font usedFont = FontLib.getFont("Dialog", 10);

	/**
	 * returns the used font
	 *
	 * @return font
	 */
	public static java.awt.Font getFont() {
		return usedFont;
	}

	/**
	 * saves the used font
	 *
	 * @param font the wanted font
	 */
	public static void setFont(java.awt.Font font) {
		usedFont = font;
	}

}
