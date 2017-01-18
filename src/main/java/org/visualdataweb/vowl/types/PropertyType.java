package org.visualdataweb.vowl.types;

/**
 * This class contains static integers used as property type identifier.
 * With a central definition this integers are equal over the whole project
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class PropertyType {
	public static final int PROPERTY = 0; // RDFS Property with green background
	public static final int OBJECT_PROPERTY = 1; // VOWL 2 ObjectProperty with blue background
	public static final int DATATYPE_PROPERTY = 2; // VOWL 2 DataTypeProperty with orange background
	public static final int SUBCLASS = 3; // VOWL 2 DataTypeProperty with orange background
	public final static String[] type = {"Property", "ObjectProperty", "DatatypeProperty", "subclass"};
}

