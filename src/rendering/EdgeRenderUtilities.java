package rendering;

import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.visual.NodeItem;
import types.ColumnNames;

import java.util.ArrayList;
import java.util.Iterator;

public class EdgeRenderUtilities {

	private NodeItem _sourceNode;
	private NodeItem _targetNode;

	/**
	 * constructor of the EdgeRenderHelper class
	 *
	 * @param edge - the current rendered edge
	 */
	public EdgeRenderUtilities(Edge edge) {
		_sourceNode = (NodeItem) edge.getSourceNode();
		_targetNode = (NodeItem) edge.getTargetNode();
	}

	/**
	 * no java doc available, see pdf
	 *
	 * @return double - the angel
	 */
	public double getTargetNodeAngelBeta() {
		double x = _sourceNode.getBounds().getCenterX() - _targetNode.getBounds().getCenterX();
		double y = _sourceNode.getBounds().getCenterY() - _targetNode.getBounds().getCenterY();
		double beta = Math.toDegrees(Math.atan(x / y));
		return beta;
	}

	/**
	 * no java doc available, see pdf
	 *
	 * @return double - the angel
	 */
	public double getSourceNodeAngelBeta() {
		double x = _targetNode.getBounds().getCenterX() - _sourceNode.getBounds().getCenterX();
		double y = _targetNode.getBounds().getCenterY() - _sourceNode.getBounds().getCenterY();
		double beta = Math.toDegrees(Math.atan(x / y));
		return beta;
	}

	/**
	 * no java doc available, see pdf
	 *
	 * @return int - value within x direction
	 */
	public double getLengthTargetNodeX(int angel) {
		double beta2 = (int) (90 - getTargetNodeAngelBeta() - angel);
		double x = Math.cos(Math.toRadians(beta2));
		double r = (double) ((Integer) _targetNode.get(ColumnNames.NODE_HEIGHT));
		r = r / 2;
		x = x * r;
		return (x);
	}

	/**
	 * no java doc available, see pdf
	 *
	 * @return int - value within x direction
	 */
	public double getLengthSourceNodeX(int angel) {
		double beta2 = (int) (90 - getSourceNodeAngelBeta() - angel);
		double x = Math.cos(Math.toRadians(beta2));
		double r = (double) ((Integer) _sourceNode.get(ColumnNames.NODE_HEIGHT));
		r = r / 2;
		x = x * r;
		return (x);
	}

	/**
	 * no java doc available, see pdf
	 *
	 * @return int - value within y direction
	 */
	public int getLengthSourceNodeY(int angel) {
		double beta2 = (int) (90 - getSourceNodeAngelBeta() - angel);
		double y = Math.sin(Math.toRadians(beta2));
		int r = (Integer) _sourceNode.get(ColumnNames.NODE_HEIGHT);
		r = r / 2;
		y = y * r;
		return ((int) y);
	}

	/**
	 * no java doc available, see pdf
	 *
	 * @return int - value within y direction
	 */
	public int getLengthTargetNodeY(int angel) {
		double beta2 = (int) (90 - getTargetNodeAngelBeta() - angel);
		double y = Math.sin(Math.toRadians(beta2));
		int r = (Integer) _targetNode.get(ColumnNames.NODE_HEIGHT);
		r = r / 2;
		y = y * r;
		return ((int) y);
	}

	/**
	 * @return int - value within y direction
	 */
	public int getArrowHightObservingCircle(int arrowHigh, int angel) {
		double beta2 = (int) (90 - getTargetNodeAngelBeta() - angel);
		double y = Math.sin(Math.toRadians(beta2));
		int r = (Integer) _targetNode.get(ColumnNames.NODE_HEIGHT) + 2 * arrowHigh;
		r = r / 2;
		y = y * r;
		return ((int) y);
	}

	/**
	 * @return int - value within x direction
	 */
	public double getArrowWidhtObservingCircle(int arrowWidht, int angel) {
		double beta2 = (int) (90 - getTargetNodeAngelBeta() - angel);
		double x = Math.cos(Math.toRadians(beta2));
		double r = (double) ((Integer) _targetNode.get(ColumnNames.NODE_HEIGHT) + 2 * arrowWidht);
		r = r / 2;
		x = x * r;
		return (x);
	}

	/**
	 * returns an ArrayList of edges between given source and target node
	 *
	 * @param sourceNode
	 * @param targetNode
	 * @return ArrayList
	 */
	public ArrayList<Edge> getEdgesBetweenTwoNodes(Node sourceNode, Node targetNode) {
		ArrayList<Edge> edgeList = new ArrayList<Edge>();
		Iterator<?> sourceNodeOutEdgesIterator = sourceNode.outEdges();
		Iterator<?> sourceNodeInEdgesIterator = sourceNode.inEdges();
		Iterator<?> targetNodeOutEdgesIterator = targetNode.outEdges();
		Iterator<?> targetNodeInEdgesIterator = targetNode.inEdges();
		while (sourceNodeOutEdgesIterator.hasNext()) {
			Edge e = (Edge) sourceNodeOutEdgesIterator.next();
			if (e.getTargetNode().equals(targetNode)) {
				edgeList.add(e);
			}
		}
		while (sourceNodeInEdgesIterator.hasNext()) {
			Edge e = (Edge) sourceNodeInEdgesIterator.next();
			if (e.getTargetNode().equals(targetNode)) {
				edgeList.add(e);
			}
		}
		while (targetNodeOutEdgesIterator.hasNext()) {
			Edge e = (Edge) targetNodeOutEdgesIterator.next();
			if (e.getTargetNode().equals(sourceNode)) {
				edgeList.add(e);
			}
		}
		while (targetNodeInEdgesIterator.hasNext()) {
			Edge e = (Edge) targetNodeInEdgesIterator.next();
			if (e.getTargetNode().equals(sourceNode)) {
				edgeList.add(e);
			}
		}
		return edgeList;
	}

	/**
	 * updates the order of edges between the same node!
	 * The result will be stored as RENDER_UTILITY_E2E_ORDER.
	 *
	 * @param edgeList ArrayList<Edge> - ArrayList of Edges between node a and b
	 */
	public void updateEdgeOrderOfEdges(ArrayList<Edge> edgeList) {
		// check consistent of the current edge order
		boolean consistentCurrentEdgeOrder = true;
		for (Edge edge : edgeList) {
			Object o = edge.get(ColumnNames.RENDER_UTILITY_E2E_ANGEL);
			if (o == null) {
				// edge order is not consistent !
				consistentCurrentEdgeOrder = false;
				break;
			}
		}
		// current edge order is consistent, stop function
		if (consistentCurrentEdgeOrder) {
			return;
		} else {
			// current edge order is not consistent -> delete current edge order
			for (Edge edge : edgeList) {
				edge.set(ColumnNames.RENDER_UTILITY_E2E_ANGEL, null);
			}
			for (int a = 0; a < edgeList.size(); a++) {
				Edge edge = edgeList.get(a);
				Object o = edge.get(ColumnNames.RENDER_UTILITY_E2E_ANGEL);
				// only update edges without edge order
				String inverseOf = (String) edge.get(ColumnNames.RDFS_INVERSE_OF);
				int flipDirection = (int) Math.pow(-1, edgeList.indexOf(edge));
				// no inverse and yet no edge order for the current edge -> create new edge order for current edge
				if (inverseOf == null || inverseOf.length() == 0) {
					edge.set(ColumnNames.RENDER_UTILITY_E2E_ANGEL, (int) edgeList.indexOf(edge) * flipDirection);
				} else {
					if (o == null) {
						// inverse found, set position of the current edge AND the inverse edge to the order of the current edge
						for (int i = 0; i < edgeList.size(); i++) {
							Edge e = edgeList.get(i);
							String iri = (String) e.get(ColumnNames.RDFS_URI);
							if (iri != null && iri.length() != 0) {
								if (iri.equals(inverseOf)) {
									edge.set(ColumnNames.RENDER_UTILITY_E2E_ANGEL, (int) edgeList.indexOf(edge) * flipDirection);
									e.set(ColumnNames.RENDER_UTILITY_E2E_ANGEL, (int) -1 * edgeList.indexOf(edge) * flipDirection);
								}
							}
						}
					}
				}
			}
		}
	}

}
