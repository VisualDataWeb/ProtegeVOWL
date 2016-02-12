package types;

/**
 * this class contains a string array witch defines the arrow types
 * these types are used among others from the EdgeRenderer to specified the edge type
 * ! be careful the order is important !
 *
 * @see
 * {@link rendering.EdgeRender}
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class EdgesType {

	/**
	 * be careful the order is important !
	 */
	public final static String[] arrowtype = {"None", "Arrow_filled", "Arrow_nonfilled"};
	public final static String[] linetype = {"normal", "dashed", "dotted"};
	public final static String[] characteristic = {"(functional)", "(symmetric)", "(transitive)", "(inverseFunctional)"};
}