package rendering;

import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.visual.EdgeItem;
import types.ColumnNames;

public class ForceDirectedLayoutExtended extends ForceDirectedLayout {

	/**
	 * constructor, does the same as constructor within ForceDirectedLayout
	 *
	 * @param graph - Prefuse Graph
	 */
	public ForceDirectedLayoutExtended(String graph) {
		super(graph);
	}

	/**
	 * constructor, does the same as constructor within ForceDirectedLayout
	 *
	 * @param group         - String
	 * @param enforceBounds - Boolean
	 */
	public ForceDirectedLayoutExtended(String group, boolean enforceBounds) {
		super(group, enforceBounds);
	}

	/**
	 * constructor, does the same as constructor within ForceDirectedLayout
	 *
	 * @param group         - String
	 * @param enforceBounds - Boolean
	 * @param runonce       - Boolean
	 */
	public ForceDirectedLayoutExtended(String group, boolean enforceBounds, boolean runonce) {
		super(group, enforceBounds, runonce);
	}

	/**
	 * returns the length of an edge
	 * 'overrides' the prefuse way of calculating the force abstraction layout length
	 *
	 * @param    e        -	EdgeItem
	 */
	@Override
	protected float getSpringLength(EdgeItem e) {
		int edgeLengthColumn = e.getColumnIndex(ColumnNames.EDGE_LENGTH);
		try {
			int edgeLength = (Integer) e.get(edgeLengthColumn);
			return edgeLength;
		} catch (NullPointerException npe) {
			return -1.f;
		}
	}


}
