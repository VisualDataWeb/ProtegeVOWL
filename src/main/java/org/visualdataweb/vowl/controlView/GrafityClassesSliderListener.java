package org.visualdataweb.vowl.controlView;

import org.visualdataweb.vowl.languages.LanguageControlViewEN;
import prefuse.data.Edge;
import org.visualdataweb.vowl.storage.GraphStorage;
import org.visualdataweb.vowl.types.ColumnNames;
import org.visualdataweb.vowl.types.PropertyType;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

public class GrafityClassesSliderListener extends GravityLiteralsSliderListener {

	/* The viewManagerID is an String extracted from the ViewManager of the current OWLWorkspace.
	 * It is used to identify the data which belongs to the current Protégé window.
	 * Within Protégé the user can open different ontologies, which can be shown within the same window or
	 * within different windows. If the are shown within different windows, they are the same protege instance.
	 * So an identifier is needed which is different for each protege instance but ontology independent.  */
	private String viewManagerID;

	/**
	 * constructor
	 *
	 * @param id - viewManagerID
	 */
	public GrafityClassesSliderListener(String id) {
		super(id);
		viewManagerID = id;
	}

	/**
	 * changes the gravity for all elements depending on the slider value
	 */
	@Override
	public void stateChanged(ChangeEvent arg0) {
		JSlider slider = (JSlider) arg0.getSource();
		int value = slider.getValue();

		for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getEdgeCount(); i++) {
			Edge e = GraphStorage.getGraph(viewManagerID).getEdge(i);
			String vowlType = (String) e.get(ColumnNames.EDGE_VOWL_TYPE);

			if (!PropertyType.type[2].equals(vowlType)) {
				e.set(ColumnNames.EDGE_LENGTH, calculate(value, e));
			}
		}

		slider.setToolTipText(LanguageControlViewEN.SLIDER_TOOLTIP + value);
	}

}
