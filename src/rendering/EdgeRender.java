package rendering;

/**
 * this class modifies the default prefuse.render.EdgeRenderer
 * to enable different arrow types within the same GraphDataModifier.
 * On default prefuse knows only directed and undirected graphs
 */

import prefuse.Constants;
import prefuse.data.Edge;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import types.ColumnNames;
import types.EdgesType;
import types.Nodetype;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;

public class EdgeRender extends prefuse.render.EdgeRenderer {

	private static final int _SPACE_BETWEEN_TWO_SYMMETRIC_PROPERTYS = 80;

	// private static final Logger log = Logger.getLogger(EdgeRender.class);

	public EdgeRender() {
		super();
	}

	public EdgeRender(int edgeType, int arrowType) {
		super(edgeType, arrowType);
	}

	@Override
	protected Shape getRawShape(VisualItem item) {
		EdgeItem edge = (EdgeItem) item;
		VisualItem item1 = edge.getSourceItem();
		VisualItem item2 = edge.getTargetItem();

		int type = m_edgeType;

		getAlignedPoint(m_tmpPoints[0], item1.getBounds(), m_xAlign1, m_yAlign1);
		getAlignedPoint(m_tmpPoints[1], item2.getBounds(), m_xAlign2, m_yAlign2);
		m_curWidth = (float) (m_width * getLineWidth(item));

		// which line type is wanted? normal, dashed or dottet?
		String lineType = item.getString(item.getColumnIndex(ColumnNames.EDGE_LINE_TYPE));
		edge.setStroke(new BasicStroke(1));        // default stroke (maybe changed later)
		if (lineType != null) {
			// lineType.equals(EdgesType.linetype[0]) -> do nothing -> normal line type
			if (lineType.equals(EdgesType.linetype[1])) {
				// dashed line
				Stroke drawingStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
						new float[]{2}, 0);
				item.setStroke((BasicStroke) drawingStroke);
			}
			if (lineType.equals(EdgesType.linetype[2])) {
				// dotted line
				Stroke drawingStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
						new float[]{1}, 0);
				item.setStroke((BasicStroke) drawingStroke);
			}
		}

		// which arrow head is needed?
		int arrowColumn = (Integer) edge.getColumnIndex(ColumnNames.EDGE_ARROW_TYPE);
		String arrowType = EdgesType.arrowtype[0];
		if (arrowColumn != -1) {
			arrowType = (String) edge.get(arrowColumn);
		}

		{ /* is code block is left to be used for datatype properties, in other cases it has been replaced */
			/* no arrow */
			if (arrowType.equals(EdgesType.arrowtype[0])) {
				m_curArrow = null;
			}
			
			/* arrow */
			if (!(arrowType.equals(EdgesType.arrowtype[0]))) {
				// get starting and ending edge endpoints
				boolean forward = (m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
				Point2D start = null, end = null;
				start = m_tmpPoints[forward ? 0 : 1];
				end = m_tmpPoints[forward ? 1 : 0];

				// compute the intersection with the target bounding box
				VisualItem dest = forward ? edge.getTargetItem() : edge.getSourceItem();
				int i = GraphicsLib.intersectLineRectangle(start, end, dest.getBounds(), m_isctPoints);
				if (i > 0) end = m_isctPoints[0];

				// GraphDataModifier the arrow head shape
				AffineTransform at = getArrowTrans(start, end, m_curWidth);
				m_arrowHead = changeArrowHead(m_arrowWidth, m_arrowHeight, arrowType);
				m_curArrow = at.createTransformedShape(m_arrowHead);

				// update the endpoints for the edge shape
				// need to bias this by arrow head size
				Point2D lineEnd = m_tmpPoints[forward ? 1 : 0];
				lineEnd.setLocation(0, -m_arrowHeight);
				at.transform(lineEnd, lineEnd);
			}

		}
		// GraphDataModifier the edge shape
		Shape shape = null;
		double n1x = m_tmpPoints[0].getX();
		double n1y = m_tmpPoints[0].getY();
		double n2x = m_tmpPoints[1].getX();
		double n2y = m_tmpPoints[1].getY();

		switch (type) {
			case Constants.EDGE_TYPE_LINE:
				if (item1.equals(item2) && Nodetype.nodetype[0].equals(item1.get(ColumnNames.NODE_FORM))) {
					/* if the edge is from a circle node a to the same node,
					   we have to use a different algorithm */
					int nodeHeight = (Integer) item1.get(ColumnNames.NODE_HEIGHT) / 2;
					
					/* how many edges are between source and target (within the whole graph) 
					 * the arrayList solves two problems:
					 *  - it acts as a counter to see how many edges we have (node specific)
					 *  - it orders each of the symmetric edges (node specific), 
					 *    the edges are placed around the node defined with their order
					 */
					EdgeRenderUtilities eru = new EdgeRenderUtilities(edge);
					ArrayList<Edge> edgeList = eru.getEdgesBetweenTwoNodes(edge.getSourceNode(), edge.getTargetNode());

					if (Nodetype.nodetype[0].equals(edge.getSourceItem().get(ColumnNames.NODE_FORM))) {
						// node type circle
						int alpha = 270 + ((1 + edgeList.indexOf(edge)) * _SPACE_BETWEEN_TWO_SYMMETRIC_PROPERTYS);
						// 270 - a constant for better human notation (upper left corner as starting area)
						// (1+edgeList.indexOf(edge))*X) - X is the distance between each multiple edge starting points
						int radius = nodeHeight; // just a 'rename' for better understanding
						while (alpha > 360) {
							// maybe not needed but for safety reasons
							alpha = alpha - 360;
						}
						// within a triangle, a is on the x-axis and b on the y-axis
						int a = (int) (Math.cos(Math.toRadians(alpha)) * radius);
						int b = (int) (Math.sin(Math.toRadians(alpha)) * radius);
						Point startPoint = new Point((int) item1.getBounds().getCenterX() + a, (int) item1.getBounds()
								.getCenterY() + b);
						alpha = alpha + 30;
						while (alpha > 360) {
							alpha = alpha - 360;
						}
						a = (int) (Math.cos(Math.toRadians(alpha)) * radius);
						b = (int) (Math.sin(Math.toRadians(alpha)) * radius);
						Point controllPoint = new Point((int) item1.getBounds().getCenterX() + 3 * a, (int) item1
								.getBounds().getCenterY() + 3 * b);
						alpha = alpha + 30;
						while (alpha > 360) {
							alpha = alpha - 360;
						}
						a = (int) (Math.cos(Math.toRadians(alpha)) * radius);
						b = (int) (Math.sin(Math.toRadians(alpha)) * radius);
						Point endPoint = new Point((int) item1.getBounds().getCenterX() + a, (int) item1.getBounds()
								.getCenterY() + b);
						QuadCurve2D curve = new QuadCurve2D.Double(startPoint.getX(), startPoint.getY(),
								controllPoint.getX(), controllPoint.getY(), endPoint.getX(), endPoint.getY());
						shape = curve;
						AffineTransform at = getArrowTrans(controllPoint, endPoint, m_curWidth);
						m_arrowHead = changeArrowHead(m_arrowWidth, m_arrowHeight, arrowType);
						m_curArrow = at.createTransformedShape(m_arrowHead);
					} else {
						// not a circle
						// old way, but has never tested on non circle elements, may give awful results !
						
						/* stepsToSide: how far to step in x and y direction. 
						 * Double stepsToSide  is the distance between start and end point */
						int stepsToSide = 15;
						/* 	arrowModifierX is hacky. Its a corrective factor to 'fix' 
						 *  getArrowTrans for non linear and non EDGE_TYPE_CURVE.
						 *  It depends on stepsToSide and need to be tested for each stepsToSide.
						 *  Fixing getArrowTrans for this line type would be better, but ... 
						 *  yet this solution is faster ;-) */
						int arrowModifierX = 2;

						Point endPoint = new Point(
								(int) item1.getBounds().getX() + nodeHeight + stepsToSide,
								(int) item1.getBounds().getY() + stepsToSide);
						Point startPoint = new Point(
								(int) item1.getBounds().getX() + nodeHeight - stepsToSide,
								(int) item1.getBounds().getY() + stepsToSide);
						Point controllPoint = new Point(
								(int) item1.getBounds().getCenterX(),
								(int) item1.getBounds().getCenterY() - (3 * nodeHeight));
						QuadCurve2D curveToTheSameNode = new QuadCurve2D.Double(
								startPoint.getX(), startPoint.getY(),
								controllPoint.getX(), controllPoint.getY(),
								endPoint.getX(), endPoint.getY()
						);
						shape = curveToTheSameNode;
						// change the arrow head (new position and direction needed)
						Point arrowPeakEndPoint = new Point(
								(int) item1.getBounds().getX() + nodeHeight + stepsToSide - arrowModifierX,
								(int) item1.getBounds().getY()
						);
						AffineTransform at = getArrowTrans(controllPoint, arrowPeakEndPoint, m_curWidth);
						m_arrowHead = changeArrowHead(m_arrowWidth, m_arrowHeight, arrowType);
						m_curArrow = at.createTransformedShape(m_arrowHead);
					}
				} // END OF: edge from node a to the same node a

				else {
					/* edge is from node a to a different node b */
					if (Nodetype.nodetype[0].equals(item2.get(ColumnNames.NODE_FORM))) {
						
						/* how many edges are between source and target (within the whole graph) 
						 * the arrayList solves two problems:
						 *  - it acts as a counter to see how many edges we have (node specific)
						 *  - it orders each of the symmetric edges (node specific), 
						 *    the edges are placed around the node defined with their order
						 *  !!! for none symmetric source-target or target-source !!!
						 */
						EdgeRenderUtilities eru = new EdgeRenderUtilities(edge);
						Object o = edge.get(ColumnNames.RENDER_UTILITY_E2E_ANGEL);
						if (o == null) {
							ArrayList<Edge> edgeList = eru.getEdgesBetweenTwoNodes(edge.getSourceNode(), edge.getTargetNode());
							eru.updateEdgeOrderOfEdges(edgeList);
						}
						o = edge.get(ColumnNames.RENDER_UTILITY_E2E_ANGEL);
						int angelDifference;
						if (o == null) {
							angelDifference = 10;
							;
						} else {
							angelDifference = 10 * (Integer) edge.get(ColumnNames.RENDER_UTILITY_E2E_ANGEL);
						}

						EdgeRenderUtilities erh = new EdgeRenderUtilities(edge);

						Point startPoint;
						if (item1.getBounds().getCenterY() < item2.getBounds().getCenterY()) {
							startPoint = new Point(
									(int) (item1.getBounds().getCenterX() + erh.getLengthSourceNodeX(angelDifference)),
									(int) (item1.getBounds().getCenterY() + erh.getLengthSourceNodeY(angelDifference))
							);
						} else {
							startPoint = new Point(
									(int) (item1.getBounds().getCenterX() - erh.getLengthSourceNodeX(angelDifference)),
									(int) (item1.getBounds().getCenterY() - erh.getLengthSourceNodeY(angelDifference))
							);
						}

						int angelDifferenceInverse = angelDifference * -1;
						
						/* change the position of the arrowhead */
						Point arrowPoint;
						if (item1.getBounds().getCenterY() < item2.getBounds().getCenterY()) {
							arrowPoint = new Point(
									(int) (item2.getBounds().getCenterX() - erh.getLengthTargetNodeX(angelDifferenceInverse)),
									(int) (item2.getBounds().getCenterY() - erh.getLengthTargetNodeY(angelDifferenceInverse))
							);
						} else {
							arrowPoint = new Point(
									(int) (item2.getBounds().getCenterX() + erh.getLengthTargetNodeX(angelDifferenceInverse)),
									(int) (item2.getBounds().getCenterY() + erh.getLengthTargetNodeY(angelDifferenceInverse))
							);
						}

						// point of intersection between edge and arrow
						Point intersectionPointEdgeArrow;
						if (item1.getBounds().getCenterY() < item2.getBounds().getCenterY()) {
							intersectionPointEdgeArrow = new Point(
									(int) (item2.getBounds().getCenterX() - erh.getArrowWidhtObservingCircle(m_arrowWidth, angelDifferenceInverse)),
									(int) (item2.getBounds().getCenterY() - erh.getArrowHightObservingCircle(m_arrowHeight, angelDifferenceInverse))
							);
						} else {
							intersectionPointEdgeArrow = new Point(
									(int) (item2.getBounds().getCenterX() + erh.getArrowWidhtObservingCircle(m_arrowWidth, angelDifferenceInverse)),

									(int) (item2.getBounds().getCenterY() + erh.getArrowHightObservingCircle(m_arrowHeight, angelDifferenceInverse))
							);
						}

						AffineTransform at = getArrowTrans(intersectionPointEdgeArrow, arrowPoint, m_curWidth);
						m_arrowHead = changeArrowHead(m_arrowWidth, m_arrowHeight, arrowType);
						m_curArrow = at.createTransformedShape(m_arrowHead);

						Line2D line = new Line2D.Double(startPoint, intersectionPointEdgeArrow);
						
						/*
						QuadCurve2D curve = new QuadCurve2D.Double(startPoint.getX(), startPoint.getY(),
							controllPoint.getX(), controllPoint.getY(),
							intersectionPointEdgeArrow.getX(), intersectionPointEdgeArrow.getY()
						);
						*/
						shape = line;

					} else {
						m_line.setLine(n1x, n1y, n2x, n2y);
						shape = m_line;
					}
				}
				break;
			case Constants.EDGE_TYPE_CURVE:

				if (item1.equals(item2) && Nodetype.nodetype[0].equals(item1.get(ColumnNames.NODE_FORM))) {
					/* if the edge is from a circle node a to the same node,
					   we have to use a different algorithm */
					int nodeHeight = (Integer) item1.get(ColumnNames.NODE_HEIGHT) / 2;
					
					/* how many edges are between source and target (within the whole graph) 
					 * the arrayList solves two problems:
					 *  - it acts as a counter to see how many edges we have (node specific)
					 *  - it orders each of the symmetric edges (node specific), 
					 *    the edges are placed around the node defined with their order
					 */
					EdgeRenderUtilities eru = new EdgeRenderUtilities(edge);
					ArrayList<Edge> edgeList = eru.getEdgesBetweenTwoNodes(edge.getSourceNode(), edge.getTargetNode());

					if (Nodetype.nodetype[0].equals(edge.getSourceItem().get(ColumnNames.NODE_FORM))) {
						// node type circle
						int alpha = 270 + ((1 + edgeList.indexOf(edge)) * _SPACE_BETWEEN_TWO_SYMMETRIC_PROPERTYS);
						// 270 - a constant for better human notation (upper left corner as starting area)
						// (1+edgeList.indexOf(edge))*X) - X is the distance between each multiple edge starting points
						int radius = nodeHeight; // just a 'rename' for better understanding
						while (alpha > 360) {
							// maybe not needed but for safety reasons
							alpha = alpha - 360;
						}
						// within a triangle, a is on the x-axis and b on the y-axis
						int a = (int) (Math.cos(Math.toRadians(alpha)) * radius);
						int b = (int) (Math.sin(Math.toRadians(alpha)) * radius);
						Point startPoint = new Point((int) item1.getBounds().getCenterX() + a, (int) item1.getBounds()
								.getCenterY() + b);
						alpha = alpha + 30;
						while (alpha > 360) {
							alpha = alpha - 360;
						}
						a = (int) (Math.cos(Math.toRadians(alpha)) * radius);
						b = (int) (Math.sin(Math.toRadians(alpha)) * radius);
						Point controllPoint = new Point((int) item1.getBounds().getCenterX() + 3 * a, (int) item1
								.getBounds().getCenterY() + 3 * b);
						alpha = alpha + 30;
						while (alpha > 360) {
							alpha = alpha - 360;
						}
						a = (int) (Math.cos(Math.toRadians(alpha)) * radius);
						b = (int) (Math.sin(Math.toRadians(alpha)) * radius);
						Point endPoint = new Point((int) item1.getBounds().getCenterX() + a, (int) item1.getBounds()
								.getCenterY() + b);
						QuadCurve2D curve = new QuadCurve2D.Double(startPoint.getX(), startPoint.getY(),
								controllPoint.getX(), controllPoint.getY(), endPoint.getX(), endPoint.getY());
						shape = curve;
						AffineTransform at = getArrowTrans(controllPoint, endPoint, m_curWidth);
						m_arrowHead = changeArrowHead(m_arrowWidth, m_arrowHeight, arrowType);
						m_curArrow = at.createTransformedShape(m_arrowHead);
					} else {
						// not a circle
						// old way, but has never tested on non circle elements, may give awful results !
						
						/* stepsToSide: how far to step in x and y direction. 
						 * Double stepsToSide  is the distance between start and end point */
						int stepsToSide = 15;
						/* 	arrowModifierX is hacky. Its a corrective factor to 'fix' 
						 *  getArrowTrans for non linear and non EDGE_TYPE_CURVE.
						 *  It depends on stepsToSide and need to be tested for each stepsToSide.
						 *  Fixing getArrowTrans for this line type would be better, but ... 
						 *  yet this solution is faster ;-) */
						int arrowModifierX = 2;

						Point endPoint = new Point((int) item1.getBounds().getX() + nodeHeight + stepsToSide,
								(int) item1.getBounds().getY() + stepsToSide);
						Point startPoint = new Point((int) item1.getBounds().getX() + nodeHeight - stepsToSide,
								(int) item1.getBounds().getY() + stepsToSide);
						Point controllPoint = new Point((int) item1.getBounds().getCenterX(), (int) item1.getBounds()
								.getCenterY() - (3 * nodeHeight));
						QuadCurve2D curveToTheSameNode = new QuadCurve2D.Double(startPoint.getX(), startPoint.getY(),
								controllPoint.getX(), controllPoint.getY(), endPoint.getX(), endPoint.getY());
						shape = curveToTheSameNode;
						// change the arrow head (new position and direction needed)
						Point arrowPeakEndPoint = new Point((int) item1.getBounds().getX() + nodeHeight + stepsToSide
								- arrowModifierX, (int) item1.getBounds().getY());
						AffineTransform at = getArrowTrans(controllPoint, arrowPeakEndPoint, m_curWidth);
						m_arrowHead = changeArrowHead(m_arrowWidth, m_arrowHeight, arrowType);
						m_curArrow = at.createTransformedShape(m_arrowHead);
					}
				} // END OF: edge from node a to the same node a
				else {
					getCurveControlPoints(edge, m_ctrlPoints, n1x, n1y, n2x, n2y);
					m_cubic.setCurve(n1x, n1y, m_ctrlPoints[0].getX(), m_ctrlPoints[0].getY(), m_ctrlPoints[1].getX(),
							m_ctrlPoints[1].getY(), n2x, n2y);
					shape = m_cubic;
				}

				break;
			default:
				throw new IllegalStateException("Unknown edge type");
		}
		
		
		/* check if the current edge is shown within the sidebar */
		Object checkIsClicked = edge.get(edge.getColumnIndex(ColumnNames.IS_CLICKED));
		if (checkIsClicked != null && (Boolean) checkIsClicked == true) {
			edge.setFillColor(ColorLib.rgb(255, 0, 0)); // red arrow head
			edge.setStrokeColor(ColorLib.rgb(255, 0, 0)); // red arrow
			// thickness of edge stroke isn't changed anymore, only change the stroke of edge label
			// edge.setStroke(new BasicStroke(4)); 	
		} else {
			edge.setFillColor(ColorLib.rgb(0, 0, 0)); // black edge color
			edge.setStrokeColor(ColorLib.rgb(0, 0, 0)); // black arrow
		}
		
		/* check if the current node is a highlighted edge (then show with different color) */
		Object checkIsHightlighted = edge.get(edge.getColumnIndex(ColumnNames.IS_HIGHLIGHTED));
		if (checkIsHightlighted != null && (Boolean) checkIsHightlighted == true) {
			edge.setFillColor(ColorLib.rgb(255, 0, 0));
			edge.setStrokeColor(ColorLib.rgb(255, 0, 0));
			// thickness of edge stroke isn't changed anymore, only change the stroke of edge label
			// edge.setStroke(new BasicStroke(3)); 	
		}

		return shape; // return the edge shape
	}

	/**
	 * this adjusts the arrow head (none, filled or non filled)
	 *
	 * @param w         Integer width
	 * @param h         Integer height
	 * @param arrowType String the type of the arrow @see EdgeType
	 * @return
	 */
	protected Polygon changeArrowHead(int w, int h, String arrowType) {
		if (m_arrowHead == null) {
			m_arrowHead = new Polygon();
		} else {
			m_arrowHead.reset();
		}
		if (arrowType != null && arrowType.equals(EdgesType.arrowtype[2])) {
			// non filled arrow head
			m_arrowHead.addPoint(0, 0);
			m_arrowHead.addPoint(-w / 2, -h);
			m_arrowHead.addPoint(w / 2, -h);
			m_arrowHead.addPoint(0, 0);
			m_arrowHead.addPoint(0, -2);
			m_arrowHead.addPoint((-w / 2) + 2, (-h) + 1);
			m_arrowHead.addPoint((w / 2) - 2, (-h) + 1);
			m_arrowHead.addPoint(0, -2);
			m_arrowHead.addPoint(0, 0);
		}
		if (arrowType != null && arrowType.equals(EdgesType.arrowtype[1])) {
			// filled arrow head
			m_arrowHead.addPoint(0, 0);
			m_arrowHead.addPoint(-w / 2, -h);
			m_arrowHead.addPoint(w / 2, -h);
			m_arrowHead.addPoint(0, 0);
		}
		return m_arrowHead;
	}

}