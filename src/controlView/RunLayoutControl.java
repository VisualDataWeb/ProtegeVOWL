package controlView;

import storage.DisplayStorage;
import storage.PrefuseForceAbstractLayoutStorage;

public class RunLayoutControl {

	private final String _LAYOUT = "layout";

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
	public RunLayoutControl(String id) {
		viewManagerID = id;
	}

	/**
	 * stops the layout process and decrease CPU load
	 */
	public void stopLayouting() {
		PrefuseForceAbstractLayoutStorage.getForceDirectedLayout(viewManagerID).setEnabled(false);
	}

	/**
	 * enables the previously disabled layout process
	 */
	public void startLayouting() {
		PrefuseForceAbstractLayoutStorage.getForceDirectedLayout(viewManagerID).setEnabled(true);
		/* previously implemented version of the same function {
		// remove the reserve layout
		removeReserveLayout();
		// enable the main layout with force abstract layout
		DisplayStorage.getPrefuseDisplay().getVisualization().run(_LAYOUT);
		} */
	}

	/**
	 * checks if the layout process is running
	 *
	 * @return boolean
	 */
	public boolean isLayouting() {
		return (DisplayStorage.getPrefuseDisplay(viewManagerID).getVisualization().getAction(_LAYOUT).isEnabled());
	}

}
