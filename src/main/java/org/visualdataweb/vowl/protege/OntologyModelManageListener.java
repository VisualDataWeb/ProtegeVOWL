package org.visualdataweb.vowl.protege;

import org.visualdataweb.vowl.graphModifier.TransformOWLtoGraph;
import org.visualdataweb.vowl.rendering.RenderPrefuseGraph;
import org.visualdataweb.vowl.infoPanel.InfoPanelManager;
import org.visualdataweb.vowl.languages.LanguageControlViewEN;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.OWLOntology;
import prefuse.Display;
import org.visualdataweb.vowl.storage.ControlViewStorage;
import org.visualdataweb.vowl.storage.DisplayStorage;
import org.visualdataweb.vowl.storage.GraphStorage;

import java.awt.*;

/**
 * this listener is the real active ontology changed by user listener.
 * This listener gets launched if the user changes the active ontology either by loading a new ontology or by changing
 * the active ontology through protege.
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class OntologyModelManageListener implements OWLModelManagerListener {

	/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
	 * It is used to identify the data which belongs to the current Protégé window.
	 * Within Protégé the user can open different ontologies, which can be shown within the same window or
	 * within different windows. If the are shown within different windows, they are the same protege instance.
	 * So an identifier is needed which is different for each protege instance but ontology independent.  */
	private String viewID;

	/**
	 * Creates a new listener for the ontology model.
	 * @param id the viewManager id
	 */
	public OntologyModelManageListener(String id) {
		viewID = id;
	}

	@Override
	public void handleChange(OWLModelManagerChangeEvent event) {
		if (event.getType() != EventType.ACTIVE_ONTOLOGY_CHANGED) {
			return;
		}

		// get current ontology from protege
		OWLOntology onto = event.getSource().getActiveOntology();
		Display prefuseGraphDisplay = DisplayStorage.getPrefuseDisplay(viewID);

		if (onto != null) {
			// clear the infoPanel
			InfoPanelManager ipm = new InfoPanelManager(viewID);
			ipm.clearTable(viewID);
			ipm.refreshTable(viewID);

			// delete the current Graph
			GraphStorage.newGraph(viewID);

			// transform the current ontology from protege to our graph
			TransformOWLtoGraph twtg = new TransformOWLtoGraph();
			twtg.transformOWLtoGraph(onto, viewID);

			// show the new ontology
			@SuppressWarnings("unused") RenderPrefuseGraph p = new RenderPrefuseGraph(prefuseGraphDisplay, GraphStorage.getGraph(viewID), viewID);

			// restore and enable infoPanel's settings to 'default' for a loaded ontology
			ControlViewStorage.getGravitiyClassesSlider().setEnabled(true);
			ControlViewStorage.getGravitiyClassesSlider().setValue(100);
			ControlViewStorage.getGravitiyLiteralsSlider().setEnabled(true);
			ControlViewStorage.getGravitiyLiteralsSlider().setValue(100);
			ControlViewStorage.getRenderToggleButton().setEnabled(true);
			ControlViewStorage.getRenderToggleButton().setLabel(LanguageControlViewEN.renderToggleButtonOff);

			// reset the view position and zoom level of the graph after changing the active ontology
			if (!DisplayStorage.getPrefuseDisplay(viewID).isTranformInProgress()) {
				DisplayStorage.getPrefuseDisplay(viewID).animatePanAndZoomToAbs(
						new Point(0, 0), 1.0 / DisplayStorage.getPrefuseDisplay(viewID).getScale(), 100
				);
			}
		}
	}
}