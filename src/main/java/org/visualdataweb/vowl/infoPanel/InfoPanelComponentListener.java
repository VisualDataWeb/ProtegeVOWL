package org.visualdataweb.vowl.infoPanel;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import org.visualdataweb.vowl.storage.GraphStorage;
import org.visualdataweb.vowl.types.ColumnNames;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class InfoPanelComponentListener implements ComponentListener {

	/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
	 * It is used to identify the data which belongs to the current Protégé window.
	 * Within Protégé the user can open different ontologies, which can be shown within the same window or
	 * within different windows. If the are shown within different windows, they are the same protege instance.
	 * So an identifier is needed which is different for each protege instance but ontology independent.  */
	private String viewManagerID;

	/**
	 * constructor
	 *
	 * @param id - 	viewManagerID
	 */
	public InfoPanelComponentListener(String id) {
		viewManagerID = id;
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// Auto-generated method stub, not used yet
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// Auto-generated method stub, not used yet
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		Graph graph = GraphStorage.getGraph(viewManagerID);

		for (int i = 0; i < graph.getNodeCount(); i++) {
			Node node = graph.getNode(i);
			Object nodeClicked = node.get(ColumnNames.IS_CLICKED);

			if (nodeClicked == null) {
				continue;
			}

			boolean isClicked = (Boolean) nodeClicked;

			if (isClicked) {
				@SuppressWarnings("unused") // InfoPanelDataExtractor uses node data to update the InfoPanel
						InfoPanelDataExtractor ipde = new InfoPanelDataExtractor(node, viewManagerID);
			}
		}

		for (int i = 0; i < graph.getEdgeCount(); i++) {
			Edge edge = graph.getEdge(i);
			Object edgeClicked = edge.get(ColumnNames.IS_CLICKED);

			if (edgeClicked == null) {
				continue;
			}

			boolean isClicked = (Boolean) edgeClicked;

			if (isClicked) {
				@SuppressWarnings("unused")  // InfoPanelDataExtractor uses edge data to update the InfoPanel
						InfoPanelDataExtractor ipde = new InfoPanelDataExtractor(edge, viewManagerID);
			}
		}
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// Auto-generated method stub, not used yet
	}

}
