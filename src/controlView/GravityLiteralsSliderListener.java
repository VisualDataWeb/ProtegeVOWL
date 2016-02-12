package controlView;

import graphModifier.GraphDataModifier;
import languages.LanguageControlViewEN;
import prefuse.data.Edge;
import storage.GraphStorage;
import types.ColumnNames;
import types.PropertyType;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GravityLiteralsSliderListener implements ChangeListener {

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
	public GravityLiteralsSliderListener(String id) {
		viewManagerID = id;
	}

	/**
	 * changes the gravity for all elements except for data type properties
	 */
	@Override
	public void stateChanged(ChangeEvent arg0) {
		JSlider slider = (JSlider) arg0.getSource();
		int value = slider.getValue();

		for (int i = 0; i < GraphStorage.getGraph(viewManagerID).getEdgeCount(); i++) {
			Edge e = GraphStorage.getGraph(viewManagerID).getEdge(i);
			String vowlType = (String) e.get(ColumnNames.EDGE_VOWL_TYPE);

			if (PropertyType.type[2].equals(vowlType)) {
				e.set(ColumnNames.EDGE_LENGTH, calculate(value, e));
			}
		}

		slider.setToolTipText(LanguageControlViewEN.SLIDER_TOOLTIP + value);
	}

	/**
	 * calculates the length of an given edge depending on their length and user's choise (slider)
	 *
	 * @param value int - the value from the slider
	 * @param e     Edge - a given edge to calculate their new length
	 * @return newLength int - the new length for the given edge e
	 */
	protected int calculate(int value, Edge e) {
		GraphDataModifier gdm = new GraphDataModifier(viewManagerID);
		int defaultEdgeLength = gdm.calculateDefaultEdgeLength(e.getSourceNode(), e.getTargetNode());
		float factor = ((float) value) / ((float) 100);

		return (int) (defaultEdgeLength * factor);
	}

}
