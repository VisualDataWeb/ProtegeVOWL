package org.visualdataweb.vowl.rendering;

import prefuse.action.layout.Layout;
import prefuse.data.Edge;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import org.visualdataweb.vowl.types.ColumnNames;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

//import org.apache.log4j.Logger;

/**
 * This class defines on which positions text should be shown within the GraphDataModifier
 */
public class TextLayoutDecorator extends Layout {

	// private static final Logger	logger	= Logger.getLogger(TextLayoutDecorator.class);

	public TextLayoutDecorator(String group) {
		super(group);
	}

	@Override
	public void run(double frac) {

		Iterator iter = m_vis.items(m_group);
		while (iter.hasNext()) {
			DecoratorItem decorator = (DecoratorItem) iter.next();
			VisualItem decoratedItem = decorator.getDecoratedItem();

			/** NODE & EDGES **/
			int colorRedColum = decoratedItem.getColumnIndex(ColumnNames.TEXT_COLOR_RED);
			int colorBlueColum = decoratedItem.getColumnIndex(ColumnNames.TEXT_COLOR_BLUE);
			int colorGreenColum = decoratedItem.getColumnIndex(ColumnNames.TEXT_COLOR_GREEN);
			int textSizeColum = decoratedItem.getColumnIndex(ColumnNames.TEXT_SIZE);

			Object hasValue1 = decoratedItem.get(ColumnNames.TEXT_COLOR_RED);
			Object hasValue2 = decoratedItem.get(ColumnNames.TEXT_COLOR_BLUE);
			Object hasValue3 = decoratedItem.get(ColumnNames.TEXT_COLOR_GREEN);
			// check if hasValue1-3 is not null (if null it hasn't been set)
			if (hasValue1 != null && hasValue2 != null && hasValue3 != null) {
				int colorRed = (Integer) decoratedItem.get(colorRedColum);
				int colorGreen = (Integer) decoratedItem.get(colorGreenColum);
				int colorBlue = (Integer) decoratedItem.get(colorBlueColum);
				int textSize = (Integer) decoratedItem.get(textSizeColum);
				decorator.setTextColor(ColorLib.rgb(colorRed, colorGreen, colorBlue));
				decorator.setFont(FontLib.getFont("Tahoma", textSize));
			}

			/** NODES ONLY **/
			if (decoratedItem instanceof NodeItem) {

				Rectangle2D bounds = decoratedItem.getBounds();

				double x = bounds.getCenterX();
				double y = bounds.getCenterY();

				setX(decorator, null, x);
				setY(decorator, null, y);
			}

			/** EDGES ONLY **/
			if (decoratedItem instanceof EdgeItem) {
				EdgeItem edgeItem = (EdgeItem) decoratedItem;

				NodeItem node1 = edgeItem.getSourceItem();
				NodeItem node2 = edgeItem.getTargetItem();

				double xx;
				double yy;

				if (node1.equals(node2)) {
					
					/* node 1 equals node 2 means the edge is a edge between the same
					 * node a and b. It must be a symmetric relationship. */
					Rectangle2D bounds = edgeItem.getBounds();
					xx = bounds.getCenterX();
					yy = bounds.getCenterY();

				} else {

					EdgeRenderUtilities eru = new EdgeRenderUtilities(edgeItem);

					Object o = edgeItem.get(ColumnNames.RENDER_UTILITY_E2E_ANGEL);
					int count;
					if (o == null) {
						ArrayList<Edge> edgeList = eru.getEdgesBetweenTwoNodes(edgeItem.getSourceNode(), edgeItem.getTargetNode());
						eru.updateEdgeOrderOfEdges(edgeList);
						count = edgeList.indexOf(edgeItem) + 1;
					} else {
						count = (Integer) o;
					}

					// Check whether the source or the target item has a higher CenterY(). This is used to 'fix' text label position for inverse properties.
					// The text label position for inverse properties should be different, otherwise user can't separate them. The Edge should stay as it is.

					// Check whether the source or the target item has a higher CenterY(). This is used to 'fix' text label position for inverse properties.
					// The text label position for inverse properties should be different, otherwise user can't separate them. The Edge should stay as it is.
					VisualItem item2 = edgeItem.getTargetItem();
					VisualItem item1 = edgeItem.getSourceItem();
					int idSource = (Integer) item1.get(ColumnNames.ID);
					int idTarget = (Integer) item2.get(ColumnNames.ID);
					String inverseOf = ((String) edgeItem.get(ColumnNames.RDFS_INVERSE_OF));

					if (inverseOf != null && inverseOf.length() > 0) {
						if (idSource < idTarget) {
							count = count - 2;
						}
					}

					Rectangle2D bounds = edgeItem.getBounds();

					xx = bounds.getCenterX();
					yy = bounds.getCenterY() + 10 * count;

				}

				// change text color, text background color and text background stroke color (defined within the edge)
				// @formatter:off
				if (decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_COLOR_RED)) != null
						&& decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_COLOR_GREEN)) != null
						&& decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_COLOR_BLUE)) != null) {
					decorator.setTextColor(ColorLib.rgb(
							(Integer) decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_COLOR_RED)),
							(Integer) decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_COLOR_GREEN)),
							(Integer) decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_COLOR_BLUE))));
				}
				if (decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_COLOR_RED)) != null
						&& decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_COLOR_GREEN)) != null
						&& decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_COLOR_BLUE)) != null) {
					decorator.setFillColor(ColorLib.rgb(
							(Integer) decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_COLOR_RED)),
							(Integer) decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_COLOR_GREEN)),
							(Integer) decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_COLOR_BLUE))));
				}
				if (decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_RED)) != null
						&& decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_GREEN)) != null
						&& decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_BLUE)) != null) {
					decorator.setStrokeColor(ColorLib.rgb(
							(Integer) decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_RED)),
							(Integer) decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_GREEN)),
							(Integer) decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.TEXT_BACKGROUND_STROKE_COLOR_BLUE))));
				}
				// @formatter:on

				setX(decorator, null, xx);
				setY(decorator, null, yy);

				decorator.setStroke(new BasicStroke(0));    // remove Stroke per default (add it again later if needed=

				Object checkIsHightlighted = decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.IS_HIGHLIGHTED));
				if (checkIsHightlighted != null && ((Boolean) checkIsHightlighted)) {
					decorator.setFillColor(ColorLib.rgb(255, 0, 0));
					decorator.setStroke(new BasicStroke(0)); // thickness of the stroke
				} 
			
				/* check if the current edge is shown within the sidebar */
				Object checkIsClicked = decoratedItem.get(decoratedItem.getColumnIndex(ColumnNames.IS_CLICKED));
				if (checkIsClicked != null && (Boolean) checkIsClicked) {
					decorator.setStrokeColor(ColorLib.rgb(255, 0, 0));
					decorator.setStroke(new BasicStroke(2)); // thickness of the stroke
				} else {
					// remove stroke color on non clicked elements
					decorator.setStrokeColor(ColorLib.rgb(255, 255, 255));
				}

			}

		}
	}
}
