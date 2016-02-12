package testing;

import graphModifier.GraphAddColumnDefinition;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import types.ColumnNames;
import types.EdgesType;
import types.Nodetype;

import java.util.Random;

/**
 * This class provides random data for random graphs (for testing and development)
 *
 * @author David Bold
 */
public class CreateRandomGraphData {

	private static final int COUNT_NODES = 15;
	private static final int COUNT_CONNECTIONS = 25;

	/**
	 * constructor, does nothing else
	 */
	public CreateRandomGraphData() {
	}

	/**
	 * GraphDataModifier a new GraphDataModifier with random date
	 *
	 * @return GraphDataModifier - a new prefuse GraphDataModifier with random nodes and connections
	 */
	public Graph create() {
		return create(new Graph(true));
	}

	/**
	 * adds random data (nodes & connections) to a given GraphDataModifier
	 *
	 * @param graph (PrefuseGraph)
	 * @return GraphDataModifier (PrefuseGraph) with random nodes and connections
	 */
	public Graph create(Graph graph) {

		graph = GraphAddColumnDefinition.addColumnDefinition(graph);

		Random rnd = new Random();

		// GraphDataModifier random Nodes with random data
		for (int i = 0; i <= COUNT_NODES; i++) {
			Node n = graph.addNode();
			// add random data (later use protege data)
			n.set(ColumnNames.ID, rnd.nextInt(25000));
			n.set(ColumnNames.NODE_HEIGHT, rnd.nextInt(35));
			n.set(ColumnNames.NODE_WIDTH, rnd.nextInt(35));
			n.set(ColumnNames.TEXT_SIZE, rnd.nextInt(12));
			n.set(ColumnNames.COLOR_RED, rnd.nextInt(250));
			n.set(ColumnNames.COLOR_GREEN, rnd.nextInt(250));
			n.set(ColumnNames.COLOR_BLUE, rnd.nextInt(250));
			n.set(ColumnNames.TEXT_COLOR_RED, rnd.nextInt(250));
			n.set(ColumnNames.TEXT_COLOR_GREEN, rnd.nextInt(250));
			n.set(ColumnNames.TEXT_COLOR_BLUE, rnd.nextInt(250));
			n.set(ColumnNames.TEXT_BACKGROUND_COLOR_RED, rnd.nextInt(250));
			n.set(ColumnNames.TEXT_BACKGROUND_COLOR_BLUE, rnd.nextInt(250));
			n.set(ColumnNames.TEXT_BACKGROUND_COLOR_GREEN, rnd.nextInt(250));
			n.set(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_RED, rnd.nextInt(250));
			n.set(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_BLUE, rnd.nextInt(250));
			n.set(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_GREEN, rnd.nextInt(250));
			n.setString(ColumnNames.NAME, "Node" + rnd.nextInt(100));
			n.setString(ColumnNames.FULL_NAME, "Node" + rnd.nextInt(100));
			n.set(ColumnNames.NODE_FORM, Nodetype.nodetype[rnd.nextInt(3)]);
			n.set(ColumnNames.NODE_VOWL_TYPE, Nodetype.vowltype[rnd.nextInt(3)]);
			n.set(ColumnNames.CLASS_INSTANCE_COUNT, rnd.nextInt(20));
		}

		// GraphDataModifier random connections
		for (int i = 0; i <= COUNT_CONNECTIONS; i++) {
			int a = rnd.nextInt(COUNT_NODES);
			int b = rnd.nextInt(COUNT_NODES);
			int edgeID = graph.addEdge(a, b);
			Edge edge = graph.getEdge(edgeID);
			edge.setString(ColumnNames.NAME, "Edge" + rnd.nextInt(100));
			edge.setString(ColumnNames.FULL_NAME, "Edge" + rnd.nextInt(100));
			edge.set(ColumnNames.TEXT_SIZE, rnd.nextInt(8));
			edge.set(ColumnNames.TEXT_COLOR_RED, rnd.nextInt(250));
			edge.set(ColumnNames.TEXT_COLOR_GREEN, rnd.nextInt(250));
			edge.set(ColumnNames.TEXT_COLOR_BLUE, rnd.nextInt(250));
			edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_RED, rnd.nextInt(250));
			edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_BLUE, rnd.nextInt(250));
			edge.set(ColumnNames.TEXT_BACKGROUND_COLOR_GREEN, rnd.nextInt(250));
			edge.set(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_RED, rnd.nextInt(250));
			edge.set(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_BLUE, rnd.nextInt(250));
			edge.set(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_GREEN, rnd.nextInt(250));
			edge.set(ColumnNames.COLOR_RED, rnd.nextInt(250));
			edge.set(ColumnNames.COLOR_GREEN, rnd.nextInt(250));
			edge.set(ColumnNames.COLOR_BLUE, rnd.nextInt(250));
			edge.set(ColumnNames.EDGE_ARROW_TYPE, EdgesType.arrowtype[rnd.nextInt(3)]);
			edge.set(ColumnNames.EDGE_LINE_TYPE, EdgesType.linetype[rnd.nextInt(3)]);
			edge.set(ColumnNames.ID, rnd.nextInt(25000));
			edge.set(ColumnNames.EDGE_LENGTH, rnd.nextInt(100));

		}

		return graph;
	}
}
