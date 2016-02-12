package rendering;

/**
 * this class is for GraphRendering the prefuse nodes, 
 * it extends the prefuse AbstractShapeRenderer with own functionality 
 * */

import org.apache.log4j.Logger;
import prefuse.render.AbstractShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import types.ColumnNames;
import types.Nodetype;

import java.awt.*;
import java.awt.geom.*;

public class NodeRenderer extends AbstractShapeRenderer {

	@SuppressWarnings("unused")// maybe needed later, don't want the warning
	private static final Logger logger = Logger.getLogger(NodeRenderer.class);
	protected Ellipse2D box_circle = new Ellipse2D.Double();
	protected RectangularShape box_block = new Rectangle2D.Double();
	protected Arc2D box_pie;
	protected GeneralPath generalPath = new GeneralPath();

	/**
	 * distinct how the nodes are rendered (how they are visualized)
	 */
	@Override
	protected Shape getRawShape(VisualItem item) {
		
		/*
		 * WORKAROUND ! item.get("size") does not work ->
		 * ArrayIndexOutOfBoundsException workaround: get column of the
		 * attribute "size" then get data from this column does work
		 */
		int heightColumn = (Integer) item.getColumnIndex(ColumnNames.NODE_HEIGHT);
		int widthColumn = (Integer) item.getColumnIndex(ColumnNames.NODE_WIDTH);

		int formColumn = (Integer) item.getColumnIndex(ColumnNames.NODE_FORM);
		int vowlTypeColumn = (Integer) item.getColumnIndex(ColumnNames.NODE_VOWL_TYPE);

		int colorRedColum = (Integer) item.getColumnIndex(ColumnNames.COLOR_RED);
		int colorBlueColum = (Integer) item.getColumnIndex(ColumnNames.COLOR_BLUE);
		int colorGreenColum = (Integer) item.getColumnIndex(ColumnNames.COLOR_GREEN);

		int height = 5;
		int width = 5;
		String form = Nodetype.nodetype[0];
		String vowlType = Nodetype.vowltype[0];

		if (heightColumn != -1 && widthColumn != -1 && formColumn != -1) {
			height = (Integer) item.get(heightColumn);
			width = (Integer) item.get(widthColumn);
			form = (String) item.get(formColumn);
			vowlType = (String) item.get(vowlTypeColumn);
		}
		
		/* check if the current node is a highlighted node (then show with different color) */
		Object checkIsHightlighted = item.get(item.getColumnIndex(ColumnNames.IS_HIGHLIGHTED));
		if (checkIsHightlighted == null || (Boolean) checkIsHightlighted == false) {
			// fill the node with the specified color
			if (colorRedColum != -1 && colorBlueColum != -1 && colorGreenColum != -1) {
				int colorRed = (Integer) item.get(colorRedColum);
				int colorGreen = (Integer) item.get(colorGreenColum);
				int colorBlue = (Integer) item.get(colorBlueColum);
				item.setFillColor(ColorLib.rgb(colorRed, colorGreen, colorBlue));
				item.setStrokeColor(ColorLib.rgb(0, 0, 0)); // stroke color black
				item.setStroke(new BasicStroke(2)); // thickness of the stroke
				item.setHighlighted(false);
			}
		} else {
			// Node is highlighted, change the colors
			if (colorRedColum != -1 && colorBlueColum != -1 && colorGreenColum != -1) {
				int colorRed = 255; // 100 - (Integer) item.get(colorRedColum);
				int colorGreen = 0; // 150 - (Integer) item.get(colorGreenColum);
				int colorBlue = 0; // 200 - (Integer) item.get(colorBlueColum);
				item.setFillColor(ColorLib.rgb(colorRed, colorGreen, colorBlue));
				item.setStrokeColor(ColorLib.rgb(0, 0, 0)); // stroke color blue
				item.setStroke(new BasicStroke(3)); // thickness of the stroke
				item.setHighlighted(true);
			}
		}
		
		/* check if the current node is shown within the sidebar */
		Object checkIsClicked = item.get(item.getColumnIndex(ColumnNames.IS_CLICKED));
		if (checkIsClicked != null && (Boolean) checkIsClicked == true) {
			item.setStrokeColor(ColorLib.rgb(255, 0, 0)); // stroke color red
			item.setStroke(new BasicStroke(4)); // thickness of the stroke
			item.setHighlighted(true);
		}
		
		/* check if node is OWLThing. If yes the margin has to be dashed */
		if (Nodetype.vowltype[6].equals(vowlType)) {
			Stroke drawingStroke;
			if (checkIsClicked != null && (Boolean) checkIsClicked == true) {
				// a thickness of 5 as above seems to be to much, so take 4 for visual reasons
				drawingStroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8}, 0);
			} else if (checkIsHightlighted != null && (Boolean) checkIsHightlighted == true) {
				drawingStroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8}, 0);
			} else {
				drawingStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8}, 0);
			}
			item.setStroke((BasicStroke) drawingStroke);
			box_circle.setFrame(item.getX(), item.getY(), width, height);
			return box_circle;
		}
		
		
		/* check if node is GenericLiteral. If yes the margin has to be dashed */
		if (Nodetype.vowltype[7].equals(vowlType)) {
			Stroke drawingStroke;
			if (checkIsClicked != null && (Boolean) checkIsClicked == true) {
				// a thickness of 5 as above seems to be to much, so take 4 for visual reasons
				drawingStroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8}, 0);
			} else if (checkIsHightlighted != null && (Boolean) checkIsHightlighted == true) {
				drawingStroke = new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8}, 0);
			} else {
				drawingStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{8}, 0);
			}
			item.setStroke((BasicStroke) drawingStroke);
			box_block.setFrame(item.getX(), item.getY(), width, height);
			return box_block;
		}
		
		/* DOUBLE CIRCLE '*/
		if (form.equals(Nodetype.nodetype[0]) && vowlType.equals(Nodetype.vowltype[8])) {
			Ellipse2D innerCircle = new Ellipse2D.Double();
			innerCircle.setFrame(item.getX() + 4, item.getY() + 4, width - 8, height - 8);
			generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
			generalPath.moveTo(item.getX(), item.getY());
			box_circle.setFrame(item.getX(), item.getY(), width, height);
			generalPath.append(box_circle, false);
			generalPath.moveTo(item.getX(), item.getY());
			generalPath.append(innerCircle, false);
			// adding box_circle twice is needed for GeneralPath.WIND_EVEN_ODD (fills the wanted part)
			generalPath.moveTo(item.getX(), item.getY());
			generalPath.append(box_circle, false);
			// override stroke (default stroke doesn't look nice)
			item.setStroke(new BasicStroke(2));
			if (checkIsClicked != null && (Boolean) checkIsClicked == true) {
				item.setStroke(new BasicStroke(3));
			} else if (checkIsHightlighted != null && (Boolean) checkIsHightlighted == true) {
				item.setStroke(new BasicStroke(3));
			}
			return generalPath;
		}
		
		/* CIRCLE */
		if (form.equals(Nodetype.nodetype[0]) || vowlType.equals(Nodetype.vowltype[1])) {
			/**
			 * x the X coordinate of the upper-left corner of the specified
			 * rectangular shape y the Y coordinate of the upper-left corner
			 * of the specified rectangular shape w the NODE_WIDTH of the
			 * specified rectangular shape h the NODE_HEIGHT of the specified
			 * rectangular shape
			 */
			box_circle.setFrame(item.getX(), item.getY(), width, height);
			return box_circle;
		}
		
		/* BOX - SQUARE */
		if (form.equals(Nodetype.nodetype[1])) {
			/**
			 * x the X coordinate of the upper-left corner of the specified
			 * rectangular shape y the Y coordinate of the upper-left corner
			 * of the specified rectangular shape w the NODE_WIDTH of the
			 * specified rectangular shape h the NODE_HEIGHT of the specified
			 * rectangular shape
			 */
			box_block.setFrame(item.getX(), item.getY(), width, height);
			return box_block;
		}
		
		/* PIE - Kuchensegment */
		if (form.equals(Nodetype.nodetype[2])) {
			int start = 30;
			int end = 290;
			/**
			 * (x,y,NODE_WIDTH,NODE_HEIGHT,start_angle,end_angle,type) x & y
			 * coordinate of the upper-left corner of the specified pie
			 * NODE_WIDTH of the specified pie, NODE_HEIGHT of the specified pie
			 * start_angle & end_angle specified types : Arc2D.PIE for a Pie
			 * Arc2D.Chord & Arc2D.OPEN seems something like a cropped
			 * circle
			 */
			box_pie = new Arc2D.Double(item.getX(), item.getY(), width, height, start, end, Arc2D.PIE);

			return box_pie;
		}

		// return default (fail safe)
		box_circle.setFrame(item.getX(), item.getY(), 100, 100);
		return box_circle;
	}

}