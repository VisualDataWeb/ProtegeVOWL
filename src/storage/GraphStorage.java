package storage;

/**
 * this class should be usefully to storage a GraphDataModifier
 * the GraphDataModifier is static because the same GraphDataModifier should be used over the whole project 
 */

import graphModifier.GraphAddColumnDefinition;
import graphModifier.GraphDataModifier;
import prefuse.data.Graph;

import java.util.HashMap;

/**
 * this class should be usefully to storage a GraphDataModifier
 * the GraphDataModifier is static because the same GraphDataModifier should be used over the whole project
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class GraphStorage {

	private static HashMap<String, Graph> _graph;
	private static int _id_counter = 1;

	/**
	 * Constructor, creates a new GraphDataModifier within this class
	 */
	public GraphStorage(String id) {
		newGraph(id);
	}

	/**
	 * Constructor, without creating a new GraphDataModifier within this class
	 */
	public GraphStorage(Graph g, String id) {
		if (_graph == null) {
			_graph = new HashMap<String, Graph>();
		}

		if (!_graph.containsKey(id)) {
			_graph.put(id, g);
		}
	}

	/**
	 *
	 * @param id viewManager id
	 * @return new GraphDataModifier
	 */
	public static GraphDataModifier getDataModifier(String id) {
		return new GraphDataModifier(id);
	}

	/**
	 * overwrites the GraphDataModifier of this class
	 *
	 * @param g the graph
	 * @param id the viewManager id.
	 */
	public static void setGraph(Graph g, String id) {
		if (!_graph.containsKey(id)) {
			_graph.put(id, g);
		}
	}

	/**
	 * @return a unused id
	 */
	public static int getNewID() {
		int i = _id_counter;
		_id_counter++;

		return i;
	}

	/**
	 * creates a new GraphDataModifier stored in this class
	 * the column definition have already been added
	 */
	public static void newGraph(String id) {
		_id_counter = 1; // reset ID counter

		if (_graph == null) {
			_graph = new HashMap<String, Graph>();
		}
		_graph.put(id, GraphAddColumnDefinition.addColumnDefinition(new Graph(true)));
	}

	/**
	 * @return the GraphDataModifier of the graph with the passed id
	 */
	public static Graph getGraph(String id) {
		return _graph.get(id);
	}

}
