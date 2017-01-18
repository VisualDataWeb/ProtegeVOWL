package org.visualdataweb.vowl.types;

/**
 * this class contains a string array witch defines the node types
 * these types are used among others from the NodeRenderer to specified the node type
 * ! be careful the order is important !
 *
 * @see rendering.NodeRenderer
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class Nodetype {

	/**
	 * be careful the order is important !
	 */
	public final static String[] nodetype = {"Circle", "Square", "Pie", "None"};

	/**
	 * be careful the order is important and each entry has to be unique!
	 */
	public final static String[] vowltype = {"None", "Class", "DeprecatedClass", "ExternalClass", "Datatype", "RDFSClass", "OWLThing", "GenericDatatype", "EquivalentClass"};

	/**
	 * just a shortcut for vowltype[6]
	 */
	public static String OWLThing = vowltype[6];

	/**
	 * just a shortcut for vowltype[4]
	 */
	public static String Literal = vowltype[4];

	/**
	 * just a shortcut for vowltype[7]
	 */
	public static String GenericLiteral = vowltype[7];
}