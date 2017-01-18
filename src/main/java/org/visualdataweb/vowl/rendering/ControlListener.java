package org.visualdataweb.vowl.rendering;

/**
 * this class is a click listener, to react if an element of a GraphDataModifier is clicked
 */

import org.visualdataweb.vowl.controlView.RunLayoutControl;
import org.visualdataweb.vowl.infoPanel.InfoPanelDataExtractor;
import org.visualdataweb.vowl.infoPanel.InfoPanelManager;
import prefuse.Display;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableDecoratorItem;
import org.visualdataweb.vowl.storage.DisplayStorage;
import org.visualdataweb.vowl.types.ColumnNames;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class ControlListener extends ControlAdapter implements Control {


	private static final int MAX_ZOOM_STEP = 3;
	/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
	 * It is used to identify the data which belongs to the current Protégé window.
	 * Within Protégé the user can open different ontologies, which can be shown within the same window or
	 * within different windows. If the are shown within different windows, they are the same protege instance.
	 * So an identifier is needed which is different for each protege instance but ontology independent.  */
	private String viewManagerID;
	private int yLast;
	private Point2D down = new Point2D.Float();

	/**
	 * Constructor
	 *
	 * @param id - viewManagerID
	 */
	public ControlListener(String id) {
		viewManagerID = id;
	}

	/**
	 * mouse-click event on a visual item (node item or edge item)
	 */
	@Override
	public void itemClicked(VisualItem item, MouseEvent e) {

		if (item instanceof NodeItem) {
			NodeItem node = (NodeItem) item;
			Object isClickedTestObject = item.get(ColumnNames.IS_CLICKED);
			removeAttributeIsClickedFromAllGraphElements(node.getGraph());
			if (isClickedTestObject != null) {
				boolean isClicked = (Boolean) isClickedTestObject;
				item.set(ColumnNames.IS_CLICKED, !isClicked);
				if (isClicked) {
					InfoPanelManager ipm = new InfoPanelManager(viewManagerID);
					ipm.clearTable(viewManagerID);
					ipm.refreshTable(viewManagerID);
				} else {
					@SuppressWarnings("unused") InfoPanelDataExtractor ipde = new InfoPanelDataExtractor(node, viewManagerID);
				}
			} else {
				item.set(ColumnNames.IS_CLICKED, true);
				@SuppressWarnings("unused") InfoPanelDataExtractor ipde = new InfoPanelDataExtractor(node, viewManagerID);
			}

		}

		if (item instanceof EdgeItem) {
			EdgeItem edge = (EdgeItem) item;
			Object isClickedTestObject = item.get(ColumnNames.IS_CLICKED);
			removeAttributeIsClickedFromAllGraphElements(edge.getGraph());
			if (isClickedTestObject != null) {
				boolean isClicked = (Boolean) isClickedTestObject;
				item.set(ColumnNames.IS_CLICKED, !isClicked);
				if (isClicked) {
					InfoPanelManager ipm = new InfoPanelManager(viewManagerID);
					ipm.clearTable(viewManagerID);
					ipm.refreshTable(viewManagerID);
				} else {
					@SuppressWarnings("unused") InfoPanelDataExtractor ipde = new InfoPanelDataExtractor(edge, viewManagerID);
				}
			} else {
				item.set(ColumnNames.IS_CLICKED, true);
				@SuppressWarnings("unused") InfoPanelDataExtractor ipde = new InfoPanelDataExtractor(edge, viewManagerID);
			}
		}

		if (item instanceof TableDecoratorItem) {
			TableDecoratorItem tdi = (TableDecoratorItem) item;

			Graph graph = (Graph) ((Display) e.getSource()).getVisualization().getGroup("GraphDataModifier");

			// find the corresponding object (EdgeItem or NodeItem) to which this TableDecoratorItem belongs to
			Object parentObject = null;
			for (int i = 0; i < graph.getEdgeCount(); i++) {
				Edge edge = graph.getEdge(i);
				Integer tdiID = (Integer) tdi.get(ColumnNames.ID);
				Integer edgeID = (Integer) edge.get(ColumnNames.ID);
				if (tdiID == edgeID) {
					parentObject = edge;
					break;
				}
			}
			if (parentObject == null) {
				for (int i = 0; i < graph.getNodeCount(); i++) {
					Node node = graph.getNode(i);
					Integer tdiID = (Integer) tdi.get(ColumnNames.ID);
					Integer nodeID = (Integer) node.get(ColumnNames.ID);
					if (tdiID == nodeID) {
						parentObject = node;
						break;
					}
				}
			}
			// if the corresponding object (EdgeItem or NodeItem) has been found call this method again
			// but with the corresponding object instead of the original TableDecoratorItem
			if (parentObject != null) {
				if (parentObject instanceof VisualItem) {
					itemClicked((VisualItem) parentObject, e);
				}
			}
		}
	}

	/**
	 * mouse-over event on a visual item (node item or edge item)
	 */
	@Override
	public void itemEntered(VisualItem item, MouseEvent e) {

		// if ctrl is pressed, user zooms -> ignore itemEntered
		if (e.getModifiers() == InputEvent.CTRL_MASK) {
			ctrlZoom(e);
			return;
		}

		// only mark items as highlighted if the layout process is active
		RunLayoutControl rlc = new RunLayoutControl(viewManagerID);
		if (rlc.isLayouting()) {

			if (item instanceof NodeItem) {
				/* set highlight attribute to true, NodeRenderer will change the color */
				item.set(ColumnNames.IS_HIGHLIGHTED, true);
			}

			if (item instanceof EdgeItem) {
				/* set highlight attribute to true, EdgeRenderer will change the color */
				item.set(ColumnNames.IS_HIGHLIGHTED, true);
			}

			if (item instanceof TableDecoratorItem) {
				/* set highlight attribute to true, EdgeRenderer will change the color */
				item.set(ColumnNames.IS_HIGHLIGHTED, true);
			}
		}

	}

	/**
	 * mouse moves away from a visual item (node item or edge item)
	 */
	@Override
	public void itemExited(VisualItem item, java.awt.event.MouseEvent e) {

		// if ctrl is pressed, user zooms -> ignore itemExited
		// disable highlight possible but it should be same as zoom by mouse wheel or zoom by prefuse's right click zoom
		if (e.getModifiers() == InputEvent.CTRL_MASK) {
			ctrlZoom(e);
			return;
		}

		if (item instanceof NodeItem) {
			/* set highlight attribute to false, NodeRenderer will change the color */
			item.set(ColumnNames.IS_HIGHLIGHTED, false);
		}

		if (item instanceof EdgeItem) {
			/* set highlight attribute to false, EdgeRenderer will change the color */
			item.set(ColumnNames.IS_HIGHLIGHTED, false);
		}

		if (item instanceof TableDecoratorItem) {
			/* set highlight attribute to false, EdgeRenderer will change the color */
			item.set(ColumnNames.IS_HIGHLIGHTED, false);
		}

	}

	/**
	 * remove the attribute "is clicked" from all nodes and edges
	 */
	private void removeAttributeIsClickedFromAllGraphElements(Graph graph) {
		for (int i = 0; i < graph.getNodeCount(); i++) {
			Node node = graph.getNode(i);
			node.set(ColumnNames.IS_CLICKED, false);
		}
		for (int i = 0; i < graph.getEdgeCount(); i++) {
			Edge edge = graph.getEdge(i);
			edge.set(ColumnNames.IS_CLICKED, false);
		}
	}

	/**
	 * item is moved (user action)
	 */
	public void itemMoved(VisualItem item, MouseEvent e) {
		if (e.getModifiers() == InputEvent.CTRL_MASK) {
			ctrlZoom(e);
		} else {
			if (DisplayStorage.getPrefuseDisplay(viewManagerID).getCursor()
					.equals(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR))) {
				DisplayStorage.getPrefuseDisplay(viewManagerID).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}

	/**
	 * mouse move listener, check if user pressed CTRL as zooming alternative
	 *
	 * @param e - MouseEvent
	 */
	public void mouseMoved(MouseEvent e) {
		ctrlZoom(e);
	}

	/**
	 * move the mouse and press CTRL as zooming alternative
	 *
	 * @param e - MouseEvent
	 */
	private void ctrlZoom(MouseEvent e) {

		if (e.getModifiers() == InputEvent.CTRL_MASK) {
			if (DisplayStorage.getPrefuseDisplay(viewManagerID).isTranformInProgress() || yLast == -1 || down == null) {
				yLast = -1;
				return;
			}
			int y = e.getY();
			int dy = y - yLast;
			double zoom = 1 + ((double) dy) / 100;
			// 'invert' the zooming, mouse up to get closer
			if (1 + (1 - zoom) > 0) {
				zoom = 1 + (1 - zoom);
			}
			// "filter" zoom levels which are too high or below zero
			if (zoom > MAX_ZOOM_STEP || zoom < 0) {
				zoom = 1;
			}
			PrefuseZoomControl zm = new PrefuseZoomControl();
			int status = zm.zoom(DisplayStorage.getPrefuseDisplay(viewManagerID), down, zoom, false);
			int cursor = Cursor.N_RESIZE_CURSOR;
			if (status == PrefuseZoomControl.PUBLIC_NO_ZOOM) {
				cursor = Cursor.WAIT_CURSOR;
			}
			DisplayStorage.getPrefuseDisplay(viewManagerID).setCursor(Cursor.getPredefinedCursor(cursor));
			yLast = y;

		} else {
			/* always zoom to the center of the view, alternative the mouse position could be taken with: = e.getPoint();  */
			int x = (int) DisplayStorage.getPrefuseDisplay(viewManagerID).getBounds().getCenterX();
			int y = (int) DisplayStorage.getPrefuseDisplay(viewManagerID).getBounds().getCenterY();
			down = new Point(x, y);
			yLast = 0;
			DisplayStorage.getPrefuseDisplay(viewManagerID).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

}

class PrefuseZoomControl extends prefuse.controls.ZoomControl {

	public static final int PUBLIC_NO_ZOOM = NO_ZOOM;

	/**
	 * Zoom the given display at the given point by the zoom factor,
	 * in either absolute (item-space) or screen co-ordinates.
	 *
	 * @param display the Display to zoom
	 * @param p       the point to center the zoom upon
	 * @param zoom    the scale factor by which to zoom
	 * @param abs     if true, the point p should be assumed to be in absolute
	 *                coordinates, otherwise it will be treated as scree (pixel) coordinates
	 * @return a return code indicating the status of the zoom operation.
	 *         One of {@link #ZOOM}, {@link #NO_ZOOM}, {@link #MIN_ZOOM}, {@link #MAX_ZOOM}.
	 */
	@Override
	public int zoom(Display display, Point2D p, double zoom, boolean abs) {
		return super.zoom(display, p, zoom, abs);
	}
}
