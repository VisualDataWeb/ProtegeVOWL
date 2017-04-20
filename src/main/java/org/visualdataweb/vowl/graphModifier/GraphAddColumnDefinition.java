package org.visualdataweb.vowl.graphModifier;

import prefuse.data.Graph;
import org.visualdataweb.vowl.types.ColumnNames;

import java.util.ArrayList;

/**
 * This class contains a method to add column definitions to a graph.
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class GraphAddColumnDefinition {

	/**
	 * Constructor
	 */
	private GraphAddColumnDefinition() {
		// doesn't do anything
	}

	/**
	 * Adds all needed columns to the passed graph.
	 *
	 * @param graph the graph
	 * @return the graph with the columns added
	 */
	public static Graph addColumnDefinition(Graph graph) {

		// add id used for node and edges 
		graph.addColumn(ColumnNames.ID, Integer.class);

		// add text and node size 
		graph.addColumn(ColumnNames.NODE_HEIGHT, Integer.class);
		graph.addColumn(ColumnNames.NODE_WIDTH, Integer.class);
		graph.addColumn(ColumnNames.TEXT_SIZE, Integer.class);

		// add color columns for nodes and edges
		graph.addColumn(ColumnNames.COLOR_RED, Integer.class);
		graph.addColumn(ColumnNames.COLOR_GREEN, Integer.class);
		graph.addColumn(ColumnNames.COLOR_BLUE, Integer.class);
		graph.addColumn(ColumnNames.TEXT_COLOR_RED, Integer.class);
		graph.addColumn(ColumnNames.TEXT_COLOR_GREEN, Integer.class);
		graph.addColumn(ColumnNames.TEXT_COLOR_BLUE, Integer.class);
		graph.addColumn(ColumnNames.TEXT_BACKGROUND_COLOR_RED, Integer.class);
		graph.addColumn(ColumnNames.TEXT_BACKGROUND_COLOR_BLUE, Integer.class);
		graph.addColumn(ColumnNames.TEXT_BACKGROUND_COLOR_GREEN, Integer.class);
		graph.addColumn(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_RED, Integer.class);
		graph.addColumn(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_BLUE, Integer.class);
		graph.addColumn(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_GREEN, Integer.class);
		graph.addColumn(ColumnNames.IS_HIGHLIGHTED, Boolean.class);
		graph.addColumn(ColumnNames.IS_CLICKED, Boolean.class);

		// @see: Nodetype.nodetype ! ORDER IS IMPORTANT !
		graph.addColumn(ColumnNames.NODE_FORM, String.class);
		graph.addColumn(ColumnNames.NODE_VOWL_TYPE, String.class);
		graph.addColumn(ColumnNames.EDGE_VOWL_TYPE, String.class);
		graph.addColumn(ColumnNames.EDGE_LINE_TYPE, String.class);

		// add columns for the name of the node / edge
		graph.addColumn(ColumnNames.NAME, String.class);
		graph.addColumn(ColumnNames.FULL_NAME, String.class);
		graph.addColumn(ColumnNames.NAME_DATA, String.class);

		// add column for edge arrow
		graph.addColumn(ColumnNames.EDGE_ARROW_TYPE, String.class);

		// add column for the length of an edge 
		graph.addColumn(ColumnNames.EDGE_LENGTH, Integer.class);

		// add column for class count (classes only) and the angel of the ordered eges (edges only)
		graph.addColumn(ColumnNames.CLASS_INSTANCE_COUNT, Integer.class);
		graph.addColumn(ColumnNames.RENDER_UTILITY_E2E_ANGEL, Integer.class);

		// add columns for RDF / OWL data
		graph.addColumn(ColumnNames.RDFS_URI, String.class);
		graph.addColumn(ColumnNames.RDFS_COMMENT, String.class);
		graph.addColumn(ColumnNames.RDFS_DEFINED_BY, String.class);
		graph.addColumn(ColumnNames.OWL_VERSION_INFO, String.class);
		graph.addColumn(ColumnNames.RDFS_DOMAIN, String.class);
		graph.addColumn(ColumnNames.RDFS_RANGE, String.class);
		graph.addColumn(ColumnNames.RDFS_INVERSE_OF, String.class);

		// add columns to store possible equivalent classes
		graph.addColumn(ColumnNames.EQUIVALENT_CLASSES, ArrayList.class);

		return graph;
	}
}

