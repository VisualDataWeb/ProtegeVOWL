package org.visualdataweb.vowl.protege;

import org.visualdataweb.vowl.graphModifier.TransformOWLtoGraph;
import org.visualdataweb.vowl.rendering.ControlListener;
import org.visualdataweb.vowl.rendering.RenderPrefuseGraph;
import org.visualdataweb.vowl.rendering.VOWLWheelZoomControl;
import layout.TableLayout;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;
import prefuse.Display;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import org.visualdataweb.vowl.storage.DisplayStorage;
import org.visualdataweb.vowl.storage.GraphStorage;
import org.visualdataweb.vowl.storage.OWLModelManagerStorage;

import java.awt.*;

/**
 *
 * @author David Bold, Vincent Link, Eduard Marbach
 * @version 1.0
 */
public class VOWLViewComponent extends AbstractOWLViewComponent {

	private static final long serialVersionUID = -4276647252433078984L;
	private Display prefuseGraphDisplay;

	@Override
	protected void initialiseOWLView() throws Exception {
		
		/* TableLayout as layout manager, VOWLViewComponent fills the size of the view component in both directions.
		 * Useful if the layout is changed later! This LayoutManager is similar to the C# WPF Grid Layout,
		 * 	 {0.8, TableLayout.FILL} for 80% of the size for view 1 and the rest for view 2.
		 */
		TableLayout pluginGraphViewComponentLayout = new TableLayout(new double[][]{{TableLayout.FILL},
				{TableLayout.FILL}});
		setLayout(pluginGraphViewComponentLayout);
		
		/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
		 * It is used to identify the data which belongs to the current Protégé window.
		 * Within Protégé the user can open different ontologies, which can be shown within the same window or
		 * within different windows. If the are shown within different windows, they are the same protege instance.
		 * So an identifier is needed which is different for each protege instance but ontology independent.  */
		String viewManagerID = getOWLWorkspace().getViewManager().toString();

		// create new display if it doesn't exist yet
		if (prefuseGraphDisplay == null) {
			prefuseGraphDisplay = new Display();
			prefuseGraphDisplay.setDamageRedraw(false);
			prefuseGraphDisplay.setHighQuality(true);
		}

		// add listenere here to avoid adding more then one listener later (while the ontology is reloaded)
		prefuseGraphDisplay.addControlListener(new DragControl());
		prefuseGraphDisplay.addControlListener(new PanControl());
		prefuseGraphDisplay.addControlListener(new ZoomControl());

		// like WheelZoomControl except the zooming direction is inverted, mouse wheel up to get closer to the object
		prefuseGraphDisplay.addControlListener(new VOWLWheelZoomControl());

		// add tooltip support
		prefuseGraphDisplay.addControlListener(new ControlListener(viewManagerID));

		GraphStorage.newGraph(viewManagerID);

		// save the current OWLManager for later use (needed to reload the ontology later)
		OWLModelManagerStorage.setOWLManager(getOWLModelManager(), viewManagerID);

		// save the current display used from prefuse (needed to reload the ontology later)
		DisplayStorage.addPrefuseDisplay(prefuseGraphDisplay, viewManagerID);
		OWLOntology onto = getOWLModelManager().getActiveOntology();
		TransformOWLtoGraph twtg = new TransformOWLtoGraph();
		twtg.transformOWLtoGraph(onto, viewManagerID);

		// GraphDataModifier prefuse GraphDataModifier view
		@SuppressWarnings("unused")
		RenderPrefuseGraph p = new RenderPrefuseGraph(prefuseGraphDisplay, GraphStorage.getGraph(viewManagerID), viewManagerID);

		// Try to center the graph. Why *-1? Seems the offset between the view and the visualization can be balanced
		// this way
		if (!prefuseGraphDisplay.isTranformInProgress()) {
			int x = (int) prefuseGraphDisplay.getBounds().getCenterX() * -1;
			int y = (int) prefuseGraphDisplay.getBounds().getCenterY() * -1;
			final Point center = new Point(x, y);
			prefuseGraphDisplay.animatePanToAbs(center, 100);
		}

		add(prefuseGraphDisplay, "0,0");
	}

	@Override
	protected void disposeOWLView() {
		OWLModelManagerStorage.disposeListener(getOWLWorkspace().getViewManager().toString());
		prefuseGraphDisplay.removeAll();
		prefuseGraphDisplay = null;
	}
}